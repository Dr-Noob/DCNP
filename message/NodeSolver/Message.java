package message.NodeSolver;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public abstract class Message {

	protected static final int FIELD_OPCODE_BYTES = 1;
	protected final int FIELD_BYTES_OF_FIELD_BYTES = 4;
	
	public static final byte OP_INVALID = 0;
	public static final byte OP_NEW_IN = 1;
	public static final byte OP_NEXT_OUT = 2;
	public static final byte OP_BYE = 3;
	
	private static final Byte[] valid_opcodes = {
			OP_NEW_IN,
			OP_NEXT_OUT,
			OP_BYE,
		};
	
	private static final Set<Byte> VALID_OPCODES =
			Collections.unmodifiableSet(new HashSet<Byte>(Arrays.asList(valid_opcodes)));
	
	protected boolean opCodeIsValid(byte opcode) {
		return VALID_OPCODES.contains(opcode);
	}
	
	protected byte opCode;
	protected boolean valid;
	
	/**
	 * Default message constructor
	 */
	//At first, it's set to true and it's the only set to true here. If any error occurs, it will be set to false
	public Message() {
		this.opCode = OP_INVALID;
		this.valid = true;
	}
	
	/**
	 * Overloaded constructor. Warning: It will read and ignore the first byte(opCode)
	 * @param dis DataInputStream to read from
	 */
	public Message(DataInputStream dis) {
		this();
		//We know which opCode is, we just want to remove it from socket
		readOpCode(dis);
	}
	
	/*
	public static Message parseMessage(DataInputStream dis) {
		byte OP = OP_INVALID;
		try {
			OP = dis.readByte();
		} catch (IOException e) {
			System.out.println("FATAL ERROR: Failed to read opCode");
			return null;
		}
		switch (OP) {
			case OP_SOLUTION:
				return new MessageSolution(OP,dis);
			case OP_NEXT_IN:
				return new MessageNextIn(OP,dis);
			default: 
				return null;
		}
	}
	*/
	
	void readOpCode(DataInputStream dis) {
		try {
			this.opCode = dis.readByte();
			this.valid = opCodeIsValid(this.opCode);
		} catch (IOException e) {
			System.out.println("FATAL ERROR: Failed to read opCode in NodeSolver");
			this.valid = false;
			return;
		}
	}
	
	public static boolean sendMessage(Message msg, DataOutputStream dos) {
		try {
			dos.write(msg.toByteArray());
			dos.flush();
			return true;
		} catch (IOException e) {
			System.out.println("FATAL ERROR: Failed to send message " + msg.getOpCodeString());
			return false;
		}
	}
	
	public final String getOpCodeString() {
		switch (opCode) {
		case OP_NEW_IN:
			return "NEW_IN";
		case OP_NEXT_OUT:
			return "NEXT_OUT";
		case OP_BYE:
			return "BYE";
		default:
			return "INVALID_TYPE";
		}
	}
	
	public static synchronized boolean printErrorIfNotValid(Message msg) {
		if(!msg.valid) {
			//TODO: No mostrar el tipo de mensaje que es, solo dejarlo para debug
			System.out.println("Failed to read message " + msg.getOpCodeString() + " in NodeSolver");
			System.out.println("Disconnecting...");
			return false;
		}
		return true;
	}
	
	public byte getOpCode() {
		return this.opCode;
	}
	
	public boolean valid() {
		return this.valid;
	}
	
	/* Abstract methods */
	public abstract String toString();
	public abstract byte[] toByteArray();
}
