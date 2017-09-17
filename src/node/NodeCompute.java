package node;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.Semaphore;

import message.NodeSolver.*;

public class NodeCompute extends Thread {

	private File folder;
	private File moduleFile;
	private String fileName;
	private String args;
	private Semaphore quit;
	private boolean shouldQuit;
	private DataInputStream dis;
	private DataInputStream des;
	private DataOutputStream dos;
	private Process problem;
	private NodeShell shell;

	
	public NodeCompute(String dirPath,NodeShell shell) {
		this.quit = new Semaphore(0);
		this.shell = shell;
		this.shouldQuit = false;
		this.folder = new File(dirPath);
		if(!folder.exists())this.shell.print("ERROR: " + dirPath + " is not a valid path");
		else if(!folder.isDirectory())this.shell.print("ERROR: " + dirPath + " is not a directory");
	}
	
	public void run() {
		this.moduleFile = new File(folder.getAbsolutePath() + "/" + this.fileName);
		
		if(!this.moduleFile.exists()) {
			this.shell.print("ERROR: Problem file not found at " + folder.getAbsolutePath() + "/" + this.fileName);
			this.shouldQuit = true;
			this.quit.release();
			return;
		}
		try {
			this.problem = Runtime.getRuntime().exec("/usr/bin/java -jar " + folder.getAbsolutePath() + "/" + this.fileName + " " + this.args);
			this.dis = new DataInputStream(this.problem.getInputStream());
			this.des = new DataInputStream(this.problem.getErrorStream());
			this.dos = new DataOutputStream(this.problem.getOutputStream());
		} catch (IOException e) {
			this.shell.print("ERROR: I/O exception when executing " + folder.getAbsolutePath() + "/" + this.fileName);
			this.shouldQuit = true;
			this.quit.release();
			return;
		}

		//Unblock semaphore to allow shouldQuit method return the result
		this.quit.release();
		
		//Listen to syserr of module
		int len = 0;
		byte[] b;
		while(!this.shouldQuit) {
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
			this.shell.print("NODE SAYS: " + new String(b));
		}
	}
	
	public void end() {
		try {
			this.des.close();
			this.dis.close();
			this.dos.close();
		} catch (IOException e) {
			this.shell.print("FATAL ERROR: Failed to close problem data streams");
		}
	}
	
	public boolean shouldQuit() {
		try {
			this.quit.acquire();
		} catch (InterruptedException e) {
			this.shell.print("ERROR: Semaphore interrupted");
			return true;
		}
		return this.shouldQuit;
	}
	
	public void setNextIn(String in) {
		//Send NEW_IN
		MessageNewIn msg = new MessageNewIn(in);
		message.NodeSolver.Message.sendMessage(msg, dos);
	}
	
	/**
	 * @return The string of the next out, or null if it was not possible to get it
	 */
	public String getNextOut() {
		MessageNextOut out = new MessageNextOut(dis);
		if(!out.valid())return null;
		else return out.getOut();
	}
	
	public boolean validPath() {
		return folder.exists() && folder.isDirectory();
	}
	
	public File getFolder() {
		return this.folder;
	}
	
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public void setArgs(String args) {
		this.args = args;
	}

}
