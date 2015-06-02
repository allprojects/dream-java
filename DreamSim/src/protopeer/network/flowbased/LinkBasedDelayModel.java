package protopeer.network.flowbased;

import java.util.List;

import protopeer.network.Message;
import protopeer.network.NetworkAddress;
import protopeer.util.quantities.Time;

/**
 * This class calculates the delay between two <code>NetworkAddress</code>es
 * according to the sum of the delays of the <code>Link</code>s which are used
 * for connections between the two <code>NetworkAddress</code>es.
 * 
 */
public class LinkBasedDelayModel implements IDelayModel {

	private Topology topology;
	
	public LinkBasedDelayModel(Topology topology) {
		this.topology = topology; 
	}
	
	@Override
	public Time getDelay(NetworkAddress source, NetworkAddress destination,
			Message message) {
		Time connectionDelay = Time.ZERO;
		List<Link> links = topology.getLinks(source, destination);
		if (links != null) {
			for (Link link : links) {
				connectionDelay = connectionDelay.add(link.getDelay());
			}
		}
		return connectionDelay;
	}

}
