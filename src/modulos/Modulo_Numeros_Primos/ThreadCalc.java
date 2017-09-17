package primos;

public class ThreadCalc extends Thread {
	
	private PrimeSolver ps;
	private int number;
	private int upperBound;
	private int lowerBound;
	private boolean result = false;

	public ThreadCalc (PrimeSolver ps, int lowerBound, int upperBound) {
		this.ps = ps;
		this.number = this.ps.getNumber();
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
	}
	
	public void run() {
		
		while (lowerBound < upperBound && (number % lowerBound) != 0) lowerBound++;
		
		if (lowerBound == upperBound)
			result = true;
		
	}
	
	public boolean getResult() {
		return result;
	}

}
