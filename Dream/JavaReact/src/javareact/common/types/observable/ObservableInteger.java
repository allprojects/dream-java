package javareact.common.types.observable;

public class ObservableInteger extends Observable {
  private int val;

  public ObservableInteger(String observableId, boolean persistent, int val) {
    super(observableId, persistent);
    set(val);
  }

  public ObservableInteger(String observableId, int val) {
    super(observableId);
    set(val);
  }

  @ImpactOn(method = { "get" })
  public final void set(int val) {
    this.val = val;
  }

  public final int get() {
    return val;
  }

}
