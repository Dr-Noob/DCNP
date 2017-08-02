package app;

import problem.*;

public class Problem {

	public static void main(String[] args) {
		
		MD5 problem = new MD5();
		problem.start();
		
		try {
			problem.join();
		} catch (InterruptedException e) {
			
		}

	}
}
