package javareact.common.types.observable;

public class ObservableDouble extends Observable {
  private double val;

  public ObservableDouble(String observableId, boolean persistent, double val) {
    super(observableId, persistent);
    set(val);
  }

  public ObservableDouble(String observableId, double val) {
    super(observableId);
    set(val);
  }

  @ImpactOn(method = { "get" })
  public final void set(double val) {
    this.val = val;
  }

  public final double get() {
    return val;
  }

}
