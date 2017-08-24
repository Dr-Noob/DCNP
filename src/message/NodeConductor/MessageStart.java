package message.NodeConductor;

import java.io.DataInputStream;
import java.nio.ByteBuffer;

public class MessageStart extends Message {

	/**
	 *     1B   
	 * +--------+
	 * | OpCode |
	 * +--------+
	 * 
	 */
	
	public MessageStart() {
		super();
		this.opCode = OP_START;
	}
	
	public MessageStart(DataInputStream dis) {
		super(dis);
		if(!this.valid)return;
	}
	
	/**
	 * Done specifically to be called from parseMessage
	 */
	public MessageStart(byte opCode) {
		super();
		this.opCode = OP_START;
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
