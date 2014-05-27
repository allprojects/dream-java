package protopeer.servers.bootstrap;

import java.util.*;

import protopeer.*;
import protopeer.network.*;


public class InitializePeerMessage extends Message {

	private HashSet<Finger> initialNeighbors;
	
	private int arrivalSequenceNum;

	private InitializePeerMessage() {		
	}

	public InitializePeerMessage(Collection<Finger> initialNeighbors, int arrivalSequenceNum) {
		this.initialNeighbors = new HashSet<Finger>(initialNeighbors);
		this.arrivalSequenceNum=arrivalSequenceNum;		
	}

	public Set<Finger> getInitialNeighbors() {
		return initialNeighbors;
	}

	public InitializePeerMessage clone() {
		InitializePeerMessage twin = (InitializePeerMessage) super.clone();
		twin.initialNeighbors = new HashSet<Finger>();
		for (Finger neighbor : this.initialNeighbors) {
			twin.initialNeighbors.add(neighbor.clone());
		}
		return twin;
	}

	public int getArrivalSequenceNum() {
		return arrivalSequenceNum;
	}

}
