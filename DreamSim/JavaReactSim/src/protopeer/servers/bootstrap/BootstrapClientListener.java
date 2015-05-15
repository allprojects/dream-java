package protopeer.servers.bootstrap;

import java.util.*;

import protopeer.*;

/**
 * 
 * Used to report events from the {@link BootstrapClient}
 *
 */
public interface BootstrapClientListener {

	/**
	 * Called when the list of the bootstrap peers is obtained from the {@link BootstrapServer}.
	 * @param bootstrapPeers
	 */
	public abstract void bootstrapCompleted(Collection<Finger> bootstrapPeers);

}
