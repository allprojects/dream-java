package dream.measurement;

import dream.experiments.DreamConfiguration;
import protopeer.BasePeerlet;
import protopeer.Peer;
import protopeer.network.Message;

/**
 * This peerlet, installed in every server, collects and prints information
 * about the network traffic.
 */
public class ServerMeasurementPeerlet extends BasePeerlet {
  private MeasurementLogger mLogger;

  @Override
  public void init(Peer peer) {
    super.init(peer);
    mLogger = MeasurementLogger.getLogger();
  }

  @Override
  public void handleOutgoingMessage(Message msg) {
    for (int i = 0; i < DreamConfiguration.get().linkLength; i++) {
      mLogger.saveMessage(msg);
    }
  }

}
