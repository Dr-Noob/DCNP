package dcnp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import message.NodeSolver.MessageNewIn;
import message.NodeSolver.MessageNextOut;

public abstract class Solver extends Thread {

	private DataInputStream dis;
	private DataOutputStream des;
	private DataOutputStream dos;
	private boolean healthy;
	
	public Solver() {
		this.dis = new DataInputStream(System.in);
		this.des = new DataOutputStream(System.err);
		this.dos = new DataOutputStream(System.out);
		this.healthy = true;
	}
	
	public final void run() {
		while(this.healthy) {
			//Receive NEW_IN
			MessageNewIn in = new MessageNewIn(dis);
			
			//Send NEXT_OUT
			MessageNextOut out = new MessageNextOut(this.getOut(in.getIn()));
			message.NodeSolver.Message.sendMessage(out, dos);
		}
	}
	
	/**
	 * Prints a message in the conductor shell
	 * @param msg Message to be printed
	 * @return true if message was successfully sent to be printed, false if not
	 */
	protected final void print(String msg) {
		byte[] b = msg.getBytes();
		try {
			this.des.writeInt(msg.length());
			this.des.write(b);
			this.des.flush();
		} catch (IOException e) {
			this.healthy = false;
		}
	}
	
	/**
	 * Exits solver necessarily. Must be called when a error occurs
	 */
	protected final void error() {
		this.healthy = false;
	}
	
	protected abstract String getOut(String input);
}
