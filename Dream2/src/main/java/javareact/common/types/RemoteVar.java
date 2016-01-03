package javareact.common.types;

import java.util.HashSet;
import java.util.Set;

import javareact.common.ValueChangeListener;
import javareact.common.packets.EventPacket;
import javareact.common.packets.content.Attribute;
import javareact.common.packets.content.Event;

public class RemoteVar<T> extends Proxy implements TimeChangingValue<T> {
  private final Set<ValueChangeListener<T>> listeners = new HashSet<ValueChangeListener<T>>();
  private T val;

  public RemoteVar(String host, String object) {
    super(host, object);
  }

  public RemoteVar(String object) {
    super(object);
  }

  public final synchronized T get() {
    return val;
  }

  @Override
  protected final synchronized void processEvent(Event ev) {
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
  public void addValueChangeListener(ValueChangeListener<T> listener) {
    listeners.add(listener);
  }

  @Override
  public void removeValueChangeListener(ValueChangeListener<T> listener) {
    listeners.remove(listener);
  }

  @Override
  public final void notifyValueChanged(EventPacket evPkt) {
    super.notifyValueChanged(evPkt);
    listeners.forEach(l -> l.notifyValueChanged(val));
  }

  @Override
  public Proxy getProxy() {
    return this;
  }

}
