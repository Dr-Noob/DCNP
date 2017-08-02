package conductor;

import java.util.HashSet;

public class NodesDatabase {
	private HashSet<NodeInfo> nodes;
	private long startComputing;
	private long endComputing;
	private int insComputed;
	
	public NodesDatabase() {
		this.nodes = new HashSet<>();
		this.insComputed = 0;
		this.startComputing = 0;
		this.endComputing = 0;
	}
	
	/**
	 * Add a node to the database. Returns false if a node with the same ip was on the database
	 * @param nThreads Number of threads of the node
	 * @param os Operating system of the node
	 * @param cpuArch CPU Architecture of the node
	 * @param ip IP of the node
	 * @return true if the node is not connected at the moment, false if it is connected
	 */
	public boolean addNode(int nThreads, String os, String cpuArch, String ip) {
		NodeInfo newNode = new NodeInfo(nThreads,os,cpuArch,ip);
		if(!this.nodes.add(newNode)) {
			for(NodeInfo n : this.nodes) {
				if(n.getIp().equals(ip)) {
					return !n.isComputing();
				}
			}
		}
		return true;
	}
	
	/**
	 * Remove a node from the database
	 * @param nThreads Number of threads of the node
	 * @param os Operating system of the node
	 * @param cpuArch CPU Architecture of the node
	 * @param ip IP of the node
	 */
	public void removeNode(int nThreads, String os, String cpuArch, String ip) {
		NodeInfo deleteNode = new NodeInfo(nThreads,os,cpuArch,ip);
		this.nodes.remove(deleteNode);
	}
	
	/**
	 * Register in the database that a new out was computed by a node
	 * @param ip The ip of the node who computed a new out
	 */
	public void newOut(String ip) {
		for(NodeInfo n : this.nodes) {
			if(n.getIp().equals(ip)) {
				n.newInComputed();
				insComputed++;
				return;
			}
		}
		System.out.println("ERROR: Ip " + ip +" is not in node db");
	}
	
	/**
	 * Sets the node, represented by the args, to have finished computing at the time this method was called
	 * @param ip IP of the node
	 */
	public void nodeDesconected(String ip) {
		for(NodeInfo n : this.nodes) {
			if(n.getIp().equals(ip)) {
				n.finishedComputing();
				return;
			}
		}
		System.out.println("ERROR: Ip " + ip +" is not in node db");
	}
	
	/**
	 * Sets the node, represented by the args, to have started computing at the time this method was called
	 * @param ip IP of the node
	 */
	public void nodeStartedComputing(String ip) {
		for(NodeInfo n : this.nodes) {
			if(n.getIp().equals(ip)) {
				n.startedComputing();
				return;
			}
		}
		System.out.println("ERROR: Ip " + ip +" is not in node db");
	}
	
	/**
	 * 	@return Number the sum of cores available in the node database
	 */
	public int getCores() {
		int i = 0;
		for(NodeInfo n : this.nodes) {
			i += n.getnCores();
		}
		return i;
	}
	
	/**
	 * @return Number of inputs computed
	 */
	public int getInsComputed() {
		return this.insComputed;
	}
	
	/**
	 * @return Number of nodes computing
	 */
	public int getNodesComputing() {
		int i = 0;
		for(NodeInfo n : this.nodes) {
			if(n.isComputing())i++;
		}
		return i;
	}
	
	/**
	 * @return Number of nodes connected
	 */
	public int getNodesConnected() {
		return this.nodes.size();
	}
	
	/**
	 * Prints statistics about nodes in the node database
	 */
	public void showStatistics() {
		int i = 0;
		for(NodeInfo n : this.nodes) {
			i++;
			System.out.println("Node Nº" + i + ":");
			System.out.println("\t[" + n.getOperatingSystem() + "][" + n.getArch() + "][" + n.getnCores() + " cores][" + n.getIp() + "]");
			System.out.println("\t" + n.getInsComputed() + " in/s computed");
			System.out.println("\t" + n.getTimeComputing() + " seconds spent computing");
			if(n.isComputing())System.out.println("\tIs computing");
			else System.out.println("\tIs not computing");
		}
	}
	
	/**
	 * Must be called at the same time that computing started
	 */
	public void computingStarted() {
		/*
		for(NodeInfo n : this.nodes) {
			n.startedComputing();
		}
		*/
		this.startComputing = System.currentTimeMillis();
	}
	
	/**
	 * Must be called at the same time that computing ended
	 */
	public void computingEnded() {
		for(NodeInfo n : this.nodes) {
			if(n.isComputing())n.finishedComputing();
		}
		this.endComputing = System.currentTimeMillis();
	}
	
	/**
	 * @return Time in seconds spent in computing the current problem
	 */
	public float getTimeComputing() {
		if(this.startComputing == 0)return 0;
		if(this.endComputing == 0)return (float)(System.currentTimeMillis() - this.startComputing)/1000;
		return (float)(this.endComputing - this.startComputing)/1000;
	}
}
