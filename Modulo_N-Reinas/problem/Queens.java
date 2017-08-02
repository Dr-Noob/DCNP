package problem;

import dcnp.Problem;

public class Queens extends Problem {
	//https://oeis.org/A000170/list
	private int N;
	private int insComputed;
	private int outputsReceived;
	private long solutions;
	private long lastOutput;
		
	public Queens(int N) {
		
		this.N = N;
		this.solutions = 0;
		this.insComputed = 0;
		this.outputsReceived = 0;
		this.lastOutput = 0;
	}

	@Override
	public boolean isSolution(String arg0) {
		if(this.N % 2 == 0)return this.outputsReceived >= this.N/2;
		else return this.outputsReceived >= (this.N/2)+1;
	}

	@Override
	protected void newOut(String newOut, String in) {
		this.outputsReceived++;
		try {
			if(this.N % 2 == 0 || Integer.parseInt(in) != this.N/2)this.solutions += Long.parseLong(newOut);
			else this.lastOutput = Long.parseLong(newOut);
		} catch(NumberFormatException e) {
			this.print("ERROR: Failed to parse " + newOut + " to long");
		}
	}

	@Override
	protected String nextIn() {
		//if((this.N % 2 == 0 && (this.insComputed)*2 < this.N) || (this.N % 2 == 1 && ((this.insComputed)*2)+1 < this.N)) {
		if((this.insComputed)*2 < this.N) {
			this.insComputed++;
			return String.valueOf(this.insComputed-1);
		}
		return this.INVALID_INPUT();
	}

	@Override
	protected String solutionMessage() {
		if(this.N % 2 == 0)return "[N=" + this.N + "][Solutions=" + this.solutions*2 + "]";
		else return "[N=" + this.N + "][Solutions=" + (this.solutions*2 + this.lastOutput) + "]"; 
	} 
	
}
