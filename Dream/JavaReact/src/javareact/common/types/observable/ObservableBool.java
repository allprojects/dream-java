package javareact.common.types.observable;

public class ObservableBool extends Observable {
  private boolean val;

  public ObservableBool(String observableId, boolean persistent, boolean val) {
    super(observableId, persistent);
    set(val);
  }

  public ObservableBool(String observableId, boolean val) {
    super(observableId);
    set(val);
  }

  @ImpactOn(method = { "get" })
  public final void set(boolean val) {
    this.val = val;
  }

  public final boolean get() {
    return val;
  }

}
