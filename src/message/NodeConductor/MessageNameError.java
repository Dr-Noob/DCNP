package message.NodeConductor;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class MessageNameError extends Message {

	/**
	 *     1B       4B
	 * +--------+---------+
	 * | OpCode | ErrCode |
	 * +--------+---------+
	 * 
	 */
	
	private int errCode;
	
	public final static int CODE_REQUIRED_NAME = 0;
	public final static int CODE_ALREADY_TAKEN_NAME = 1;
	private final int FIELD_ERRCODE_BYTES = Integer.SIZE/8;
	
	public MessageNameError(DataInputStream dis) {
		super(dis);
		if(!this.valid)return;
		
		try {
			this.errCode = dis.readInt();
		} catch (IOException e) {
			System.out.println("FATAL ERROR: Failed to read field " + "ErrCode in Message 'NameError'");
			this.valid = false;
			return;
		}
	}
	
	public MessageNameError(int errCode) {
		super();
		this.opCode = OP_NAME_ERROR;
		this.errCode = errCode;
	}
	
	/**
	 * Done specifically to be called from parseMessage
	 */
	public MessageNameError(byte opCode, DataInputStream dis) {
		super();
		this.opCode = OP_NAME_ERROR;
		
		try {
			this.errCode = dis.readInt();
		} catch (IOException e) {
			System.out.println("FATAL ERROR: Failed to read field " + "ErrCode in Message 'NameError'");
			this.valid = false;
			return;
		}
	}
	
	@Override
	public byte[] toByteArray() {
		ByteBuffer buf = ByteBuffer.allocate(FIELD_OPCODE_BYTES + FIELD_ERRCODE_BYTES);

		// Opcode
		buf.put((byte)this.getOpCode());
		// ErrorCode
		buf.putInt(this.errCode);

		return buf.array();
	}
	
	@Override
	public String toString() {
		StringBuffer strBuf = new StringBuffer();
		strBuf.append(" Type: " + this.getOpCodeString());
		strBuf.append(" ErrorCode: " + this.errCode);
		return strBuf.toString();
	}
	
	public int getErrorCode() {
		return this.errCode;
	}
}
