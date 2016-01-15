package dream.common.packets.locking;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import polimi.reds.NodeDescriptor;

/**
 * Manages the requests and release of locks. It is not thread safe, so it
 * assumes that requests are sequential (e.g., generated from a single server).
 */
public class LockReleasePacket implements Serializable {
  private static final long serialVersionUID = -1523880233653918696L;
  public static final String subject = "__DREAM_LOCK_RELEASE_PACKET_SUBJECT";

  /**
   * Nodes submitting the release
   */
  private final NodeDescriptor sender;

  /**
   * Nodes to lock.
   */
  private final Set<String> lockNodes;

  private final UUID lockID;
  private final LockType type;

  public LockReleasePacket(NodeDescriptor sender, Set<String> lockNodes, UUID lockID, LockType type) {
    this.sender = sender;
    this.lockNodes = new HashSet<>(lockNodes);
    this.lockID = lockID;
    this.type = type;
  }

  public final NodeDescriptor getSender() {
    return sender;
  }

  public final Set<String> getLockNodes() {
    return lockNodes;
  }

  public final UUID getLockID() {
    return lockID;
  }

  public final LockType getType() {
    return type;
  }

  @Override
  public String toString() {
    return "LockReleasePacket [sender=" + sender + ", lockID=" + lockID + ", type=" + type + "]";
  }

}
