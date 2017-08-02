package conductor;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Folder {
	/**
	 * Looks for file TEXT_FILE and reads its fields
	 * Supported mandatory fields: problem, solver, name
	 * Supported optional fields: problem_args, solver_args
	 */
	private String problem;
	private String solver;
	private String name;
	private String solverArgs;
	private String problemArgs;
	private String folderName;
	
	Pattern problemPattern = Pattern.compile("problem\\s*=\\s*([^\\n\\t\\r|]*)\\s*");
	Pattern solverPattern = Pattern.compile("solver\\s*=\\s*([^\\n\\t\\r|]*)\\s*");
	Pattern namePattern = Pattern.compile("name\\s*=\\s*([^\\n\\t\\r|]*)\\s*");
	Pattern generalPattern = Pattern.compile("(problem|solver|name|problem_args|solver_args)\\s*=\\s*([^\\n\\t\\r|]*)\\s*");
	Matcher validator;
	
	private Scanner f;
	private final String TEXT_FILE = "dcnp.txt";
	private boolean folderIsCorrect;
	
	public Folder(String folderName) {
		this.folderIsCorrect = false;
		this.folderName = folderName;
		this.problem = null;
		this.solver = null;
		this.name = null;
		this.solverArgs = null;
		this.problemArgs = null;
		
		String line = null;
		int nLine = 0;
		boolean matches = true;
		
		if(this.folderName == null) {
			System.out.println("ERROR: Folder argument is empty");
			return;
		}
		if(this.folderName.charAt(this.folderName.length()-1) == '/')this.folderName =this. folderName.substring(0, this.folderName.length()-1);
		try {
			f = new Scanner(new File(this.folderName + "/" + TEXT_FILE));
			if(!f.hasNextLine()){
				System.out.println("ERROR: File " + this.folderName + "/" + TEXT_FILE + " is empty");
				return;
			}
			System.out.println("Reading file '" + this.folderName + "/" + TEXT_FILE + "'");
			while(f.hasNextLine() && matches){ //Read line by line
				nLine++;
				line = f.nextLine();
				validator = generalPattern.matcher(line);
				if(!validator.matches()){ //Whatever it is, it won't be a valid line
					System.out.println("ERROR: Text at line " + nLine + " is not valid: '" + line + "'");
					matches = false;
				}
				else { //It's a valid field, just figure out which
					switch (validator.group(1)) {
					case "problem":
						if(this.problem == null){
							this.problem = validator.group(2);
						}
						else {
							System.out.println("ERROR: Problem field specified more than once");
							matches = false;
						}
						break;
					case "solver":
						if(this.solver == null){
							this.solver = validator.group(2);
						}
						else {
							System.out.println("ERROR: Solver field specified more than once");
							matches = false;
						}
						break;
					case "name":
						if(this.name == null){
							this.name = validator.group(2);
						}
						else {
							System.out.println("ERROR: Problem field specified more than once");
							matches = false;
						}
						break;
					case "problem_args":
						if(this.problemArgs == null){
							this.problemArgs = validator.group(2);
						}
						else {
							System.out.println("ERROR: Problem args field specified more than once");
							matches = false;
						}
						break;
					case "solver_args":
						if(this.solverArgs == null){
							this.solverArgs = validator.group(2);
						}
						else {
							System.out.println("ERROR: Solver args field specified more than once");
							matches = false;
						}
						break;
					default: //Should never happen
						System.out.println("Nice bug :)");
						break;
					}
				}
			}
			
		} catch (FileNotFoundException e) {
			System.out.println("ERROR: Either file '" + this.folderName + "/" + TEXT_FILE + "' was not found or it can not be acessible(permission denied)");
			return;
		}
		//It's not necessary to check the problem file size, because MessageProblemModule can transport variable size files
		if (problem != null && solver != null && name != null){
			if(matches) {
				this.folderIsCorrect = true; //Everything is good only if every field was filled up
				System.out.println(TEXT_FILE + " file was verified successfully");
			}
			else System.out.println("Some fields were not recognised");
		}
		else System.out.println("ERROR: There are some missing fields");
	}
	
	public boolean folderIsCorrect() {
		return this.folderIsCorrect;
	}
	
	public String getProblem() {
		return this.problem;
	}
	
	public String getSolver() {
		return this.solver;
	}
	
	public String getProblemPath() {
		return (this.folderName + "/" + this.problem);
	}
	
	public String getSolverPath() {
		return (this.folderName + "/" + this.solver);
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getProblemArgs() {
		return this.problemArgs;
	}
	
	public String getSolverArgs() {
		return this.solverArgs;
	}
}

