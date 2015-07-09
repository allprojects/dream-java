package javareact.common.types;

import javareact.common.packets.content.ValueType;

public abstract class ReactiveInteger extends AbstractReactive<Integer> {
  public ReactiveInteger(String name, Proxy... proxies) {
    super(name, proxies);
    val = 0;
  }
  
  @Override
  public final synchronized IntegerProxy getProxy() {
    return (IntegerProxy)super.getProxy().toProxyOfType(ValueType.INT);
  }
}
