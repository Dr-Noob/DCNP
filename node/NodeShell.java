package node;

import utils.Shell;
import utils.Statistics;

public class NodeShell extends Shell {
	/**
	 * Supported commands: shell_commands
	 */
		
	public static final byte COM_EXIT_NOW = 4;
	
	private NodeClient client;
	private Statistics stats;
	
	public NodeShell(Statistics stats) {
		super();
		this.stats = stats;
	}
	
	public void setClient(NodeClient client) {
		this.client = client;
	}
	
	@Override
	protected void printPrompt() {
		System.out.print("> ");
	}
	
	@Override
	public synchronized void print(String msg) {
		System.out.println("\r" + msg);
		this.printPrompt();
	}
	
	@Override
	public synchronized void printError(String error) {
		/*
		System.out.println();
		System.out.println(error);
		*/
		System.out.println("\r" + error);
	}
	
	@Override
	protected void printHelp() {
		System.out.println("Showing help");
		System.out.println("stats:");
		System.out.println("\t Show information about computation and node");
		System.out.println("exit:");
		System.out.println("\t Exit node and finish the computation when possible(may require some time)");
		System.out.println("help:");
		System.out.println("\t Shows this help");
	}
	
	@Override
	protected void printStats() {
		System.out.println("Statistics:");
		System.out.println("Current state: " + client.currentState());
		System.out.println("Cores: " + stats.getCores());
		System.out.println("Operating System: " + stats.getOs());
		System.out.println("CPU Architecture: " + stats.getArch());
		System.out.println("Inputs computed: " + stats.getInsComputed());
		System.out.println("Time computing: " + stats.getSecondsComputing() + "s");
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
				client.endClient();
				this.closeShell(); 
				break;
			case COM_HELP:
				printHelp();
				break;
			case COM_STATS:
				printStats();
				break;
			case COM_INVALID:
				System.out.println("WARNING: Command " + fullCommand[0] + " is not a valid command");
				System.out.println("Try 'help' command to see a list of valid commands");
		}
	}
}
