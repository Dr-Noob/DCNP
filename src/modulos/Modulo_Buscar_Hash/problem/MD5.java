package problem;

import dcnp.Problem;

public class MD5 extends Problem {
	
	private String crack;
	private String password;
	private int len;
	private int timesRequested;
	private boolean hashFound;
	private final int INTERVAL = 7;
		
	public MD5() {
		this.crack = new String();
		this.hashFound = false;
		this.len = 1;
		this.timesRequested = 0;
	}

	@Override
	public boolean isSolution(String in) {
		if(!in.equals("NO")) {
			this.password = in;
			this.hashFound = true;
			return true;
		}
		return false;
	}

	@Override
	protected void newOut(String newOut, String in) {
		
	}

	@Override
	protected String nextIn() {
		if(hashFound) return INVALID_INPUT();
		else if(this.timesRequested % INTERVAL == 0) {
			this.crack = "0-9-";
			this.crack += String.valueOf(this.len + this.timesRequested / INTERVAL);
		}
		else if(this.timesRequested % INTERVAL == 1) {
			this.crack = "A-I-";
			this.crack += String.valueOf(this.len + this.timesRequested / INTERVAL);
		}
		else if(this.timesRequested % INTERVAL == 2) {
			this.crack = "J-R-";
			this.crack += String.valueOf(this.len + this.timesRequested / INTERVAL);
		}
		else if(this.timesRequested % INTERVAL == 3) {
			this.crack = "S-Z-";
			this.crack += String.valueOf(this.len + this.timesRequested / INTERVAL);
		}
		else if(this.timesRequested % INTERVAL == 4) {
			this.crack = "a-i-";
			this.crack += String.valueOf(this.len + this.timesRequested / INTERVAL);
		}
		else if(this.timesRequested % INTERVAL == 5) {
			this.crack = "j-r-";
			this.crack += String.valueOf(this.len + this.timesRequested / INTERVAL);
		}
		else {
			this.crack = "s-z-";
			this.crack += String.valueOf(this.len + this.timesRequested / INTERVAL);
		}
		this.timesRequested++;
		return this.crack;
	}

	@Override
	protected String solutionMessage() {
		return "Password was '" + this.password + "'";
	} 
	
}
