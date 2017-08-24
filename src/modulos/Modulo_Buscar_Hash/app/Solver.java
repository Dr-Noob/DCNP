package app;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import solver.*;

public class Solver {

	public static void main(String[] args) {
		
		if(args.length != 1) {
    		System.out.println("Please, specify the hash");
    		return;
    	}
		Pattern hashpattern = Pattern.compile("^[a-fA-F0-9]{32}$");
		String uhash = args[0];
		Matcher validator = hashpattern.matcher(uhash);
		if (!validator.matches()){
			System.out.println("ERROR: " + uhash + " is not a valid MD5 hash");
			return;
		}
		uhash = uhash.toUpperCase();
		
		MD5 solver = new MD5(uhash);
		solver.start();
		
		try {
			solver.join();
		} catch (InterruptedException e) {
			
		}

	}

}
