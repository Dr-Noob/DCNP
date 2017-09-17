package node;

import java.util.regex.Pattern;

public class NodeArgumentsParser {
	
	private String[] args;
	
	private int port;
	private String ip;
	private String folder;
	private String name;
	private boolean debugModeActivated;
	
	private boolean ipSet;
	private boolean portSet;
	private boolean folderSet;
	private boolean debugModeSet;
	private boolean nameSet;
	
	private final String HELP_MESSAGE = "To read help, run with '-help' option";
	private final String ERROR_MESSAGE = "ERROR: Ip and folder must be specified";
	private final Pattern IP_PATTERN = Pattern.compile("^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");
	private final String DEBUG_ACTIVATED_WORD = "verbose";
	private final String DEBUG_DEACTIVATED_WORD = "quiet";
	private final int DEFAULT_CONDUCTOR_PORT = 4450;
	
	public NodeArgumentsParser(String[] args) {
		this.args = args;
		
		this.port = -1;
		this.ip = new String();
		this.folder = new String();
		this.name = null;
		
		this.ipSet = false;
		this.portSet = false;
		this.folderSet = false;
		this.debugModeActivated = false;
	}
	
	public boolean parseArgs() {
		int i = 0;
		int argLen = args.length;
		String tmp = new String();
		
		while(i < argLen) {
			tmp = this.args[i];
			switch (tmp) {
			case "-port":
				if(this.portSet) {
					System.out.println("ERROR: Port specified more than once");
					return false;
				}
				
				if(i+1 < argLen) {
					i++;
					try {
						this.port = Integer.parseInt(this.args[i]);
					} catch (NumberFormatException e) {
						System.out.println("ERROR:" + this.args[i] + " is not a number");
						return false;
					}	
				}
				else return error();
				
				this.portSet = true;
				break;
				
			case "-ip":
				if(this.ipSet) {
					System.out.println("ERROR: IP specified more than once");
					return false;
				}
				if(i+1 < argLen) {
					i++;
					this.ip = this.args[i];
					if(!IP_PATTERN.matcher(this.ip).matches() || this.ip.startsWith("127")){
						System.out.println("ERROR: " + this.ip + " is not a valid IP");
						return false;
					}
				}
				else return error();
				
				this.ipSet = true;
				break;
				
			case "-name":
				if(this.nameSet) {
					System.out.println("ERROR: Name specified more than once");
					return false;
				}
				
				if(i+1 < argLen) {
					i++;
					this.name = this.args[i];
					if(!this.name.matches("\\A\\p{ASCII}*\\z")) {
						System.out.println("ERROR: Invalid name");
						System.out.println("Name contains at least one non-ascii character");
						return false;
					}
				}
				else {
					System.out.println("ERROR: Invalid name");
					return false;
				}
				
				this.nameSet = true;
				break;
			
			case "-debug":
				if(this.debugModeSet) {
					System.out.println("ERROR: Debug mode set specified more than once");
					return false;
				}
				if(i+1 < argLen) {
					i++;
					if(this.args[i].equals(DEBUG_ACTIVATED_WORD))this.debugModeActivated = true;
					else if (this.args[i].equals(DEBUG_DEACTIVATED_WORD))this.debugModeActivated = false;
					else {
						System.out.println("ERROR: " + this.args[i] + " is not a valid mode for debug");
						return false;
					}
				}
				else return error();
				
				this.debugModeSet = true;
				break;
				
			case "-folder":
				if(this.folderSet) {
					System.out.println("ERROR: Folder specified more than once");
					return false;
				}
				if(i+1 < argLen) {
					i++;
					this.folder = this.args[i];
				}
				else return error();
				
				this.folderSet = true;
				break;
				
			case "-help":
				if(i != 0 || i+1 < argLen) {
					System.out.println("ERROR: Help parameter must be the first one and must be alone");
					System.out.println(HELP_MESSAGE);
					return false;
				}
				printHelp();
				return false;

			default:
				System.out.println("ERROR: Option not recognised: '" + tmp + "'");
				System.out.println(HELP_MESSAGE);
				return false;
			}
			i++;
		}
		
		if(this.ipSet && this.folderSet)return true;
		else return error();
	}
	
	private void printHelp() {
		System.out.println("Showing help. Available parameters are:");
		System.out.println("-port X");
		System.out.println("\t OPTIONAL. Specify the port where node will connect to(Conductor port). Default is 4450");
		System.out.println("-ip X");
		System.out.println("\t REQUIERED. Specify the IP where node will connect to(Conductor IP)");
		System.out.println("-folder X");
		System.out.println("\t REQUIRED. Specify the folder where jar file needed to solve the problem will be saved");
		System.out.println("-debug X");
		System.out.println("\t OPTIONAL. Change debug mode(messages that notifies outputs to be computed)"); 
		System.out.println("\t Default is 'quiet', which will not display debug messages at all, whereas 'verbose' mode will display every debug message");
		System.out.println("-name X");
		System.out.println("\t OPTIONAL/REQUIRED. It depends on what conductor needs. If he needs all node's names, it's a required field. If not, it will have no effect");
		System.out.println("\t Name will be displayed in conductor if its configured to do so");
		System.out.println("-help");
		System.out.println("\t Shows this help");
	}
	
	private boolean error() {
		System.out.println(ERROR_MESSAGE);
		System.out.println(HELP_MESSAGE);
		return false;
	}
	
	public boolean isPortSet() {
		return this.portSet;
	}
	
	public String getIp() {
		return this.ip;
	}
	
	public String getName() {
		return this.name;
	}
	
	public boolean isDebugModeActivated() {
		return this.debugModeActivated;
	}
	
	public int getPort() {
		if(this.portSet)return this.port;
		else return DEFAULT_CONDUCTOR_PORT;
	}
	
	public String getPathModule() {
		return this.folder;
	}
}
