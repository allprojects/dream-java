package protopeer.queues;

import java.util.*;

import protopeer.network.*;
import protopeer.time.*;
import protopeer.time.Timer;

/**
 * 
 * Implements the leaky-bucket rate limiter. The bucket can store up to
 * <code>maxMessagesInBucket</code> and will make the messages available for
 * dequeue (leak) at the rate <code>maxMessagesPerSecond</code>. If the
 * <code>maxMessagesInBucket</code> is exceeded (the bucket spills over) the
 * messages are dropped. This queue preserves the FIFO order.
 * 
 */
public class LeakyBucketRateLimiter extends FIFOQueue {

	private double maxMessagesPerSecond;

	private int maxMessagesInBucket;

	private Timer timer;

	private LinkedList<Message> bucket = new LinkedList<Message>();

	/**
	 * Creates a leaky-bucket queue using the <code>clock</code> as the source
	 * of timers and sets the
	 * 
	 * @param clock
	 *            the source of timers
	 * @param maxMessagesPerSecond
	 *            the maximum rate at which the queue makes the messages
	 *            available for dequeuing
	 * @param maxMessagesInBucket
	 *            the capacity of the bucket, if exceeded the messages are
	 *            dropped on <code>enqueue</code>
	 */
	public LeakyBucketRateLimiter(Clock clock, double maxMessagesPerSecond, int maxMessagesInBucket) {
		super();
		this.maxMessagesPerSecond = maxMessagesPerSecond;
		this.maxMessagesInBucket = maxMessagesInBucket;

		this.timer = clock.createNewTimer();
		timer.addTimerListener(new TimerListener() {
			public void timerExpired(Timer timer) {
				timerTick();
			}
		});
	}

	private void scheduleTimer() {
		timer.schedule(1e3 / maxMessagesPerSecond);
	}

	private void timerTick() {
		// assert: there is at least one message in the bucket
		synchronized (queue) {
			queue.addFirst(bucket.pollLast());
			if (bucket.size() > 0) {
				scheduleTimer();
			}
		}
		fireMessageAvailable();
	}

	@Override
	public void enqueue(Message message) {
		boolean added = false;
		synchronized (queue) {
			if (bucket.size() < maxMessagesInBucket) {
				bucket.addFirst(message);
				added = true;
			}

			if (!timer.isScheduled() && bucket.size() > 0) {
				scheduleTimer();
			}
		}

		if (!added) {
			fireMessageDropped(message);
		}
	}

	public int getMaxMessagesInBucket() {
		return maxMessagesInBucket;
	}

	public double getMaxMessagesPerSecond() {
		return maxMessagesPerSecond;
	}

}
