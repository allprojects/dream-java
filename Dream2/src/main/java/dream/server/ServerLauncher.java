package dream.server;

import java.io.IOException;
import java.util.logging.Logger;

import dream.common.Consts;
import dream.common.packets.AdvertisementPacket;
import dream.common.packets.EventPacket;
import dream.common.packets.SubscriptionPacket;
import dream.common.packets.discovery.ServerHelloPacket;
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

public class ServerLauncher implements NeighborhoodChangeListener {
  private static ServerLauncher launcher;

  private final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
  private final Overlay overlay;

  private ServerLauncher() {
    final Transport tr = new TCPTransport(Consts.serverPort);
    final TopologyManager tm = new SimpleTopologyManager();
    overlay = new GenericOverlay(tm, tr);
    final GenericRouter router = new GenericRouter(overlay);
    final ServerEventForwarder forwarder = new ServerEventForwarder();
    overlay.addNeighborhoodChangeListener(forwarder);
    router.setPacketForwarder(EventPacket.subject, forwarder);
    router.setPacketForwarder(SubscriptionPacket.subject, forwarder);
    router.setPacketForwarder(AdvertisementPacket.subject, forwarder);
    overlay.addNeighborhoodChangeListener(this);
  }

  public static final void start() {
    if (launcher == null) {
      launcher = new ServerLauncher();
    }
    launcher.logger.info("Starting server");
    launcher.overlay.start();
  }

  public static final void stop() {
    if (launcher != null) {
      launcher.logger.info("Stopping server");
      launcher.overlay.stop();
    }
  }

  @Override
  public void notifyNeighborAdded(NodeDescriptor sender) {
    try {
      overlay.send(ServerHelloPacket.subject, new ServerHelloPacket(), sender);
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
