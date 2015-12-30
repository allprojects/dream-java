package javareact.common.types;

import java.util.function.Supplier;

public class Signal<T> extends AbstractReactive<T>implements ProxyGenerator {
  private final RemoteVar<T> proxy;
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
    proxy = new RemoteVar<T>(name);
    this.evaluation = evaluation;
  }

  @Override
  public synchronized RemoteVar<T> getProxy() {
    return proxy;
  }

  @Override
  public final T evaluate() {
    return evaluation.get();
  }
}
