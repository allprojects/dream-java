package javareact.common.packets.content;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Event implements Iterable<Attribute>, Serializable {
  private static final long serialVersionUID = 831217881290695190L;

  private final String objectId;
  private final String hostId;
  private final Map<String, Attribute> attributes = new HashMap<String, Attribute>();

  public Event(String hostId, String objectId, Attribute... attributes) {
    this.hostId = hostId;
    this.objectId = objectId;
    for (final Attribute attr : attributes) {
      this.attributes.put(attr.getName(), attr);
    }
  }

  public boolean hasAttribute(String name) {
    return attributes.containsKey(name);
  }

  public Attribute getAttributeFor(String name) {
    return attributes.get(name);
  }

  @Override
  public Iterator<Attribute> iterator() {
    return attributes.values().iterator();
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

  /**
   * Returns true iff the event carries the same information as ev, i.e., refers
   * to the same time changing values and contains the same attributes.
   *
   * @param ev
   *          the event.
   * @return true iff the event carries the same information as ev.
   */
  public boolean containsTheSameInformationAs(Event ev) {
    if (!ev.hostId.equals(hostId)) {
      return false;
    }
    if (!ev.objectId.equals(objectId)) {
      return false;
    }
    final Set<String> names = attributes.keySet();
    final Set<String> evNames = attributes.keySet();
    if (names.size() != evNames.size()) {
      return false;
    }
    if (!names.containsAll(evNames)) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (attributes == null ? 0 : attributes.hashCode());
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
    if (getClass() != obj.getClass()) {
      return false;
    }
    final Event other = (Event) obj;
    if (attributes == null) {
      if (other.attributes != null) {
        return false;
      }
    } else if (!attributes.equals(other.attributes)) {
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
    return true;
  }

  @Override
  public String toString() {
    return objectId + "@" + hostId + "(" + attributes.values() + ")";
  }

}
