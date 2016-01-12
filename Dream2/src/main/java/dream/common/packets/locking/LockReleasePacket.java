package dream.common.packets.locking;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Manages the requests and release of locks. It is not thread safe, so it
 * assumes that requests are sequential (e.g., generated from a single server).
 */
public class LockReleasePacket implements Serializable {
  private static final long serialVersionUID = -1523880233653918696L;
  public static final String subject = "__DREAM_LOCK_RELEASE_PACKET_SUBJECT";

  private final Set<String> nodes = new HashSet<>();
  private final LockType type;

  public LockReleasePacket(Set<String> node, LockType type) {
    this.nodes.addAll(nodes);
    this.type = type;
  }

  public final Set<String> getNodes() {
    return nodes;
  }

  public final LockType getType() {
    return type;
  }

}
