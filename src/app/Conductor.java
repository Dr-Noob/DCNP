package app;

import conductor.*;

public class Conductor {
	
	public static void main(String[] args) {
		
		ConductorArgumentsParser parser = new ConductorArgumentsParser(args);
		if(!parser.parseArgs())return;
		
		Folder f = new Folder(parser.getFolderPath());
		if(!f.folderIsCorrect())return;
		
		ConductorProblem conductorProblem = new ConductorProblem(f.getProblemPath(), f.getProblemArgs());
		if(!conductorProblem.problemIsCorrect())return;
		conductorProblem.start();
		
		ConductorServer server = new ConductorServer(f,parser);
		if(server.shouldQuit()) {
			conductorProblem.end();
			return;
		}
		server.start();
		
		ConductorShell shell = new ConductorShell(server,f);
		server.setProblem(conductorProblem);
		
		System.out.println("Conductor started");
		
		do{
			shell.readCommand();
		}while(!server.shouldQuit());
		
	}

}
