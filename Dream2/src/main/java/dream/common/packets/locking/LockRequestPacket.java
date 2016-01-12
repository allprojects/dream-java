package dream.common.packets.locking;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class LockRequestPacket implements Serializable {
  private static final long serialVersionUID = -1523880233653918696L;
  public static final String subject = "__DREAM_LOCK_REQUEST_PACKET_SUBJECT";

  private final Set<String> nodes = new HashSet<>();
  private final LockType type;

  public LockRequestPacket(Set<String> node, LockType type) {
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
