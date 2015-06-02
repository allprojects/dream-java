package protopeer.queues;

import java.util.*;

import org.apache.log4j.*;

import protopeer.network.*;

/**
 * Manages a set of queues, one per each message destination, for now uses {@link FIFOQueue}s as the default.
 * 
 * 
 *
 */
public class PerDestinationMessageQueue {

	private final Logger logger = Logger.getLogger(PerDestinationMessageQueue.class);

	private HashMap<NetworkAddress, MessageQueue> destination2queueMap = new HashMap<NetworkAddress, MessageQueue>();

	private LinkedList<PerDestinationMessageQueueListener> listeners = new LinkedList<PerDestinationMessageQueueListener>();

	private void fireMessageAvailable(NetworkAddress destination) {
		for (PerDestinationMessageQueueListener listener : listeners) {
			listener.messageAvailable(destination);
		}
	}

	public void addPerDesitnationMessageQueueListener(PerDestinationMessageQueueListener listener) {
		listeners.add(listener);
	}

	public void removePerDestinationMessageQueueListener(PerDestinationMessageQueueListener listener) {
		listeners.remove(listener);
	}

	private MessageQueue getQueue(NetworkAddress destination, boolean createQueue) {
		synchronized (destination2queueMap) {
			MessageQueue queue = destination2queueMap.get(destination);
			if (queue == null && createQueue) {
				queue = new FIFOQueue();
				destination2queueMap.put(destination, queue);
			}
			return queue;
		}
	}
	
	
	/**
	 * Return the message queue used for a specific destination.
	 * @param destination
	 * @return
	 */
	public MessageQueue getQueue(NetworkAddress destination) {
		return getQueue(destination,false);
	}
	
	/**
	 * Enqueues a message to the queue appriopriate for its destination.
	 * Logs an error and returns without any enqueue if the message destination is null.
	 *
	 * @param message
	 */
	public void enqueue(Message message) {
		NetworkAddress destination = message.getDestinationAddress();
		if (destination==null) {
			logger.error("Message destination address is null.");
		}
		MessageQueue queue = getQueue(destination, true);
		queue.enqueue(message);
		fireMessageAvailable(destination);
	}

	/**
	 * Dequeues a message from the queue corresponding to the specific destination.
	 * @param destination
	 * @return
	 */
	public Message dequeue(NetworkAddress destination) {
		MessageQueue queue = getQueue(destination, false);
		return queue.dequeue();
	}

}
