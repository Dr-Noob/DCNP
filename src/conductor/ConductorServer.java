package conductor;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.concurrent.Semaphore;

import message.NodeConductor.MessageProblemModule;


public class ConductorServer extends Thread {
	
	public NodesDatabase nodesDatabase;
	
	private ServerSocket serverSocket;
	private File solverModule;
	private ConductorThread[] threads;
	private final int PORT = 4450;
	private final int MAX_CONDUCTOR_THREADS = 100;
	private final int TIMEOUT = 1000;
	private boolean shouldQuit;
	private boolean computingStarted;
	private boolean solutionFound;
	private int threadCounter;
	private Folder folder;
	private MessageProblemModule problem;
	private ConductorShell shell;
	private ConductorProblem conductorProblem;
	private ConductorArgumentsParser parser;
	private Semaphore start;
	private Semaphore constructor;
	private PriorityQueue<Integer> threadQueue; //Queue to handle threads array
	private LinkedList<String> inputsNotComputed; //Inputs sent to nodes to be computed, but that we don't have their output yet
	
	public ConductorServer(Folder folder, ConductorArgumentsParser parser) {
		this.shouldQuit = false;
		this.parser = parser;
		this.computingStarted = false;
		this.solutionFound = false;
		this.folder = folder;
		this.threads = new ConductorThread[MAX_CONDUCTOR_THREADS];
		this.nodesDatabase = new NodesDatabase();
		this.threadCounter = 0;
		this.start = new Semaphore(0);
		this.constructor = new Semaphore(0);
		this.threadQueue = new PriorityQueue<>(MAX_CONDUCTOR_THREADS);
		this.inputsNotComputed = new LinkedList<>();
		
		this.solverModule = new File(this.folder.getSolverPath());
		if(!this.solverModule.exists()) {
			System.out.println("ERROR: File '" + solverModule.getName() + "' does not exist");
			this.shouldQuit = true;
			this.constructor.release();
			return;
		}
		this.problem = new MessageProblemModule(this.solverModule,this.folder.getSolverArgs());
		
		try { //Open socket and bind port
			this.serverSocket = new ServerSocket();
			this.serverSocket.bind(new InetSocketAddress(PORT));
		} catch (IOException e) {
			System.out.println("ERROR: Couldn't bind port " + PORT);
			System.out.println("A process is running at that port, finish it before launching Conductor again");
			this.shouldQuit = true;
			this.constructor.release();
			return;
		}
		try { //Set socket timeout
			this.serverSocket.setSoTimeout(TIMEOUT);
		} catch (SocketException e) {
			System.out.println("ERROR: Can not set socket timeout to " + TIMEOUT + "ms");
			this.shouldQuit = true;
			this.constructor.release();
			return;
		}
		this.constructor.release();
	}
	
	public void run() {
		int n = 0;
		Socket clientSocket;
		while(!shouldQuit) { 
			try {
				clientSocket = this.serverSocket.accept();
				ConductorThread ct = new ConductorThread(clientSocket, nodesDatabase,this,problem,parser.areNamesRequired(),parser.isDebugModeActivated(),start,threadCounter);
				n = nextThread();
				threads[n] = ct;
				threads[n].start();
			} catch (SocketTimeoutException e1) {
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		for(int i=0;i<this.threadCounter;i++) {
			threads[i].endThread();
		}
		this.shell.closeShell();
		this.conductorProblem.end();
	}
	
	/**
	 * Removes the thread from the thread array
	 * @param threadId Thread to be removed
	 */
	private int nextThread() {
		//First, look in queue. If there's something, return first element of queue and remove it, if
		//there isn't, add 1 to threadCounter and return it
		if(this.threadQueue.isEmpty()) {
			this.threadCounter++;
			return this.threadCounter-1;
		}
		return this.threadQueue.poll();
	}
	
	/**
	 * Removes the thread from the thread array
	 * @param threadId Thread to be removed
	 */
	public void removeThread(int threadId) {
		//Add id to the queue
		this.threadQueue.add(threadId);
	}
	
	public void setShell(ConductorShell shell) {
		this.shell = shell;
	}
	
	public void setProblem(ConductorProblem problem) {
		this.conductorProblem = problem;
	}
	
	//================STATISTICS================//
	
	public int getNumNodesComputing() {
		return this.nodesDatabase.getNodesComputing();
	}
	
	public int getNumNodesConnected() {
		return this.nodesDatabase.getNodesConnected();
	}
	
	public int getNumCores() {
		return this.nodesDatabase.getCores();
	}
	
	public int getInsComputed() {
		return this.nodesDatabase.getInsComputed();
	}
	
	public float getTimeComputing() {
		return this.nodesDatabase.getTimeComputing();
	}
	
	public boolean shouldQuit() {
		try {
			this.constructor.acquire();
		} catch (InterruptedException e) {
			this.shell.printError("ERROR: Semaphore interrupted");
			this.shouldQuit = true;
		}
		this.constructor.release();
		return this.shouldQuit;
	}
	
	public String getInvalidInput() {
		return this.conductorProblem.getInvalidInput();
	}
	
	/**
	 * Ends the server
	 */
	public void end() {
		if(this.computingStarted) {
			System.out.println("Showing final statistics:\n");
			this.nodesDatabase.showStatistics();
			this.shell.print("Total time spent: " + this.nodesDatabase.getTimeComputing() + "s");
		}
		System.out.println("Exiting conductor...");
		this.shouldQuit = true;
	}
	
	public void startComputing() {
		if(this.computingStarted) {
			System.out.println("WARNING: Server is already started");
		}
		else {
			this.computingStarted = true;
			this.start.release();
			this.nodesDatabase.computingStarted();
		}
	}
	
	//================SYNCHRONIZED================//
	
	/**
	 * ConductorThreads will ask if computation started or not, to tell nodes
	 * @return If computation started or not
	 */
	synchronized boolean startedComputing() {
		return this.computingStarted;
	}
	
	synchronized public void refreshShell() {
		this.shell.refresh();
	}
	
	/**
	 * Get the next in to serve a node
	 * @return The next in
	 */
	synchronized String nextIn() {
		if(this.solutionFound)return null;
		String s = this.conductorProblem.getNextIn();
		if(this.conductorProblem.solutionFound()) { //Solution found
			System.out.println("SOLUTION FOUND");
			System.out.println("Solution: [" + s + "]");
			this.solutionFound = true;
			this.nodesDatabase.computingEnded();
			this.end();
		}
		else if (s == null) { //Solution not found, but found a error in communication with problem module
			System.out.println("FATAL ERROR: Message not recognised; problem module does not behave as expected");
			this.end();
		}
		
		if(!s.equals(this.getInvalidInput()))this.inputsNotComputed.add(s); //If valid add to the list
		else if(!this.inputsNotComputed.isEmpty())return inputsNotComputed.getFirst(); //If is not valid, try to return a valid one stored in the list
		return s;
	}
	
	/**
	 * Send to problem module a new fresh out recently computed
	 * @param out The new computed output
	 * @param input The input for which the output was computed
	 * @param ip The ip of the node who computed the out
	 */
	synchronized void newOut(String out, String input, String ip) {
		this.inputsNotComputed.remove(input);
		this.conductorProblem.sendNewOut(out,input);
		this.nodesDatabase.newOut(ip);
	}
	
	/**
	 * @return true if the solution was found, false if not
	 */
	synchronized boolean solutionFound() {
		return this.solutionFound;
	}
}
