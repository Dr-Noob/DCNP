package message.NodeConductor;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class MessageNewIn extends Message {
	
	/**
	 *     1B          4B            ¿?B
	 * +--------+--------------+------------+
	 * | OpCode | BytesOfField |     In     |
	 * +--------+--------------+------------+
	 * 
	 */
	
	private String in;
	private byte [] inByteArray;
	private int bytesOfField = 0;
	
	public MessageNewIn(DataInputStream dis) {
		super(dis);
		if(!this.valid)return;
		this.bytesOfField = 0;
		
		try {
			bytesOfField = dis.readInt();
		} catch (IOException e) {
			System.out.println("FATAL ERROR: Failed to read field " + "BytesOfField in Message 'NewIn'");
			this.valid = false;
			return;
		}
		
		inByteArray = new byte[this.bytesOfField];
		
		try {
			dis.readFully(inByteArray);
		} catch (IOException e) {
			System.out.println("FATAL ERROR: Failed to read field " + "In");
			this.valid = false;
			return;
		}
		
		this.in = new String(inByteArray);
	}
	
	public MessageNewIn(String in) {
		super();
		this.opCode = OP_NEW_IN;
		this.bytesOfField = in.length();
		this.inByteArray = new byte[this.bytesOfField];
		this.in = in;
		this.inByteArray = this.in.getBytes();
	}
	
	/**
	 * Done specifically to be called from parseMessage
	 * @param opCode
	 * @param dis
	 */
	MessageNewIn(byte opCode, DataInputStream dis) {
		super();
		this.opCode = OP_NEW_IN;
		this.bytesOfField = 0;
		
		try {
			bytesOfField = dis.readInt();
		} catch (IOException e) {
			System.out.println("FATAL ERROR: Failed to read field " + "BytesOfField in Message 'NewIn'");
			this.valid = false;
			return;
		}
		
		inByteArray = new byte[this.bytesOfField];
		
		try {
			dis.readFully(inByteArray);
		} catch (IOException e) {
			System.out.println("FATAL ERROR: Failed to read field " + "In");
			this.valid = false;
			return;
		}
		
		this.in = new String(inByteArray);
	}
	
	public String getIn() {
		return this.in;
	}
	
	@Override
	public byte[] toByteArray() {
		ByteBuffer buf = ByteBuffer.allocate(FIELD_OPCODE_BYTES + FIELD_BYTES_OF_FIELD_BYTES + bytesOfField);

		// Opcode
		buf.put((byte)this.getOpCode());
		// BytesOfField
		buf.putInt(this.bytesOfField);
		// In
		buf.put(this.inByteArray);

		return buf.array();
	}
	
	@Override
	public String toString() {
		StringBuffer strBuf = new StringBuffer();
		strBuf.append(" Type: " + this.getOpCodeString());
		strBuf.append(" New In: " + this.in);
		return strBuf.toString();
	}

}
