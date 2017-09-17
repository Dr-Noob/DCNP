package primos;

import dcnp.Solver;

public class PrimeSolver extends Solver {

	private int number;
	private int cores = 1;
	private ThreadCalc[] threads;
	
	@Override
	protected String getOut(String arg0) {
		number = Integer.parseInt(arg0);
		cores = Runtime.getRuntime().availableProcessors();
		//print("cores = " + cores);
		threads = new ThreadCalc[cores];
		
		int max = (int)Math.sqrt(number);
		int i = 2;
		int range = (max - 1) / cores;
		int a = (max - 1) % cores;
		int b = cores - a;
		
		//print("max = " + max);
		//print("aaaaaaaaaaaaaaaaaaaaaaaaaaa");
		for ( int j = 0 ; j < a ; j++ ) {
			//print(j + " " + i + " " + (i + range + 1));
			threads[j] = new ThreadCalc(this, i, i + range + 1);
			i = i + range + 1;
		}
		
		//print("bbbbbbbbbbbbbbbbbbbbbbbbbbb");
		for ( int j = 0 ; j < b ; j++ ) {
			//print((a+j) + " " + i + " " + (i + range));
			threads[j+a] = new ThreadCalc(this, i, i + range);
			i = i + range;
		}
		
		for (int j = 0 ; j < cores ; j++ ) {
			threads[j].start();
		}
		
		for (int j = 0 ; j < cores ; j++ ) {
			try {
				threads[j].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			};
		}
		
		int j = 0;
		while ( j < cores && threads[j].getResult() ) j++; 
		
		if ( j == cores ) return "true";
		return "false";
		
	}
	
	public int getNumber() {
		return number;
	}

	
	
}
