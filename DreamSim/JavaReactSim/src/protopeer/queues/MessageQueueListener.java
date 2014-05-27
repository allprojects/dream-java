package protopeer.queues;

import protopeer.network.*;

/**
 * Callback interface for all the queue events. The contract is: each message
 * enqueued in the {@link MessageQueue} should be followed at some point with a
 * call to either <code>messageAvailable</code> or
 * <code> messageDropped</code>.
 */
public interface MessageQueueListener {

	/**
	 * Called when the message is available for dequeue. The
	 * {@link MessageQueue} must call this method once for each message
	 * available for dequeue.
	 * 
	 * 
	 */
	public abstract void messageAvailable();

	/**
	 * Called when the queue decides to drop a message.
	 * 
	 * @param message
	 */
	public abstract void messageDropped(Message message);

}
