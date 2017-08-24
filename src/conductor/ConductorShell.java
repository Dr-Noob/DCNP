package conductor;

import utils.Shell;

public class ConductorShell extends Shell {
	
	/**
	 * Supported commands: shell_commands,START
	 */
	
	public static final byte COM_START = 4;
	
	private String problemName;
	private ConductorServer server;
	private Folder f;
	
	public ConductorShell(ConductorServer server, Folder f) {
		this.server = server;
		this.server.setShell(this);
		this.f = f;
		this.problemName = f.getName();
		if(this.problemName.contains(" "))this.problemName = getInitials(this.problemName);
	}
	
	private String getInitials(String name) {
		String tmp = new String();
		for (String s : name.split(" ")) {
			tmp += Character.toUpperCase(s.charAt(0));
		}
		return tmp;
	}
	
	public void refresh() {
		System.out.print("\r");
		this.printPrompt();
	}
	
	@Override
	protected void printPrompt() {
		int nodes = this.server.getNumNodesConnected();
		if(nodes != 1)System.out.print("[" + this.problemName + "//" + nodes + " nodes" + "] ");
		else System.out.print("[" + this.problemName + "//" + nodes + " node" + "] ");
	}
	
	@Override
	public synchronized void print(String msg) {
		System.out.println("\r" + msg);
		this.printPrompt();
	}
	
	@Override
	public synchronized void printError(String error) {
		System.out.println("ERROR: " + error);
	}
	
	@Override
	protected void printHelp() {
		System.out.println("Showing help");
		System.out.println("exit:");
		System.out.println("\t Exit conductor and tell all nodes to exit to");
		System.out.println("help:");
		System.out.println("\t Shows this help");
		System.out.println("stats:");
		System.out.println("\t Shows statistics about the server at the moment");
		System.out.println("start:");
		System.out.println("\t Start computing of the problem");
	}
	
	@Override
	protected void printStats() {
		System.out.println("Statistics:");
		System.out.println("Name: " + this.f.getName()); 
		System.out.println("Nodes active: " + this.server.getNumNodesComputing());
		System.out.println("Total cores: " + this.server.getNumCores());
		System.out.println("Total inputs computed: " + this.server.getInsComputed());
		System.out.println("Time since computing started: " + this.server.getTimeComputing() + "s");
		System.out.println();
		this.server.nodesDatabase.showStatistics();
	}
	
	@Override
	protected byte getCommandCode(String command) {
		switch (command) {
		case "exit":
			return COM_EXIT;
		case "help":
			return COM_HELP;
		case "stats":
			return COM_STATS;		
		case "start":
			return COM_START;		
		default:
			return COM_INVALID;
		}
	}
	
	@Override
	public void readCommand() {
		super.readCommand();
		if(this.fullCommand[0] == null || this.fullCommand[0].isEmpty())return;
		this.commandCode = getCommandCode(fullCommand[0]);
		switch (commandCode) {
			case COM_EXIT:
				server.end();
				break;
			case COM_HELP:
				printHelp();
				break;
			case COM_STATS:
				printStats();
				break;
			case COM_START:
				server.startComputing();
				break;
			case COM_INVALID:
				System.out.println("WARNING: Command " + fullCommand[0] + " is not a valid command");
				System.out.println("Try 'help' command to see a list of valid commands");
		}
	}
	
	public void printSemaphoreExecption() {
		this.print("FATAL ERROR: Semaphore interrupted");
	}

}
