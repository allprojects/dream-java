package javareact.common.types.observable;

import java.util.function.Consumer;
import javareact.common.packets.content.Attribute;

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

  public final void set(T val) {
    this.val = val;
    impactOnGet();
  }
  
  public final void update(Consumer<T> modification) {
    modification.accept(this.val);
    impactOnGet();
  }

  public final T get() {
    return val;
  }
  
  private final void impactOnGet() {
    Attribute[] attrs = new Attribute[1];
    try {
      attrs[0] = new Attribute("get()", get());
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    sendEvent(attrs);
  }
}
