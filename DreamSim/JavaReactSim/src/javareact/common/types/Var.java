package javareact.common.types;

import java.util.function.Consumer;

import protopeer.Peer;
import javareact.common.packets.content.Attribute;

public class Var<T> extends Observable {
  private T val;
  private RemoteVar<T> proxy;

  public Var(Peer peer, String observableId, boolean persistent, T val) {
    super(peer, observableId, persistent);
    this.val = val;
  }

  public Var(Peer peer, String observableId, T val) {
    super(peer, observableId);
    this.val = val;
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
      proxy = new RemoteVar<T>(peer, observableId);
    }
    return proxy;
  }

  private final void impactOnGet() {
    Attribute[] attrs = new Attribute[1];
    try {
      attrs[0] = new Attribute<T>("get", get());
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    sendEvent(attrs);
  }
}
