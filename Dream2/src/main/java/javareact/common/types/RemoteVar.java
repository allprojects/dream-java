package javareact.common.types;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javareact.common.SerializablePredicate;
import javareact.common.ValueChangeListener;
import javareact.common.packets.EventPacket;
import javareact.common.packets.content.Event;

public class RemoteVar<T> extends Proxy implements TimeChangingValue<T> {
  private final Set<ValueChangeListener<T>> listeners = new HashSet<ValueChangeListener<T>>();
  private T val;

  @SuppressWarnings("unchecked")
  public RemoteVar(String host, String object) {
    super(host, object, new ArrayList<SerializablePredicate>());
  }

  @SuppressWarnings("unchecked")
  public RemoteVar(String object) {
    super(object, new ArrayList<SerializablePredicate>());
  }

  @SuppressWarnings("unchecked")
  RemoteVar(String host, String object, List<SerializablePredicate> constraints) {
    super(host, object, constraints);
  }

  @SuppressWarnings("unchecked")
  RemoteVar(String object, List<SerializablePredicate> constraints) {
    super(object, constraints);
  }

  public final synchronized T get() {
    return val;
  }

  @SuppressWarnings("unchecked")
  @Override
  protected final synchronized void processEvent(Event<?> ev) {
    val = (T) ev.getVal();
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

  @Override
  public ProxyGenerator<T> filter(SerializablePredicate<T> constraint) {
    return this;
  }

}
