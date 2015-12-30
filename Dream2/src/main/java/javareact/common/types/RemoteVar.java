package javareact.common.types;

import java.util.HashSet;
import java.util.Set;

import javareact.common.packets.EventPacket;
import javareact.common.packets.content.Attribute;
import javareact.common.packets.content.Event;

public class RemoteVar<T> extends Proxy implements Reactive<T>, ProxyGenerator {
  private final Set<ReactiveChangeListener<T>> listeners = new HashSet<ReactiveChangeListener<T>>();
  private T val;

  public RemoteVar(String host, String object) {
    super(host, object);
  }

  public RemoteVar(String object) {
    super(object);
  }

  public final T get() {
    return val;
  }

  @Override
  protected final void processEvent(Event ev) {
    if (ev.hasAttribute(method)) {
      final Attribute attr = ev.getAttributeFor(method);
      val = (T) attr.getValue();
    }
  }

  @Override
  public T evaluate() {
    return this.get();
  }

  @Override
  public void addReactiveChangeListener(ReactiveChangeListener<T> listener) {
    listeners.add(listener);
  }

  @Override
  public void removeReactiveChangeListener(ReactiveChangeListener<T> listener) {
    listeners.remove(listener);
  }

  @Override
  public final void notifyValueChanged(EventPacket evPkt) {
    super.notifyValueChanged(evPkt);
    listeners.forEach(l -> l.notifyReactiveChanged(val));
  }

  @Override
  public RemoteVar<T> getProxy() {
    return this;
  }

}
