package message.NodeConductor;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class MessageAddNode extends Message {
	
	/**
	 *     1B        4B          	4B           ¿?            4B            ¿?
	 * +--------+-----------+---------------+----------+----------------+----------+
	 * | OpCode | NºThreads |  BytesOfField | CPU Arch |  BytesOfField  |    OS    | 
	 * +--------+-----------+---------------+----------+----------------+----------+
	 * 
	 */
	
	private final int FIELD_NTHREADS_BYTES = Integer.SIZE/8;
	
	private int nThreads;
	private String cpuArch;
	private byte [] cpuArchByteArray;
	private int bytesOfFieldArch;
	private int bytesOfFieldOs;
	private String os;
	private byte [] osByteArray;
	
	public MessageAddNode(DataInputStream dis) {
		super(dis);
		if(!this.valid)return;
		this.bytesOfFieldArch = 0;
		this.bytesOfFieldOs = 0;
		
		try {
			this.nThreads = dis.readInt();
		} catch (IOException e) {
			System.out.println("FATAL ERROR: Failed to read field " + "NºThreads");
			this.valid = false;
			return;
		}
		
		try {
			this.bytesOfFieldArch = dis.readInt();
		} catch (IOException e) {
			System.out.println("FATAL ERROR: Failed to read field " + "BytesOfField from MessageAddNode");
			this.valid = false;
			return;
		}
		
		this.cpuArchByteArray = new byte[bytesOfFieldArch];
		try {
			dis.readFully(this.cpuArchByteArray);
		} catch (IOException e) {
			System.out.println("FATAL ERROR: Failed to read field " + "CPU Arch");
			this.valid = false;
			return;
		}
		
		this.cpuArch = new String(this.cpuArchByteArray);
		
		try {
			this.bytesOfFieldOs = dis.readInt();
		} catch (IOException e) {
			System.out.println("FATAL ERROR: Failed to read field " + "BytesOfField from MessageAddNode");
			this.valid = false;
			return;
		}
		
		this.osByteArray = new byte[this.bytesOfFieldOs];
		
		try {
			dis.readFully(osByteArray);
		} catch (IOException e) {
			System.out.println("FATAL ERROR: Failed to read field " + "OS");
			this.valid = false;
			return;
		}
		this.os = new String(this.osByteArray);
	}
	
	public MessageAddNode(int nThreads, String cpuArch, String os) {
		super();
		this.opCode = OP_ADD_NODE;
		this.nThreads = nThreads;
		
		this.bytesOfFieldArch = cpuArch.length();
		this.cpuArch = cpuArch;
		this.cpuArchByteArray = this.cpuArch.getBytes();
		
		this.bytesOfFieldOs = os.length();
		this.os = os;
		this.osByteArray = this.os.getBytes();
	}
	
	public int getnThreads() {
		return this.nThreads;
	}

	public String getCpuArch() {
		return this.cpuArch;
	}

	public String getOs() {
		return this.os;
	}

	@Override
	public byte[] toByteArray() {
		ByteBuffer buf = ByteBuffer.allocate(FIELD_OPCODE_BYTES + FIELD_NTHREADS_BYTES + FIELD_BYTES_OF_FIELD_BYTES + bytesOfFieldArch + FIELD_BYTES_OF_FIELD_BYTES + bytesOfFieldOs);

		// Opcode
		buf.put((byte)this.getOpCode());
		// Nº Threads
		buf.putInt(this.nThreads);
		//Bytes of Field Arch
		buf.putInt(this.bytesOfFieldArch);
		// CPU Arch
		buf.put(this.cpuArchByteArray);
		//Bytes of Field OS
		buf.putInt(this.bytesOfFieldOs);
		//OS
		buf.put(osByteArray);

		return buf.array();
	}
	
	@Override
	public String toString() {
		StringBuffer strBuf = new StringBuffer();
		strBuf.append(" Type: " + this.getOpCodeString());
		strBuf.append(" Nº Threads: " + this.nThreads);
		strBuf.append(" CPU Arch: " + this.cpuArch);
		strBuf.append(" OS: " + this.os);
		return strBuf.toString();
	}
}
