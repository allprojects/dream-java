package javareact.client;

import javareact.measurement.ClientMeasurementPeerlet;
import protopeer.Experiment;
import protopeer.Peer;
import protopeer.PeerFactory;

public class ComponentFactory implements PeerFactory {

  @Override
  public Peer createPeer(int peerIndex, Experiment experiment) {
    Peer newPeer = new Peer(peerIndex);
    newPeer.addPeerlet(new ConnectionManager());
    newPeer.addPeerlet(new ClientEventForwarder());
    newPeer.addPeerlet(new ClientMeasurementPeerlet());
    newPeer.addPeerlet(new TrafficGeneratorPeerlet());
    return newPeer;
  }

}
