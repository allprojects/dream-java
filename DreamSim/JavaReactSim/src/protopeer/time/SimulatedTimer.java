package protopeer.time;

import org.apache.log4j.Logger;

import protopeer.util.quantities.Time;

/**
 * The <code>Timer</code> implementation for the use during the simulation.
 *
 */
public class SimulatedTimer extends Timer {

	private static Logger logger = Logger.getLogger(SimulatedTimer.class);

	private SimulatedTimerEvent currentTimerEvent;

	private EventScheduler scheduler;

	public SimulatedTimer(Clock clock, EventScheduler scheduler) {
		super(clock);
		this.scheduler = scheduler;
	}

	@Override
	public void schedule(double delay) {
		if (isScheduled()) {
			if (logger.isDebugEnabled()) {
				logger.debug("rescheduling a timer before expiration or cancellation");
			}
			cancel();
		}
		currentTimerEvent = new SimulatedTimerEvent(this);
		scheduler.enqueueEventArbitrary(currentTimerEvent, delay);
	}
	
	@Override
	public void schedule(Time delay) {
		if (Double.isInfinite(Time.inSeconds(delay)) || Double.isNaN(Time.inSeconds(delay)) || Time.inSeconds(delay)<0) {
			throw new IllegalArgumentException("illegal delay: " + delay);
		}
		if (isScheduled()) {
			if (logger.isDebugEnabled()) {
				logger.debug("rescheduling a timer before expiration or cancellation");
			}
			cancel();
		}
		currentTimerEvent = new SimulatedTimerEvent(this);
		scheduler.enqueueEvent(currentTimerEvent, delay);
	}

	@Override
	public void cancel() {
		if (currentTimerEvent != null) {
			boolean cancelSuccessful = scheduler.cancelEvent(currentTimerEvent);
			if (!cancelSuccessful) {
				logger.warn("timer not cancelled successfully (it wasn't scheduled?)");
			}
			currentTimerEvent = null;
		}
	}
		
	public void expire() {
		currentTimerEvent = null;
		fireTimerExpired();
	}
	
	@Override
	public boolean isScheduled() {
		return currentTimerEvent!=null;
	}

}
