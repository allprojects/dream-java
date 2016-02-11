package dream.client;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import dream.common.ConsistencyType;
import dream.common.Consts;
import dream.common.SerializablePredicate;
import dream.common.packets.EventPacket;
import dream.common.packets.content.Advertisement;
import dream.common.packets.content.Event;
import dream.common.packets.content.Subscription;
import dream.common.packets.locking.LockGrantPacket;

public class Signal<T extends Serializable>
		implements TimeChangingValue<T>, UpdateProducer<T>, UpdateConsumer, LockApplicant {

	// Management of local subscribers
	private final Map<UpdateConsumer, List<SerializablePredicate>> consumers = new HashMap<>();
	private final Queue<EventProducerPair> eventQueue = new ArrayDeque<>();
	private int pendingAcks = 0;
	private final Set<UpdateProducer> waitingProducers = new HashSet<>();

	private final ClientEventForwarder clientEventForwarder;
	private final QueueManager queueManager = new QueueManager();

	private final String host;
	private final String object;
	private final List<SerializablePredicate> constraints = new ArrayList<>();

	private final Supplier<T> evaluation;

	private UUID lockID = null;

	private T val;

	private final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	public Signal(String object, Supplier<T> evaluation, UpdateProducer... prods) {
		this.host = Consts.hostName;
		this.object = object;
		this.evaluation = evaluation;

		final Set<Subscription> subs = new HashSet<>();
		for (final UpdateProducer prod : prods) {
			prod.registerUpdateConsumer(this, prod.getConstraints());
			subs.add(new Subscription(prod.getHost(), prod.getObject(), prod.getConstraints()));
		}

		clientEventForwarder = ClientEventForwarder.get();
		clientEventForwarder.advertise(new Advertisement(Consts.hostName, object), subs, true);
	}

	private final synchronized void processNextUpdate() {
		if (pendingAcks == 0) {
			// Notify that the previous update has finished
			if (!waitingProducers.isEmpty()) {
				waitingProducers.forEach(prod -> prod.notifyUpdateFinished());
				waitingProducers.clear();
			}
			// Process the next packet, if any
			if (!eventQueue.isEmpty()) {
				processUpdate(eventQueue.poll());
			}
		}
	}

	private final void processUpdate(EventProducerPair update) {
		logger.finest("processTask method invoked with " + update);
		final List<EventProducerPair> pairs = queueManager.processEventPacket(update, object + "@" + Consts.hostName);
		logger.finest("The queueManager returned the following pairs " + pairs);

		if (!pairs.isEmpty()) {
			logger.finest("Actual update");
			// Extract information from any of the packets
			final EventPacket anyPkt = pairs.stream().findAny().get().getEventPacket();

			// Compute the new value
			try {
				val = evaluate();
				logger.finest("New value computed for the reactive object: " + val);
			} catch (final Exception e) {
				logger.info(
						"Exception during the evaluation of the expression. Acknowledging the producers, releasing the locks, and returning.");
				pairs.forEach(pair -> pair.getUpdateProducer().notifyUpdateFinished());
				// Release locks, if needed
				if ((Consts.consistencyType == ConsistencyType.COMPLETE_GLITCH_FREE || //
						Consts.consistencyType == ConsistencyType.ATOMIC) && //
						anyPkt.getLockReleaseNodes().contains(object + "@" + host)) {
					clientEventForwarder.sendLockRelease(anyPkt.getId());
				}
				return;
			}

			// Notify local and remote dependent objects
			logger.finest("Sending event to dependent objects.");
			final Event<T> event = new Event<T>(Consts.hostName, object, val);
			// Notify remote subscribers
			clientEventForwarder.sendEvent(anyPkt.getId(), event, anyPkt.getSource());

			final Set<UpdateConsumer> satConsumers = //
			consumers.entrySet().stream()
					.filter(e -> e.getValue().stream()
							.allMatch(constr -> ((SerializablePredicate<T>) constr).test(val)))//
					.map(e -> e.getKey())//
					.collect(Collectors.toSet());
			// Notify local subscribers
			if (!satConsumers.isEmpty()) {
				pairs.forEach(pair -> waitingProducers.add(pair.getUpdateProducer()));
				final EventPacket newEvPkt = new EventPacket(event, anyPkt.getId(), anyPkt.getSource());
				newEvPkt.setLockReleaseNodes(anyPkt.getLockReleaseNodes());
				pendingAcks = satConsumers.size();
				satConsumers.forEach(c -> c.updateFromProducer(newEvPkt, this));
			} else {
				// Acknowledge the producers if there are no pending acks
				logger.finest("Acknowledging the producers.");
				pairs.forEach(pair -> pair.getUpdateProducer().notifyUpdateFinished());
			}

			// Release locks, if needed
			if ((Consts.consistencyType == ConsistencyType.COMPLETE_GLITCH_FREE || //
					Consts.consistencyType == ConsistencyType.ATOMIC) && //
					anyPkt.getLockReleaseNodes().contains(object + "@" + host)) {
				clientEventForwarder.sendLockRelease(anyPkt.getId());
			}

		} else {
			logger.finest(object + ": update call but waiting: " + update);
		}
	}

	@Override
	public final synchronized T evaluate() {
		return evaluation.get();
	}

	public T get() {
		return val;
	}

	public T atomicGet() {
		acquireLock();
		// TODO: this should actually be a copy of the object
		final T currentVal = val;
		releaseLock();
		return currentVal;
	}

	private final synchronized void acquireLock() {
		if (Consts.consistencyType != ConsistencyType.ATOMIC) {
			return;
		}
		clientEventForwarder.sendReadOnlyLockRequest(object + "@" + host, this);
		while (lockID == null) {
			try {
				wait();
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private final synchronized void releaseLock() {
		if (Consts.consistencyType != ConsistencyType.ATOMIC) {
			return;
		}
		clientEventForwarder.sendLockRelease(lockID);
		lockID = null;
	}

	@Override
	public UpdateProducer<T> filter(SerializablePredicate<T> constraint) {
		final List<SerializablePredicate> constrList = new ArrayList<>();
		constrList.add(constraint);
		return new FilteredUpdateProducer<>(this, constrList);
	}

	@Override
	public final synchronized void updateFromProducer(EventPacket packet, UpdateProducer producer) {
		final EventProducerPair pair = new EventProducerPair(packet, producer);
		eventQueue.add(pair);
		logger.finest("Method update called for event " + pair + ". Added to the queue.");
		if (eventQueue.size() == 1) {
			logger.finest("The element is the only one in the queue. Let's process it.");
			processNextUpdate();
		}

	}

	@Override
	public final synchronized void notifyUpdateFinished() {
		pendingAcks--;
		processNextUpdate();
	}

	@Override
	public void registerUpdateConsumer(UpdateConsumer consumer, List<SerializablePredicate> constraints) {
		consumers.put(consumer, constraints);
	}

	@Override
	public void unregisterUpdateConsumer(UpdateConsumer consumer) {
		consumers.remove(consumer);
	}

	@Override
	public String getHost() {
		return host;
	}

	@Override
	public String getObject() {
		return object;
	}

	@Override
	public List<SerializablePredicate> getConstraints() {
		return constraints;
	}

	@Override
	public final synchronized void notifyLockGranted(LockGrantPacket lockGrant) {
		lockID = lockGrant.getLockID();
		notifyAll();
	}

	@Override
	public ChangeEvent<T> change() {
		return new ChangeEvent<T>(this);
	}

}
