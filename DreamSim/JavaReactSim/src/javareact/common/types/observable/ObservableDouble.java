package javareact.common.types.observable;

import protopeer.Peer;

public class ObservableDouble extends Observable {
  private double val;

  public ObservableDouble(Peer peer, String observableId, boolean persistent, double val) {
    super(peer, observableId, persistent);
    set(val);
  }

  public ObservableDouble(Peer peer, String observableId, double val) {
    super(peer, observableId);
    set(val);
  }

  @ImpactOn(method = { "get" })
  public final void set(double val) {
    this.val = val;
  }

  public final double get() {
    return val;
  }

}
