package dream.common.packets;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import dream.common.packets.content.Event;

/**
 * Packet used to deliver events, which notify about some state change.
 */
public class EventPacket implements Serializable {
	private static final long serialVersionUID = 8208653909787190211L;
	public static final String subject = "__DREAM_PUBLICATION_PACKET_SUBJECT";

	private final Event event;

	// Uniquely identifies a propagation. In the case of complete glitch free
	// and
	// atomic consistency, it is identical to the id of the read lock associated
	// to the propagation.
	private final UUID id;

	// Original source of the change
	private final String source;

	// Nodes that should release the lock for the propagation, if any
	private final Set<String> lockReleaseNodes = new HashSet<>();

	public EventPacket(Event event, UUID id, String source) {
		this.event = event;
		this.id = id;
		this.source = source;
	}

	public final Event getEvent() {
		return event;
	}

	public final UUID getId() {
		return id;
	}

	public final String getSource() {
		return source;
	}

	public final void setLockReleaseNodes(Set<String> lockReleaseNodes) {
		this.lockReleaseNodes.addAll(lockReleaseNodes);
	}

	public final Set<String> getLockReleaseNodes() {
		return lockReleaseNodes;
	}

	@Override
	public String toString() {
		return "EventPacket [event=" + event + ", id=" + id + ", source=" + source + ", lockReleaseNodes="
				+ lockReleaseNodes + "]";
	}

}
