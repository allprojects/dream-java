package dream.client;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

import dream.common.Consts;
import dream.common.packets.AdvertisementPacket;
import dream.common.packets.EventPacket;
import dream.common.packets.SubscriptionPacket;
import dream.common.packets.content.Advertisement;
import dream.common.packets.content.Event;
import dream.common.packets.content.Subscription;
import dream.common.packets.locking.LockGrantPacket;
import dream.common.packets.locking.LockReleasePacket;
import dream.common.packets.locking.LockRequestPacket;
import dream.common.packets.locking.LockType;
import dream.common.utils.AtomicDependencyDetector;
import dream.common.utils.CompleteGlitchFreeDependencyDetector;
import dream.common.utils.FinalNodesDetector;
import dream.experiments.DreamConfiguration;
import protopeer.BasePeerlet;
import protopeer.Peer;
import protopeer.network.Message;
import protopeer.time.Timer;
import protopeer.util.quantities.Time;

class ClientEventForwarder extends BasePeerlet {
	private ConnectionManager connectionManager;
	private final ClientSubscriptionTable subTable = new ClientSubscriptionTable();
	private DreamConfiguration conf;

	// Dependency detectors
	private final CompleteGlitchFreeDependencyDetector completeGlitchDepDetector = CompleteGlitchFreeDependencyDetector.instance;
	private final AtomicDependencyDetector atomicDepDetector = AtomicDependencyDetector.instance;
	private final FinalNodesDetector finalNodesDetector = FinalNodesDetector.instance;

	// Lock applicants waiting for a grant
	private final Map<UUID, LockApplicant> lockApplicants = new HashMap<>();

	private final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	@Override
	public void init(Peer peer) {
		super.init(peer);
		conf = DreamConfiguration.get();
		connectionManager = (ConnectionManager) peer.getPeerletOfType(ConnectionManager.class);
	}

	@Override
	public void handleIncomingMessage(Message packet) {
		if (packet instanceof AdvertisementPacket) {
			logger.finer("Received an advertisement packet " + packet);
			processAdvertisementFromServer((AdvertisementPacket) packet);
		} else if (packet instanceof SubscriptionPacket) {
			logger.fine("Received a subscription packet " + packet);
			processSubscriptionFromServer((SubscriptionPacket) packet);
		} else if (packet instanceof EventPacket) {
			logger.finer("Received an event packet " + packet);
			processEventFromServer((EventPacket) packet);
		} else if (packet instanceof LockGrantPacket) {
			logger.finer("Received lock grant packet " + packet);
			processLockGrant((LockGrantPacket) packet);
		}
	}

	final void sendEvent(UUID id, Event ev, double creationTime, String initialVar) {
		logger.finer("Sending an event " + ev);
		Set<String> lockReleaseNodes;
		switch (conf.consistencyType) {
		case DreamConfiguration.COMPLETE_GLITCH_FREE:
		case DreamConfiguration.COMPLETE_GLITCH_FREE_OPTIMIZED:
			lockReleaseNodes = completeGlitchDepDetector.getNodesToLockFor(initialVar);
			break;
		case DreamConfiguration.ATOMIC:
		case DreamConfiguration.SIDUP:
			lockReleaseNodes = finalNodesDetector.getFinalNodesFor(initialVar);
			break;
		default:
			lockReleaseNodes = new HashSet<>();
		}

		final EventPacket evPkt = new EventPacket(ev, id, creationTime, initialVar);
		evPkt.setLockReleaseNodes(lockReleaseNodes);
		subTable.getMatchingSubscribers(ev).forEach(s -> s.notifyEventReceived(evPkt));
		if (subTable.needsToDeliverToServer(ev)) {
			connectionManager.sendEvent(evPkt);
		}
	}

	/**
	 * Return false if the lock request is not needed
	 */
	final void sendReadOnlyLockRequest(String node, LockApplicant applicant) {
		if (conf.consistencyType != DreamConfiguration.ATOMIC) {
			assert false : conf.consistencyType;
			logger.warning("Invoked sendReadOnlyLockRequest() even if the consistency level does not require it.");
			return;
		}

		logger.finer("Invoked sendReadOnlyLockRequest for node " + node);
		final Set<String> nodesToLock = new HashSet<>();
		nodesToLock.add(node);

		final UUID lockId = UUID.randomUUID();
		final LockRequestPacket reqPkt = new LockRequestPacket(getPeer().getNetworkAddress(), lockId, nodesToLock,
		    nodesToLock, LockType.READ_ONLY);
		lockApplicants.put(lockId, applicant);

		connectionManager.sendLockRequest(reqPkt);
	}

	/**
	 * Return false if the lock request is not needed
	 */
	final boolean sendReadWriteLockRequest(String source, UUID packetID, LockApplicant applicant) {
		if (conf.consistencyType != DreamConfiguration.COMPLETE_GLITCH_FREE && //
		    conf.consistencyType != DreamConfiguration.COMPLETE_GLITCH_FREE_OPTIMIZED && //
		    conf.consistencyType != DreamConfiguration.ATOMIC && //
		    conf.consistencyType != DreamConfiguration.SIDUP) {
			assert false : conf.consistencyType;
			logger.warning("Invoked sendReadWriteLockRequest() even if the consistency level does not require it.");
			return false;
		}

		logger.finer("Invoked sendReadWriteLockRequest for source " + source);
		Set<String> nodesToLock;
		switch (conf.consistencyType) {
		case DreamConfiguration.COMPLETE_GLITCH_FREE:
		case DreamConfiguration.COMPLETE_GLITCH_FREE_OPTIMIZED:
			nodesToLock = completeGlitchDepDetector.getNodesToLockFor(source);
			break;
		case DreamConfiguration.ATOMIC:
			nodesToLock = atomicDepDetector.getNodesToLockFor(source);
			break;
		case DreamConfiguration.SIDUP:
			nodesToLock = new HashSet<>();
			nodesToLock.add(source);
			break;
		default:
			assert false : conf.consistencyType;
			logger.warning("Invoked sendReadWriteLockRequest() even if the consistency level does not require it.");
			return false;
		}

		final Set<String> releaseNodes = getLockReleaseNodesFor(source);

		if (nodesToLock.isEmpty()) {
			return false;
		}

		final LockRequestPacket reqPkt = new LockRequestPacket(getPeer().getNetworkAddress(), packetID, nodesToLock,
		    releaseNodes, LockType.READ_WRITE);
		lockApplicants.put(packetID, applicant);

		connectionManager.sendLockRequest(reqPkt);
		return true;
	}

	final Set<String> getLockReleaseNodesFor(String source) {
		switch (conf.consistencyType) {
		case DreamConfiguration.COMPLETE_GLITCH_FREE:
		case DreamConfiguration.COMPLETE_GLITCH_FREE_OPTIMIZED:
			return completeGlitchDepDetector.getNodesToLockFor(source);
		case DreamConfiguration.ATOMIC:
		case DreamConfiguration.SIDUP:
			return finalNodesDetector.getFinalNodesFor(source);
		default:
			return new HashSet<>();
		}
	}

	final void sendLockRelease(UUID lockID) {
		if (conf.consistencyType != DreamConfiguration.COMPLETE_GLITCH_FREE && //
		    conf.consistencyType != DreamConfiguration.COMPLETE_GLITCH_FREE_OPTIMIZED && //
		    conf.consistencyType != DreamConfiguration.ATOMIC && //
		    conf.consistencyType != DreamConfiguration.SIDUP) {
			assert false : conf.consistencyType;
			logger.warning("Invoked sendLockRelease() even if the consistency level does not require it.");
			return;
		}

		connectionManager.sendLockRelease(new LockReleasePacket(lockID));
	}

	final void advertise(Advertisement adv) {
		logger.fine("Sending advertisement " + adv);
		connectionManager.sendAdvertisement(adv);
	}

	final void unadvertise(Advertisement adv) {
		logger.fine("Sending unadvertisement " + adv);
		connectionManager.sendUnadvertisement(adv);
	}

	final void advertise(Advertisement adv, Set<Subscription> subs) {
		logger.fine("Sending advertisement " + adv + " with subscriptions " + subs);
		connectionManager.sendAdvertisement(adv, subs);
	}

	final void unadvertise(Advertisement adv, Set<Subscription> subs, boolean isPublic) {
		logger.fine("Sending unadvertisement " + adv + " with subscriptions " + subs);
		connectionManager.sendUnadvertisement(adv);
	}

	final void addSubscription(Subscriber subscriber, Subscription subscription) {
		logger.fine("Adding subscription " + subscription);
		subTable.addSubscription(subscriber, subscription);
		if (!isLocal(subscription)) {
			final Timer timer = getPeer().getClock().createNewTimer();
			timer.addTimerListener(t -> {
				connectionManager.sendSubscription(subscription);
			});
			timer.schedule(Time.inSeconds(Consts.delayBeforeSendingSubscriptions));
		}
	}

	final void removeSubscription(Subscriber subscriber, Subscription subscription) {
		logger.fine("Removing subscription " + subscription);
		subTable.addSubscription(subscriber, subscription);
		if (!isLocal(subscription)) {
			connectionManager.sendSubscription(subscription);
		}
	}

	private final boolean isLocal(Subscription sub) {
		final int clientId = ((TrafficGeneratorPeerlet) getPeer().getPeerletOfType(TrafficGeneratorPeerlet.class))
		    .getClientId();
		final String hostId = Consts.hostPrefix + clientId;
		return sub.getHostId().equals(hostId);
	}

	private final void processEventFromServer(EventPacket evPkt) {
		subTable.getMatchingSubscribers(evPkt.getEvent()).forEach(sub -> sub.notifyEventReceived(evPkt));
	}

	private final void processAdvertisementFromServer(AdvertisementPacket advPkt) {
		// Nothing to do: in the simulation the update detectors are implemented as
		// a singleton object shared between nodes
	}

	private final void processSubscriptionFromServer(SubscriptionPacket subPkt) {
		switch (subPkt.getSubType()) {
		case SUB:
			subTable.addServerSubscription(subPkt.getSubscription());
			break;
		case UNSUB:
			subTable.removeServerSubscription(subPkt.getSubscription());
			break;
		default:
			assert false : subPkt.getSubType();
		}
	}

	private final void processLockGrant(LockGrantPacket lockGrant) {
		final UUID lockID = lockGrant.getLockID();
		assert lockApplicants.containsKey(lockID);
		final LockApplicant applicant = lockApplicants.remove(lockID);
		applicant.notifyLockGranted(lockGrant);
	}

}
