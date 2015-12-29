package javareact.common.types;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

import javareact.client.ClientEventForwarder;
import javareact.common.Consts;
import javareact.common.packets.content.Advertisement;
import javareact.common.packets.content.Attribute;
import javareact.common.packets.content.Event;

public class Var<T> implements ProxyGenerator {
  protected final String observableId;
  private final ClientEventForwarder forwarder;

  private T val;
  private RemoteVar<T> proxy;

  public Var(String observableId, T val) {
    forwarder = ClientEventForwarder.get();
    this.observableId = observableId;
    sendAdvertisement();
    set(val);
  }

  public final synchronized void set(T val) {
    this.val = val;
    impactOnGet();
  }

  public final synchronized void modify(Consumer<T> modification) {
    modification.accept(this.val);
    impactOnGet();
  }

  public final T get() {
    return val;
  }

  @Override
  public synchronized RemoteVar<T> getProxy() {
    if (proxy == null) {
      proxy = new RemoteVar<T>(observableId);
    }
    return proxy;
  }

  private final void impactOnGet() {
    final Attribute[] attrs = new Attribute[1];
    try {
      attrs[0] = new Attribute<T>("get", get());
    } catch (final Exception e) {
      e.printStackTrace();
    }
    sendEvent(attrs);
  }

  private final void sendAdvertisement() {
    final Advertisement adv = new Advertisement(Consts.hostName, observableId);
    forwarder.advertise(adv, true);
  }

  private final synchronized void sendEvent(Attribute[] attributes) {
    final Event ev = new Event(Consts.hostName, observableId, attributes);
    final Set<String> computedFrom = new HashSet<String>();
    computedFrom.add(ev.getSignature());
    forwarder.sendEvent(UUID.randomUUID(), ev, computedFrom, false);
  }
}
