package protopeer.queues;

import protopeer.network.*;

/**
 * 
 * Used by the {@link FailureInjectionQueue} to inject message passing failures.
 * 
 */
public interface MessageFailureModel {

	/**
	 * 
	 * Takes the message to be modified as an input and outputs either the
	 * modified message or <code>null</code> to indicate to the
	 * {@link FailureInjectionQueue} that the message should be dropped.
	 * 
	 * @param message
	 * @return
	 */
	public abstract Message replaceMessage(Message message);

}
