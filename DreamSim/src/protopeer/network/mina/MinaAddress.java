package protopeer.network.mina;

import java.io.*;
import java.net.*;

import protopeer.network.*;
import protopeer.util.*;

public class MinaAddress extends NetworkAddress implements LightweightSerializable {

	private InetSocketAddress socketAddress;

	public MinaAddress(InetSocketAddress socketAddress) {
		this.socketAddress = socketAddress;
	}

	public MinaAddress(InetAddress address, int port) {
		this.socketAddress = new InetSocketAddress(address, port);
	}

	public InetSocketAddress getSocketAddress() {
		return socketAddress;
	}

	public int getPort() {
		return socketAddress.getPort();
	}

	public InetAddress getInetAddress() {
		return socketAddress.getAddress();
	}

	public long toLongValue() {
		byte[] bytes = socketAddress.getAddress().getAddress();
		int port = socketAddress.getPort();
		return ((long) bytes[0]) | (((long) bytes[1]) << 8) | (((long) bytes[2]) << 16) | (((long) bytes[3]) << 24)
				| (((long) port) << 32);
	}

	@Override
	public int hashCode() {
		return socketAddress.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		return socketAddress.equals(((MinaAddress) obj).socketAddress);
	}

	public String toString() {
		return socketAddress.toString();
	}

	public void deserialize(LightweightObjectInputStream in) throws IOException {
		byte addressBytesLength=in.readByte();
		byte[] addressBytes=new byte[addressBytesLength];
		in.read(addressBytes);
		InetAddress inetAddress=InetAddress.getByAddress(addressBytes);
		int port=in.readInt();
		this.socketAddress=new InetSocketAddress(inetAddress,port);
	}

	public void serialize(LightweightObjectOutputStream out) throws IOException {
		byte[] addressBytes=socketAddress.getAddress().getAddress();
		out.writeByte((byte)addressBytes.length);
		out.write(addressBytes);
		out.writeInt(socketAddress.getPort());
	}
}
