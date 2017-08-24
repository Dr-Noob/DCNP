package solver;

import dcnp.Solver;

public class MD5 extends Solver {
	
	//private int ncores;
	private String uhash;
	private HThread threads[];
		
	public MD5(String uhash) {
		//this.ncores = Runtime.getRuntime().availableProcessors();
		this.uhash = uhash;
	}
	
	@Override
	protected String getOut(String input) {

		char lc = input.charAt(0);
		char rc = input.charAt(2);
		int len = Integer.parseInt(input.substring(4, input.length()));
		String password = null;
		//this.print("lc=> '" + lc + "': rc=> '" + rc + "': len= " + len);
		/*
		int frac = (rc - lc)/this.ncores;
		if(frac == 0)frac = 1;
		this.print("frac= " + frac);

		for(int i=0;i<this.ncores-1;i++) {
			this.print(":::lc=> '" + (char)(lc+i*frac) + "': rc=> '" + (char)(lc+i*frac+1));
			HThread t = new HThread((char)(lc+i*frac),(char)(lc+i*frac+1),len,this.uhash);
			this.threads[i] = t;
			t.start();
		}
		this.print(":::lc=> '" + (char)(lc+this.ncores-1*frac) + "': rc=> '" + rc);
		HThread t = new HThread((char)(lc+this.ncores-1*frac),rc,len,this.uhash);
		this.threads[this.ncores-1] = t;
		t.start();
		
		for(int i=0;i<this.ncores;i++) {
			try {
				this.threads[i].join();
			} catch (InterruptedException e) {
				this.print("Failed to join threads");
			}
			if(this.threads[i].hashFound()) password = this.threads[i].getPassword();
		}
		*/
		int dif = rc-lc;
		this.threads = new HThread[dif];
		for(int i=0;i<dif;i++) {
			//this.print(":::lc=> '" + (char)(lc+i) + "': rc=> '" + (char)(lc+i+1));
			HThread t = new HThread((char)(lc+i),(char)(lc+i+1),len,this.uhash);
			this.threads[i] = t;
			t.start();
		}
		for(int i=0;i<dif;i++) {
			try {
				this.threads[i].join();
			} catch (InterruptedException e) {
				this.print("Failed to join threads");
			}
			if(this.threads[i].hashFound()) password = this.threads[i].getPassword();
		}
		if(password != null)return password;
		else return "NO";
	}
	
}
