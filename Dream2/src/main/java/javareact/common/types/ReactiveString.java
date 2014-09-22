package javareact.common.types;

public abstract class ReactiveString extends AbstractReactive<String> {
  public ReactiveString(String name, Proxy... proxies) {
    super(name, proxies);
    val = "";
  }
  
  @Override
  public final synchronized StringProxy getProxy() {
    return (StringProxy)super.getProxy();
  }
}
