package dream.overlay;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import dream.experiments.DreamConfiguration;
import protopeer.util.RandomnessSource;
import protopeer.util.RandomnessSourceType;

public class TreeOverlayGenerator implements IOverlayGenerator {
	public static final int LINEAR = 1;
	public static final int STAR = 2;
	public static final int SCALEFREE = 3;

	private final int type;

	private static TreeOverlayGenerator instance = null;
	private final Set<Link> links = new HashSet<>();
	private boolean linksGenerated = false;

	public static final IOverlayGenerator get() {
		if (instance == null) {
			instance = new TreeOverlayGenerator();
		}
		return instance;
	}

	private TreeOverlayGenerator() {
		this.type = DreamConfiguration.get().brokersTopologyType;
		switch (type) {
		case LINEAR:
		case STAR:
		case SCALEFREE:
			break;
		default:
			throw new IllegalArgumentException("Unknown Tree Topology Type");
		}
	}

	@Override
	public void clean() {
		linksGenerated = false;
		links.clear();
	}

	@Override
	public Set<Link> generateOverlay() {
		final DreamConfiguration conf = DreamConfiguration.get();
		final int brokersMin = 1;
		final int brokersMax = conf.numberOfBrokers;
		final Random random = RandomnessSource.getRandom(RandomnessSourceType.TOPOLOGY);

		if (!linksGenerated) {
			linksGenerated = true;
			final Set<Node> nodes = IntStream.rangeClosed(brokersMin, brokersMax)//
			    .mapToObj(Node::new)//
			    .collect(Collectors.toSet());

			switch (type) {
			case LINEAR:
				generateLinearOverlay(nodes, random);
				break;
			case STAR:
				generateStarOverlay(nodes, random);
				break;
			case SCALEFREE:
				generateScaleFreeOverlay(nodes, random);
				break;
			}
		}
		return links;
	}

	private void generateLinearOverlay(Set<Node> nodes, Random random) {
		if (nodes.size() < 2) {
			return;
		}
		final List<Node> nodesList = new ArrayList<Node>(nodes);
		for (int i = 1; i < nodesList.size(); i++) {
			links.add(new Link(nodesList.get(i - 1), nodesList.get(i), DreamConfiguration.get().numHopsPerLink));
		}
	}

	private void generateStarOverlay(Set<Node> nodes, Random random) {
		final Node firstNode = nodes.stream() //
		    .filter(n -> n.getId() == 1) //
		    .findFirst() //
		    .get();

		nodes.stream() //
		    .filter(n -> !n.equals(firstNode)) //
		    .forEach(n -> links.add(new Link(firstNode, n, DreamConfiguration.get().numHopsPerLink)));
	}

	private void generateScaleFreeOverlay(Set<Node> nodes, Random random) {
		if (nodes.size() < 2) {
			return;
		}
		final int nlinks = nodes.size() - 1;
		final int d = 2 * nlinks / nodes.size();

		final List<Node> nodesList = new ArrayList<>(nodes);
		final List<Node> tmpNodes = nodesList.stream().skip(d + 1).collect(Collectors.toList());
		final List<Node> addedNodes = nodesList.stream().limit(d + 1).collect(Collectors.toList());

		for (Integer i = 0; i < d; i++) {
			for (Integer j = i + 1; j <= d; j++) {
				links.add(new Link(addedNodes.get(i), addedNodes.get(j), DreamConfiguration.get().numHopsPerLink));
			}
		}
		while (tmpNodes.size() > 0) {
			final Node sourceNode = tmpNodes.remove(0);
			final Node destinationNode = getCandidateNode(addedNodes, links, random);
			links.add(new Link(sourceNode, destinationNode, DreamConfiguration.get().numHopsPerLink));
			addedNodes.add(sourceNode);
		}
	}

	private Node getCandidateNode(List<Node> tmpNodes, Set<Link> links, Random random) {
		final Double p = random.nextDouble();
		Double previous_p = 0.0;
		Double current_p = 0.0;
		for (Integer i = 0; i < tmpNodes.size(); i++) {
			previous_p = current_p;
			current_p += getDegree(tmpNodes.get(i), links) / (2.0 * links.size());
			if (p >= previous_p && p <= current_p) {
				return tmpNodes.get(i);
			}
		}
		return null;
	}

	private long getDegree(Node node, Set<Link> links) {
		return links.stream() //
		    .filter(l -> l.getNode1().equals(node) || l.getNode2().equals(node)) //
		    .count();
	}

}
