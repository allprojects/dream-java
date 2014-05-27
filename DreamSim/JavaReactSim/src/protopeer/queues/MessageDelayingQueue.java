package protopeer.queues;

import protopeer.network.*;
import protopeer.time.*;
import protopeer.util.*;

/**
 * 
 * This queue delays each message by a uniformly randomly selected delay from
 * between <code>lowerDelayBound</code> and <code>upperDelayBound</code>. After the delay the message is made available for dequeue.  
 * Because of different delays applied to different messages, this queue does _not_ preserve the FIFO order.
 * 
 */
public class MessageDelayingQueue extends FIFOQueue {

	private Clock clock;

	private double lowerDelayBound;

	private double upperDelayBound;

	class DelayedEnqueueTimerListener implements TimerListener {

		private Message message;

		public DelayedEnqueueTimerListener(Message message) {
			this.message = message;
		}

		public void timerExpired(Timer timer) {
			synchronized (queue) {
				queue.addFirst(message);
			}
			fireMessageAvailable();
		}

	}

	@Override
	public void enqueue(Message message) {
		Timer timer = clock.createNewTimer();
		timer.addTimerListener(new DelayedEnqueueTimerListener(message));
		timer.schedule(RandomnessSource.getNextGeneralDouble() * (upperDelayBound - lowerDelayBound) + lowerDelayBound);
	}

	public double getLowerDelayBound() {
		return lowerDelayBound;
	}

	public double getUpperDelayBound() {
		return upperDelayBound;
	}

	public MessageDelayingQueue(Clock clock, double lowerDelayBound, double upperDelayBound) {
		super();
		this.clock = clock;
		this.lowerDelayBound = lowerDelayBound;
		this.upperDelayBound = upperDelayBound;
	}

}
