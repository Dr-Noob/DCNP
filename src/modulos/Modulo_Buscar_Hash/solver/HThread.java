package solver;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HThread extends Thread {
	
	
	private void generar() {
		if(this.password[this.level] == 0x39)this.password[this.level] = 'A';
		else if(this.password[this.level] == 0x5A)this.password[this.level] = 'a';
		else this.password[this.level]++;
		this.hash = md.digest(this.password);
	}
	
	private boolean solucion() {
		return MessageDigest.isEqual(this.origHash, this.hash);
	}
	
	private boolean criterio() {
		return this.password[this.level] <= 'z' && this.level < N-1 && this.password[0] <= (byte) this.rc;
	}
	
	private boolean masHermanos() {
		return this.password[this.level] < 'z' && this.password[0] <= (byte) this.rc;
	}
	
	private void retroceder() {
		this.password[this.level] = '0';
		this.level--;
	}
	
	private byte[] origHash;
	private byte[] hash;
	private byte[] password;
	private char lc; //left char
	private char rc; //right char
	private MessageDigest md;
	private int level;
	private int N;
	private boolean hashFound;
	
	public HThread(char lc, char rc, int N, String uhash) {
		this.origHash = new byte[16];
		for(int i = 0;i<32;i+=2) {
			this.origHash[i/2] = (byte) ((Character.digit(uhash.charAt(i), 16) << 4)
                    					+ Character.digit(uhash.charAt(i+1), 16));
		}
		this.N = N;
		this.level = 0;
		this.lc = lc;
		this.rc = rc;
		this.hashFound = false;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
		
	public void run() {
		this.password = new byte[N];
		for(int i=1;i<N;i++)this.password[i] = '0';
		this.password[0] = (byte) (this.lc - 1);
		do {
			generar();
			if(solucion())this.hashFound = true;
			else if(criterio()) {
				if(level == 0)level = N-1;
				else level++;
			}
			else {
				while(level > -1 && !masHermanos())retroceder();
			}
		} while(!this.hashFound && level > -1);
	}
	
	public boolean hashFound() {
		return this.hashFound;
	}
	
	public String getPassword() {
		return new String(this.password);
	}
	
}
