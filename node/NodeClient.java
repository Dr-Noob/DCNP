package node;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import message.NodeConductor.*;
import utils.Statistics;

public class NodeClient extends Thread {

	private InetSocketAddress conductorAdress;
	private NodeShell shell;
	private Socket nodeSocket;
	private DataOutputStream dos;
	private DataInputStream dis;
	private Statistics stats;
	private NodeCompute compute;
	private boolean conectionIsHealthy;
	private boolean end;
	private boolean computeStarted;
	private State currentState;
	private final int CONNECTION_TIMEOUT = 2000;
	
	private enum State {
		WAITING, WORKING, TALKING, WAITING_TO_EXIT
	}
	
	public NodeClient(NodeArgumentsParser parser, Statistics stats, NodeCompute compute, NodeShell shell) {
		this.stats = stats;
		this.shell = shell;
		this.shell.setClient(this);
		this.compute = compute;
		this.conductorAdress = new InetSocketAddress(parser.getIp(), parser.getPort());
		this.conectionIsHealthy = false;
		this.end = false;
		this.computeStarted = false;
		this.currentState = State.TALKING;
		this.nodeSocket = new Socket();
		try {
			this.nodeSocket.connect(this.conductorAdress, CONNECTION_TIMEOUT);
			//this.nodeSocket = new Socket(InetAddress.getByName(conductorAdress.getHostName()),conductorAdress.getPort());
		} catch (UnknownHostException e) {
			shell.printError("ERROR: Invalid IP adress " + parser.getIp());
			return;
		} catch (IOException e) {
			shell.printError("ERROR: Conductor can not be found at " + parser.getIp() + ":" + parser.getPort());
			return;
		}
		shell.print("Conection established with conductor at " + parser.getIp() + ":" + parser.getPort());
		this.conectionIsHealthy = true;
		try {
			this.dos = new DataOutputStream(this.nodeSocket.getOutputStream());
			this.dis = new DataInputStream(this.nodeSocket.getInputStream());
		} catch (IOException e) {
			this.conectionIsHealthy = false;
			shell.printError("ERROR: Failed to open DataStream");
		}
	}
	
	public String currentState() {
		switch (this.currentState) {
			case WAITING:
				return "Waiting the conductor 'START' signal";
			case WORKING:
				return "Computing";
			case TALKING:
				return "Conversating with Conductor";
			case WAITING_TO_EXIT:
				return "Waiting the conductor 'START' signal to exit properly";
		}
		return null;
	}
	
	public boolean conectionIsHealthy() {
		return this.conectionIsHealthy;
	}
	
	/**
	 * Ends this node, the shell and NodeCompute
	 */
	private void end() {
		shell.printError("Exiting node...");
		try {
			this.nodeSocket.close();
			this.shell.closeShell();
			//Just end it if it has started, because if it didn't, closing input stream will fail
			if(this.computeStarted)this.compute.end();
		} catch (IOException e) {
			shell.printError("ERROR: Failed to close Socket");
		} finally {
			this.end = true;
		}	
	}
	
	public void endClient() {
		this.end = true;
		if(this.currentState == State.WAITING) {
			shell.printError("WARNING: Can not exit at the moment. Conductor must send 'START' before I exit");
		}
	}
	
	public void run () {
		Message resp = null;
		//Send ADD_NODE
		MessageAddNode addNode = new MessageAddNode(stats.getCores(),stats.getArch(),stats.getOs());
		if(!message.NodeConductor.Message.sendMessage(addNode, dos)) {
			this.end();
			return;
		}
		
		//Receive PROBLEM_MODULE
		MessageProblemModule problemModule = new MessageProblemModule(dis);
		if(!message.NodeConductor.Message.printErrorIfNotValid(problemModule)) {
			this.end();
			return;
		}
		
		if(!problemModule.writeExecutable(compute.getFolder(),this.shell)) {
			shell.printError("ERROR: Failed to write executable module, exiting");
			this.end();
			return;
		}
		this.compute.setFileName(problemModule.getModuleName());
		this.compute.setArgs(problemModule.getModuleArgs());
		this.compute.start();
		this.computeStarted = true;
		//Check compute is ok to start computing
		if(this.compute.shouldQuit()) {
			end();
			return;
		}
		
		//Receive HAS_STARTED
		MessageHasStarted started = new MessageHasStarted(dis);
		if(!message.NodeConductor.Message.printErrorIfNotValid(started)) {
			end();
			return;
		}

		//If computing didn't started, receive START
		if(!started.hasStarted()) {
			this.currentState = State.WAITING;
			resp = Message.parseMessage(dis);
			
			if(resp == null) {
				shell.printError("Disconnecting...");
				this.end();
				return;
			}
			
			switch (resp.getOpCode()) {
			
			case message.NodeConductor.Message.OP_BYE:
				shell.print("Conductor asks to finish the conection");
				this.end();
				return;
			
			case message.NodeConductor.Message.OP_START:
				break;
				
			default:
				shell.printError("FATAL ERROR: Message not recognised. Probably someone it's trying to shoot me down\n Exiting because of security measures");
				this.end();
				return;
			}
		}
		
		shell.print("Computing started");
		this.currentState = State.WORKING;
		this.stats.startedComputing();
		
		
		do {
			resp = Message.parseMessage(dis);
			
			if(resp == null) {
				shell.printError("Disconnecting...");
				this.end();
				return;
			}
			
			switch (resp.getOpCode()) {
			case message.NodeConductor.Message.OP_BYE:
				shell.print("Conductor asks to finish the conection");
				this.end();
				return;
				
			case message.NodeConductor.Message.OP_NEW_IN:
				this.shell.print("Computing input [" + ((MessageNewIn) resp).getIn() + "]");
				this.compute.setNextIn(((MessageNewIn) resp).getIn());
				break;	
				
			default:
				shell.printError("FATAL ERROR: Message not recognised. Probably someone it's trying to shoot me down\n Exiting because of security measures");
				this.end();
				return;
			}
			
			String out = this.compute.getNextOut();
			if(out == null) {
				shell.printError("Failed to compute the next out");
				end();
				return;
			}
			else if(this.end) { //Send NEW_OUT_BYE
				MessageNewOutBye end = new MessageNewOutBye(out);
				this.conectionIsHealthy = message.NodeConductor.Message.sendMessage(end, dos);
				end();
				return;
			} 
			//Send NEW_OUT
			else { 
				MessageNewOut newout = new MessageNewOut(out);
				this.conectionIsHealthy = message.NodeConductor.Message.sendMessage(newout,dos);
			}
		} while(!this.end && this.conectionIsHealthy);
		
	}
}
