package solver;

import dcnp.Solver;

public class Queens extends Solver {
	
	private int ncores;
	private int N;
	private QThread threads[];
		
	public Queens(int N) {
		this.ncores = Runtime.getRuntime().availableProcessors();
		this.threads = new QThread[ncores];
		this.N = N;
	}
	
	@Override
	protected String getOut(String input) {

		int seed = 0;
		int root = Integer.valueOf(input);
		int a = 0;
		long solutions = 0;
		
		while(seed < N) {
			a = 0;
			while(a < ncores && seed < N) {
				QThread h = new QThread(seed,root,N,this);
				threads[a] = h;
				h.start();
				a++;
				seed++;
			}
			
			try {
				for(int i=0; i < a; i++)threads[i].join();
			} catch (InterruptedException e) {
			}
			
			for(int i=0; i < a; i++)solutions += threads[i].getSolutions();
		}
		
		return String.valueOf(solutions);	
		
	}
	
	synchronized void printmsg(String msg) {
		this.print(msg);
	}

}
