package protopeer.time;

import java.util.LinkedList;

import protopeer.Experiment;
import protopeer.util.quantities.Time;

/**
 * The <code>Timer</code>s are used to schedule events that should happen
 * asynchronoulsy after some time interval elapses (the timer expires). Each
 * timer keeps a set of <code>TimerListener</code>s which are notified when the
 * timer expires. The <code>Timer</code>s are created by the <code>Clock</code>.
 * 
 */

public abstract class Timer {

	private LinkedList<TimerListener> listeners = new LinkedList<TimerListener>();

	private Clock clock;

	public Timer(Clock clock) {
		this.clock = clock;
	}

	/**
	 * Lets the current thread know that it entered the clock's execution context.
	 * 
	 */
	private void enterExecutionContext() {
		if (Experiment.getSingleton() != null) {
			Experiment.getSingleton().enterExecutionContext(
					clock.getExecutionContext());
		}
	}

	/**
	 * Lets the current thread know that it left the clock's execution context.
	 * 	  
	 */
	private void leaveExecutionContext() {
		if (Experiment.getSingleton() != null) {
			Experiment.getSingleton().leaveExecutionContext();
		}
	}

	/**
	 * Schedules the timer to expire after the <code>delay</code>. If the timer
	 * is currently scheduled it is first cancelled and then rescheduled.
	 * 
	 * @param delay
	 *            the delay in milliseconds after which all the listeners are
	 *            notified that the timer expired
	 */
	@Deprecated
	public abstract void schedule(double delay);
	
	/**
	 * Schedules the timer to expire after the <code>delay</code>. If the timer
	 * is currently scheduled it is first cancelled and then rescheduled.
	 * 
	 * @param delay
	 *            the delay as a <code>Time</code> object after which all the
	 *             listeners are notified that the timer expired
	 */
	public abstract void schedule(Time delay);

	/**
	 * If the timer is currently scheduled to expire, it is cancelled. The timer
	 * can still be used to schedule more callbacks after this.
	 * 
	 */
	public abstract void cancel();

	public abstract boolean isScheduled();

	/**
	 * Adds the listener to the list of listeners that are called when the timer
	 * expires.
	 * 
	 * @param listener
	 *            the listener
	 */
	public void addTimerListener(TimerListener listener) {
		listeners.add(listener);
	}

	/**
	 * Removes the listener from the list of listeners.
	 * 
	 * @param listener
	 */
	public void removeTimerListener(TimerListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Returns the Clock that was used to create this timer.
	 * 
	 * @return
	 */
	public Clock getClock() {
		return clock;
	}

	protected void fireTimerExpired() {
		if (clock.isActive()) {
			enterExecutionContext();
			for (TimerListener listener : listeners) {
				listener.timerExpired(this);
			}
			leaveExecutionContext();
		}
	}

}