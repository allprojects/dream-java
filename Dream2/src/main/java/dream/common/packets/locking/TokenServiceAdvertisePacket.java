package dream.common.packets.locking;

import java.io.Serializable;

import dream.common.packets.content.AdvType;
import dream.common.packets.registry.RegistryAdvertisePacket;

/**
 * Packet used by a registry to advertise its existence.
 */
public class TokenServiceAdvertisePacket implements Serializable {
  private static final long serialVersionUID = 527835287295833577L;

  public static final String subject = "__DREAM_TOKEN_SERVICE_ADVERTISE_PACKET_SUBJECT";

  private final AdvType type;

  public TokenServiceAdvertisePacket(AdvType type) {
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
    final TokenServiceAdvertisePacket other = (TokenServiceAdvertisePacket) obj;
    if (type != other.type) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "TokenServiceAdvertisePacket [type=" + type + "]";
  }

}
