package javareact.common.types;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import javareact.client.ClientEventForwarder;
import javareact.common.Consts;
import javareact.common.SerializablePredicate;
import javareact.common.packets.content.Advertisement;
import javareact.common.packets.content.Event;

public class Var<T extends Serializable> implements ProxyGenerator<T> {
  protected final ClientEventForwarder forwarder;
  protected final String objectId;
  private final List<SerializablePredicate> constraints = new ArrayList<SerializablePredicate>();

  protected T val;
  protected RemoteVar<T> proxy;

  public Var(String objectId, T val) {
    this.forwarder = ClientEventForwarder.get();
    this.objectId = objectId;
    this.val = val;
    forwarder.advertise(new Advertisement(Consts.hostName, objectId), true);
  }

  /**
   * Private constructor used only for filters. Does not send advertisements and
   * does not retain any value.
   */
  private Var(Var<T> copy, SerializablePredicate<T> constraint) {
    this.forwarder = ClientEventForwarder.get();
    this.objectId = copy.objectId;
    this.val = null;
    constraints.addAll(copy.constraints);
    constraints.add(constraint);
  }

  public final synchronized void set(T val) {
    this.val = val;
    propagateChange();
  }

  public final synchronized void modify(Consumer<T> modification) {
    modification.accept(val);
    propagateChange();
  }

  public final synchronized T get() {
    assert val != null;
    return proxy.get();
  }

  protected final void propagateChange() {
    final Event<? extends Serializable> ev = new Event(Consts.hostName, objectId, val);
    forwarder.sendEvent(UUID.randomUUID(), ev, ev.getSignature(), false);
  }

  @Override
  public synchronized RemoteVar<T> getProxy() {
    if (proxy == null) {
      proxy = new RemoteVar<T>(objectId, constraints);
    }
    return proxy;
  }

  @Override
  public ProxyGenerator<T> filter(SerializablePredicate<T> constraint) {
    return new Var<T>(this, constraint);
  }

}
