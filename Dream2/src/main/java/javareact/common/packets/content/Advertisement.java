package javareact.common.packets.content;

import java.io.Serializable;

public class Advertisement implements Serializable {
	private static final long serialVersionUID = -6636280874981657399L;

	private final String hostId;
	private final String observableId;

	public Advertisement(String hostId, String observableId) {
		this.hostId = hostId;
		this.observableId = observableId;
	}

	public boolean isSatisfiedBy(Subscription sub) {
		if (!sub.isBroadcast() && !hostId.equals(sub.getHostId()))
			return false;
		if (!observableId.equals(sub.getObservableId()))
			return false;
		return true;
	}

	public final String getObservableId() {
		return observableId;
	}

	public final String getHostId() {
		return hostId;
	}

	public final String getSignature() {
		return hostId + "." + observableId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((hostId == null) ? 0 : hostId.hashCode());
		result = prime * result + ((observableId == null) ? 0 : observableId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Advertisement)) {
			return false;
		}
		Advertisement other = (Advertisement) obj;
		if (hostId == null) {
			if (other.hostId != null) {
				return false;
			}
		} else if (!hostId.equals(other.hostId)) {
			return false;
		}
		if (observableId == null) {
			if (other.observableId != null) {
				return false;
			}
		} else if (!observableId.equals(other.observableId)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Advertisement [" + hostId + "." + observableId + "]";
	}

}
