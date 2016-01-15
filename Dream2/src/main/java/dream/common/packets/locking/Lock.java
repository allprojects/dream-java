package dream.common.packets.locking;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class Lock implements Serializable {
  private static final long serialVersionUID = -2899946414523738807L;

  private final Set<String> nodes = new HashSet<>();
  private final LockType type;

  public Lock(Set<String> nodes, LockType type) {
    this.nodes.addAll(nodes);
    this.type = type;
  }

  public final Set<String> getNodes() {
    return nodes;
  }

  public final LockType getType() {
    return type;
  }

  @Override
  public String toString() {
    return "Lock [nodes=" + nodes + ", type=" + type + "]";
  }

}
