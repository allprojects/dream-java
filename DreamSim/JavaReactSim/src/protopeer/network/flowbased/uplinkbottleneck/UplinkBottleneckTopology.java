package protopeer.network.flowbased.uplinkbottleneck;

import java.util.*;

import org.apache.log4j.*;

import protopeer.network.*;
import protopeer.network.flowbased.*;
import protopeer.util.quantities.*;

/**
 * Very simple <code>Topology</code> where the uplink is the
 * only bottleneck.
 *  
 */
public class UplinkBottleneckTopology extends Topology {

	private static final Logger logger = 
		Logger.getLogger(UplinkBottleneckTopology.class);
	
	/**
	 * Holds the association between source addresses and the
	 * uplinks.
	 */
	private LinkedHashMap<NetworkAddress, Link> sourceToUplinkMap;

	
	public UplinkBottleneckTopology() {
		sourceToUplinkMap = new LinkedHashMap<NetworkAddress, Link>();
	}
	
	/**
	 * Associates the <code>Link uplink</code> with the <code>NetworkAddress
	 * source</code>, i.e., it tells the network model that <code>uplink</code>
	 * has to be used if data is sent from <code>source</code>. 
	 * 
	 * @param source the source address
	 * @param uplink the link
	 */
	public void addUplink(NetworkAddress source, Link uplink) {
		// check if source already has an uplink
		if (sourceToUplinkMap.containsKey(source)) {
			StringBuffer sb = new StringBuffer("Network address ");
			sb.append(source.toString());
			sb.append(" has already an uplink.");
			sb.append(" Cannot specifiy multiple uplinks.");
			logger.error(sb.toString());
		} else {
			// associate network address and link
			sourceToUplinkMap.put(source, uplink);
			// notify upper class
			super.addLink(uplink);
			
			if (logger.isDebugEnabled()) {
				StringBuffer sb = new StringBuffer("Associated network address ");
				sb.append(source.toString());
				sb.append(" with an uplink of ");
				sb.append(Bandwidth.inKBitPerSecond(uplink.getCapacity()));
				sb.append(" kbit/s.");
				logger.debug(sb.toString());
			}
		}
	}
	
	public Link getUplink(NetworkAddress source) {
		return sourceToUplinkMap.get(source);
	}
	
	@Override
	public List<Link> getLinks(NetworkAddress source, NetworkAddress destination) {
		List<Link> links = new ArrayList<Link>(1);
		Link uplinkFromSource = sourceToUplinkMap.get(source);
		if (uplinkFromSource != null) {
			links.add(uplinkFromSource);
		} else {
			StringBuffer message = new StringBuffer("Configuration error: ");
			message.append("No uplink specified for network address ");
			message.append(source.toString());
			logger.error(message.toString());
		}
		return links;
	}
	

}
