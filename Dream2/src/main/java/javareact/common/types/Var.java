package javareact.common.types;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

import javareact.client.ClientEventForwarder;
import javareact.common.Consts;
import javareact.common.packets.content.Attribute;
import javareact.common.packets.content.Event;

public class Var<T> implements ProxyGenerator {
  private final ClientEventForwarder forwarder;
  private final String objectId;

  private T val;
  private RemoteVar<T> proxy = null;

  public Var(String objectId, T val) {
    this.forwarder = ClientEventForwarder.get();
    this.objectId = objectId;
    set(val);
  }

  public final synchronized void set(T val) {
    this.val = val;
    impactOnGet();
  }

  public final synchronized void modify(Consumer<T> modification) {
    modification.accept(val);
    impactOnGet();
  }

  public final T get() {
    return val;
  }

  private final void impactOnGet() {
    final Event ev = new Event(Consts.hostName, objectId, new Attribute<T>("get", get()));
    final Set<String> computedFrom = new HashSet<String>();
    computedFrom.add(ev.getSignature());
    forwarder.sendEvent(UUID.randomUUID(), ev, computedFrom, false);
  }

  @Override
  public synchronized RemoteVar<T> getProxy() {
    if (proxy == null) {
      proxy = new RemoteVar<T>(objectId);
    }
    return proxy;
  }

}
