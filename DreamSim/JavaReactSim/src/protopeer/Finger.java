package protopeer;

import java.io.*;

import org.apache.log4j.*;

import protopeer.network.*;

/**
 * A <code>Finger</code> is a pair consisting of the peer identifier and the
 * peer's network address. The <code>Finger</code> is immutable and implements
 * <code>hashCode()</code>, <code>equals()</code> and <code>clone()</code>
 * 
 */
public class Finger implements Serializable, Cloneable {

	private static final Logger logger = Logger.getLogger(Finger.class);

	private final PeerIdentifier identifier;

	private final NetworkAddress networkAddress;

	public Finger(NetworkAddress networkAddress, PeerIdentifier identifier) {
		this.identifier = identifier;
		this.networkAddress = networkAddress;
	}

	public PeerIdentifier getIdentifier() {
		return identifier;
	}

	public NetworkAddress getNetworkAddress() {
		return networkAddress;
	}

	public String toString() {
		return "(" + networkAddress + ", " + identifier + ")";
	}

	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((networkAddress == null) ? 0 : networkAddress.hashCode());
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Finger other = (Finger) obj;
		if (networkAddress == null) {
			if (other.networkAddress != null)
				return false;
		} else if (!networkAddress.equals(other.networkAddress))
			return false;
		return true;
	}

	public Finger clone() {
		return new Finger(this.networkAddress == null ? null : this.networkAddress.clone(),
				this.identifier == null ? null : this.identifier.clone());
	}

}
