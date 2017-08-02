package app;

import conductor.*;

public class Conductor {
	
	public static void main(String[] args) {
		
		if(args.length != 1){
			System.out.println("ERROR: Conductor needs one argument(folder):");
			return;
		}
		
		Folder f = new Folder(args[0]);
		if(!f.folderIsCorrect())return;
		
		ConductorProblem conductorProblem = new ConductorProblem(f.getProblemPath(), f.getProblemArgs());
		if(!conductorProblem.problemIsCorrect())return;
		conductorProblem.start();
		
		ConductorServer server = new ConductorServer(f);
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
