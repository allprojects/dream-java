package javareact.common.types.observable;

public class ObservableString extends Observable {
  private String val;

  public ObservableString(String observableId, boolean persistent, String val) {
    super(observableId, persistent);
    set(val);
  }

  public ObservableString(String observableId, String val) {
    super(observableId);
    set(val);
  }

  @ImpactOn(method = { "get" })
  public final void set(String val) {
    this.val = val;
  }

  public final String get() {
    return val;
  }
}
