package dream.client;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

import dream.common.ConsistencyType;
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
import dream.common.utils.DependencyGraph;
import dream.common.utils.FinalNodesDetector;
import dream.common.utils.InterSourceDependencyDetector;
import dream.common.utils.IntraSourceDependencyDetector;
import dream.eval.utils.EvalUtils;
import polimi.reds.NodeDescriptor;
import polimi.reds.broker.routing.Outbox;
import polimi.reds.broker.routing.PacketForwarder;

class ClientEventForwarder implements PacketForwarder {
	private static ClientEventForwarder self = null;

	private final ConnectionManager connectionManager;
	private final ClientSubscriptionTable subTable;

	// Dependency graph
	private final DependencyGraph dependencyGraph = DependencyGraph.instance;

	// Dependency detectors
	private final IntraSourceDependencyDetector intraDepDetector = IntraSourceDependencyDetector.instance;
	private final InterSourceDependencyDetector interDepDetector = //
			Consts.consistencyType == ConsistencyType.ATOMIC //
					? new AtomicDependencyDetector() //
					: new CompleteGlitchFreeDependencyDetector();
	private final FinalNodesDetector finalNodesDetector = new FinalNodesDetector();

	private final Map<String, Long> trafficPkts = new HashMap<>();
	private final Map<String, Long> trafficBytes = new HashMap<>();

	// Lock applicants waiting for a grant
	private final Map<UUID, LockApplicant> lockApplicants = new HashMap<>();

	private final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	static final ClientEventForwarder get() {
		if (self == null) {
			self = new ClientEventForwarder();
		}
		return self;
	}

	static final void stop() {
		if (self != null) {
			self.stopClient();
			self = null;
		}
	}

	private final void stopClient() {
		connectionManager.stop();
	}

	private ClientEventForwarder() {
		connectionManager = new ConnectionManager();
		subTable = new ClientSubscriptionTable();
		connectionManager.registerForwarder(this, AdvertisementPacket.subject);
		connectionManager.registerForwarder(this, SubscriptionPacket.subject);
		connectionManager.registerForwarder(this, EventPacket.subject);
		connectionManager.registerForwarder(this, LockGrantPacket.subject);
	}

	@Override
	public synchronized Collection<NodeDescriptor> forwardPacket(String subject, NodeDescriptor sender,
			Serializable packet, Collection<NodeDescriptor> neighbors, Outbox outbox) {
		final Collection<NodeDescriptor> result = new ArrayList<NodeDescriptor>();
		if (Consts.enableEvaluation) {
			EvalUtils.updateTraffic(packet, subject, trafficPkts, trafficBytes);
			EvalUtils.saveTrafficToFile(trafficPkts, trafficBytes);
		}
		if (subject.equals(AdvertisementPacket.subject)) {
			assert packet instanceof AdvertisementPacket;
			logger.finer("Received an advertisement packet " + packet);
			processAdvertisementFromServer((AdvertisementPacket) packet);
		} else if (subject.equals(SubscriptionPacket.subject)) {
			assert packet instanceof SubscriptionPacket;
			logger.fine("Received a subscription packet " + packet);
			processSubscriptionFromServer((SubscriptionPacket) packet);
		} else if (subject.equals(EventPacket.subject)) {
			assert packet instanceof EventPacket;
			logger.finer("Received an event packet " + packet);
			processEventFromServer((EventPacket) packet);
		} else if (subject.equals(LockGrantPacket.subject)) {
			assert packet instanceof LockGrantPacket;
			logger.finer("Received lock grant packet " + packet);
			processLockGrant((LockGrantPacket) packet);
		} else {
			assert false : subject;
		}
		return result;
	}

	final synchronized void sendEvent(UUID id, Event<?> ev, String initialVar) {
		logger.finer("Sending an event " + ev);
		Set<String> lockReleaseNodes;
		switch (Consts.consistencyType) {
		case COMPLETE_GLITCH_FREE:
			lockReleaseNodes = interDepDetector.getNodesToLockFor(initialVar);
			break;
		case ATOMIC:
			lockReleaseNodes = finalNodesDetector.getFinalNodesFor(initialVar);
			break;
		default:
			lockReleaseNodes = new HashSet<>();
		}

		if (subTable.needsToDeliverToServer(ev)) {
			connectionManager.sendEvent(id, ev, initialVar, lockReleaseNodes);
		}
	}

	final synchronized void sendReadOnlyLockRequest(Set<String> nodesToLock, LockApplicant applicant) {
		if (Consts.consistencyType != ConsistencyType.ATOMIC) {
			assert false : Consts.consistencyType;
			logger.warning("Invoked sendReadOnlyLockRequest() even if the consistency level does not require it.");
			return;
		}

		logger.finer("Invoked sendReadOnlyLockRequest for nodes " + nodesToLock);

		final LockRequestPacket reqPkt = new LockRequestPacket(connectionManager.getNodeDescriptor(), nodesToLock,
				nodesToLock, LockType.READ_ONLY);
		final UUID lockId = reqPkt.getLockID();
		lockApplicants.put(lockId, applicant);

		connectionManager.sendLockRequest(reqPkt);
	}

	/**
	 * Return false if the lock request is not needed
	 */
	final synchronized boolean sendReadWriteLockRequest(String source, LockApplicant applicant) {
		if (Consts.consistencyType != ConsistencyType.COMPLETE_GLITCH_FREE && //
				Consts.consistencyType != ConsistencyType.ATOMIC) {
			assert false : Consts.consistencyType;
			logger.warning("Invoked sendReadWriteLockRequest() even if the consistency level does not require it.");
			return false;
		}

		logger.finer("Invoked sendReadWriteLockRequest for source " + source);
		final Set<String> nodesToLock = interDepDetector.getNodesToLockFor(source);
		final Set<String> releaseNodes = getLockReleaseNodesFor(source);

		if (nodesToLock.isEmpty()) {
			return false;
		}

		final LockRequestPacket reqPkt = new LockRequestPacket(connectionManager.getNodeDescriptor(), nodesToLock,
				releaseNodes, LockType.READ_WRITE);
		final UUID lockId = reqPkt.getLockID();
		lockApplicants.put(lockId, applicant);

		connectionManager.sendLockRequest(reqPkt);
		return true;
	}

	final synchronized Set<String> getLockReleaseNodesFor(String source) {
		switch (Consts.consistencyType) {
		case COMPLETE_GLITCH_FREE:
			return interDepDetector.getNodesToLockFor(source);
		case ATOMIC:
			return finalNodesDetector.getFinalNodesFor(source);
		default:
			return new HashSet<>();
		}
	}

	final synchronized void sendLockRelease(UUID lockID) {
		if (Consts.consistencyType != ConsistencyType.COMPLETE_GLITCH_FREE && //
				Consts.consistencyType != ConsistencyType.ATOMIC) {
			assert false : Consts.consistencyType;
			logger.warning("Invoked sendLockRelease() even if the consistency level does not require it.");
			return;
		}

		connectionManager.sendLockRelease(new LockReleasePacket(lockID));
	}

	final synchronized void advertise(Advertisement adv, boolean isPublic) {
		logger.fine("Sending advertisement " + adv);
		if (Consts.consistencyType == ConsistencyType.SINGLE_SOURCE_GLITCH_FREE || //
				Consts.consistencyType == ConsistencyType.COMPLETE_GLITCH_FREE || //
				Consts.consistencyType == ConsistencyType.ATOMIC) {
			dependencyGraph.processAdv(adv);
			updateDetectors();
		}
		connectionManager.sendAdvertisement(adv, isPublic);
	}

	final synchronized void unadvertise(Advertisement adv, boolean isPublic) {
		logger.fine("Sending unadvertisement " + adv);
		if (Consts.consistencyType == ConsistencyType.SINGLE_SOURCE_GLITCH_FREE || //
				Consts.consistencyType == ConsistencyType.COMPLETE_GLITCH_FREE || //
				Consts.consistencyType == ConsistencyType.ATOMIC) {
			dependencyGraph.processUnAdv(adv);
			updateDetectors();
		}
		connectionManager.sendUnadvertisement(adv, isPublic);
	}

	final synchronized void advertise(Advertisement adv, Set<Subscription<?>> subs, boolean isPublic) {
		logger.fine("Sending advertisement " + adv + " with subscriptions " + subs);
		if (Consts.consistencyType == ConsistencyType.SINGLE_SOURCE_GLITCH_FREE || //
				Consts.consistencyType == ConsistencyType.COMPLETE_GLITCH_FREE || //
				Consts.consistencyType == ConsistencyType.ATOMIC) {
			dependencyGraph.processAdv(adv, subs);
			updateDetectors();
		}
		connectionManager.sendAdvertisement(adv, subs, isPublic);
	}

	final synchronized void unadvertise(Advertisement adv, Set<Subscription<?>> subs, boolean isPublic) {
		logger.fine("Sending unadvertisement " + adv + " with subscriptions " + subs);
		if (Consts.consistencyType == ConsistencyType.SINGLE_SOURCE_GLITCH_FREE || //
				Consts.consistencyType == ConsistencyType.COMPLETE_GLITCH_FREE || //
				Consts.consistencyType == ConsistencyType.ATOMIC) {
			dependencyGraph.processUnAdv(adv, subs);
			updateDetectors();
		}
		connectionManager.sendUnadvertisement(adv, isPublic);
	}

	final synchronized void addSubscription(Subscriber subscriber, Subscription<?> subscription) {
		logger.fine("Adding subscription " + subscription);
		subTable.addSubscription(subscriber, subscription);
		if (needToSendToServer(subscription)) {
			connectionManager.sendSubscription(subscription);
		}
	}

	final synchronized void removeSubscription(Subscriber subscriber, Subscription<?> subscription) {
		logger.fine("Removing subscription " + subscription);
		subTable.addSubscription(subscriber, subscription);
		if (needToSendToServer(subscription)) {
			connectionManager.sendSubscription(subscription);
		}
	}

	private final boolean needToSendToServer(Subscription<?> sub) {
		return !isLocal(sub);
	}

	private final boolean isLocal(Subscription<?> sub) {
		return sub.getHostId().equals(Consts.getHostName());
	}

	private final void processEventFromServer(EventPacket evPkt) {
		subTable.getMatchingSubscribers(evPkt.getEvent()).forEach(sub -> sub.notifyEventReceived(evPkt));
		if (Consts.consistencyType == ConsistencyType.SINGLE_SOURCE_GLITCH_FREE || //
				Consts.consistencyType == ConsistencyType.COMPLETE_GLITCH_FREE || //
				Consts.consistencyType == ConsistencyType.ATOMIC) {
			subTable.getSignatureOnlyMatchingSubscribers(evPkt.getEvent())
					.forEach(sub -> sub.notifyEventReceived(evPkt));
		}
	}

	private final void processAdvertisementFromServer(AdvertisementPacket advPkt) {
		if (Consts.consistencyType == ConsistencyType.SINGLE_SOURCE_GLITCH_FREE || //
				Consts.consistencyType == ConsistencyType.COMPLETE_GLITCH_FREE || //
				Consts.consistencyType == ConsistencyType.ATOMIC) {
			final Set<Subscription<?>> subs = advPkt.getSubscriptions();
			switch (advPkt.getAdvType()) {
			case ADV:
				if (subs.isEmpty()) {
					dependencyGraph.processAdv(advPkt.getAdvertisement());
				} else {
					dependencyGraph.processAdv(advPkt.getAdvertisement(), subs);
				}
				break;
			case UNADV:
				if (subs.isEmpty()) {
					dependencyGraph.processUnAdv(advPkt.getAdvertisement());
				} else {
					dependencyGraph.processUnAdv(advPkt.getAdvertisement(), subs);
				}
				break;
			}
			updateDetectors();
		}
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

	private final void updateDetectors() {
		finalNodesDetector.consolidate();
		intraDepDetector.consolidate();
		interDepDetector.consolidate();
	}

}
