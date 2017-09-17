package conductor;

public class ConductorArgumentsParser {

	private String[] args;
	private String folderPath;
	private boolean debugModeActivated;
	
	private boolean folderSet;
	private boolean debugModeSet;
	private boolean namesRequiredSet;
	private boolean areNamesRequired;
	
	private final String HELP_MESSAGE = "To read help, run with '-help' option";
	private final String ERROR_MESSAGE = "ERROR: Folder must be specified";
	private final String DEBUG_ACTIVATED_WORD = "verbose";
	private final String DEBUG_DEACTIVATED_WORD = "quiet";
	
	public ConductorArgumentsParser(String[] args) {
		this.args = args;
		this.folderPath = null;
		this.debugModeActivated = false;
		
		this.debugModeSet = false;
		this.areNamesRequired = false;
		this.folderSet = false;
	}
	
	public boolean parseArgs()  {
		int i = 0;
		int argLen = args.length;
		String tmp = new String();
		
		while(i < argLen) {
			tmp = this.args[i];
			switch (tmp) {
			case "-folder":
				if(this.folderSet) {
					System.out.println("ERROR: Folder specified more than once");
					return false;
				}
				if(i+1 < argLen) {
					i++;
					this.folderPath = args[i];	
				}
				else return error();
				
				this.folderSet = true;
				break;
				
			case "-names":
				if(this.namesRequiredSet) {
					System.out.println("ERROR: Names required specified more than once");
					return false;
				}
				if(i+1 < argLen && args[i+1].equals("required")) this.areNamesRequired = true;
				else return error();
				
				this.namesRequiredSet = true;
				i++;
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
		if(this.folderSet)return true;
		else return error();
	}

	private void printHelp() {
		System.out.println("Showing help. Available parameters are:");
		System.out.println("-folder X");
		System.out.println("\t REQUIERED. Specify the folder where the configuration file 'dcnp.txt' is");
		System.out.println("\t Please look at documentation if you don't know what's the configuration file");
		System.out.println("-debug (quiet | verbose)");
		System.out.println("\t OPTIONAL. Change debug mode(messages that notifies inputs to be computed by nodes and its outputs)"); 
		System.out.println("\t Default is 'quiet', which will not display debug messages at all, whereas 'verbose' mode will display every debug message");
		System.out.println("-names required");
		System.out.println("\t OPTIONAL. Specify if nodes need to identify when connecting to the conductor");
		System.out.println("\t This will make conductor to show nodes's names");
		System.out.println("-help");
		System.out.println("\t Shows this help");
	}
	
	private boolean error() {
		System.out.println(ERROR_MESSAGE);
		System.out.println(HELP_MESSAGE);
		return false;
	}
	
	public String getFolderPath() {
		return this.folderPath;
	}
	
	public boolean isDebugModeActivated() {
		return this.debugModeActivated;
	}
	
	public boolean areNamesRequired() {
		return this.areNamesRequired;
	}
}
