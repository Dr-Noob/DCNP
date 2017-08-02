package solver;

public class QThread extends Thread {
	
	public void printQueens(int[] a) {
		StringBuffer buf = new StringBuffer();
        int n = a.length;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (a[i] == j) { 
                	buf.append('Q');
                	buf.append(' ');
                }
                else {
                	buf.append('*');
                	buf.append(' ');
                }
            }
            buf.append('\n');
        }  
        buf.append('\n');
        q.printmsg(buf.toString());
    }
	
	private void generar() {
		s[nivel]++;
	}
	
	private boolean solucion() {
		if(nivel != _N)return false;
		for (int i = 0; i < _N; i++) {
            if (s[i] == s[_N])             return false;
            if ((s[i] - s[_N]) == (_N - i)) return false;
            if ((s[_N] - s[i]) == (_N - i)) return false;
        }
		return ((s[0] != s[1]) && (s[0] - s[1]) != 1 && (s[1] - s[0]) != 1);
	}
	
	private boolean criterio() {
		if(nivel == _N)return false;
		for (int i = 0; i < nivel; i++) {
            if (s[i] == s[nivel])             return false; 
            if ((s[i] - s[nivel]) == (nivel - i)) return false;
            if ((s[nivel] - s[i]) == (nivel - i)) return false;
        }
		return ((s[0] != s[1]) && (s[0] - s[1]) != 1 && (s[1] - s[0]) != 1);
	}
	
	private boolean masHermanos() {
		return s[nivel] < _N;
	}
	
	private void retroceder() {
		s[nivel] = -1;
		nivel--;
	}
	
	private int N;
	private int _N;
	private int nivel;
	private int[] s;
	private long soluciones;
	private Queens q;
	
	public QThread(int semilla, int root, int N, Queens q) {
		this.N = N;
		this._N = this.N-1;
    	this.s = new int[N];
    	this.nivel = 2;
		for(int i=0;i<this.N;i++)this.s[i] = -1;
		this.s[0] = root;
		this.s[1] = semilla;
		this.q = q;
	}
	//-1
	//0
	//1
	//2
	//3
	//4
	//5
	//6
	//7
	//8
	
	public void run() {
		do {
			generar();
			if(solucion())this.soluciones++;
			if(criterio())nivel++;
			else {
				while(nivel > 1 && !masHermanos())retroceder();
			}
		} while(nivel > 1);
	}
	
	public long getSolutions() {
		return this.soluciones;
	}
	
}
