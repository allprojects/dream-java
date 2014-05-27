package protopeer.time;

import protopeer.*;
import protopeer.util.quantities.Time;

/**
 * 
 * The real-time implemenation of the <code>Clock</code>. Uses the system
 * clock for time measurement, the system clock is adjusted by
 * <code>MainConfiguration.clockOffset</code>.
 * 
 */
public class RealClock extends Clock {

	private java.util.Timer sharedTimer;

	private long offsetNanos;

	private long offsetMillis;

	public RealClock() {

		this.sharedTimer = new java.util.Timer();
		offsetNanos = (long) (MainConfiguration.getSingleton().clockOffset * 1e9);
		offsetMillis = (long) (MainConfiguration.getSingleton().clockOffset * 1e3);
	}

	private long getCorrectedNanoTime() {
		return System.nanoTime() + offsetNanos;
	}

	private long getCorrectedMillisTime() {
		return System.currentTimeMillis() + offsetMillis;
	}

	@Override
	public RealTimer createNewTimer() {
		return new RealTimer(this, sharedTimer);
	}

	@Override
	public double getCurrentTime() {
		// return (double) (getCorrectedNanoTime() / 1e6 - creationTime);
		// return getCorrectedNanoTime() / 1e6 ;
		return getCorrectedMillisTime();
	}

	@Override
	public Time getTime() {
		return Time.inMilliseconds(getCorrectedMillisTime());
	}

}
