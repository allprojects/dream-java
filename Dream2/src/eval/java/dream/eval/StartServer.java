package dream.eval;

import java.io.IOException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import dream.common.packets.AdvertisementPacket;
import dream.common.packets.EventPacket;
import dream.common.packets.SubscriptionPacket;
import dream.common.packets.discovery.ServerHelloPacket;
import dream.server.ServerEventForwarder;
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

public class StartServer implements NeighborhoodChangeListener {
	private final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	private Overlay overlay = null;

	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println("Usage: StartServer <port> [reds-tcp:address:port]*");
			System.exit(0);
		}
		int port = Integer.parseInt(args[0]);
		List<String> neighbors = Arrays.asList(args);
		new StartServer(port, neighbors.subList(1, neighbors.size()));
	}

	private StartServer(final int port, final List<String> neighbors) {
		logger.info("Starting server");
		final Transport tr = new TCPTransport(port);
		final TopologyManager tm = new SimpleTopologyManager();
		overlay = new GenericOverlay(tm, tr);
		final GenericRouter router = new GenericRouter(overlay);
		final ServerEventForwarder forwarder = new ServerEventForwarder(overlay);
		overlay.addNeighborhoodChangeListener(forwarder);
		router.setPacketForwarder(EventPacket.subject, forwarder);
		router.setPacketForwarder(SubscriptionPacket.subject, forwarder);
		router.setPacketForwarder(AdvertisementPacket.subject, forwarder);
		overlay.addNeighborhoodChangeListener(this);
		overlay.start();
		logger.info("Server started");
		neighbors.forEach(n -> {
			try {
				overlay.addNeighbor(n);
			} catch (ConnectException | MalformedURLException | NotRunningException e) {
				e.printStackTrace();
			}
		});
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
