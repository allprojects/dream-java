package dream.common.packets.content;

import java.io.Serializable;

public class Event<T extends Serializable> implements Serializable {
	private static final long serialVersionUID = 831217881290695190L;

	private final String objectId;
	private final String hostId;
	private final T val;

	public Event(String hostId, String objectId, T val) {
		this.hostId = hostId;
		this.objectId = objectId;
		this.val = val;
	}

	public final String getObjectId() {
		return objectId;
	}

	public final String getHostId() {
		return hostId;
	}

	public final T getVal() {
		return val;
	}

	public final String getSignature() {
		return objectId + "@" + hostId;
	}

	@Override
	public String toString() {
		return objectId + "@" + hostId + "(val = " + val.toString() + ")";
	}

}
