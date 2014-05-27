package protopeer.queues;

import java.util.concurrent.*;

import protopeer.network.*;


/**
 * 
 * The superclass for all message queues, maintains a set of {@link MessageQueueListner}s.
 *
 */
public abstract class MessageQueue {

	private CopyOnWriteArrayList<MessageQueueListener> listeners = new CopyOnWriteArrayList<MessageQueueListener>();

	public void addMessageQueueListener(MessageQueueListener listener) {
		listeners.add(listener);
	}

	public void removeListener(MessageQueueListener listener) {
		listeners.remove(listener);
	}

	protected void fireMessageAvailable() {
		for (MessageQueueListener listener : listeners) {
			listener.messageAvailable();
		}
	}
	
	protected void fireMessageDropped(Message message) {
		for (MessageQueueListener listener : listeners) {
			listener.messageDropped(message);
		}
	}
	
	public abstract int size();
	
	public abstract Message dequeue();

	public abstract void enqueue(Message message);
	
	public abstract void dropAll();
}
