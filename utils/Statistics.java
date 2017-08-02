package utils;

public class Statistics {

	private int cores;
	private int nInsComputed;
	private long startComputing;
	private String arch;
	private String os;
	
	public Statistics() {
		this.cores = Runtime.getRuntime().availableProcessors();
		this.os = System.getProperty("os.name");
		this.arch = System.getProperty("os.arch");
		this.nInsComputed = 0;
		this.startComputing = 0;
	}

	public void startedComputing() {
		this.startComputing = System.currentTimeMillis();
	}
	
	public int getCores() {
		return this.cores;
	}

	public String getArch() {
		return this.arch;
	}

	public String getOs() {
		return this.os;
	}
	
	public int getInsComputed() {
		return this.nInsComputed;
	}
	
	public float getSecondsComputing() {
		if(this.startComputing == 0)return 0;
		else return (float)(System.currentTimeMillis() - this.startComputing)/1000;
	}
	
}