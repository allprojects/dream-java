package javareact.common.types;

import java.util.function.Consumer;

import javareact.common.packets.content.Attribute;

public class Var<T> extends Observable {
  private T val;
  private RemoteVar<T> proxy;

  public Var(String observableId, T val) {
    super(observableId);
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
}
