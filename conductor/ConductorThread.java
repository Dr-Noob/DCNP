package conductor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.Semaphore;

import message.NodeConductor.*;

public class ConductorThread extends Thread {
	private Socket socket;
	private DataOutputStream dos;
    private DataInputStream dis;
    private NodesDatabase nodesDatabase;
    private MessageProblemModule problem;
    private MessageAddNode addNode;
    private ConductorServer server;
    private boolean end;
    private int threadId;
    private Semaphore start;
	
	public ConductorThread(Socket socket, NodesDatabase nodesDatabase, ConductorServer server, MessageProblemModule problem, Semaphore start, int threadId){
		this.problem = problem;
		this.socket = socket;
		this.server = server;
		this.threadId =  threadId;
		this.end = false;
		this.nodesDatabase = nodesDatabase;
		this.start = start;
		try {
			this.dos = new DataOutputStream(this.socket.getOutputStream());
			this.dis = new DataInputStream(this.socket.getInputStream());
		} catch (IOException e) {
			System.out.println("ERROR: Failed to open Data Streams with " + socket.getInetAddress().getHostAddress());
			return;
		}
	}

	/**
	 * Call this method if a this single node must be disconnected from the server, but never if all nodes must be disconnected
	 */
	private void end() {
		this.server.removeThread(this.threadId);
		//this.server.refreshShell();
		//if(addNode != null)this.nodesDatabase.removeNode(addNode.getnThreads(),addNode.getOs(),addNode.getCpuArch(),socket.getInetAddress().getHostAddress());
		this.nodesDatabase.nodeDesconected(socket.getInetAddress().getHostAddress());
		this.closeSocket();
	}
	
	/**
	 * Ends the thread in a friendly way as fast as possible
	 */

	public void endThread() {
		this.end = true;
		this.start.release();
	}
	
	public void run() {
		//Receive ADD_NODE
		addNode = new MessageAddNode(dis);
		if(!message.NodeConductor.Message.printErrorIfNotValid(addNode)) {
			this.end();
			return;
		}
		
		//Add this node to the database
		if(!nodesDatabase.addNode(addNode.getnThreads(),addNode.getOs(),addNode.getCpuArch(),socket.getInetAddress().getHostAddress())){
			System.out.println("ERROR: Node with ip " + socket.getInetAddress().getHostAddress() + " is already active in the server, and tried to establish a connection again");
			System.out.println("It'll be ignored");
			closeSocket();
			return;
		}
		server.refreshShell();
		
		//Send PROBLEM_MODULE
		message.NodeConductor.Message.sendMessage(problem, dos);
		
		//Send HAS_STARTED
		if(server.startedComputing()) {
			MessageHasStarted started = new MessageHasStarted(true);
			message.NodeConductor.Message.sendMessage(started, dos);
		}
		else {
			MessageHasStarted started = new MessageHasStarted(false);
			message.NodeConductor.Message.sendMessage(started, dos);
			//Block thread in semaphore
			try {
				start.acquire();
			} catch (InterruptedException e) {
				System.out.println("FATAL ERROR: Start semaphore was interrupted");
				return;
			}
			//Chained awakening of the global semaphore
			start.release();
			if(this.end) {
				MessageBye bye = new MessageBye();
				message.NodeConductor.Message.sendMessage(bye, dos);
				this.end();
				return;
			}
			MessageStart start = new MessageStart();
			//Send START
			message.NodeConductor.Message.sendMessage(start, dos);
		}
		
		this.server.nodesDatabase.nodeStartedComputing(this.socket.getInetAddress().getHostAddress());
		Message resp = null;
		while(!this.end) {
			String newIn = this.server.nextIn();
			System.out.println("New input [" + newIn + "]");
			if(newIn == null) {
				this.end();
				return;
			} else if (this.server.solutionFound() || newIn.equals(this.server.getInvalidInput())) {
				MessageBye bye = new MessageBye();
				message.NodeConductor.Message.sendMessage(bye, dos);
				this.end();
				return;
			}
			//Send NEW_IN(send a new in to the client)
			MessageNewIn in = new MessageNewIn(newIn);
			message.NodeConductor.Message.sendMessage(in, dos);
			
			//Receive NEW_OUT or BYE
			resp = message.NodeConductor.Message.parseMessage(dis);
			
			if(resp == null) {
				System.out.println("FATAL ERROR: Message not recognised. Probably someone it's trying to shoot me down");
				System.out.println("Disconnecting...");
				this.end();
				return;
			}
			
			switch (resp.getOpCode()) {
			case message.NodeConductor.Message.OP_NEW_OUT:
				//Give to the server the new out given by the node just if solution was not found
				if(!this.server.solutionFound())this.server.newOut(((MessageNewOut) resp).getOut(), newIn, this.socket.getInetAddress().getHostAddress());
				System.out.println("New Output [" + ((MessageNewOut) resp).getOut() + "]");
				break;
			case message.NodeConductor.Message.OP_NEW_OUT_BYE:
				this.end = true;
				if(!this.server.solutionFound())this.server.newOut(((MessageNewOutBye) resp).getOut(), newIn, this.socket.getInetAddress().getHostAddress());
				System.out.println("New Output [" + ((MessageNewOutBye) resp).getOut() + "]");
				this.server.nodesDatabase.nodeDesconected(this.socket.getInetAddress().getHostAddress());
				System.out.println("Node asks to finish the conection");
				break;
			case message.NodeConductor.Message.OP_BYE:
				this.end = true;
				this.server.nodesDatabase.nodeDesconected(this.socket.getInetAddress().getHostAddress());
				System.out.println("Node asks to finish the conection");
				break;
			default:
				//Closes this thread immediately
				System.out.println("FATAL ERROR: Message not recognised. Probably someone it's trying to shoot me down");
				this.end();
				return;
			}
			
		}
		
		MessageBye bye = new MessageBye();
		message.NodeConductor.Message.sendMessage(bye, dos);
		this.end();
	}

	private void closeSocket() {
		try {
			this.socket.close();
		} catch (IOException e) {
			System.out.println("ERROR: Failed to close Socket with " + socket.getInetAddress().getHostAddress());
		}
	}
	
}
