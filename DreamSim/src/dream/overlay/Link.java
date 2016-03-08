package dream.overlay;

import java.io.Serializable;

public class Link implements Serializable {

	private static final long serialVersionUID = -8557693370724758574L;

	private final Node node1;
	private final Node node2;
	private final int numHops;

	/**
	 * Create a new link between node1 and node2 consisting of numHops hops.
	 *
	 * The first node will always have the smallest id, thus if node2 has a lower
	 * id than node1 the two nodes will be SWAPPED!
	 *
	 * @param node1
	 *          First node of the link
	 * @param node2
	 *          Second node of the link
	 * @param numHops
	 *          Number of hops in the link
	 */
	public Link(Node node1, Node node2, int numHops) {
		this.node1 = node1.getId() < node2.getId() ? node1 : node2;
		this.node2 = node1.getId() < node2.getId() ? node2 : node1;
		this.numHops = numHops;
	}

	/**
	 * Get the first endpoint of this link.
	 *
	 * @return the first endpoint of this link
	 */
	public final Node getNode1() {
		return node1;
	}

	/**
	 * Get the second endpoint of this link.
	 *
	 * @return the second endpoint of this link
	 */
	public final Node getNode2() {
		return node2;
	}

	/**
	 * Return the number of hops in the link.
	 *
	 * @return the number of hops in the link.
	 */
	public final int getNumHops() {
		return numHops;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (o instanceof Link) {
			final Link link = (Link) o;
			return link.getNode1().equals(node1) && link.getNode2().equals(node2);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return Integer.MIN_VALUE + node1.getId() + 1000001 * node2.getId();
	}

	@Override
	public String toString() {
		return "(" + node1 + " - " + node2 + ")";
	}

}
