package primos;

import dcnp.Problem;

public class PrimeProblem extends Problem {

	private Integer input = 1;
	
	@Override
	public boolean isSolution(String arg0) {
		// obtener primos hasta que se cierre el programa
		return false;
	}

	@Override
	protected void newOut(String out, String in) {
		if (out.equals("true")) print("\n\n--------" + in + " es primo" + "--------\n\n");
	}

	@Override
	protected String nextIn() {
		input++;
		return input.toString();
	}

	@Override
	protected String solutionMessage() {
		return "Bye!";
	}

}
