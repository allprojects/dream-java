package dream.common.packets.locking;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class LockReplyPacket implements Serializable {
  private static final long serialVersionUID = -3499224800050816098L;

  public static final String subject = "__DREAM_LOCK_REQUEST_PACKET_SUBJECT";

  private final Set<String> nodes = new HashSet<>();
  private final LockType type;

  public LockReplyPacket(Set<String> node, LockType type) {
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
