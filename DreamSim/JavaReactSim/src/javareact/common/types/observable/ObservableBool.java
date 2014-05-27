package javareact.common.types.observable;

import protopeer.Peer;

public class ObservableBool extends Observable {
  private boolean val;

  public ObservableBool(Peer peer, String observableId, boolean persistent, boolean val) {
    super(peer, observableId, persistent);
    set(val);
  }

  public ObservableBool(Peer peer, String observableId, boolean val) {
    super(peer, observableId);
    set(val);
  }

  @ImpactOn(method = { "get" })
  public final void set(boolean val) {
    this.val = val;
  }

  public final boolean get() {
    return val;
  }

}
