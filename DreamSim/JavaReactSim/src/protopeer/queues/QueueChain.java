package protopeer.queues;

import java.util.*;

import protopeer.network.*;

/**
 * This queue chains several queues together and is visible as a single queue.
 * When a message is made available for dequeue at some queue in the chain it is
 * immediately dequed and enqued in the next queue in the chain. If the queue is
 * the last one in the chain and a message is made available then the
 * <code>QueueChain</code> calls
 * <code>messageAvailable</<code> on all of its listeners.
 * When enqueue is called on <code>QueueChain</code> the message is enqueued on the first queue in the chain.
 * 
 *
 */
public class QueueChain extends MessageQueue {

	private InternalQueueListener tailListener;

	private LinkedList<MessageQueue> queues = new LinkedList<MessageQueue>();

	private class InternalQueueListener implements MessageQueueListener {

		private MessageQueue nextQueue;

		private MessageQueue queue;

		public InternalQueueListener(MessageQueue queue) {
			this.queue = queue;
		}

		public void messageAvailable() {
			if (nextQueue == null) {
				// reached the end of the chain fire the event on the
				// QueueChain's listener
				fireMessageAvailable();
			} else {
				// pick the message from the current queue and pass it on to the
				// nextQueue in the chain of queues
				Message message = queue.dequeue();
				if (message != null) {
					nextQueue.enqueue(message);
				}
			}
		}

		public void messageDropped(Message message) {
			fireMessageDropped(message);
		}

		public void setNextQueue(MessageQueue nextQueue) {
			this.nextQueue = nextQueue;
		}
	}

	public QueueChain() {
	}

	/**
	 * Appends a queue at the end of the queue chain.
	 * @param queue
	 */
	public void appendQueue(MessageQueue queue) {
		synchronized (queues) {
			queues.addLast(queue);

			if (tailListener != null) {
				tailListener.setNextQueue(queue);
			}
			tailListener = new InternalQueueListener(queue);
			queue.addMessageQueueListener(tailListener);
		}
	}

	@Override
	public Message dequeue() {
		synchronized (queues) {
			return queues.getLast().dequeue();
		}
	}

	@Override
	public void enqueue(Message message) {
		synchronized (queues) {
			queues.getFirst().enqueue(message);
		}
	}

	@Override
	public int size() {
		int size = 0;
		synchronized (queues) {
			for (MessageQueue queue : queues) {
				size += queue.size();
			}
		}
		return size;
	}

	public void dropAll() {
		synchronized (queues) {
			for (MessageQueue queue : queues) {
				queue.dropAll();
			}
		}
	}

}
