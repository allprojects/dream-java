package javareact.common.types;

import javareact.common.packets.content.ValueType;

public class ObservableInteger extends Var<Integer> {
  public ObservableInteger(String observableId, boolean persistent, Integer val) {
    super(observableId, persistent, val);
  }

  public ObservableInteger(String observableId, Integer val) {
    super(observableId, val);
  }
  
  @Override
  public final synchronized IntegerProxy getProxy() {
    return (IntegerProxy)super.getProxy().toProxyOfType(ValueType.INT);
  }
}
