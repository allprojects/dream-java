package dream.measurement;

import dream.experiments.DreamConfiguration;
import protopeer.BasePeerlet;
import protopeer.Peer;
import protopeer.network.Message;

/**
 * This peerlet, installed in the lock manager, collects and prints information
 * about the network traffic.
 */
public class LockManagerMeasurementPeerlet extends BasePeerlet {
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
