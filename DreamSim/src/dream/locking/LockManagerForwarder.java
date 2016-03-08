package dream.locking;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.logging.Logger;

import dream.common.Outbox;
import dream.common.packets.locking.LockGrantPacket;
import dream.common.packets.locking.LockReleasePacket;
import dream.common.packets.locking.LockRequestPacket;
import dream.experiments.DreamConfiguration;
import protopeer.BasePeerlet;
import protopeer.Peer;
import protopeer.network.Message;
import protopeer.network.NetworkAddress;

public class LockManagerForwarder extends BasePeerlet {
	private final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	private final LockManager lockManager = //
	DreamConfiguration.get().consistencyType == DreamConfiguration.SIDUP //
	    ? new SidUpLockManager() //
	    : new DreamLockManager();

	@Override
	public void init(Peer peer) {
		super.init(peer);
	}

	@Override
	public void handleIncomingMessage(Message packet) {
		final Outbox outbox = new Outbox();
		if (packet instanceof LockRequestPacket) {
			final LockRequestPacket reqPkt = (LockRequestPacket) packet;
			logger.fine("Received a request packet: " + reqPkt);
			processRequestPacket(packet.getSourceAddress(), reqPkt, outbox);
		} else if (packet instanceof LockReleasePacket) {
			final LockReleasePacket relPkt = (LockReleasePacket) packet;
			logger.finer("Received a release packet: " + relPkt);
			processReleasePacket(packet.getSourceAddress(), relPkt, outbox);
		}
		deliverPacketsInOutbox(outbox);
	}

	private final void processRequestPacket(NetworkAddress sender, LockRequestPacket reqPkt, Outbox outbox) {
		final boolean granted = lockManager.processLockRequest(reqPkt);
		if (granted) {
			final Collection<NetworkAddress> recipients = new ArrayList<>(1);
			recipients.add(sender);
			outbox.add(LockGrantPacket.subject, new LockGrantPacket(reqPkt), recipients);
		}
	}

	private final void processReleasePacket(NetworkAddress sender, LockReleasePacket relPkt, Outbox outbox) {
		final Set<LockRequestPacket> granted = lockManager.processLockRelease(relPkt);
		granted.forEach(req -> {
			final Collection<NetworkAddress> recipients = new ArrayList<>(1);
			recipients.add(req.getApplicant());
			outbox.add(LockGrantPacket.subject, new LockGrantPacket(req), recipients);
		});
	}

	private final void deliverPacketsInOutbox(Outbox outbox) {
		outbox.getPacketsToSend().forEach(p -> {
			outbox.getRecipientsFor(p).forEach(r -> getPeer().sendMessage(r, p));
		});
	}

}
