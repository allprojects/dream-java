package javareact.common.types;

public abstract class ReactiveBoolean extends AbstractReactive<Boolean> {
  public ReactiveBoolean(String name, Proxy... proxies) {
    super(name, proxies);
    val = false;
  }
  
  @Override
  public final synchronized BooleanProxy getProxy() {
    return (BooleanProxy)super.getProxy();
  }
}
