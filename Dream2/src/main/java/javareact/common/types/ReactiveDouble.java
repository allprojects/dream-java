package javareact.common.types;

public abstract class ReactiveDouble extends Signal<Double> {
  public ReactiveDouble(String name, Proxy... proxies) {
    super(name, proxies);
    val = 0.0;
  }
  
  @Override
  public final synchronized DoubleProxy getProxy() {
    return (DoubleProxy)super.getProxy();
  }
}