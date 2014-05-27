package javareact.server;

import javareact.measurement.ServerMeasurementPeerlet;
import javareact.overlay.OverlayPeerlet;
import protopeer.Experiment;
import protopeer.Peer;
import protopeer.PeerFactory;

public class ServerFactory implements PeerFactory {

  @Override
  public Peer createPeer(int peerIndex, Experiment experiment) {
    Peer newPeer = new Peer(peerIndex);
    newPeer.addPeerlet(new OverlayPeerlet());
    newPeer.addPeerlet(new ServerMeasurementPeerlet());
    newPeer.addPeerlet(new ServerEventForwarder());
    return newPeer;
  }

}
