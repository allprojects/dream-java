package protopeer.util;

import java.io.*;

import protopeer.network.*;

public class NetworkAddressPair implements Serializable, Cloneable {

	private NetworkAddress address1;

	private NetworkAddress address2;

	public NetworkAddressPair(NetworkAddress address1, NetworkAddress address2) {
		super();
		this.address1 = address1;
		this.address2 = address2;
	}

	public NetworkAddress getAddress1() {
		return address1;
	}

	public NetworkAddress getAddress2() {
		return address2;
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		// result = PRIME * result
		// + ((address1 == null) ? 0 : address1.hashCode());
		// result = PRIME * result
		// + ((address2 == null) ? 0 : address2.hashCode());
		result = PRIME * result + address1.hashCode();
		result = PRIME * result + address2.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		NetworkAddressPair otherPair = (NetworkAddressPair) obj;
		return otherPair.address1.equals(this.address1) && otherPair.address2.equals(this.address2);
		// if (this == obj)
		// return true;
		// if (obj == null)
		// return false;
		// if (getClass() != obj.getClass())
		// return false;
		// final NetworkAddressPair other = (NetworkAddressPair) obj;
		// if (address1 == null) {
		// if (other.address1 != null)
		// return false;
		// } else if (!address1.equals(other.address1))
		// return false;
		// if (address2 == null) {
		// if (other.address2 != null)
		// return false;
		// } else if (!address2.equals(other.address2))
		// return false;
		// return true;
	}

	@Override
	public NetworkAddressPair clone() {
		try {
			NetworkAddressPair twin = (NetworkAddressPair) super.clone();
			twin.address1 = this.address1.clone();
			twin.address2 = this.address2.clone();
			return twin;
		} catch (CloneNotSupportedException e) {
		}
		return null;

	}
	
	public String toString() {
		return address1.toString()+"\t"+address2.toString();
	}

}
