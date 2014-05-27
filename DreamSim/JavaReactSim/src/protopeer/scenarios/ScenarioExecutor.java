package protopeer.scenarios;

import java.util.*;

import org.apache.log4j.*;

import protopeer.*;
import protopeer.time.*;
import protopeer.time.Timer;

/**
 * The peerlet that takes care of executing {@link Scenario}s on the peer. The
 * <code>executionTime</code> of the {@link ScenarioEvent}s contained in the
 * scenarios added to the executor are assumed to be relative to
 * <code>timeZero</code> (set in the constructor).
 * 
 * 
 * 
 */
public class ScenarioExecutor extends BasePeerlet {

	private final static Logger logger = Logger
			.getLogger(ScenarioExecutor.class);

	private Scenario mainScenario = new Scenario();

	private double timeZero;

	private LinkedList<Timer> currentlyUsedTimers = new LinkedList<Timer>();

	private LinkedList<Scenario> scenariosToAddOnInit = new LinkedList<Scenario>();

	private String schedulingLock = "ScenarioExecutor scheduling lock";

	/**
	 * Creates an executor with an empty scenario (no events).
	 * 
	 * @param timeZero
	 *            the time specifying the time zero for all scenarios, all event
	 *            execution times in the scenarios are specified relative to that time
	 */
	public ScenarioExecutor(double timeZero) {
		this.timeZero = timeZero;
	}

	@Override
	public void init(Peer peer) {
		super.init(peer);
		for (Scenario scenario:scenariosToAddOnInit) {
			addScenario(scenario);
		}
		//free the refs to scenarios
		scenariosToAddOnInit=null;
		scheduleEventExecution();		
	}

	private void scheduleEventExecution() {
		synchronized (schedulingLock) {
			final ScenarioEvent poppedEvent = mainScenario.popNextEvent();
			if (poppedEvent != null) {
				Timer timer = getPeer().getClock().createNewTimer();
				timer.addTimerListener(new TimerListener() {
					public void timerExpired(Timer timer) {
						// exceute the scheduled event
						logger.info("executing event: \"" + poppedEvent
								+ "\" @ peer " + getPeer().getIndexNumber()
								+ " @ " + getPeer().getClock().getCurrentTime()
								+ "ms");
						poppedEvent.executeMethodOnPeer(getPeer());
						// check whether there are other timers scheduled
						synchronized (schedulingLock) {
							currentlyUsedTimers.remove(timer);
							if (currentlyUsedTimers.isEmpty()) {
								// execute the next one
								scheduleEventExecution();
							}
						}
					}
				});
				double executionDelay = poppedEvent.getExecutionTime()
						- (getPeer().getClock().getCurrentTime() - timeZero);
				executionDelay = Math.max(executionDelay, 0);
				logger.info("scheduling event: \"" + poppedEvent + "\" in "
						+ executionDelay);
				timer.schedule(executionDelay);
				currentlyUsedTimers.add(timer);
			}
		}
	}

	/**
	 * Takes the events from the <code>newScenario</code> and merges them in to
	 * the currently maintained scenario. Events that are in the past are
	 * ignored.
	 * 
	 * @param newScenario
	 */
	public void addScenario(Scenario newScenario) {
		if (newScenario == null) {
			return;
		}
		if (getPeer() == null) {
			// defer the adding until init
			scenariosToAddOnInit.add(newScenario);
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug(getPeer().getIndexNumber()
						+ " - number of events before adding: "
						+ mainScenario.getNumEvents()
						+ ", number of events in scenario being added: "
						+ newScenario.getNumEvents());
				logger.debug("time from the begining of the experiment: "
						+ (getPeer().getClock().getCurrentTime() - timeZero));
			}
			// add the events only matching only the current peer's index number
			// and only events that are later than the current time
			mainScenario.mergeIn(newScenario, getPeer().getIndexNumber(),
					getPeer().getClock().getCurrentTime() - timeZero);
			if (logger.isDebugEnabled()) {
				logger.debug(getPeer().getIndexNumber()
						+ " - number of events after adding: "
						+ mainScenario.getNumEvents());
			}
			scheduleEventExecution();
		}
	}
}
