package protopeer.servers.bootstrap;

import java.util.*;

import org.apache.log4j.*;

import protopeer.*;
import protopeer.network.*;
import protopeer.util.*;
import cern.jet.random.engine.*;

public class RingTopologyGenerator implements TopologyGenerator {

	private static final Logger logger = Logger.getLogger(RingTopologyGenerator.class);

	private Peer peer;

	public RingTopologyGenerator(Peer peer) {
		this.peer = peer;
	}

	public PeerIdentifier generateInitialIdentifier(NetworkAddress address) {
		// FIXME: just a quick hack for manets single destination sending
		return new RingIdentifier(((IntegerNetworkAddress) address).toLongValue() / 1000.0);
		// return new RingIdentifier(RandomnessSource.getNextTopologyDouble());
	}

	public Set<Finger> getInitialNeighbors(Collection<Finger> knownFingers, PeerIdentifier peerIdentifier, boolean coreNode) {
		if (coreNode) {
			// return getRunningFingers();
			// return getBootstrapNodes(peerIdentifier);
			// return getBootstrapNodesUniform(peerIdentifier);
			return getBootstrapNodesHopSpaceExponential(knownFingers, peerIdentifier);
		} else {
			// return getBootstrapNodes(peerIdentifier);
			// return getBootstrapNodesUniform(peerIdentifier);
			return getBootstrapNodesHopSpaceExponential(knownFingers, peerIdentifier);
			// /return getBootstrapNodesRandom();
		}
	}

	public int getMinimalNumberOfPeers() {
		return MainConfiguration.getSingleton().initialCoreNodes;
	}

	private Peer getPeer() {
		return peer;
	}

	class FingerComparator implements Comparator<Finger> {

		private PeerIdentifier nodeID;

		FingerComparator(PeerIdentifier nodeID) {
			this.nodeID = nodeID;
		}

		public int compare(Finger f1, Finger f2) {
			// double distance1=nodeID.distanceTo(f1.getIdentifier());
			// double distance2=nodeID.distanceTo(f2.getIdentifier());
			//			
			// if (distance1<distance2)
			// {
			// return -1;}
			// else if (distance1>distance2){
			// return 1;}

			double pos1 = ((RingIdentifier) f1.getIdentifier()).getPosition();
			double pos2 = ((RingIdentifier) f2.getIdentifier()).getPosition();
			if (pos1 < pos2) {
				return -1;
			} else if (pos1 > pos2) {
				return 1;
			}

			return 0;
		}

	}

	protected Set<Finger> getBootstrapNodesHopSpaceExponential(Collection<Finger> knownFingers, PeerIdentifier nodeID) {
		// int numBootstrapNodes =
		// Configuration.getSingleton().initialNodeDegree;
		HashSet<Finger> bootstrapNodes = new HashSet<Finger>();
		Vector<Finger> candidateFingers = new Vector<Finger>(knownFingers);
		// create a fake finger for the current node and insert it
		// FIXME: this is ugly
		// Finger fakeFinger = new Finger(null, nodeID);
		// if (!candidateFingers.contains(fakeFinger)) {
		// candidateFingers.add(fakeFinger);
		// }

		Collections.sort(candidateFingers, new FingerComparator(nodeID));
		int numFingers = candidateFingers.size();
		int centralIndex = -1;
		for (int i = 0; i < numFingers; i++) {
			if (candidateFingers.elementAt(i).getIdentifier().equals(nodeID)) {
				centralIndex = i;
				break;
			}
		}

		if (centralIndex == -1) {
			logger.error("no central index");
			return null;
		}

		int jump = 1;
		while (jump < (numFingers - 1) / 4) {
			int leftIndex = (centralIndex - jump + 100 * numFingers) % numFingers;
			int rightIndex = (centralIndex + jump) % numFingers;
			//boolean linkBothSides = jump<8;
			boolean linkBothSides=true;
			if (linkBothSides) {
				if (leftIndex != centralIndex) {
					if (candidateFingers.elementAt(leftIndex).getNetworkAddress() == null) {
						logger.warn("null network address");
					}
					bootstrapNodes.add(candidateFingers.elementAt(leftIndex));
				}
				if (rightIndex != centralIndex) {
					if (candidateFingers.elementAt(rightIndex).getNetworkAddress() == null) {
						logger.warn("null network address");
					}
					bootstrapNodes.add(candidateFingers.elementAt(rightIndex));
				}
			} else {
				if (RandomnessSource.getNextTopologyDouble() < 0.5) {
					if (leftIndex != centralIndex) {
						if (candidateFingers.elementAt(leftIndex).getNetworkAddress() == null) {
							logger.warn("null network address");
						}
						bootstrapNodes.add(candidateFingers.elementAt(leftIndex));
					}
				} else {
					if (rightIndex != centralIndex) {
						if (candidateFingers.elementAt(rightIndex).getNetworkAddress() == null) {
							logger.warn("null network address");
						}
						bootstrapNodes.add(candidateFingers.elementAt(rightIndex));
					}
				}
			}
			jump *= 2.25;
		}

		return bootstrapNodes;
	}

	protected Set<Finger> getBootstrapNodesUniform(Collection<Finger> knownFingers, PeerIdentifier identifier) {
		int numBootstrapNodes = MainConfiguration.getSingleton().initialNodeDegree;
		HashSet<Finger> bootstrapNodes = new HashSet<Finger>();
		Vector<Finger> candidateFingerVector = new Vector<Finger>(knownFingers);

		while (bootstrapNodes.size() < numBootstrapNodes) {
			int candidateIndex = (int) (candidateFingerVector.size() * RandomnessSource.getNextTopologyDouble());
			Finger chosenFinger = candidateFingerVector.elementAt(candidateIndex);
			if (!chosenFinger.getIdentifier().equals(identifier)) {
				bootstrapNodes.add(chosenFinger);
			}
		}
		return bootstrapNodes;
	}

	protected Set<Finger> getBootstrapNodesExpDecrease(Collection<Finger> knownFingers, PeerIdentifier identifier,
			double initialJumpDistance, double distanceShorteningFactor, int numIterations) {
		HashSet<Finger> bootstrapNodes = new HashSet<Finger>();

		double jump = initialJumpDistance;
		for (int i = 0; /* bootstrapNodes.size() < numBootstrapNodes && */i < numIterations; i++) {
			Finger selectedFinger = null;
			double bestDistance = Double.MAX_VALUE;
			for (Finger finger : knownFingers) {
				if (finger.getIdentifier().equals(identifier)) {
					continue;
				}
				double distance = Math.abs(jump - identifier.distanceTo(finger.getIdentifier()));
				if (distance < bestDistance) {
					selectedFinger = finger;
					bestDistance = distance;
				}
			}
			bootstrapNodes.add(selectedFinger);
			jump /= distanceShorteningFactor;
		}
		return bootstrapNodes;
	}

	private Finger findClosestFinger(Collection<Finger> fingers, PeerIdentifier desiredID,
			PeerIdentifier excludedIdentifier) {
		double minDistance = Double.MAX_VALUE;
		Finger bestFinger = null;
		for (Finger finger : fingers) {
			if (finger.getIdentifier().equals(excludedIdentifier)) {
				continue;
			}
			double distance = finger.getIdentifier().distanceTo(desiredID);
			if (distance < minDistance) {
				minDistance = distance;
				bestFinger = finger;
			}
		}
		return bestFinger;
	}

//	protected Set<Finger> getBootstrapNodesKleinbergian(Collection<Finger> knownFingers, RingIdentifier identifier, int peerDegree) {
//		PowerLawDistribution plaw = new PowerLawDistribution(0.05 /knownFingers.size(), -2, 1,
//				RandomnessSourceType.TOPOLOGY);
//		HashSet<Finger> bootstrapNodes = new HashSet<Finger>();
//		while (bootstrapNodes.size() < peerDegree) {
//			double position = identifier.getPosition();
//			double distance = 1;
//			while (distance > 0.5 || distance < -0.5) {
//				distance = plaw.getNextDouble() * (RandomnessSource.getNextTopologyDouble() < 0.5 ? -1 : 1);
//			}
//			double desiredPosition = position + distance;
//			desiredPosition = desiredPosition - Math.floor(desiredPosition);
//			Finger closestFinger = findClosestFinger(knownFingers, new RingIdentifier(desiredPosition), identifier);
//			bootstrapNodes.add(closestFinger);
//		}
//		return bootstrapNodes;
//	}

	private Set<Finger> getBootstrapNodesRandom(Collection<Finger> knownFingers) {
		Vector<Finger> fingers = new Vector<Finger>(knownFingers);
		int neighborsToReturn = Math.min(MainConfiguration.getSingleton().initialNodeDegree, fingers.size() - 1);
		HashSet<Finger> bootstrapNodes = new HashSet<Finger>();
		while (bootstrapNodes.size() < neighborsToReturn) {
			bootstrapNodes.add(fingers.elementAt((int) (RandomnessSource.getNextTopologyDouble() * fingers.size())));
		}
		return bootstrapNodes;
	}

	private static double myNextPowLaw(double cut, double alpha, RandomEngine randomGenerator) {
		return cut * Math.pow(randomGenerator.raw(), 1.0 / (alpha + 1.0));
	}

//	public static void main(String[] args) {
//		PowerLawDistribution plaw = new PowerLawDistribution(0.5 / 100, -1.5, 1, RandomnessSourceType.TOPOLOGY);
//		for (int i = 0; i < 100000; i++) {
//			// double distance = plaw.getNextDouble() * (Math.random() < 0.5 ?
//			// -1.0 : 1.0);
//			double distance = plaw.getNextDouble();
//			System.out.println(distance);
//		}
//
//		// RandomEngine engine = new MersenneTwister();
//		// for (int i = 0; i < 1000; i++) {
//		// //double distance = Distributions.nextPowLaw(0.001, 3, engine);
//		// double distance = myNextPowLaw(0.001, -2, engine);
//		// System.out.println(distance);
//		// }
//
//	}
}
