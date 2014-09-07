package javareact.common.types;

public class ObservableBool extends Var<Boolean> {
  public ObservableBool(String observableId, boolean persistent, Boolean val) {
    super(observableId, persistent, val);
  }

  public ObservableBool(String observableId, Boolean val) {
    super(observableId, val);
  }
  
  @Override
  public final synchronized BooleanProxy getProxy() {
    return (BooleanProxy)super.getProxy();
  }
}
