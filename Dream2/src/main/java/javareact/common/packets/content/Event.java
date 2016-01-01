package javareact.common.packets.content;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Event implements Iterable<Attribute>, Serializable {
	private static final long serialVersionUID = 831217881290695190L;

	private final String observableId;
	private final String hostId;
	private final Map<String, Attribute> attributes = new HashMap<String, Attribute>();
	private final boolean persistent;

	public Event(String hostId, String observableId, boolean persistent, Attribute... attributes) {
		this.hostId = hostId;
		this.observableId = observableId;
		this.persistent = persistent;
		for (Attribute attr : attributes) {
			this.attributes.put(attr.getName(), attr);
		}
	}

	public Event(String hostId, String observableId, Attribute... attributes) {
		this(hostId, observableId, false, attributes);
	}

	public boolean hasAttribute(String name) {
		return attributes.containsKey(name);
	}

	public Attribute getAttributeFor(String name) {
		return attributes.get(name);
	}

	@Override
	public Iterator<Attribute> iterator() {
		return attributes.values().iterator();
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

	public final boolean isPersistent() {
		return persistent;
	}

	/**
	 * Returns true iff the event carries the same information as ev, i.e.,
	 * refers to the same observable and contains the same attributes.
	 * 
	 * @param ev
	 *            the event.
	 * @return true iff the event carries the same information as ev.
	 */
	public boolean containsTheSameInformationAs(Event ev) {
		if (!ev.hostId.equals(hostId))
			return false;
		if (!ev.observableId.equals(observableId))
			return false;
		Set<String> names = attributes.keySet();
		Set<String> evNames = attributes.keySet();
		if (names.size() != evNames.size())
			return false;
		if (!names.containsAll(evNames))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((attributes == null) ? 0 : attributes.hashCode());
		result = prime * result + ((hostId == null) ? 0 : hostId.hashCode());
		result = prime * result + ((observableId == null) ? 0 : observableId.hashCode());
		result = prime * result + (persistent ? 1231 : 1237);
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
		if (!(obj instanceof Event)) {
			return false;
		}
		Event other = (Event) obj;
		if (attributes == null) {
			if (other.attributes != null) {
				return false;
			}
		} else if (!attributes.equals(other.attributes)) {
			return false;
		}
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
		if (persistent != other.persistent) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return hostId + "." + observableId + "(" + attributes.values() + ")";
	}

}
