package protopeer.measurement;

import java.util.*;

import org.apache.log4j.*;

import protopeer.time.*;
import protopeer.time.Timer;
import protopeer.util.quantities.*;

/**
 * Stopwatch is a tool for measuring time intervals. A stopwatch works just like
 * a real-world stopwatch, you start it by calling <code>markStart</code> then
 * stop it by calling <code>markEnd</code> which returns the time elapsed since
 * the <code>markStart</code> call.
 * 
 * You can associate a "tag" with each measured interval to uniquely identify
 * it. The tag is used when doing markStart and markStop. The tags are compared
 * using their equals() methods.
 */
public class Stopwatch {

	private static final Logger logger = Logger.getLogger(Stopwatch.class);

	private Clock clock;

	private Time maxWait;

	private HashMap<Object, Double> pair2startTimeMap = new HashMap<Object, Double>();

	private class GarbageCollectionTimerListener implements TimerListener {

		private Object tag;

		public GarbageCollectionTimerListener(Object tag) {
			this.tag = tag;
		}

		public void timerExpired(Timer timer) {
			synchronized (pair2startTimeMap) {
				pair2startTimeMap.remove(tag);
			}
		}

	}

	/**
	 * Creates a stopwatch that uses the <code>clock</code>.
	 * 
	 * @param clock
	 * @param maxWait
	 *            the maximum amount of time to wait for <code>markStop</code>
	 *            after <code>markStart</code>, this is just for safety to avoid
	 *            memory leaks when there are markStarts without the
	 *            corresponding markEnds
	 */
	public Stopwatch(Clock clock, Time maxWait) {
		this.clock = clock;
		this.maxWait=maxWait;
	}

	/**
	 * Marks the start of the interval.
	 * 
	 * @param stopwatchTag
	 * @param measuredObject
	 */
	public void markStart(Object tag) {
		synchronized (pair2startTimeMap) {
			pair2startTimeMap.put(tag, clock.getCurrentTime());
			// FIXME it probably makes more sense to run the garbage collection
			// periodically instead of allocating the timer every time
			Timer removalTimer = clock.createNewTimer();
			removalTimer.addTimerListener(new GarbageCollectionTimerListener(tag));
			removalTimer.schedule(Time.inMilliseconds(maxWait));
		}
	}

	/**
	 * Marks the end of the interval and returns the elapsed time since the
	 * corresponding <code>markStart</code> call.
	 * 
	 * @param stopwatchTag
	 * @param measuredObject
	 * @return
	 */
	public double markEnd(Object tag) {
		synchronized (pair2startTimeMap) {
			Double startTime = pair2startTimeMap.get(tag);
			if (startTime == null) {
				logger.warn("markEnd, without markStart on tag " + tag);
				return -1;
			} else {
				pair2startTimeMap.remove(tag);
				return clock.getCurrentTime() - startTime;
			}
		}
	}

}
