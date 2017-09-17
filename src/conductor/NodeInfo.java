package conductor;

public class NodeInfo {

	private int nCores;
	private int nInsComputed;
	private long startComputing;
	private long endComputing;
	private boolean isComputing;
	private String operatingSystem;
	private String arch;
	private String ip;
	private String name;
	
	public NodeInfo(int nCores, String operatingSystem, String arch, String ip, String name) {
		this.nCores = nCores;
		this.operatingSystem = operatingSystem;
		this.arch = arch;
		this.ip = ip;
		this.name = name;
		this.nInsComputed = 0;
		this.startComputing = 0;
		this.endComputing = 0;
		this.isComputing = false;
	}

	public void startedComputing() {
		this.isComputing = true;
		this.startComputing = System.currentTimeMillis();
	}
	
	public void finishedComputing() {
		this.isComputing = false;
		this.endComputing = System.currentTimeMillis();
	}
	
	public void newInComputed() {
		this.nInsComputed++;
	}
	
	public int getnCores() {
		return this.nCores;
	}

	public String getOperatingSystem() {
		return this.operatingSystem;
	}

	public String getArch() {
		return this.arch;
	}
	
	public String getName() {
		return this.name;
	}

	public String getIp() {
		return this.ip;
	}
	
	public int getInsComputed() {
		return this.nInsComputed;
	}
	
	public float getTimeComputing() {
		if(this.isComputing)return (float)(System.currentTimeMillis() - this.startComputing)/1000;
		return (float)(this.endComputing - this.startComputing)/1000;
	}
	
	public boolean isComputing() {
		return this.isComputing;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ip == null) ? 0 : ip.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NodeInfo other = (NodeInfo) obj;
		if (ip == null) {
			if (other.ip != null)
				return false;
		} else if (!ip.equals(other.ip))
			return false;
		return true;
	}
	
	
}
