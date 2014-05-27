package javareact.common.types.reactive;

import javareact.common.packets.content.Value;
import javareact.common.types.Types;

final class ReactiveDoubleImpl extends AbstractReactive implements ReactiveDouble {

  protected ReactiveDoubleImpl(String expression, String name, boolean isPublic) {
    super(expression, Types.DOUBLE, name, isPublic);
  }

  protected ReactiveDoubleImpl(String expression, Value startingValue, String name, boolean isPublic) {
    super(expression, startingValue, Types.DOUBLE, name, isPublic);
  }

  @Override
  public synchronized double get() {
    requiresUpdatedValue();
    return value.doubleVal();
  }

}