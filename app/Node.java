package app;

import node.*;
import utils.Statistics;

public class Node {
	
	public static void main(String[] args) {
		
		NodeArgumentsParser parser = new NodeArgumentsParser(args);
		if(!parser.parseArgs())return;
		
		Statistics stats = new Statistics();
		
		NodeShell shell = new NodeShell(stats);
		
		NodeCompute compute = new NodeCompute(parser.getPathModule(),shell);
		if(!compute.validPath())return;
		
		NodeClient client = new NodeClient(parser,stats,compute,shell);
		if(!client.conectionIsHealthy())return;
		client.start();
		
		shell.printError("Node started");
		
		do {
			shell.readCommand();
		} while (client.conectionIsHealthy() && !shell.shouldEnd());

	}

}
