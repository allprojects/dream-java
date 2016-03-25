package dream.common.packets.content;

import java.io.Serializable;

public class Subscription implements Serializable {
	private static final long serialVersionUID = -3452847781395458670L;

	private final String objectId;
	private final String hostId;

	public Subscription(String hostId, String objectId) {
		this.hostId = hostId;
		this.objectId = objectId;
	}

	public final boolean isSatisfiedBy(Event ev) {
		return ev.getHostId().equals(hostId) && ev.getObjectId().equals(objectId);
	}

	public final String getObjectId() {
		return objectId;
	}

	public final String getHostId() {
		return hostId;
	}

	public final String getSignature() {
		return objectId + "@" + hostId;
	}

	@Override
	public String toString() {
		return "Subscription[" + getSignature() + "]";
	}

}
