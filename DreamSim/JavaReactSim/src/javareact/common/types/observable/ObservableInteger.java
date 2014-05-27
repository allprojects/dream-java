package javareact.common.types.observable;

import protopeer.Peer;

public class ObservableInteger extends Observable {
  private int val;

  public ObservableInteger(Peer peer, String observableId, boolean persistent, int val) {
    super(peer, observableId, persistent);
    set(val);
  }

  public ObservableInteger(Peer peer, String observableId, int val) {
    super(peer, observableId);
    set(val);
  }

  @ImpactOn(method = { "get" })
  public final void set(int val) {
    this.val = val;
  }

  public final int get() {
    return val;
  }

}
