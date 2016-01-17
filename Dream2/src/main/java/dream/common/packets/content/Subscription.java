package dream.common.packets.content;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import dream.common.SerializablePredicate;

public class Subscription<T extends Serializable> implements Serializable {
  private static final long serialVersionUID = -3452847781395458670L;

  private final String objectId;
  private final String hostId;
  private final UUID subId;
  private final List<SerializablePredicate<T>> constraints = new ArrayList<SerializablePredicate<T>>();

  public Subscription(String hostId, String objectId) {
    this.hostId = hostId;
    this.objectId = objectId;
    this.subId = UUID.randomUUID();
    this.constraints.addAll(constraints);
  }

  public Subscription(String hostId, String objectId, List<SerializablePredicate<T>> constraints) {
    this(hostId, objectId);
    this.constraints.addAll(constraints);
  }

  public final boolean isSatisfiedBy(Event<?> ev) {
    @SuppressWarnings("unchecked")
    final T val = (T) ev.getVal();
    return hostId.equals(ev.getHostId()) && //
        objectId.equals(ev.getObjectId()) && //
        constraints.stream().allMatch(c -> c.test(val));
  }

  public final boolean matchesOnlySignatureOf(Event<?> ev) {
    @SuppressWarnings("unchecked")
    final T val = (T) ev.getVal();
    return hostId.equals(ev.getHostId()) && //
        objectId.equals(ev.getObjectId()) && //
        constraints.stream().anyMatch(c -> !c.test(val));
  }

  public final String getObjectId() {
    return objectId;
  }

  public final String getHostId() {
    return hostId;
  }

  public final UUID getSubId() {
    return subId;
  }

  public final String getSignature() {
    return objectId + "@" + hostId;
  }

  @Override
  public String toString() {
    return objectId + "@" + hostId + "(" + constraints + ")";
  }

}
