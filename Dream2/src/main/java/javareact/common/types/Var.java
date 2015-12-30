package javareact.common.types;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

import javareact.common.Consts;
import javareact.common.packets.content.Advertisement;
import javareact.common.packets.content.Attribute;
import javareact.common.packets.content.Event;

public class Var<T> extends Proxy {
  private T val;

  public Var(String observableId, T val) {
    super(observableId);
    forwarder.advertise(new Advertisement(Consts.hostName, observableId), true);
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
    final Event ev = new Event(Consts.hostName, object, new Attribute<T>("get", get()));
    final Set<String> computedFrom = new HashSet<String>();
    computedFrom.add(ev.getSignature());
    forwarder.sendEvent(UUID.randomUUID(), ev, computedFrom, false);
  }

  @Override
  protected void processEvent(Event ev) {
    assert false : "A Var should never receive events";
  }

}
