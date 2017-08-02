package dcnp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import message.ConductorProblem.Message;
import message.ConductorProblem.MessageNewOut;
import message.ConductorProblem.MessageNextIn;
import message.ConductorProblem.MessageSolution;

public abstract class Problem extends Thread {

	private DataInputStream dis;
	private DataOutputStream des;
	private DataOutputStream dos;
	private boolean solutionFound;
	private boolean healthy;
	
	public Problem() {
		//Uses stdout,stderr and stdin to create streams
		this.dis = new DataInputStream(System.in);
		this.des = new DataOutputStream(System.err);
		this.dos = new DataOutputStream(System.out);
		this.solutionFound = false;
		this.healthy = true;
	}
	
	/**
	 * Prints a message in the conductor shell
	 * @param msg Message to be printed
	 * @return true if message was successfully sent to be printed, false if not
	 */
	public final void print(String msg) {
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
	
	protected final String INVALID_INPUT() {
		return Message.INVALID_INPUT();
	}
	
	public final void run() {
		while(!solutionFound && this.healthy) {
			Message msg = message.ConductorProblem.Message.parseMessage(dis);
			if(msg == null) {
				this.print("ERROR: Message not recognised");
				return;
			}
			
			switch (msg.getOpCode()) {
			case Message.OP_NEXT_IN_REQ:
				MessageNextIn resp = new MessageNextIn(this.nextIn());
				message.ConductorProblem.Message.sendMessage(resp, dos);
				break;
			case Message.OP_NEW_OUT:
				this.newOut(((MessageNewOut) msg).getOut(), ((MessageNewOut) msg).getIn());
				if(this.isSolution(((MessageNewOut) msg).getOut())) {
					MessageSolution sol = new MessageSolution(this.solutionMessage());
					message.ConductorProblem.Message.sendMessage(sol, dos);
					return;
				}
				break;
			default:
				this.print("ERROR: Message not recognised");
				return;
			}
		}
	}
	
	/**
	 * @return A new input to be computed
	 */
	protected abstract String nextIn();
	
	/**
	 * Gets a new output and does with it what is needed
	 * @param out A new output
	 * @param in The input for which the output was computed
	 */
	protected abstract void newOut(String out, String in);
	
	/**
	 * @return The message that will be printed in the conductor when the solution will be found
	 */
	protected abstract String solutionMessage();
	
	/**
	 * Checks if the string is the solution
	 * @param input String which must be checked to know if it's a solution or not
	 * @return true if it's the solution, false if not
	 */
	public abstract boolean isSolution(String input);
	
}
