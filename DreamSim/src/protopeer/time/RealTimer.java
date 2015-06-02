package protopeer.time;

import org.apache.log4j.Logger;

import protopeer.MainConfiguration;
import protopeer.util.quantities.Time;

/**
 * 
 * A <code>Timer</code> implementation using the real time.
 * 
 */
public class RealTimer extends Timer {

	private final Logger logger = Logger.getLogger(RealTimer.class);

	private java.util.Timer sharedTimer;

	private java.util.Timer ownTimer;

	private TimerTask timerTask = new TimerTask(this);

	private boolean scheduled = false;

	public RealTimer(Clock clock, java.util.Timer sharedTimer) {
		super(clock);
		this.sharedTimer = sharedTimer;
		if (MainConfiguration.getSingleton().multiThreadedTimers) {
			this.ownTimer = new java.util.Timer();
		}
	}

	@Override
	public synchronized void schedule(double delay) {
		cancel();
		timerTask = new TimerTask(this);
		if (MainConfiguration.getSingleton().multiThreadedTimers) {
			ownTimer.schedule(timerTask, (long) delay);
		} else {
			sharedTimer.schedule(timerTask, (long) delay);
		}
		scheduled = true;
	}
	
	@Override
	public synchronized void schedule(Time delay) {
		cancel();
		timerTask = new TimerTask(this);
		if (MainConfiguration.getSingleton().multiThreadedTimers) {
			ownTimer.schedule(timerTask, (long) Time.inMilliseconds(delay));
		} else {
			sharedTimer.schedule(timerTask, (long) Time.inMilliseconds(delay));
		}
		scheduled = true;
	}

	@Override
	public synchronized void cancel() {
		try {
			timerTask.cancel();
			scheduled = false;
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	@Override
	public synchronized boolean isScheduled() {
		return scheduled;
	}

}
