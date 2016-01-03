package javareact.common.packets.content;

import java.io.Serializable;

public class Advertisement implements Serializable {
  private static final long serialVersionUID = -6636280874981657399L;

  private final String hostId;
  private final String objectId;

  public Advertisement(String hostId, String objectId) {
    this.hostId = hostId;
    this.objectId = objectId;
  }

  public boolean isSatisfiedBy(Subscription sub) {
    if (!sub.isBroadcast() && !hostId.equals(sub.getHostId())) {
      return false;
    }
    if (!objectId.equals(sub.getObjectId())) {
      return false;
    }
    return true;
  }

  public final String getObjectId() {
    return objectId;
  }

  public final String getHostId() {
    return hostId;
  }

  public final String getSignature() {
    return objectId + "@" + hostId;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (hostId == null ? 0 : hostId.hashCode());
    result = prime * result + (objectId == null ? 0 : objectId.hashCode());
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
    if (!(obj instanceof Advertisement)) {
      return false;
    }
    final Advertisement other = (Advertisement) obj;
    if (hostId == null) {
      if (other.hostId != null) {
        return false;
      }
    } else if (!hostId.equals(other.hostId)) {
      return false;
    }
    if (objectId == null) {
      if (other.objectId != null) {
        return false;
      }
    } else if (!objectId.equals(other.objectId)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "Advertisement [" + objectId + "@" + hostId + "]";
  }

}
