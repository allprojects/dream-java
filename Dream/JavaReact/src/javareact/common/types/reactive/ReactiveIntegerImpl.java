package javareact.common.types.reactive;

import javareact.common.packets.content.Value;
import javareact.common.types.Types;

final class ReactiveIntegerImpl extends AbstractReactive implements ReactiveInteger {

  protected ReactiveIntegerImpl(String expression, String name, boolean isPublic) {
    super(expression, Types.INT, name, isPublic);
  }

  protected ReactiveIntegerImpl(String expression, Value startingValue, String name, boolean isPublic) {
    super(expression, startingValue, Types.INT, name, isPublic);
  }

  @Override
  public synchronized int get() {
    requiresUpdatedValue();
    return value.intVal();
  }

}
