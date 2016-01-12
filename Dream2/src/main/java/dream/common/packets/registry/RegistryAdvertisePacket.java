package dream.common.packets.registry;

import java.io.Serializable;

import dream.common.packets.content.AdvType;

/**
 * Packet used by a registry to advertise its existence.
 */
public class RegistryAdvertisePacket implements Serializable {
  private static final long serialVersionUID = -2076191326020987758L;
  public static final String subject = "__DREAM_REGISTRY_ADVERTISE_PACKET_SUBJECT";

  private final AdvType type;

  public RegistryAdvertisePacket(AdvType type) {
    this.type = type;
  }

  public AdvType getType() {
    return type;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (type == null ? 0 : type.hashCode());
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
    if (!(obj instanceof RegistryAdvertisePacket)) {
      return false;
    }
    final RegistryAdvertisePacket other = (RegistryAdvertisePacket) obj;
    if (type != other.type) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "RegistryAdvertisePacket [type=" + type + "]";
  }

}
