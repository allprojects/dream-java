package protopeer.time;

import protopeer.util.quantities.Time;

/**
 * 
 * The implementation of a simulated <code>Clock</code>. It uses the
 * <code>EventScheduler</code> for scheduling the timer events and creates
 * <code>SimulatedTimer</code>.
 */
public class SimulatedClock extends Clock {

	private EventScheduler eventScheduler;

	public SimulatedClock(EventScheduler eventscheduler) {
		this.eventScheduler = eventscheduler;
	}

	@Override
	public SimulatedTimer createNewTimer() {
		return new SimulatedTimer(this, eventScheduler);
	}

	@Override
	public double getCurrentTime() {
		return eventScheduler.getCurrentTime();
	}

	public EventScheduler getEventScheduler() {
		return eventScheduler;
	}

	@Override
	public Time getTime() {
		return eventScheduler.now();
	}
	
	

}
