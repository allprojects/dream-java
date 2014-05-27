package javareact.common.types.reactive;

import javareact.common.packets.content.Value;
import javareact.common.types.Types;

final class ReactiveBoolImpl extends AbstractReactive implements ReactiveBool {

  protected ReactiveBoolImpl(String expression, String name, boolean isPublic) {
    super(expression, Types.BOOL, name, isPublic);
  }

  protected ReactiveBoolImpl(String expression, Value startingValue, String name, boolean isPublic) {
    super(expression, startingValue, Types.BOOL, name, isPublic);
  }

  @Override
  public synchronized boolean get() {
    requiresUpdatedValue();
    return value.boolVal();
  }

}
