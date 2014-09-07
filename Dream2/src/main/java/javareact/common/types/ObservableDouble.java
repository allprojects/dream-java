package javareact.common.types;

public class ObservableDouble extends Var<Double> {
  public ObservableDouble(String observableId, boolean persistent, Double val) {
    super(observableId, persistent, val);
  }

  public ObservableDouble(String observableId, Double val) {
    super(observableId, val);
  }
  
  @Override
  public final synchronized DoubleProxy getProxy() {
    return (DoubleProxy)super.getProxy();
  }
}
