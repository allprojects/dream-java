package javareact.common.types.reactive;

import javareact.common.packets.content.Value;
import javareact.common.types.Types;
import protopeer.Peer;

final class ReactiveIntegerImpl extends Reactive implements ReactiveInteger {

  protected ReactiveIntegerImpl(Peer peer, String expression, String name, boolean isPublic) {
    super(peer, expression, Types.INT, name, isPublic);
  }

  protected ReactiveIntegerImpl(Peer peer, String expression, Value startingValue, String name, boolean isPublic) {
    super(peer, expression, startingValue, Types.INT, name, isPublic);
  }

  @Override
  public synchronized int get() {
    requiresUpdatedValue();
    return value.intVal();
  }

}
