package dream.common.packets.content;

import java.io.Serializable;

public class Event implements Serializable {
	private static final long serialVersionUID = 831217881290695190L;

	private final String objectId;
	private final String hostId;

	public Event(String hostId, String objectId) {
		this.hostId = hostId;
		this.objectId = objectId;
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
		return "Event [" + getSignature() + "]";
	}

}
