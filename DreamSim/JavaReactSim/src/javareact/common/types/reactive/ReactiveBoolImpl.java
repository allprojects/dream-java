package javareact.common.types.reactive;

import javareact.common.packets.content.Value;
import javareact.common.types.Types;
import protopeer.Peer;

final class ReactiveBoolImpl extends Reactive implements ReactiveBool {

  protected ReactiveBoolImpl(Peer peer, String expression, String name, boolean isPublic) {
    super(peer, expression, Types.BOOL, name, isPublic);
  }

  protected ReactiveBoolImpl(Peer peer, String expression, Value startingValue, String name, boolean isPublic) {
    super(peer, expression, startingValue, Types.BOOL, name, isPublic);
  }

  @Override
  public synchronized boolean get() {
    requiresUpdatedValue();
    return value.boolVal();
  }

}
