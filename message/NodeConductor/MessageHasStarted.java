package message.NodeConductor;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class MessageHasStarted extends Message {

	/**
	 *     1B       1B   
	 * +--------+--------+
	 * | OpCode | Answer |
	 * +--------+--------+
	 * 
	 */
	
	private final int FIELD_ANSWER_BYTES = 1;
	
	private byte answer;
	
	public MessageHasStarted(DataInputStream dis) {
		super(dis);
		if(!this.valid)return;
		
		try {
			this.answer = dis.readByte();
		} catch (IOException e) {
			System.out.println("FATAL ERROR: Failed to read field " + "Answer");
			this.valid = false;
			return;
		}
	}
	
	public MessageHasStarted(boolean hasStarted) {
		super();
		this.opCode = OP_HAS_STARTED;
		if(hasStarted)this.answer = 0x1;
		else this.answer = 0x0;
	}
	
	public boolean hasStarted() {
		return this.answer == 0x1;
	}
	
	@Override
	public byte[] toByteArray() {
		ByteBuffer buf = ByteBuffer.allocate(FIELD_OPCODE_BYTES + FIELD_ANSWER_BYTES);

		// Opcode
		buf.put((byte)this.getOpCode());
		// Asnwer
		buf.put((byte)this.answer);

		return buf.array();
	}
	
	@Override
	public String toString() {
		StringBuffer strBuf = new StringBuffer();
		strBuf.append(" Type: " + this.getOpCodeString());
		if(hasStarted())System.out.println(" Has Started: Yes");
		else System.out.println(" Has Started: No");
		return strBuf.toString();
	}
}
