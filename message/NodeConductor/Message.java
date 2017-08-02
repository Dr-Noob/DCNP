package message.NodeConductor;

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
	public static final byte OP_ADD_NODE = 1;
	public static final byte OP_PROBLEM_MODULE = 2;
	public static final byte OP_NEW_OUT = 3;
	public static final byte OP_NEW_IN = 4;
	public static final byte OP_HAS_STARTED = 5;
	public static final byte OP_START = 6;
	public static final byte OP_BYE = 7;
	public static final byte OP_NEW_OUT_BYE = 8;
	
	private static final Byte[] valid_opcodes = {
		OP_ADD_NODE,
		OP_PROBLEM_MODULE,
		OP_NEW_OUT,
		OP_NEW_IN,
		OP_HAS_STARTED,
		OP_START,
		OP_BYE,
		OP_NEW_OUT_BYE,
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
	
	public static Message parseMessage(DataInputStream dis) {
		byte OP = OP_INVALID;
		try {
			OP = dis.readByte();
		} catch (IOException e) {
			System.out.println("FATAL ERROR: Failed to read opCode");
			return null;
		}
		switch (OP) {
		
			//case OP_ADD_NODE:
			//	return new MessageAddNode(OP,dis);
			//case OP_PROBLEM_MODULE:
			//	return new MessageProblemModule(OP,dis);
			case OP_NEW_OUT:
				return new MessageNewOut(OP,dis);
			case OP_NEW_OUT_BYE:
				return new MessageNewOutBye(OP,dis);
			case OP_NEW_IN:
				return new MessageNewIn(OP,dis);
			//case OP_HAS_STARTED:
			//	return new MessageHasStarted(OP,dis);
			case OP_START:
				return new MessageStart(OP);
			case OP_BYE:
				return new MessageBye(OP);
			default: 
				System.out.println("FATAL ERROR: Message not recognised. Probably someone it's trying to shoot me down\n Exiting because of security measures");
				return null;
		}
	}
	
	void readOpCode(DataInputStream dis) {
		try {
			this.opCode = dis.readByte();
			this.valid = opCodeIsValid(this.opCode);
		} catch (IOException e) {
			System.out.println("FATAL ERROR: Failed to read opCode in NodeConductor");
			this.valid = false;
			return;
		}
	}
	
	public static boolean sendMessage(Message msg, DataOutputStream dos) {
		try {
			dos.write(msg.toByteArray());
			return true;
		} catch (IOException e) {
			System.out.println("FATAL ERROR: Failed to send message " + msg.getOpCodeString());
			return false;
		}
	}
	
	public final String getOpCodeString() {
		switch (opCode) {
		case OP_ADD_NODE:
			return "ADD_NODE";
		case OP_PROBLEM_MODULE:
			return "PROBLEM_MODULE";
		case OP_NEW_OUT:
			return "NEW_OUT";
		case OP_NEW_IN:
			return "NEW_IN";
		case OP_HAS_STARTED:
			return "HAS_STARTED";
		case OP_START:
			return "START";
		case OP_BYE:
			return "BYE";
		case OP_NEW_OUT_BYE:
			return "NEW_OUT_BYE";
		default:
			return "INVALID_TYPE";
		}
	}
	
	public static synchronized boolean printErrorIfNotValid(Message msg) {
		if(!msg.valid) {
			//TODO: No mostrar el tipo de mensaje que es, solo dejarlo para debug
			System.out.println("Failed to read message " + msg.getOpCodeString() + " in NodeConductor");
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
