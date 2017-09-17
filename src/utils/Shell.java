package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public abstract class Shell {

	/**
	 * Supported commands: EXIT,HELP,STATS
	 */
	
	public static final byte COM_INVALID = 0;
	public static final byte COM_EXIT = 1;
	public static final byte COM_HELP = 2;
	public static final byte COM_STATS = 3;
	
	protected final int READER_TIMEOUT = 200;
	protected final int MAX_ARGS = 5;
	
	protected boolean shouldEnd;
	protected int count;
	protected byte commandCode;
	protected String[] fullCommand;
	protected BufferedReader reader;

	public Shell() {
		this.reader = new BufferedReader(new InputStreamReader(System.in));
		this.fullCommand = new String[this.MAX_ARGS];
		this.commandCode = COM_INVALID;
		this.shouldEnd = false;
		this.count = 1;
	}
	
	protected abstract void printPrompt();
	protected abstract void print(String msg);
	protected abstract void printError(String error);
	protected abstract void printHelp();
	protected abstract void printStats();
	protected abstract byte getCommandCode(String command);
	
	public void closeShell() {
		this.shouldEnd = true;
		try {
			this.reader.close();
		} catch (IOException e) {
			System.out.println("ERROR: Failed to close shell's reader");
		}
	}
	
	public boolean shouldEnd() {
		return this.shouldEnd;
	}
	
	public void readCommand() {
		this.fullCommand[0] = null;
		do{					
			try {
				if(this.count < 2)this.printPrompt();
				//End if this is requested to be done
				while(!reader.ready()){
					this.count = 0;
					try {
						Thread.sleep(READER_TIMEOUT);
					} catch (InterruptedException e) {
						System.out.println("FATAL ERROR: Reader was interrupted");
						return;
					}
					if(this.shouldEnd)return;
				}
				if(this.shouldEnd)return;
				//Will exit while only if input is available; time to read
				fullCommand = reader.readLine().split(" ");
			} catch (IOException e) {
				System.out.println("FATAL ERROR: I/O happened in reader");
				return;
			}
			//Wont exit if just enter was pressed
			this.count++;
			//If fullCommand contains anything, try to find it
			if(this.fullCommand.length != 0 && fullCommand[0].isEmpty())fullCommand[0] = searchCommand();
		} while(this.fullCommand.length == 0 || fullCommand[0].isEmpty());
	}
	
	//Search for a command in the whole line
	private String searchCommand() {
		for(int i=0;i<fullCommand.length;i++)if(!fullCommand[i].isEmpty())return fullCommand[i];
		//Full command searched, but nothing found, return empty command
		return "";
	}
}
