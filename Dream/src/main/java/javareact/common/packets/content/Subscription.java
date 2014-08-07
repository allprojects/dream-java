package javareact.common.packets.content;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

public class Subscription implements Serializable {
  private static final long serialVersionUID = -3452847781395458670L;

  public static final String wildcard = "*";

  private final String observableId;
  private final String hostId;
  private final Collection<Constraint> constraints = new ArrayList<Constraint>();
  private final boolean blocking;

  public Subscription(String observableId, String hostId, boolean blocking, Constraint... constraints) {
    this.observableId = observableId;
    this.hostId = hostId;
    this.blocking = blocking;
    for (Constraint constraint : constraints) {
      this.constraints.add(constraint);
    }
  }

  public Subscription(String observableId, String hostId, Constraint... constraints) {
    this(observableId, hostId, false, constraints);
  }

  public final boolean isSatisfiedBy(Event ev) {
    if (!isBroadcast() && !hostId.equals(ev.getHostId())) return false;
    if (!observableId.equals(ev.getObservableId())) return false;
    for (Constraint c : constraints) {
      if (!c.isSatisfiedBy(ev)) return false;
    }
    return true;
  }

  public final boolean matchesOnlySignatureOf(Event ev) {
    if (!isBroadcast() && !hostId.equals(ev.getHostId())) return false;
    if (!observableId.equals(ev.getObservableId())) return false;
    for (Constraint c : constraints) {
      if (!c.isSatisfiedBy(ev)) return true;
    }
    return false;
  }

  public final String getObservableId() {
    return observableId;
  }

  public final String getHostId() {
    return hostId;
  }

  public final String getSignature() {
    return hostId + "." + observableId;
  }

  public final boolean isBroadcast() {
    return hostId.equals(wildcard);
  }

  public final boolean isBlocking() {
    return blocking;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (blocking ? 1231 : 1237);
    result = prime * result + ((constraints == null) ? 0 : constraints.hashCode());
    result = prime * result + ((hostId == null) ? 0 : hostId.hashCode());
    result = prime * result + ((observableId == null) ? 0 : observableId.hashCode());
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
    Subscription other = (Subscription) obj;
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
    if (observableId == null) {
      if (other.observableId != null) {
        return false;
      }
    } else if (!observableId.equals(other.observableId)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return hostId + "." + observableId + "(" + constraints + ")";
  }

}
