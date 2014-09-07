package javareact.common.types;

public abstract class Signal<T> extends AbstractReactive<T> {
  private RemoteVar<T> proxy = null;

  public Signal(String name, Proxy... proxies) {
    super(name, proxies);
    val = null;
  }

  public final T get() {
    return val;
  }

  @Override
  public synchronized RemoteVar<T> getProxy() {
    if (proxy == null) {
      proxy = new RemoteVar<T>(name);
    }
    return proxy;
  }
}
