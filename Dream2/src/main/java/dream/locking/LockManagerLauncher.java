package dream.locking;

import java.io.IOException;
import java.util.logging.Logger;

import dream.common.Consts;
import dream.common.packets.discovery.LockManagerHelloPacket;
import dream.common.packets.locking.LockReleasePacket;
import dream.common.packets.locking.LockRequestPacket;
import polimi.reds.NodeDescriptor;
import polimi.reds.broker.overlay.GenericOverlay;
import polimi.reds.broker.overlay.NeighborhoodChangeListener;
import polimi.reds.broker.overlay.NotRunningException;
import polimi.reds.broker.overlay.Overlay;
import polimi.reds.broker.overlay.SimpleTopologyManager;
import polimi.reds.broker.overlay.TCPTransport;
import polimi.reds.broker.overlay.TopologyManager;
import polimi.reds.broker.overlay.Transport;
import polimi.reds.broker.routing.GenericRouter;

public class LockManagerLauncher implements NeighborhoodChangeListener {
  private static LockManagerLauncher launcher;

  private final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
  private final Overlay overlay;

  private LockManagerLauncher() {
    final Transport tr = new TCPTransport(Consts.lockManagerPort);
    final TopologyManager tm = new SimpleTopologyManager();
    overlay = new GenericOverlay(tm, tr);
    final GenericRouter router = new GenericRouter(overlay);
    final LockManagerForwarder forwarder = new LockManagerForwarder();
    router.setPacketForwarder(LockRequestPacket.subject, forwarder);
    router.setPacketForwarder(LockReleasePacket.subject, forwarder);
    overlay.addNeighborhoodChangeListener(this);
  }

  public static final void start() {
    if (launcher == null) {
      launcher = new LockManagerLauncher();
    }
    launcher.logger.info("Starting lock manager");
    launcher.overlay.start();
  }

  public static final void stop() {
    if (launcher != null) {
      launcher.logger.info("Stopping lock manager");
      launcher.overlay.stop();
    }
  }

  @Override
  public void notifyNeighborAdded(NodeDescriptor sender) {
    try {
      overlay.send(LockManagerHelloPacket.subject, new LockManagerHelloPacket(), sender);
    } catch (IOException | NotRunningException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void notifyNeighborDead(NodeDescriptor sender) {
    // Nothing to do
  }

  @Override
  public void notifyNeighborRemoved(NodeDescriptor sender) {
    // Nothing to do
  }

}
