package message.NodeSolver;

import java.io.DataInputStream;
import java.nio.ByteBuffer;

public class MessageBye extends Message {

	/**
	 *     1B   
	 * +--------+
	 * | OpCode |
	 * +--------+
	 * 
	 */
	
	/**
	 * Creates a Bye Message ready to be sent
	 */
	public MessageBye() {
		super();
		this.opCode = OP_BYE;
	}
	
	/**
	 * Reads a ByeMessage
	 * @param dis DataInputStream
	 */
	public MessageBye(DataInputStream dis) {
		super(dis);
		if(!this.valid)return;
	}
	
	/**
	 * Done specifically to be called from parseMessage
	 */
	MessageBye(byte opCode) {
		super();
		this.opCode = OP_BYE;
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
