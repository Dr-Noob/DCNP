package message.NodeConductor;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class MessageNewOutBye extends Message {

	/**
	 *     1B          4B            ¿?B
	 * +--------+--------------+------------+
	 * | OpCode | BytesOfField |     Out    |
	 * +--------+--------------+------------+
	 * 
	 */

	private String out;
	private byte [] outByteArray;
	private int bytesOfField = 0;
	
	public MessageNewOutBye(DataInputStream dis) {
		super(dis);
		if(!this.valid)return;
		this.bytesOfField = 0;
		
		try {
			bytesOfField = dis.readInt();
		} catch (IOException e) {
			System.out.println("FATAL ERROR: Failed to read field " + "BytesOfField in Message 'NewOutBye'");
			this.valid = false;
			return;
		}
		
		outByteArray= new byte[this.bytesOfField];
		
		try {
			dis.readFully(outByteArray);
		} catch (IOException e) {
			System.out.println("FATAL ERROR: Failed to read field " + "Out");
			this.valid = false;
			return;
		}
		
		this.out = new String(outByteArray);
	}
	
	public MessageNewOutBye(String out) {
		super();
		this.opCode = OP_NEW_OUT_BYE;
		this.bytesOfField = out.length();
		this.out = out;
		this.outByteArray = this.out.getBytes();
	}
	
	/**
	 * Done specifically to be called from parseMessage
	 * @param opCode
	 * @param dis
	 */
	MessageNewOutBye(byte opCode, DataInputStream dis) {
		super();
		this.opCode = OP_NEW_OUT_BYE;
		this.bytesOfField = 0;
		
		try {
			bytesOfField = dis.readInt();
		} catch (IOException e) {
			System.out.println("FATAL ERROR: Failed to read field " + "BytesOfField in Message 'NewOutBye'");
			this.valid = false;
			return;
		}
		
		outByteArray = new byte[this.bytesOfField];
		
		try {
			dis.readFully(outByteArray);
		} catch (IOException e) {
			System.out.println("FATAL ERROR: Failed to read field " + "Out");
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
		// Out
		buf.put(this.outByteArray);

		return buf.array();
	}
	
	@Override
	public String toString() {
		StringBuffer strBuf = new StringBuffer();
		strBuf.append(" Type: " + this.getOpCodeString());
		strBuf.append(" New Out: " + this.out);
		return strBuf.toString();
	}
}
