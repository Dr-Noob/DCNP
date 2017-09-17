package primos;

public class MainProblem {

	public static void main(String[] args) {

		PrimeProblem p = new PrimeProblem();
		p.start();
		
		try {
			p.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
