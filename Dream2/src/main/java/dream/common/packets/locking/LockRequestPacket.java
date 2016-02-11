package dream.common.packets.locking;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import polimi.reds.NodeDescriptor;

public class LockRequestPacket implements Serializable {
	private static final long serialVersionUID = -1523880233653918696L;
	public static final String subject = "__DREAM_LOCK_REQUEST_PACKET_SUBJECT";

	/**
	 * Node that requests the lock.
	 */
	private final NodeDescriptor applicant;

	/**
	 * Nodes to lock.
	 */
	private final Set<String> lockNodes;

	/**
	 * Nodes that will sent a lock release.
	 */
	private final Set<String> unlockNodes;

	private final LockType type;
	private final UUID lockID = UUID.randomUUID();

	public LockRequestPacket(NodeDescriptor applicant, Set<String> lockNodes, Set<String> unlockNodes, LockType type) {
		this.applicant = applicant;
		this.lockNodes = new HashSet<>(lockNodes);
		this.unlockNodes = new HashSet<>(unlockNodes);
		this.type = type;
	}

	public final NodeDescriptor getApplicant() {
		return applicant;
	}

	public final Set<String> getLockNodes() {
		return lockNodes;
	}

	public final Set<String> getUnlockNodes() {
		return unlockNodes;
	}

	public final LockType getType() {
		return type;
	}

	public final UUID getLockID() {
		return lockID;
	}

}
