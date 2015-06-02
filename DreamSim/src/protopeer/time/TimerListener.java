package protopeer.time;

/**
 * 
 * The interface for receiving the timer expiration callbacks.
 */
public interface TimerListener {
		
	/**
	 * Called whenever the <code>timer</code> expires.
	 * @param timer the <code>Timer</code> that has expired
	 */
	public abstract void timerExpired(Timer timer);
	
}
