package javareact.common.types;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javareact.common.Consts;
import javareact.common.SerializablePredicate;
import javareact.common.packets.content.Event;

public class RemoteVar<T> extends Proxy implements ProxyRegistrar<T> {
  private T val;

  @SuppressWarnings("unchecked")
  RemoteVar(String host, String object, List<SerializablePredicate> constraints) {
    super(host, object, constraints);
  }

  @SuppressWarnings("unchecked")
  RemoteVar(String object, List<SerializablePredicate> constraints) {
    this(Consts.hostName, object, constraints);
  }

  @SuppressWarnings("unchecked")
  public RemoteVar(String host, String object) {
    this(host, object, new ArrayList<SerializablePredicate>());
  }

  @SuppressWarnings("unchecked")
  public RemoteVar(String object) {
    this(Consts.hostName, object);
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
  public ProxyRegistrar<T> filter(SerializablePredicate<T> constraint) {
    return this;
  }

  @Override
  public void addProxyChangeListener(ProxyChangeListener proxyChangeListener) {
    proxyChangeListeners.add(proxyChangeListener);
  }

  @Override
  public void removeProxyChangeListener(ProxyChangeListener proxyChangeListener) {
    proxyChangeListeners.remove(proxyChangeListener);

  }

  @Override
  public String getHost() {
    return host;
  }

  @Override
  public String getObject() {
    return object;
  }

  @Override
  public UUID getProxyID() {
    return proxyID;
  }

  @Override
  public List<SerializablePredicate> getConstraints() {
    return constraints;
  }

}
