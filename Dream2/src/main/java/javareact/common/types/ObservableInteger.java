package javareact.common.types;

public class ObservableInteger extends Var<Integer> {
  public ObservableInteger(String observableId, boolean persistent, Integer val) {
    super(observableId, persistent, val);
  }

  public ObservableInteger(String observableId, Integer val) {
    super(observableId, val);
  }
  
  @Override
  public final synchronized IntegerProxy getProxy() {
    RemoteVar<Integer> proxy = super.getProxy();
    assert proxy instanceof IntegerProxy;
    return (IntegerProxy)proxy;
  }
}
