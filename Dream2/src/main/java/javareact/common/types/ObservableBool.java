package javareact.common.types;

public class ObservableBool extends Var<Boolean> {
  public ObservableBool(String observableId, Boolean val) {
    super(observableId, val);
  }

  @Override
  public final synchronized BooleanProxy getProxy() {
    return super.getProxy().toBooleanProxy();
  }
}
