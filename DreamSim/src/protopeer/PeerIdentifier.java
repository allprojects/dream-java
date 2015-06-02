package protopeer;

import java.io.*;

/**
 * The base class for all peer identifiers. There should be a well defined
 * distance between any two peer IDs, the distance should not be greater than
 * <code>getMaxDistance()</code>. All <code>PeerIdentifier</code>s must be
 * correctly <code>Cloneable</code> and <code>Serializable</code> and have
 * consistent implementations of <code>hashCode()</code> and
 * <code>equals()</code>. All <code>PeerIdentifier</code>s must be
 * immutable.
 * 
 */

public abstract class PeerIdentifier implements Serializable, Cloneable {

	/**
	 * Computes the distance from this identifier to another.
	 * 
	 * @param otherIdentifier
	 * @return
	 */
	public abstract double distanceTo(PeerIdentifier otherIdentifier);

	/**
	 * Returns the maximum possible value the <code>distanceTo</code> function
	 * can return.
	 * 
	 * @return
	 */
	public abstract double getMaxDistance();

	public PeerIdentifier clone() {
		try {
			return (PeerIdentifier) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}
}
