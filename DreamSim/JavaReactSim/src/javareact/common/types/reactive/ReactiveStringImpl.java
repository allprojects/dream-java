package javareact.common.types.reactive;

import javareact.common.packets.content.Value;
import javareact.common.types.Types;
import protopeer.Peer;

class ReactiveStringImpl extends Reactive implements ReactiveString {

  protected ReactiveStringImpl(Peer peer, String expression, String name, boolean isPublic) {
    super(peer, expression, Types.STRING, name, isPublic);
  }

  protected ReactiveStringImpl(Peer peer, String expression, Value startingValue, String name, boolean isPublic) {
    super(peer, expression, startingValue, Types.STRING, name, isPublic);
  }

  @Override
  public synchronized String get() {
    requiresUpdatedValue();
    return value.stringVal();
  }
}
