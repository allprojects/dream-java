package protopeer.time;

import protopeer.*;
import protopeer.measurement.*;
import protopeer.util.quantities.Time;

/**
 * 
 * The <code> Clock </code> creates and manages <code>Timer</code>s. All time is
 * measured as a <code>double</code> and the unit is milliseconds.
 */

public abstract class Clock {

	public static MeasurementLogger measurementLogger;

	private ExecutionContext executionContext;

	private boolean active = true;

	/**
	 * Creates a new <code>Timer</code>
	 * 
	 * @return
	 */
	public abstract Timer createNewTimer();

	/**
	 * 
	 * @return the current time in milliseconds since the Great Beginning. The
	 *         Great Beginning is an event in the past which happened before any
	 *         other events. i.e. this function should always return a positive
	 *         number The Author of the <code> Clock </code> implementation is
	 *         free to choose the Great Beginning to be any event that suits the
	 *         author's convictions, beliefs or world view ;)
	 */
	public abstract double getCurrentTime();
	
	
	/**
	 * 
	 * @return the current time as a Quantity since the Great Beginning. The
	 *         Great Beginning is an event in the past which happened before any
	 *         other events. i.e. this function should always return a positive
	 *         number The Author of the <code> Clock </code> implementation is
	 *         free to choose the Great Beginning to be any event that suits the
	 *         author's convictions, beliefs or world view ;)
	 */
	public abstract Time getTime();

	/**
	 * Deactivates the clock, existing timers will not fire on expiry, no new
	 * timers can be created.
	 */
	public void deactivate() {
		active = false;
	}

	/**
	 * The clock is active until <code>deactivate()</code> is called on it.
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * Returns the execution context that is hosting this network interface.
	 * 
	 * @return
	 */
	public ExecutionContext getExecutionContext() {
		return executionContext;
	}

	/**
	 * Lets the clock know which execution context it is a part of.
	 * 
	 * @param hostPeer
	 */
	public void setExecutionContext(ExecutionContext executionContext) {
		this.executionContext = executionContext;
	}
}
