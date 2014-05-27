package protopeer.network.flowbased.accessbottleneck;

import java.util.*;

import org.apache.log4j.*;

import protopeer.network.flowbased.*;
import protopeer.util.quantities.*;

public class OriginalImpl {
	
	private static final Logger logger = Logger.getLogger(OriginalImpl.class);
	
	/**
	 * <code>toFix</code> contains all BWNodes which have to
	 * assign new bandwidth to their connections. It is filled by the
	 * <code>defreeze()</code> function of the BWNodes. The nodes
	 * are sorted in ascending order according to the new fair share
	 * of the unallocated connections ("bwProCapite").
	 * 
	 * The comparator allows that more nodes in the set have the same
	 * bwProCapite. These are ordered according to the time when they 
	 * are created.
	 * 
	 * Be sure not to change the value <code>bwProCapite</code> of a node while it
	 * is in the set. If you do so, the node will remain in the set and it
	 * is not possible to remove it.
	 */
	public static SortedSet<BWNode> toFix = new TreeSet<BWNode>(new Comparator<BWNode>() {
		
		public int compare(BWNode n1, BWNode n2) {
			int comp = Double.compare(n1.getBWProCapite(), n2.getBWProCapite());
			if (comp == 0) {
				comp = Double.compare(n1.getId(), n2.getId());
			}
			return comp;
		}
	});
	
	/**
	 * Helper set to collect the nodes which have to assign a new bandwidth
	 * to their connections. The function <code>defreeze()</code> of a 
	 * <code>BWNode</code> adds all its neighbors which are affected
	 * by the allocation process to this set and moves itself from this
	 * set to the set <code>toFix</code>. As long as <code>tmpToFix</code>
	 * is not empty, <code>defreeze()</code> is called for every <code>BWNode</code> 
	 * in it. In this manner, the all nodes throughout the network are calculated
	 * which have to assign new bandwidth to their connections.
	 */
	public static Set<BWNode> tmpToFix = new LinkedHashSet<BWNode>();

	public static Map<Connection, Bandwidth> newRates = new LinkedHashMap<Connection, Bandwidth>();
	
	/**
	 * Adds a new bandwidth relation to the network
	 * 
	 * @param r the new bandwidth relation
	 */
	public void addBWRel(BWRel r) {

		assert tmpToFix.isEmpty();
		assert toFix.isEmpty();
		assert newRates.isEmpty();

		/*
		 * calls defreeze on the server and the client of the connection
		 * which adds them to toFix set and their neighbors to tmpToFix
		 */
		r.getClient().add(r);
		r.getServer().add(r);

		/*
		 * call management function in order to reallocate
		 * bandwidth across all network nodes 
		 */
		reallocate();

		assert tmpToFix.isEmpty();
		assert toFix.isEmpty();

	}
	
	/**
	 * Removes the bandwidth relation <code>r</code> from the network
	 * @param r
	 */
	public void removeBWRel(BWRel r) {

		assert tmpToFix.isEmpty();
		assert toFix.isEmpty();
		assert newRates.isEmpty();

		/*
		 * calls defreeze on the server and the client of the connection
		 * which adds tmpToFix set with distance 1 (this triggers the correct
		 * behavior as distance-1-neighbors can increase the bandwidth of their
		 * connections)
		 */
		r.getServer().del(r);
		r.getClient().del(r);
		
		reallocate();
		
		assert tmpToFix.isEmpty();
		assert toFix.isEmpty();

	}
	
	private void reallocate() {

		// invoking the defreeze function (by passing the parameter 0) for 
		// every node in the mTmpToFix set
		while (!tmpToFix.isEmpty()) {
			tmpToFix.iterator().next().defreeze(null);
		}


		// setCompCost(mToFix.size());

		// executing the classical algorithm for the max-min fair 
		// bandwidth allocation on the mToFix set nodes
		while (!toFix.isEmpty()) {
			BWNode n = toFix.first();

			if (logger.isDebugEnabled()) {
				StringBuffer sb = new StringBuffer("Freezing peer ");
				sb.append(n.getId());
				sb.append(", bwProCapite=");
				sb.append(n.getBWProCapite());
				logger.debug(sb.toString());
			}
			
			n.freeze();
		}
		
	}

}
