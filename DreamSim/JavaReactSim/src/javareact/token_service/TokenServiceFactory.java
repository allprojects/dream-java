package javareact.token_service;

import javareact.measurement.ServerMeasurementPeerlet;
import protopeer.Experiment;
import protopeer.Peer;
import protopeer.PeerFactory;

public class TokenServiceFactory implements PeerFactory {

  @Override
  public Peer createPeer(int peerIndex, Experiment experiment) {
    Peer newPeer = new Peer(peerIndex);
    newPeer.addPeerlet(new TokenService());
    newPeer.addPeerlet(new ServerMeasurementPeerlet());
    return newPeer;
  }

}
