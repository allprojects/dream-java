package javareact.common.types.reactive;

import javareact.common.packets.content.Value;
import javareact.common.types.Types;
import protopeer.Peer;

final class ReactiveDoubleImpl extends Reactive implements ReactiveDouble {

  protected ReactiveDoubleImpl(Peer peer, String expression, String name, boolean isPublic) {
    super(peer, expression, Types.DOUBLE, name, isPublic);
  }

  protected ReactiveDoubleImpl(Peer peer, String expression, Value startingValue, String name, boolean isPublic) {
    super(peer, expression, startingValue, Types.DOUBLE, name, isPublic);
  }

  @Override
  public synchronized double get() {
    requiresUpdatedValue();
    return value.doubleVal();
  }

}