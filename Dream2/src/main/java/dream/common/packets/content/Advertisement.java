package dream.common.packets.content;

import java.io.Serializable;

public class Advertisement implements Serializable {
	private static final long serialVersionUID = -6636280874981657399L;

	private final String hostId;
	private final String objectId;

	public Advertisement(String hostId, String objectId) {
		this.hostId = hostId;
		this.objectId = objectId;
	}

	public boolean isSatisfiedBy(Subscription<?> sub) {
		return hostId.equals(sub.getHostId()) && objectId.equals(sub.getObjectId());
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
		return "Advertisement [" + objectId + "@" + hostId + "]";
	}

}
