package protopeer.network;

import java.io.*;

import protopeer.util.*;

/**
 * A simple network address implementation using an integer. 
 * 
 *
 */
public class IntegerNetworkAddress extends NetworkAddress implements LightweightSerializable {

	private int address;

	public IntegerNetworkAddress(int address) {
		this.address = address;
	}

	@Override
	public long toLongValue() {
		return address;
	}

	public int getIntValue() {
		return address;
	}

	@Override
	public String toString() {
		return "" + address;
	}

	public void deserialize(LightweightObjectInputStream in) throws IOException {
		this.address=in.readInt();		
	}

	public void serialize(LightweightObjectOutputStream out) throws IOException {
		out.writeInt(this.address);		
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + address;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof IntegerNetworkAddress))
			return false;
		IntegerNetworkAddress other = (IntegerNetworkAddress) obj;
		if (address != other.address)
			return false;
		return true;
	}
}
