package protopeer.queues;

import java.util.*;

import protopeer.network.*;

/**
 * The basic FIFO queue.
 * 
 * 
 */
public class FIFOQueue extends MessageQueue {

	LinkedList<Message> queue = new LinkedList<Message>();

	@Override
	public Message dequeue() {
		synchronized (queue) {
			return queue.pollLast();
		}
	}

	@Override
	public void enqueue(Message message) {
		synchronized (queue) {
			queue.addFirst(message);
		}
		fireMessageAvailable();
	}

	public int size() {
		synchronized (queue) {
			return queue.size();
		}
	}

	public void dropAll() {
		synchronized (queue) {
			for (Message message : queue) {
				fireMessageDropped(message);
			}
			queue.clear();
		}
	}

}
