package javareact.common.types.observable;

public class Var<T> extends Observable {
  private T val;

  public Var(String observableId, boolean persistent, T val) {
    super(observableId, persistent);
    set(val);
  }

  public Var(String observableId, T val) {
    super(observableId);
    set(val);
  }

  @ImpactOn(method = { "get" })
  public final void set(T val) {
    this.val = val;
  }

  public final T get() {
    return val;
  }

}
