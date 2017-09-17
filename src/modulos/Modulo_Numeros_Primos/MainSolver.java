package primos;

public class MainSolver {
	
	public static void main(String[] args) {

		PrimeSolver s = new PrimeSolver();
		s.start();
		
		try {
			s.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
