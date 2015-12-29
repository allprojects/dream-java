package javareact.common.types;

public class ObservableInteger extends Var<Integer> {
  public ObservableInteger(String observableId, Integer val) {
    super(observableId, val);
  }

  @Override
  public final synchronized IntegerProxy getProxy() {
    return super.getProxy().toIntegerProxy();
  }
}
