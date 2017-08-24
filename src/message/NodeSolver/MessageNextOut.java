package message.NodeSolver;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class MessageNextOut extends Message {
	
	/**
	 *     1B          4B              ¿?B
	 * +--------+--------------+----------------+
	 * | OpCode | BytesOfField |     Output     |
	 * +--------+--------------+----------------+
	 * 
	 */
	
	private String out;
	private byte [] outByteArray;
	private int bytesOfField = 0;
	
	public MessageNextOut(DataInputStream dis) {
		super(dis);
		if(!this.valid)return;
		this.bytesOfField = 0;
		
		try {
			bytesOfField = dis.readInt();
		} catch (IOException e) {
			System.out.println("FATAL ERROR: Failed to read field " + "BytesOfField in Message 'NextOut'");
			this.valid = false;
			return;
		}
		
		outByteArray = new byte[this.bytesOfField];
		
		try {
			dis.readFully(outByteArray);
		} catch (IOException e) {
			//System.out.println("FATAL ERROR: Failed to read field " + "Output");
			this.valid = false;
			return;
		}
		
		this.out = new String(outByteArray);
	}
	
	public MessageNextOut(String out) {
		super();
		this.opCode = OP_NEXT_OUT;
		this.bytesOfField = out.length();
		this.outByteArray = new byte[this.bytesOfField];
		this.out = out;
		this.outByteArray = this.out.getBytes();
	}
	
	/**
	 * Done specifically to be called from parseMessage
	 * @param opCode
	 * @param dis
	 */
	MessageNextOut(byte opCode, DataInputStream dis) {
		super();
		this.opCode = OP_NEXT_OUT;
		this.bytesOfField = 0;
		
		try {
			bytesOfField = dis.readInt();
		} catch (IOException e) {
			System.out.println("FATAL ERROR: Failed to read field " + "BytesOfField in Message 'NextOut'");
			this.valid = false;
			return;
		}
		
		outByteArray = new byte[this.bytesOfField];
		
		try {
			dis.readFully(outByteArray);
		} catch (IOException e) {
			System.out.println("FATAL ERROR: Failed to read field " + "Output");
			this.valid = false;
			return;
		}
		
		this.out = new String(outByteArray);
	}
	
	public String getOut() {
		return this.out;
	}
	
	@Override
	public byte[] toByteArray() {
		ByteBuffer buf = ByteBuffer.allocate(FIELD_OPCODE_BYTES + FIELD_BYTES_OF_FIELD_BYTES + bytesOfField);

		// Opcode
		buf.put((byte)this.getOpCode());
		// BytesOfField
		buf.putInt(this.bytesOfField);
		// In
		buf.put(this.outByteArray);

		return buf.array();
	}
	
	@Override
	public String toString() {
		StringBuffer strBuf = new StringBuffer();
		strBuf.append(" Type: " + this.getOpCodeString());
		strBuf.append(" Next Out: " + this.out);
		return strBuf.toString();
	}

}
