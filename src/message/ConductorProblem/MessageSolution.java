package message.ConductorProblem;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class MessageSolution extends Message {

	/**
	 *     1B            4B             ¿? 
	 * +--------+----------------+----------------+
	 * | OpCode | Bytes of Field |   Solution     | 
	 * +--------+----------------+----------------+
	 * 
	 */
	
	private String solution;
	private byte[] solutionByteArray;
	private int bytesOfField;
	
	public MessageSolution(DataInputStream dis) {
		super(dis);
		if(!this.valid)return;
		this.bytesOfField = 0;
		
		try {
			bytesOfField = dis.readInt();
		} catch (IOException e) {
			System.out.println("FATAL ERROR: Failed to read field " + "BytesOfField in Message 'Solution'");
			this.valid = false;
			return;
		}
		
		solutionByteArray = new byte[this.bytesOfField];
		
		try {
			dis.readFully(solutionByteArray);
		} catch (IOException e) {
			System.out.println("FATAL ERROR: Failed to read field " + "Solution");
			this.valid = false;
			return;
		}
		
		this.solution = new String(solutionByteArray);
	}
	
	public MessageSolution(String solution) {
		super();
		this.opCode = OP_SOLUTION;
		this.bytesOfField = solution.length();
		this.solutionByteArray = new byte[this.bytesOfField];
		this.solution = solution;
		this.solutionByteArray = this.solution.getBytes();
	}
	
	/**
	 * Done specifically to be called from parseMessage
	 * @return
	 */
	
	public MessageSolution(byte opCode, DataInputStream dis) {
		super();
		this.opCode = OP_SOLUTION;
		this.bytesOfField = 0;
		
		try {
			bytesOfField = dis.readInt();
		} catch (IOException e) {
			System.out.println("FATAL ERROR: Failed to read field " + "BytesOfField in Message 'Solution'");
			this.valid = false;
			return;
		}
		
		solutionByteArray = new byte[this.bytesOfField];
		
		try {
			dis.readFully(solutionByteArray);
		} catch (IOException e) {
			System.out.println("FATAL ERROR: Failed to read field " + "Solution");
			this.valid = false;
			return;
		}
		
		this.solution = new String(solutionByteArray);
	}
	
	public String getSolution() {
		return this.solution;
	}
	
	@Override
	public byte[] toByteArray() {
		ByteBuffer buf = ByteBuffer.allocate(FIELD_OPCODE_BYTES + FIELD_BYTES_OF_FIELD_BYTES + bytesOfField);

		// Opcode
		buf.put((byte)this.getOpCode());
		// BytesOfField
		buf.putInt(this.bytesOfField);
		// In
		buf.put(this.solutionByteArray);

		return buf.array();
	}
	
	@Override
	public String toString() {
		StringBuffer strBuf = new StringBuffer();
		strBuf.append(" Type: " + this.getOpCodeString());
		strBuf.append(" Solution: " + this.solution);
		return strBuf.toString();
	}
}
