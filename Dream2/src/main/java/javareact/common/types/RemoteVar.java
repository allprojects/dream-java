package javareact.common.types;

import java.util.ArrayList;
import java.util.List;

import javareact.common.SerializablePredicate;
import javareact.common.packets.content.Event;

public class RemoteVar<T> extends Proxy implements ProxyGenerator<T> {
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
  public Proxy getProxy() {
    return this;
  }

  @Override
  public ProxyGenerator<T> filter(SerializablePredicate<T> constraint) {
    return this;
  }

}
