package protopeer.time;

import java.util.*;

import org.apache.log4j.*;

import protopeer.util.*;
import protopeer.util.quantities.*;
import cern.jet.random.*;


public class EventScheduler {

	public static final Logger logger = Logger.getLogger(EventScheduler.class);

	public static final double DEFAULT_DELAY = 100.0;

	public static final double DEFAULT_RATE = 1e-2;

	//private PriorityBuffer events = new PriorityBuffer(true);
	private TreeSet<Event> events = new TreeSet<Event>();

	/**
	 * The current time
	 */
	private Time currentTime = Time.ZERO;
	
	private int numEventsProcessed=0;

	private Poisson distributionPoisson = new Poisson(DEFAULT_DELAY, RandomnessSource.getGeneralRandomEngine());

	private Exponential distributionExp = new Exponential(DEFAULT_RATE, RandomnessSource.getGeneralRandomEngine());

	public void reset() {
		events.clear();
		currentTime = Time.ZERO;
	}

	public void enqueueEventPoisson(Event event, double delayMultiplier) {
		Time delay = Time.inMilliseconds(distributionPoisson.nextDouble() * delayMultiplier);
		enqueueEvent(event, delay);
	}

	public void enqueueEventExp(Event event, double rateMultiplier) {
		distributionExp = new Exponential(DEFAULT_RATE * rateMultiplier, RandomnessSource.getGeneralRandomEngine());
		Time delay = Time.inMilliseconds(distributionExp.nextDouble());
		enqueueEvent(event, delay);
	}

	/**
	 * This method is deprecated. Use <code>enqueueEvent()</code> instead.
	 * 
	 * Schedules the event <code>event</code> at the time
	 * <code>currentTime + delay</code>.
	 * 
	 * @param event the event to schedule
	 * @param delay the delay from <code>currentTime</code> to the
	 *              execution time of the event in milliseconds. 
	 */
	@Deprecated
	public void enqueueEventArbitrary(Event event, double delay) {
		Time tdelay = Time.inMilliseconds(delay);
		enqueueEvent(event, tdelay);
	}

	/**
	 * Schedules the event <code>event</code> at the time
	 * <code>currentTime + delay</code>.
	 * 
	 * @param event the event to schedule
	 * @param delay the delay from <code>currentTime</code> to the
	 *              execution time of the event. 
	 */
	public void enqueueEvent(Event event, Time delay) {
		event.setExecTime(currentTime.add(delay));
		
		if (logger.isDebugEnabled())
			logger.debug("Scheduling an event: " + event);
		
		if (Double.isNaN(Time.inSeconds(event.getExecTime()))) {
			StringBuffer sb = new StringBuffer(
					"Error inserting event into event queue: ");
			sb.append("Execution time must not be \"NaN\".");
			logger.error(sb.toString());
		} else {
			int oldSize = events.size();
			
			events.add(event);
			
			if (events.size() != oldSize + 1) {
				StringBuffer sb = new StringBuffer(
						"Error inserting event into event queue: ");
				sb.append("New event eliminated another event in the queue.");
				logger.error(sb.toString());
			}	
		}
	}
	

	public void executeNextEvent() {		
		Event event = (Event) events.first();
		boolean result=events.remove(event);
		if (!result) {
			logger.error("event removal failure!");
		}
		currentTime = event.getExecTime();		
		event.execute();
	}

	public boolean cancelEvent(Event event) {
		return events.remove(event);
	}

	/**
	 * Runs the experiment for <code>timeToRun</code> time.
	 * 
	 * @param timeToRun the time the experiment should run
	 */
	public void run(Time timeToRun) {
		Time endTime = currentTime.add(timeToRun);
		while (!events.isEmpty() && currentTime.isLowerThan(endTime)) {
			executeNextEvent();
			numEventsProcessed++;
		}
	}

	/**
	 * Deprecated. Use <code>run(Time timeToRun)</code> instead.
	 * 
	 * @param timeToRun the time the experiment should run
	 */
	@Deprecated
	public void run(double timeToRun) {
		run(Time.inMilliseconds(timeToRun));
	}

	/**
	 * Deprecated. Use <code>now()</code> instead.
	 * Returns the current time specified in milliseconds
	 * 
	 * @return the current time in milliseconds
	 */
	@Deprecated
	public double getCurrentTime() {
		return Time.inMilliseconds(currentTime);
	}
	
	public Time now() {
		return currentTime;
	}

	public int getNumEvents() {
		return events.size();
	}

	public int getNumEventsProcessed() {
		return numEventsProcessed;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		Iterator<Event> it = events.iterator();
		while (it.hasNext()) {
			sb.append(it.next());
			sb.append(",");
		}
		return sb.toString();
	}

}
