package javareact.common.packets.content;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public class Subscription implements Serializable {
  private static final long serialVersionUID = -3452847781395458670L;

  public static final String wildcard = "*";

  private final String objectId;
  private final String hostId;
  private final Collection<Constraint> constraints = new ArrayList<Constraint>();
  private final boolean blocking;
  private final UUID proxyID;

  public Subscription(String hostId, String objectId, boolean blocking, UUID proxyID, Constraint... constraints) {
    this.hostId = hostId;
    this.objectId = objectId;
    this.blocking = blocking;
    this.proxyID = proxyID;
    for (final Constraint constraint : constraints) {
      this.constraints.add(constraint);
    }
  }

  public Subscription(String hostId, String objectId, UUID proxyID, Constraint... constraints) {
    this(hostId, objectId, false, proxyID, constraints);
  }

  public final boolean isSatisfiedBy(Event ev) {
    if (!isBroadcast() && !hostId.equals(ev.getHostId())) {
      return false;
    }
    if (!objectId.equals(ev.getObjectId())) {
      return false;
    }
    for (final Constraint c : constraints) {
      if (!c.isSatisfiedBy(ev)) {
        return false;
      }
    }
    return true;
  }

  public final boolean matchesOnlySignatureOf(Event ev) {
    if (!isBroadcast() && !hostId.equals(ev.getHostId())) {
      return false;
    }
    if (!objectId.equals(ev.getObjectId())) {
      return false;
    }
    for (final Constraint c : constraints) {
      if (!c.isSatisfiedBy(ev)) {
        return true;
      }
    }
    return false;
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

  public final boolean isBroadcast() {
    return hostId.equals(wildcard);
  }

  public final boolean isBlocking() {
    return blocking;
  }

  public UUID getProxyID() {
    return proxyID;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (blocking ? 1231 : 1237);
    result = prime * result + (constraints == null ? 0 : constraints.hashCode());
    result = prime * result + (hostId == null ? 0 : hostId.hashCode());
    result = prime * result + (objectId == null ? 0 : objectId.hashCode());
    result = prime * result + (proxyID == null ? 0 : proxyID.hashCode());
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
    if (!(obj instanceof Subscription)) {
      return false;
    }
    final Subscription other = (Subscription) obj;
    if (blocking != other.blocking) {
      return false;
    }
    if (constraints == null) {
      if (other.constraints != null) {
        return false;
      }
    } else if (!constraints.equals(other.constraints)) {
      return false;
    }
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
    if (proxyID == null) {
      if (other.proxyID != null) {
        return false;
      }
    } else if (!proxyID.equals(other.proxyID)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return objectId + "@" + hostId + "(" + constraints + ")";
  }

}
