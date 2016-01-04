package javareact.common.types;

import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javareact.client.ClientEventForwarder;
import javareact.common.Consts;
import javareact.common.packets.content.Advertisement;
import javareact.common.packets.content.Attribute;
import javareact.common.packets.content.Event;

public class Var<T> implements ProxyGenerator {
  protected final ClientEventForwarder forwarder;
  protected final String objectId;

  protected T val;
  private RemoteVar<T> proxy;

  public Var(String objectId, T val) {
    this.forwarder = ClientEventForwarder.get();
    this.objectId = objectId;
    forwarder.advertise(new Advertisement(Consts.hostName, objectId), true);
    this.val = val;
  }

  public final synchronized void set(T val) {
    this.val = val;
    impactOn("get", () -> this.get());
  }

  public final synchronized void modify(Consumer<T> modification) {
    modification.accept(val);
    impactOn("get", () -> get());
  }

  public final synchronized T get() {
    return val;
  }

  protected final void impactOn(String methodName, Supplier<?> method) {
    final Event ev = new Event(Consts.hostName, objectId, new Attribute(methodName, method.get()));
    forwarder.sendEvent(UUID.randomUUID(), ev, ev.getSignature(), false);
  }

  @Override
  public synchronized RemoteVar<T> getProxy() {
    if (proxy == null) {
      proxy = new RemoteVar<T>(objectId);
    }
    return proxy;
  }

}
