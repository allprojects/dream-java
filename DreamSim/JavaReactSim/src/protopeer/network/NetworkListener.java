package protopeer.network;


/**
 * The callback interface for listening to network events.
 * 
 * @author galuba
 * 
 */
public interface NetworkListener {

	/**
	 * Called whenever a network interface receives a message.
	 * 
	 * @param networkInterface
	 *            the network interface that received the message
	 * @param sourceAddress
	 *            the address of the network interface from which the message
	 *            came from
	 * @param message
	 *            the received message
	 */
	public abstract void messageReceived(NetworkInterface networkInterface, NetworkAddress sourceAddress,
			Message message);

	/**
	 * Called when an exception happens during one of the network operations.
	 * 
	 * @param networkInterface the network interface at which the exception happened
	 * @param remoteAddress the remote address that was involved, null if no message involved 
	 * @param message the message that was involved, null if no message involved 
	 * @param cause what actually happened
	 */
	public abstract void exceptionHappened(NetworkInterface networkInterface, NetworkAddress remoteAddress,
			Message message, Throwable cause);

	/**
	 * Called when the message has been sent. Note that this does not indicate
	 * that the message has been delivered to the destination, it only signals
	 * that there were no exceptions on the sender side while sending the message.
	 * 
	 * @param networkInterface the network interface on which the message was sent
	 * @param destinationAddress the destination address to which the message was sent
	 * @param message 
	 */
	public abstract void messageSent(NetworkInterface networkInterface, NetworkAddress destinationAddress, Message message);

	/**
	 * Called after the interface has been completely brought down. 
	 * @param networkInterface
	 */
	public abstract void interfaceDown(NetworkInterface networkInterface);

	/**
	 * Called when the interface is brought up and is ready for sending and receiving messages.
	 * @param networkInterface
	 */
	public abstract void interfaceUp(NetworkInterface networkInterface);
	
}