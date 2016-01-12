package dream.server;

import java.util.logging.Logger;

import dream.common.Consts;
import dream.common.packets.AdvertisementPacket;
import dream.common.packets.EventPacket;
import dream.common.packets.SubscriptionPacket;
import dream.common.packets.locking.TokenServiceAdvertisePacket;
import dream.common.packets.registry.RegistryAdvertisePacket;
import polimi.reds.broker.overlay.GenericOverlay;
import polimi.reds.broker.overlay.Overlay;
import polimi.reds.broker.overlay.SimpleTopologyManager;
import polimi.reds.broker.overlay.TCPTransport;
import polimi.reds.broker.overlay.TopologyManager;
import polimi.reds.broker.overlay.Transport;
import polimi.reds.broker.routing.GenericRouter;

public class ServerLauncher {
  private static ServerLauncher launcher;

  private final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
  private final Overlay overlay;

  private ServerLauncher() {
    Transport tr = new TCPTransport(Consts.serverPort);
    TopologyManager tm = new SimpleTopologyManager();
    overlay = new GenericOverlay(tm, tr);
    GenericRouter router = new GenericRouter(overlay);
    ServerEventForwarder forwarder = new ServerEventForwarder();
    overlay.addNeighborhoodChangeListener(forwarder);
    router.setPacketForwarder(EventPacket.subject, forwarder);
    router.setPacketForwarder(SubscriptionPacket.subject, forwarder);
    router.setPacketForwarder(AdvertisementPacket.subject, forwarder);
    router.setPacketForwarder(RegistryAdvertisePacket.subject, forwarder);
    router.setPacketForwarder(TokenServiceAdvertisePacket.subject, forwarder);
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

}
