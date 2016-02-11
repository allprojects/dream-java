package dream.locking;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.logging.Logger;

import dream.common.packets.locking.LockGrantPacket;
import dream.common.packets.locking.LockReleasePacket;
import dream.common.packets.locking.LockRequestPacket;
import polimi.reds.NodeDescriptor;
import polimi.reds.broker.routing.Outbox;
import polimi.reds.broker.routing.PacketForwarder;

public class LockManagerForwarder implements PacketForwarder {
	private final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	private final LockManager lockManager = new LockManager();

	@Override
	public final Collection<NodeDescriptor> forwardPacket(String subject, NodeDescriptor sender, Serializable packet,
			Collection<NodeDescriptor> neighbors, Outbox outbox) {
		if (subject.equals(LockRequestPacket.subject)) {
			assert packet instanceof LockRequestPacket;
			final LockRequestPacket reqPkt = (LockRequestPacket) packet;
			logger.fine("Received a request packet: " + reqPkt);
			processRequestPacket(sender, reqPkt, outbox);
		} else if (subject.equals(LockReleasePacket.subject)) {
			assert packet instanceof LockReleasePacket;
			final LockReleasePacket relPkt = (LockReleasePacket) packet;
			logger.finer("Received a release packet: " + relPkt);
			processReleasePacket(sender, relPkt, outbox);
		} else {
			assert false;
			logger.warning("Received an unknown packet subject");
		}
		return new ArrayList<NodeDescriptor>();
	}

	private final void processRequestPacket(NodeDescriptor sender, LockRequestPacket reqPkt, Outbox outbox) {
		final boolean granted = lockManager.processLockRequest(reqPkt);
		if (granted) {
			final Collection<NodeDescriptor> recipients = new ArrayList<>(1);
			recipients.add(sender);
			outbox.add(LockGrantPacket.subject, new LockGrantPacket(reqPkt), recipients);
		}
	}

	private final void processReleasePacket(NodeDescriptor sender, LockReleasePacket relPkt, Outbox outbox) {
		final Set<LockRequestPacket> granted = lockManager.processLockRelease(relPkt);
		granted.forEach(req -> {
			final Collection<NodeDescriptor> recipients = new ArrayList<>(1);
			recipients.add(req.getApplicant());
			outbox.add(LockGrantPacket.subject, new LockGrantPacket(req), recipients);
		});
	}

}
