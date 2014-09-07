package javareact.common.types;

public abstract class ReactiveBoolean extends Signal<Boolean> {
  public ReactiveBoolean(String name, Proxy... proxies) {
    super(name, proxies);
    val = false;
  }
  
  @Override
  public final synchronized BooleanProxy getProxy() {
    return (BooleanProxy)super.getProxy();
  }
}
