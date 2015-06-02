package protopeer.scenarios;

import java.util.*;

/**
 * 
 * Represents a scenario, i.e. a set of {@link ScenarioEvent}s, it is the main
 * input for the {@link ScenarioExcutor} peerlet. The set of events is ordered
 * chronologically and the events can be popped from the scenario for execution.
 * 
 * TODO: make this thread-safe.
 * 
 * @author galuba
 * 
 */

public class Scenario {

	private TreeSet<ScenarioEvent> scenarioEvents = new TreeSet<ScenarioEvent>();

	public void addEvent(ScenarioEvent event) {
		scenarioEvents.add(event);
	}

	public SortedSet<ScenarioEvent> getAllEvents() {
		return Collections.unmodifiableSortedSet(scenarioEvents);
	}

	/**
	 * Removes the next event in the chronologia;l
	 * 
	 * @return
	 */
	public ScenarioEvent popNextEvent() {
		ScenarioEvent nextEvent = scenarioEvents.pollFirst();
		return nextEvent;
	}

	/**
	 * Dumps all the events into a <code>StringBuffer</code> in the *.scenario
	 * format.
	 * 
	 * @return
	 */
	public StringBuffer dumpToStringBuffer() {
		StringBuffer buffer = new StringBuffer();
		for (ScenarioEvent event : scenarioEvents) {
			buffer.append(event.toString());
			buffer.append("\n");
		}
		return buffer;
	}

	/**
	 * Merges in the events from the <code>newScenario</code> into this
	 * scenario, only takes the events older than the <code>timeFilter</code>
	 * and matching a specific <code>peerIndexNumberFilter</code>
	 * 
	 * @param newScenario
	 * @param peerIndexNumberFilter
	 * @param timeFilter
	 */
	public void mergeIn(Scenario newScenario, int peerIndexNumberFilter, double timeFilter) {
		for (ScenarioEvent scenarioEvent : newScenario.scenarioEvents) {
			if (scenarioEvent.getExecutionTime() > timeFilter
					&& scenarioEvent.indexNumberMatches(peerIndexNumberFilter)) {
				this.scenarioEvents.add(scenarioEvent);
			}
		}
	}

	public int getNumEvents() {
		return scenarioEvents.size();
	}
}
