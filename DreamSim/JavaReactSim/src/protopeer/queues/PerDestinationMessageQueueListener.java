package protopeer.queues;

import protopeer.network.*;

/**
 * Interface for getting callbacks from the {@link PerDestinationMessageQueue}
 * TODO: add the <code> messageDropped</code> callbacks.
 * @author wojtek
 *
 */
public interface PerDestinationMessageQueueListener {

	public abstract void messageAvailable(NetworkAddress address);
	
}
