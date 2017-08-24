package conductor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.Semaphore;

import message.ConductorProblem.*;

public class ConductorProblem extends Thread {
	
	private Process problem;
	private boolean problemIsCorrect;
	private boolean solutionFound;
	private DataInputStream dis;
	private DataInputStream des;
	private DataOutputStream dos;
	private Semaphore mutex;
	private File problemFile;
	
	public ConductorProblem(String problemPath, String problemArgs) {
		this.problemIsCorrect = true;
		this.solutionFound = false;
		this.mutex = new Semaphore(1);
		this.problemFile = new File(problemPath);
		
		if(!this.problemFile.exists()) {
			System.out.println("ERROR: Problem file not found at " + problemPath);
			this.problemIsCorrect = false;
		}
		else {
			try {
				this.problem = Runtime.getRuntime().exec("/usr/bin/java -jar " + problemPath + " " + problemArgs);
				this.dis = new DataInputStream(this.problem.getInputStream());
				this.des = new DataInputStream(this.problem.getErrorStream());
				this.dos = new DataOutputStream(this.problem.getOutputStream());
			} catch (IOException e) {
				System.out.println("ERROR: I/O exception when executing " + problemPath);
				this.problemIsCorrect = false;
			}
		}
	}
	
	public void run() {
		int len = 0;
		byte[] b;
		while(this.problemIsCorrect && !this.solutionFound) {
			try {
				len = des.readInt();
			} catch (IOException e) {
				//Silent catch; exception will be thrown when closing des
				return;
			}
			b = new byte[len];
			try {
				des.readFully(b);
			} catch (IOException e) {
				return;
			}
			System.out.print("PROBLEM SAYS; " + new String(b));
		}
	}
	
	public String getNextIn() {
		try {
			this.mutex.acquire();
		} catch (InterruptedException e) {
			System.out.println("FATAL ERROR: Semaphore interrupted");
			return null;
		}		
		
		MessageNextInReq req = new MessageNextInReq();
		message.ConductorProblem.Message.sendMessage(req, dos);
		
		Message resp = message.ConductorProblem.Message.parseMessage(dis);
		if(resp == null) {
			this.mutex.release();
			return null;
		}
		
		switch (resp.getOpCode()) {
		case Message.OP_NEXT_IN:
			this.mutex.release();
			return ((MessageNextIn) resp).getIn();
			
		case Message.OP_SOLUTION:			
			this.solutionFound = true;
			return ((MessageSolution) resp).getSolution();
			
		default: 
			this.mutex.release();
			return null;
		}
		
	}
	
	public void sendNewOut(String out, String in) {
		try {
			this.mutex.acquire();
		} catch (InterruptedException e) {
			System.out.println("FATAL ERROR: Semaphore interrupted");
			return;
		}	
		MessageNewOut msg = new MessageNewOut(out, in);
		message.ConductorProblem.Message.sendMessage(msg, dos);
		this.mutex.release();
	}
	
	public boolean solutionFound() {
		return this.solutionFound;
	}
	
	public boolean problemIsCorrect() {
		return this.problemIsCorrect;
	}
	
	public String getInvalidInput() {
		return Message.INVALID_INPUT();
	}
	
	public void end() {
		try {
			this.des.close();
			this.dis.close();
			this.dos.close();
		} catch (IOException e) {
			System.out.println("FATAL ERROR: Failed to close problem data streams");
		}
	}

}
