package app;

import solver.*;

public class Solver {

	public static void main(String[] args) {
		
		if(args.length != 1) {
    		System.out.println("Please, specify the number N");
    		return;
    	}
    	int N = 0;
    	try {
    		 N = Integer.parseInt(args[0]);
    	} catch(NumberFormatException e) {
    		System.out.println(args[0] + " is not a number");
    		return;
    	}
		
		if(N == 1) {
			System.out.println("1");
			return;
		}
		
		Queens solver = new Queens(N);
		solver.start();
		
		try {
			solver.join();
		} catch (InterruptedException e) {
			
		}

	}

}
