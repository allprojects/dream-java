package javareact.common.types;

import java.util.function.Supplier;

public class Signal<T> extends AbstractReactive<T> {
  private RemoteVar<T> proxy = null;
  private final Supplier<T> evaluation;

  private static <T> Proxy[] proxiesFromVars(ProxyGenerator[] vars) {
    final Proxy[] proxies = new Proxy[vars.length];
    for (int i = 0; i < vars.length; i++) {
      proxies[i] = vars[i].getProxy();
    }
    return proxies;
  }

  @SafeVarargs
  public Signal(String name, Supplier<T> evaluation, ProxyGenerator... vars) {
    super(name, proxiesFromVars(vars));
    val = evaluation.get();
    this.evaluation = evaluation;
  }

  @Override
  public synchronized RemoteVar<T> getProxy() {
    if (proxy == null) {
      proxy = new RemoteVar<T>(name);
    }
    return proxy;
  }

  @Override
  public final T evaluate() {
    return evaluation.get();
  }
}
