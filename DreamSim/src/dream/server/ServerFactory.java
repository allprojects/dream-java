package dream.server;

import dream.measurement.ServerMeasurementPeerlet;
import dream.overlay.OverlayPeerlet;
import protopeer.Experiment;
import protopeer.Peer;
import protopeer.PeerFactory;

public class ServerFactory implements PeerFactory {

  @Override
  public Peer createPeer(int peerIndex, Experiment experiment) {
    final Peer newPeer = new Peer(peerIndex);
    newPeer.addPeerlet(new OverlayPeerlet());
    newPeer.addPeerlet(new ServerMeasurementPeerlet());
    newPeer.addPeerlet(new ServerEventForwarder());
    return newPeer;
  }

}
