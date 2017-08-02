package message.ConductorProblem;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class MessageNewOut extends Message {
	
	/**
	 *     1B          4B             ¿? 			4B			  ¿?
	 * +--------+----------------+----------+----------------+----------+
	 * | OpCode | Bytes of Field |  Output  | Bytes of Field |    In    |
	 * +--------+----------------+----------+----------------+----------+
	 * 
	 */
	
	private String out;
	private String in;
	private byte[] outByteArray;
	private byte[] inByteArray;
	private int bytesOfFieldOut;
	private int bytesOfFieldIn;
	
	public MessageNewOut(DataInputStream dis) {
		super(dis);
		if(!this.valid)return;
		this.bytesOfFieldOut = 0;
		this.bytesOfFieldIn = 0;
		
		try {
			bytesOfFieldOut = dis.readInt();
		} catch (IOException e) {
			System.out.println("FATAL ERROR: Failed to read field " + "BytesOfField in Message 'NewOut'");
			this.valid = false;
			return;
		}
		
		outByteArray = new byte[this.bytesOfFieldOut];
		
		try {
			dis.readFully(outByteArray);
		} catch (IOException e) {
			System.out.println("FATAL ERROR: Failed to read field " + "Out");
			this.valid = false;
			return;
		}
		
		this.out = new String(outByteArray);
		
		try {
			bytesOfFieldIn = dis.readInt();
		} catch (IOException e) {
			System.out.println("FATAL ERROR: Failed to read field " + "BytesOfField in Message 'NewOut'");
			this.valid = false;
			return;
		}
		
		inByteArray = new byte[this.bytesOfFieldIn];
		
		try {
			dis.readFully(inByteArray);
		} catch (IOException e) {
			System.out.println("FATAL ERROR: Failed to read field " + "In");
			this.valid = false;
			return;
		}
		
		this.in = new String(inByteArray);
	}
	
	public MessageNewOut(String out, String in) {
		super();
		this.opCode = OP_NEW_OUT;
		
		this.bytesOfFieldOut = out.length();
		this.out = out;
		this.outByteArray = this.out.getBytes();
		
		this.bytesOfFieldIn = in.length();
		this.in = in;
		this.inByteArray = this.in.getBytes();
	}
	
	/**
	 * Done specifically to be called from parseMessage
	 * @return
	 */
	public MessageNewOut(byte opCode, DataInputStream dis) {
		super();
		this.opCode = OP_NEW_OUT;
		this.bytesOfFieldOut = 0;
		
		try {
			bytesOfFieldOut = dis.readInt();
		} catch (IOException e) {
			System.out.println("FATAL ERROR: Failed to read field " + "BytesOfField in Message 'NewOut'");
			this.valid = false;
			return;
		}
		
		outByteArray = new byte[this.bytesOfFieldOut];
		
		try {
			dis.readFully(outByteArray);
		} catch (IOException e) {
			System.out.println("FATAL ERROR: Failed to read field " + "Out");
			this.valid = false;
			return;
		}
		
		this.out = new String(outByteArray);
		
		try {
			bytesOfFieldIn = dis.readInt();
		} catch (IOException e) {
			System.out.println("FATAL ERROR: Failed to read field " + "BytesOfField in Message 'NewOut'");
			this.valid = false;
			return;
		}
		
		inByteArray = new byte[this.bytesOfFieldIn];
		
		try {
			dis.readFully(inByteArray);
		} catch (IOException e) {
			System.out.println("FATAL ERROR: Failed to read field " + "In");
			this.valid = false;
			return;
		}
		
		this.in = new String(inByteArray);
	}
	
	public String getOut() {
		return this.out;
	}
	
	public String getIn() {
		return this.in;
	}
	
	@Override
	public byte[] toByteArray() {
		ByteBuffer buf = ByteBuffer.allocate(FIELD_OPCODE_BYTES + 2*FIELD_BYTES_OF_FIELD_BYTES + bytesOfFieldOut + bytesOfFieldIn);

		// Opcode
		buf.put((byte)this.getOpCode());
		// BytesOfFieldOut
		buf.putInt(this.bytesOfFieldOut);
		// Out
		buf.put(this.outByteArray);
		// BytesOfFieldIn
		buf.putInt(this.bytesOfFieldIn);
		// In
		buf.put(this.inByteArray);

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
