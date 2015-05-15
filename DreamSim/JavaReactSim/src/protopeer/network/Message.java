package protopeer.network;

import java.io.*;

import protopeer.util.*;
import protopeer.util.quantities.*;

/**
 * The base class for messages, all messages should be its subclass. All
 * messages are <code>Serializable</code> and <code>Cloneable</code>. Subclasses
 * should override the <code>clone()</code> method to properly deep clone the
 * message. 
 * 
 * Each message stores the source and the destination address, both of these are
 * set to appropriate values when the message is send or received.
 * 
 * The message also stores the <code>messageSize</code> which is the actual size
 * of the message after serialization. This field is only updated when
 * serialization is enabled in ProtoPeer.
 * 
 * 
 */
public class Message implements Serializable, Cloneable {

	protected NetworkAddress sourceAddress;

	transient private NetworkAddress destinationAddress;

	transient private Data messageSize;

	/**
	 * This property of the Message indicates whether it should be sent over a
	 * datagram connection (e.g. UDP) or a socket-based connection (e.g. TCP).
	 * The <code>NetworkInterface</code> implementation is expected to call
	 * this function every time before sending the message. Returns false by
	 * default.
	 * 
	 */
	public boolean isDatagram() {
		return false;
	}

	/**
	 * Returns the source address. The source address is set to its appropriate
	 * value only when the source is known, which happens when the
	 * <code>Peer.sendMessage</code> is called.
	 * 
	 * @return
	 */
	public NetworkAddress getSourceAddress() {
		return sourceAddress;
	}

	public void setSourceAddress(NetworkAddress source) {
		this.sourceAddress = source;
	}

	public Message clone() {
		try {
			Message twin = (Message) super.clone();
			if (this.sourceAddress != null) {
				twin.sourceAddress = this.sourceAddress.clone();
			}
			return twin;
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

	/**
	 * Returns the destination address. The destination address is set to its
	 * appropriate value only when the destination is known, which happens when
	 * the <code>Peer.sendMessage</code> is called.
	 * 
	 * @return
	 */
	public NetworkAddress getDestinationAddress() {
		return destinationAddress;
	}

	public void setDestinationAddress(NetworkAddress destinationAddress) {
		this.destinationAddress = destinationAddress;
	}

	/**
	 * Returns the size of the message in bytes after serialization. This value
	 * is only available when serialization is enabled in ProtoPeer.
	 * 
	 * This method is deprecated. Use instead <code>getSize()</code>.
	 * 
	 * @return the size of the message in byte.
	 */
	@Deprecated
	public long getMessageSize() {
		if (messageSize == null) {
			return 0;
		} else {
			return Math.round(Data.inByte(messageSize));
		}
	}

	/**
	 * Sets the size of the message in byte.
	 * 
	 * This method is deprecated. Switch to use
	 * <code>setSize(Data messageSize)</code>.
	 * 
	 * @param messageSize
	 *            in byte
	 */
	@Deprecated
	public void setMessageSize(long messageSize) {
		setSize(Data.inByte(messageSize));
	}

	/**
	 * Sets the size of the message to <code>messageSize</code>.
	 * 
	 * @param messageSize
	 *            the size of the message
	 */
	public void setSize(Data messageSize) {
		this.messageSize = messageSize;
	}

	/**
	 * Returns the size of the message.
	 * 
	 * @return the size of the message
	 */
	public Data getSize() {
		if (messageSize==null) {
			return Data.inByte(getMessageSize());
		}
		return messageSize;
	}

	protected void baseMessageDeserialize(LightweightObjectInputStream in) throws IOException {
		this.sourceAddress = (NetworkAddress) in.readObject();
	}

	protected void baseMessageSerialize(LightweightObjectOutputStream out) throws IOException {
		out.writeObject(this.sourceAddress);
	}
	
	@Override
	public String toString() {
		StringBuffer buffer=new StringBuffer();
		buffer.append(this.getClass().getCanonicalName());
		buffer.append("(src=");
		buffer.append(sourceAddress);
		buffer.append(")");
		return buffer.toString();
	}

}
