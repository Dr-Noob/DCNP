package message.ConductorProblem;

import java.io.DataInputStream;
import java.nio.ByteBuffer;

public class MessageNextInReq extends Message {

	/**
	 *     1B    
	 * +--------+
	 * | OpCode | 
	 * +--------+
	 * 
	 */
	
	/**
	 * Creates a NextInReq Message ready to be sent
	 */
	public MessageNextInReq() {
		super();
		this.opCode = OP_NEXT_IN_REQ;
	}
	
	/**
	 * Reads a ByeMessage
	 * @param dis DataInputStream
	 */
	public MessageNextInReq(DataInputStream dis) {
		super(dis);
		if(!this.valid)return;
	}
	
	/**
	 * Done specifically to be called from parseMessage
	 * @return
	 */
	public MessageNextInReq(byte opCode, DataInputStream dis) {
		super();
		this.opCode = OP_NEXT_IN_REQ;
	}
	
	@Override
	public String toString() {
		StringBuffer strBuf = new StringBuffer();
		strBuf.append(" Type: " + this.getOpCodeString());
		return strBuf.toString();
	}
	
	@Override
	public byte[] toByteArray() {
		ByteBuffer buf = ByteBuffer.allocate(FIELD_OPCODE_BYTES);

		// Opcode
		buf.put((byte)this.getOpCode());

		return buf.array();
	}
}
