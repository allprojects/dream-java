package protopeer.network.flowbased.accessbottleneck;

import java.util.*;

import protopeer.network.flowbased.*;
import protopeer.util.quantities.*;

public class BWNode {

	private static long IDCounter = 0;
	private long id; // for ordering
	
	/**
	 * The corresponding link 
	 * (1:1 mapping between the paper/original C++ impl. and our scenario)
	 */
	private Link link;

	/**
	 * Contains the l.b. connections (unsorted)
	 */
	private Set<BWRel> lbCons;

	/**
	 * Contains the r.b. connections with 
	 * their bandwidth in ascending order
	 * 
	 * Do not change the rate of a connection which resides in this set.
	 * Otherwise it won't be possible to remove the connection from the set.
	 * 
	 */
	private SortedSet<BWRel> rbCons = new TreeSet<BWRel>(new Comparator<BWRel>() {
		public int compare(BWRel r1, BWRel r2) {
			int comp = Double.compare(r1.getRate(), r2.getRate());
			if (comp == 0) {
				comp = Double.compare(r1.getId(), r2.getId());
			} 
			return comp;
		}
	});
	
	/**
	 * Contains the new connection or null
	 */
	private BWRel pendingConnection;
	
	public void setRemovedConnection(BWRel r) {
		removedConnection = r;
	}
	
	private BWRel removedConnection;
	
	/**
	 * The distance of the node to one of the nodes establishing
	 * the new connection or terminating the old one.
	 * 
	 * 4 values are possible:
	 * 
	 * 0: this node establishes or terminates a connection
	 * 1: this node is a direct neighbor of a node establishing
	 *    or terminating a connection or an odd number of hops
	 *    away
	 * 2: this node is an even number (!) of hops away
	 * 3: this node is an odd number > 2 of hops away and so far 
	 *    reached only by nodes with distance 2
	 * 
	 * An node can be a different number of hops away from the
	 * two nodes establishing a connection. If one of them is even,
	 * the distance has to be 2 (cf. paper, Sect. III.B)
	 */
	private int distance;
	
	private double bwProCapite;
	
	private boolean reached;
	private double bwOfFrozenRBCons;
	private int nrOfFrozenRBCons;

	private String description; // for debugging
	


	public BWNode(Link link) {
		this.link = link;
		distance = -1;
		lbCons = new LinkedHashSet<BWRel>();
		reached = false;
		id = IDCounter++;
	}

	/**
	 * Called when a new relation is established
	 * 
	 * @param r new relation
	 */
	public void add(BWRel r) {
		
		assert(r.isFrozen() == false);
		assert(pendingConnection == null);
		
		pendingConnection = r;
		distance = 0;

		// triggering the propagation of the bandwidth allocation 
		defreeze(r.getOtherNode(this));
	}

	/**
	 * Called when an old relation is removed
	 * 
	 * @param r the removed relation 
	 */
	public void del(BWRel r){

		removedConnection = r;
		
		assert lbCons.contains(r) || rbCons.contains(r);
		
		if (lbCons.contains(r)) {
			lbCons.remove(r);
		} else {
			rbCons.remove(r);
		}
		
		// triggering the propagation of the bandwidth allocation 
		// process (for a node erasing a relation, some previously
		// allocated bandwidth is now free, so that the involved 
		// node may only propagate the bandwidth allocation process
		// among the l.b. links. It behaves like a distance-1-node)
		if(rbCons.size() + lbCons.size() > 0) {   
			distance=1;
			getTmpToFixSet().add(this);
		}

	}
	
	private Set<BWNode> getToFixSet() {
		return OriginalImpl.toFix;
	}
	
	private Set<BWNode> getTmpToFixSet() {
		return OriginalImpl.tmpToFix;
	}
	
	/**
	 * Iterates over the BWRels <code>rels</code> originating at this node and
	 * and adds the respective other node to <code>tmpToFix</code>. Furthermore,
	 * it calls <code>defreeze()</code> for all relations.
	 *   
	 * @param rels
	 * @param distance
	 * @param exclude
	 */
	private void addToTmpToFix(Iterator<BWRel> rels, int dist, BWNode exclude) {
		while (rels.hasNext()) {
			BWRel r = rels.next();
			r.defreeze();
			BWNode other = r.getOtherNode(this);

			if (!other.equals(exclude)) {
				other.setDistance(dist);
				getTmpToFixSet().add(other);
			}
			
			if (other.isReached()) {
				other.calculateBWProCapite();
			}
		}
	}
	
	private int getTotalNumberOfConnections() {
		return lbCons.size() + rbCons.size() + ((pendingConnection != null) ? 1 : 0);
	}


	/**                                                         
	 * The initialDefreeze function is invoked 
	 * when the relative node has just           
	 * established a new relation                              
	 */                                                         
	private void initialDefreeze(BWNode altro) {

		/*
		 * Step 1:
		 * 
		 * Insert all l.b. neighbors of this bwnode into the
		 * tmpToFix set, but not the one at the other end of
		 * the new connection ('altro'). Also, set their distance to 1
		 */
		addToTmpToFix(lbCons.iterator(), 1, altro);


		/*
		 * Step 2:
		 * 
		 * Calculate all the r.b. neighbors which currently have a higher
		 * bandwidth than the new fair share
		 */
		
		double allocatedCapacity = 0.0;
		int nrUnallocCons = getTotalNumberOfConnections();

		// only some r.b. links have to be involved
		Iterator<BWRel> rbConsIterator = rbCons.iterator();
		while (rbConsIterator.hasNext()) {
			BWRel r = rbConsIterator.next();
			BWNode other = r.getOtherNode(this);
			if (r.isFrozen() && !other.equals(altro)) {
				
				if (r.getRate() < getFairShare(allocatedCapacity, nrUnallocCons)) {
					allocatedCapacity += r.getRate();
					nrUnallocCons--;
				} else {
					/*
					 * as soon as the comparison gives negative result, the
					 * relative link and all the higher bandwidth links
					 * have to be involved
					 * 
					 * TODO refactoring
					 */
					if (!other.equals(altro)) {
						r.defreeze();
						other.setDistance(1);
						getTmpToFixSet().add(other);
					}

					break;
				}
			}
		}

		addToTmpToFix(rbConsIterator, 1, altro);

	}
	
	
	
	/**
	 * This function is called be <code>defreeze()</code> when 
	 * the <code>distance == 1</code>, i.e., when this node is 
	 * a direct neighbor of one of the two nodes which established/
	 * removed the connection.
	 * 
	 * It adds all neighbors connected through a l.b. frozen link
	 * to the tmpToFix set, sets their distance to 2 and recalculates 
	 * the fair share for the original nodes (<code>distance == 0</code>).
	 * 
	 */
	private void defreeze1HOP() {
		// 1-hop neighbours - they involve only the l.b. frozen links
		Iterator<BWRel> lbConIterator = lbCons.iterator();
		while (lbConIterator.hasNext()) {
			BWRel r = lbConIterator.next();
			if (r.isFrozen()) {
				r.defreeze();
				BWNode other = r.getOtherNode(this);
				
				// TODO comment !
				if (removedConnection != null && removedConnection.getOtherNode(this).equals(other)) {
					getToFixSet().remove(other);
					other.setBWProCapite(other.calculateBWProCapite());
					getToFixSet().add(other);
					continue;
				}
				
				if (other.getDistance() != 0) { 
					other.setDistance(2); // odd distances can be come even but not vice-versa
					getTmpToFixSet().add(other);
				} else {
					/*
					 * this is one of the nodes whichs established the
					 * connection.
					 * 
					 * calucalte the new fair share for it (it has 
					 * more unfrozen connections now)
					 */
					getToFixSet().remove(other);
					other.setBWProCapite(other.calculateBWProCapite());
					getToFixSet().add(other);
				}                          
			}
		}
	}
	
	/**
	 * Called by <code>defreeze()</code> if the <code>
	 * distance == 3</code>, i.e., if this is a 3-hop neighbor.
	 * 
	 * - like defreeze1HOP()
     * - they involve only the l.b. links and 
     * - don't need to check whether the links are frozen or not
     * 
     * Using defreeze1HOP() would provide the same result but has slightly
     * more comparisons.
     * 
	 */
	private void defreeze3HOP() {
		addToTmpToFix(lbCons.iterator(), 2, null);
	}

	/**
	 * 
	 */
	private void defreeze2HOP() {
        // 2-hop neighbours - they involve the l.b. links and some r.b. links

		/*
	     * if the node has already been involved, it has already involved
	     * its l.b. links regardless of its distance
	     */
		if(!isReached()) {
			for (BWRel r : lbCons) {
				if (r.isFrozen()) {
					r.defreeze();
					BWNode other = r.getOtherNode(this);
					
					if (!other.isReached()) {
						getTmpToFixSet().add(other);
						if (other.getDistance() !=2 && other.getDistance() != 1) {
							assert (other.getDistance() == -1 || other.getDistance() == 3);
							other.setDistance(3);
						} 
					} else {
						getToFixSet().remove(other);
						other.setBWProCapite(other.calculateBWProCapite());
						getToFixSet().add(other);
					}
				}
			}
		} else {
			getToFixSet().remove(this);
		}
		
		double allocatedCapacity = 0.0;
		int nrUnallocCons = getTotalNumberOfConnections();

		// only some r.b. links have to be involved
		Iterator<BWRel> rbConsIterator = rbCons.iterator();
		while (rbConsIterator.hasNext()) {
			BWRel r = rbConsIterator.next();
			if (r.isFrozen()) {
				
				if (r.getRate() < getFairShare(allocatedCapacity, nrUnallocCons)) {
					allocatedCapacity += r.getRate();
					nrUnallocCons--;
				} else {
					/*
					 * as soon as the comparison gives negative result, the
					 * relative link and all the higher bandwidth links
					 * have to be involved 
					 * 
					 * TODO refactoring
					 */
		        	r.defreeze();
		        	BWNode other = r.getOtherNode(this);
		        	if (!other.isReached()) {
		        		getTmpToFixSet().add(other);
		        		if (other.getDistance() != 2) {
		        			other.setDistance(1);
		        		}
		        	}

					break;
				}
			}
		}

		/*
		 * Add all remaining neighbors to toTmpToFix 
		 */
        while (rbConsIterator.hasNext()) {
        	BWRel r = rbConsIterator.next();
        	r.defreeze();
        	BWNode other = r.getOtherNode(this);
        	if (!other.isReached()) {
        		getTmpToFixSet().add(other);
        		if (other.getDistance() != 2) {
        			other.setDistance(1);
        		}
        	}
        }

	}
	
	
	/**
	 * This function is called when a new connection is established or an old
	 * one is removed. It does the following tasks:
	 *
	 *  - adds the neighbors which have to be involved in the allocation process
	 *    to <code>tmpToFix</code>. The calculation of them depends on the
	 *    <code>distance</code> of this node.
	 *
	 *  - calculates and sets 
	 *     - <code>bwProCapite</code>: the fair share of the unfrozen connections
	 *     - <code>nrOfFrozenRBCons</code>: the number of frozen r.b. connections 
	 *       (These do not need to be involved in the allocation process)
	 *     - <code>bwOfFrozenRBCons</code>: the cumulative bandwidth of these
	 *       connections
	 *      
	 *  - remove this node from <code>tmpToFix</code> (if present) and adds it 
	 *    to the <code>toFix</code> set according to the fair share of the 
	 *    unfrozen connection
	 *    
	 * @param altro only used if <code>distance == null</code>, 
	 *              the other nod of the new connection
	 */
	public void defreeze(BWNode altro) {
		
		assert (distance >= 0) : "defreeze() invoked for a node with invalid distance";
		
		if (distance == 0) {
			initialDefreeze(altro);
		} else if (distance == 1) {
			defreeze1HOP();
		} else if (distance == 2) {
			defreeze2HOP();
		} else if (distance == 3) {
			defreeze3HOP();
		}

		setBWProCapite(calculateBWProCapite());
		getToFixSet().add(this);
		getTmpToFixSet().remove(this);
		reached = true;
	}
	
	/**
	 * Freezes all connections which are l.b. from now on, removes
	 * this node from <code>toFix</code> and resets the parameters
	 * <code>distance</code>, <code>reached</code>, ... to their 
	 * initial values as preparation for the next run.
	 * 
	 */
	public void freeze() {
        
		assert(getToFixSet().contains(this));
		
		/*
		 * 1) fixing the bandwidth of all the l.b. links
		 */
		for (BWRel r : lbCons) {
            // fixing the bandwidth of the current link
			// updating the mBwProCapite value of the other node
			r.freeze(bwProCapite, this);
		}
        
		/*
		 * 2) fix the bandwidth of the defrozen r.b. connections
		 *    and change them to l.b. connections
		 */
		Set<BWRel> newLBCons = new LinkedHashSet<BWRel>();
        for (BWRel r : rbCons) {
        	if (!r.isFrozen()) {
        		newLBCons.add(r);
        	}
        }
        if (newLBCons.size() > 0) {
        	assert rbCons.containsAll(newLBCons);
            rbCons.removeAll(newLBCons);
        	for (BWRel r : newLBCons) {
        		r.freeze(bwProCapite, this);
        	}
            lbCons.addAll(newLBCons);
        }
        
        
        /*
         * 3) fix the bandwidth of the pending link (if any)
         */
        if (pendingConnection != null) {
        	pendingConnection.freeze(bwProCapite, this);
        	lbCons.add(pendingConnection);
        }
        pendingConnection = null;
        
        /*
         * 4) removing the current node from the mToFix set
         *    and resetting the allocation parameters 
         */
        getToFixSet().remove(this);
        distance = -1;
        reached = false;
        removedConnection = null;
        
        assert(!getToFixSet().contains(this));
        
	}
		

	/**
	 * Called at the end of defreeze().
	 * 
	 * Allocates the bandwidth for all r.b. frozen links and 
	 * divides the remaining capacity by the number of unallocated 
	 * connections
	 * 
	 * @return the fair share bandwidth for unallocated connections
	 */
	private double calculateBWProCapite() {
		
		/*
		 * for performance reasons, calculate these values here 
		 * and save them for the use in freeze() and updateBWProCapite()
		 */
		bwOfFrozenRBCons = 0.0;
		nrOfFrozenRBCons = 0;
		
		for (BWRel r : rbCons) {
			if (r.isFrozen()) {
				bwOfFrozenRBCons += r.getRate();
				nrOfFrozenRBCons++;
			}
		}
		
		int nrUnallocCons = getTotalNumberOfConnections() - nrOfFrozenRBCons;
		double result = getFairShare(bwOfFrozenRBCons, nrUnallocCons);
		assert (result >= 0);
        return result;
	}
	
	private double getFairShare(double allocatedCapacity, int nrUnallocCons) { 
		return (Bandwidth.inBitPerMillisecond(link.getCapacity()) - allocatedCapacity) 
				/ (double) nrUnallocCons;
	}
	
	public void removeCon(BWRel r) {
		
		assert r.equals(pendingConnection) 
				|| lbCons.contains(r)
				|| rbCons.contains(r);

		if (r.equals(pendingConnection)) {
        	pendingConnection = null;
        }
        
        lbCons.remove(r);
        rbCons.remove(r);
	}
	
	public void addRBCon(BWRel r) {

		rbCons.add(r);
//        Collections.sort(rbCons, new BWRelComparator());
		
//		bwOfFrozenRBCons = 0.0;
//		nrOfFrozenRBCons = 0;
//		for (BWRel rel : rbCons) {
//			if (rel.isFrozen()) {
		bwOfFrozenRBCons += r.getRate();
		nrOfFrozenRBCons++;
//			}
//		}

//		assert (bwOfFrozenRBCons <= link.getCapacity()); ??
		assert (nrOfFrozenRBCons <= getTotalNumberOfConnections());


        // modifying consequently the mToFix set
        // searching and erasing the current node from mToFix set
        getToFixSet().remove(this);

        // inserting the current node in the mToFix set only if it has
        // links to set    

        int nrUnallocCons = getTotalNumberOfConnections() - nrOfFrozenRBCons;
        
        if(nrUnallocCons > 0) {
        	// update bwProCapite and re-insert this node into toFix
        	setBWProCapite(getFairShare(bwOfFrozenRBCons, nrUnallocCons));
        	getToFixSet().add(this);
        } else {
        	// the bandwidth allocation process ends and the allocation parameters
            // have to be reset
        	distance = -1;
        	reached = false;
        	removedConnection = null;
        }
	}

	
	/*
	 * Getter and setter ...
	 */
	
	public boolean isReached() {
		return reached;
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}
	
	public double getBWProCapite() {
		return bwProCapite;
	}
	
	public void setBWProCapite(double bwProCapite) {
		assert(bwProCapite > 0);
		this.bwProCapite = bwProCapite;
	}
	
	public Link getLink() {
		return link;
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public long getId() {
		return id;
	}
	
	public String toString() {
		return getDescription();
	}
	
//	/**
//	 * Called when the other node of <code>r</code> is freezed. Since
//	 * <code>freeze()</code> is called in ascending order of 
//	 * <code>bwProCapite</code> the new relation becomes r.b. for this
//	 * node here regardless of its prior states. For the same reason,
//	 * <code>updateBwProCapite</code> is only called on unfreezed nodes.
//	 * 
//	 * function updateBwProCapite - the goal is to upadate the mBwProCapite value
//	 * after that a node relation is bottlenecked by a remote node
//	 * 
//	 * @param r
//	 */
//	public void updateBwProCapite(BWRel r){    
//	            
//		assert IncrementalMaxMinFairShare.toFix.contains(this);
//		assert bwProCapite < Double.POSITIVE_INFINITY;
//		/*
//		 * incrementing the allocated bandwidth
//		 * decrementing the number of links to set 
//		 */
//		
//	      
//        if (r.equals(pendingConnection)) {
//        	pendingConnection = null;
//        } else if (r.equals(removedConnection)) {
//        	removedConnection = null;
////        } else if (lbCons.contains(r)) {
////        	
////        } else if (rbCons.contains(r)) {
////        	
//        }
//        
//        lbCons.remove(r);
//        rbCons.remove(r);
//        rbCons.add(r);
////        Collections.sort(rbCons, new BWRelComparator());
//        
//		bwOfFrozenRBCons = 0.0;
//		nrOfFrozenRBCons = 0;
//		for (BWRel rel : rbCons) {
//			if (rel.isFrozen()) {
//				bwOfFrozenRBCons += rel.getRate();
//				nrOfFrozenRBCons++;
//			}
//		}
//
//		assert (bwOfFrozenRBCons <= link.getCapacity());
//		assert (nrOfFrozenRBCons <= getTotalNumberOfConnections());
//
//
//        // modifying consequently the mToFix set
//        // searching and erasing the current node from mToFix set
//        removeFromToFix(this);
//
//        // inserting the current node in the mToFix set only if it has
//        // links to set    
//
//        int nrUnallocCons = getTotalNumberOfConnections() - nrOfFrozenRBCons;
//        
//        if(nrUnallocCons > 0) {
//        	// update bwProCapite and re-insert this node into toFix
//        	setBWProCapite(getFairShare(bwOfFrozenRBCons, nrUnallocCons));
//        	addToToFix(this);
//        } else {
//        	// the bandwidth allocation process ends and the allocation parameters
//            // have to be reset
//        	distance=-1;
//        	reached=false;
//        }
//	}
//	
}
