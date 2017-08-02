package message.NodeConductor;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

import node.NodeShell;

public class MessageProblemModule extends Message {

	/**
	 *     1B          4B            ¿?           4B            ¿?             4B			   ¿?
	 * +--------+----------------+---------+--------------+------------+----------------+--------------+
	 * | OpCode |  BytesOfField  |  Name   | BytesOfField | Executable |  BytesOfField  |  SolverArgs  |
	 * +--------+----------------+---------+--------------+------------+----------------+--------------+
	 * 
	 */
		
	private String name;
	private String solverArgs;
	private byte[] nameByteBuffer;
	private byte[] executable;
	private byte[] solverArgsByteBuffer;
	private int bytesOfFieldName;
	private int bytesOfFieldExecutable;
	private int bytesOfFieldSolverArgs;
	
	private RandomAccessFile rfi;
	
	public MessageProblemModule(DataInputStream dis) {
		super(dis);
		if(!this.valid)return;
		this.bytesOfFieldExecutable = 0;
		this.bytesOfFieldName = 0;
		this.bytesOfFieldSolverArgs = 0;
		
		//Read NAME
		try {
			this.bytesOfFieldName = dis.readInt();
		} catch (IOException e) {
			System.out.println("FATAL ERROR: Failed to read field " + "BytesOfFieldName in Message 'ProblemModule'");
			this.valid = false;
			return;
		}
		
		this.nameByteBuffer = new byte[this.bytesOfFieldName];
		
		try {
			dis.readFully(nameByteBuffer);
		} catch (IOException e) {
			System.out.println("FATAL ERROR: Failed to read field " + "Name");
			this.valid = false;
			return;
		}
		
		this.name = new String(nameByteBuffer);
		
		//Read EXECUTABLE
		try {
			this.bytesOfFieldExecutable = dis.readInt();
		} catch (IOException e) {
			System.out.println("FATAL ERROR: Failed to read field " + "BytesOfFieldExecutable in Message 'ProblemModule'");
			this.valid = false;
			return;
		}
		
		this.executable = new byte[this.bytesOfFieldExecutable];
		
		try {
			dis.readFully(this.executable);
		} catch (IOException e) {
			System.out.println("FATAL ERROR: Failed to read field " + "Executable");
			this.valid = false;
			return;
		}	
		
		//Read SOLVER_ARGS
		try {
			this.bytesOfFieldSolverArgs = dis.readInt();
		} catch (IOException e) {
			System.out.println("FATAL ERROR: Failed to read field " + "BytesOfFieldSolverArgs in Message 'ProblemModule'");
			this.valid = false;
			return;
		}
		
		this.solverArgsByteBuffer = new byte[this.bytesOfFieldSolverArgs];
		
		try {
			dis.readFully(this.solverArgsByteBuffer);
		} catch (IOException e) {
			System.out.println("FATAL ERROR: Failed to read field " + "Executable");
			this.valid = false;
			return;
		}
		
		this.solverArgs = new String(this.solverArgsByteBuffer);
	}
	
	/**
	 * Used by conductor to send the message
	 * @param f Which file is the one to send
	 */
	public MessageProblemModule(File file, String solverArgs) {
		super();
		this.opCode = OP_PROBLEM_MODULE;
		
		try {
			rfi = new RandomAccessFile(file, "r");
		} catch (FileNotFoundException e) {
			System.out.println("ERROR: Could not create Random Access File");
			this.valid = false;
			return;
		}
		
		this.bytesOfFieldName = file.getName().length();
		this.name = file.getName();
		this.nameByteBuffer = this.name.getBytes();
		if(solverArgs != null) {
			this.bytesOfFieldSolverArgs = solverArgs.length();
			this.solverArgs = solverArgs;
			this.solverArgsByteBuffer = this.solverArgs.getBytes();
		}
		this.bytesOfFieldExecutable = (int)file.length();
		this.executable = new byte[this.bytesOfFieldExecutable];
		
		try {
			rfi.seek(0);
			rfi.readFully(executable);
		} catch (IOException e) {
			System.out.println("ERROR: Could not read file " + file.getName());
			this.valid = false;
			return;
		}
	}
	
	/**
	 * Writes the executable(message already created) to the folder specified
	 * @param folder Folder to write the file 
	 * @param shell The shell where messages will be printed in
	 * @return true if writing succeeded, false if not
	 */
	public boolean writeExecutable(File folder, NodeShell shell) {
		String absPath = folder.getAbsolutePath() + "/" + this.name;
		RandomAccessFile rfo;
		File exec = new File(absPath);
		if(exec.exists()) {
			shell.print("WARNING: File " + absPath + " already exists");
			shell.print("Replacing it");
			exec.delete();
		}
		
		try {
			exec.createNewFile();
		} catch (IOException e) {
			shell.print("ERROR: Failed to create new file " + absPath);
			return false;
		}
		
		try {
			rfo = new RandomAccessFile(exec, "rw");
			rfo.seek(0);
			rfo.write(this.executable);
		} catch (FileNotFoundException e) {
			shell.print("ERROR: Failed to open file " + absPath + " to write");
			return false;
		} catch (IOException e) {
			shell.print("ERROR: Failed to write on file " + absPath);
			return false;
		}
		
		try {
			rfo.close();
		} catch (IOException e) {
			shell.print("ERROR: Failed to close file " + absPath);
			//It's better to return true than false, because this a error that does not affect file integrity
		}
		
		return true;
	}
	
	@Override
	public byte[] toByteArray() {
		ByteBuffer buf = ByteBuffer.allocate(FIELD_OPCODE_BYTES + bytesOfFieldName + 3*FIELD_BYTES_OF_FIELD_BYTES + bytesOfFieldExecutable + bytesOfFieldSolverArgs);

		// Opcode
		buf.put((byte)this.getOpCode());
		// BytesOfFieldName
		buf.putInt(this.bytesOfFieldName);
		//Name
		buf.put(this.nameByteBuffer);
		// BytesOfFieldExecutable
		buf.putInt(this.bytesOfFieldExecutable);
		// Executable
		buf.put(this.executable);
		// BytesOfFieldArgs
		buf.putInt(this.bytesOfFieldSolverArgs);
		// SolverArgs
		if(this.bytesOfFieldSolverArgs > 0)buf.put(this.solverArgsByteBuffer);

		return buf.array();
	}
	
	@Override
	public String toString() {
		StringBuffer strBuf = new StringBuffer();
		strBuf.append(" Type: " + this.getOpCodeString());
		strBuf.append(" Name: " + this.name);
		strBuf.append(" Executable size: " + this.bytesOfFieldExecutable);
		if(this.bytesOfFieldSolverArgs != 0)strBuf.append(" Args: " + this.bytesOfFieldSolverArgs);
		else strBuf.append(" Args: none");
		return strBuf.toString();
	}
	
	public String getModuleName() {
		return this.name;
	}
	
	public String getModuleArgs() {
		return this.solverArgs;
	}

}
