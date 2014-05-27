package javareact.registry;

import javareact.measurement.ServerMeasurementPeerlet;
import protopeer.Experiment;
import protopeer.Peer;
import protopeer.PeerFactory;

public class RegistryFactory implements PeerFactory {

  @Override
  public Peer createPeer(int peerIndex, Experiment experiment) {
    Peer newPeer = new Peer(peerIndex);
    newPeer.addPeerlet(new Registry());
    newPeer.addPeerlet(new ServerMeasurementPeerlet());
    return newPeer;
  }

}
