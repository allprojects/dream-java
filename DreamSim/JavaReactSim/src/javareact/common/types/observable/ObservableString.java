package javareact.common.types.observable;

import protopeer.Peer;

public class ObservableString extends Observable {
  private String val;

  public ObservableString(Peer peer, String observableId, boolean persistent, String val) {
    super(peer, observableId, persistent);
    set(val);
  }

  public ObservableString(Peer peer, String observableId, String val) {
    super(peer, observableId);
    set(val);
  }

  @ImpactOn(method = { "get" })
  public final void set(String val) {
    this.val = val;
  }

  public final String get() {
    return val;
  }
}
