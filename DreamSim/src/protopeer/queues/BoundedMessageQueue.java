package protopeer.queues;

import protopeer.network.*;

/**
 * 
 * A FIFO queue that can store up to <code>maxMessages</code>, if on
 * <code>enqueue</code> the current number of messages is equal to
 * <code>maxMessages</code>, the equeued message is dropped.
 * 
 */

public class BoundedMessageQueue extends FIFOQueue {

	private int maxMessages;

	public BoundedMessageQueue(int maxMessages) {
		this.maxMessages = maxMessages;
	}

	@Override
	public void enqueue(Message message) {
		boolean added = false;
		synchronized (queue) {
			if (queue.size() < maxMessages) {
				queue.addFirst(message);
				added = true;
			}
		}
		if (added) {
			fireMessageAvailable();
		} else {
			fireMessageDropped(message);
		}
	}

	public int getMaxMessages() {
		return maxMessages;
	}

}
