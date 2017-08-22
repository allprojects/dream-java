package dream.client;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import dream.common.ConsistencyType;
import dream.common.Consts;
import dream.common.SerializablePredicate;
import dream.common.packets.EventPacket;
import dream.common.packets.content.Advertisement;
import dream.common.packets.content.Event;
import dream.common.packets.locking.LockGrantPacket;

public class Var<T extends Serializable> implements UpdateProducer<T>, LockApplicant {
	private final ClientEventForwarder forwarder;

	private final String host;
	private final String object;
	private final List<SerializablePredicate<T>> constraints = new ArrayList<>();

	private final Map<UpdateConsumer, List<SerializablePredicate<T>>> consumers = new HashMap<>();
	private final Queue<TimestampedModification> waitingModifications = new ArrayDeque<>();
	private int pendingAcks = 0;

	private T val;

	public Var(String object, T val) {
		this.forwarder = ClientEventForwarder.get();
		this.host = Consts.getHostName();
		this.object = object;
		this.val = val;
		forwarder.advertise(new Advertisement(Consts.getHostName(), object), true);
	}

	public final synchronized void set(T val) {
		final long timestamp = System.nanoTime();
		final Supplier<T> supplier = () -> val;
		waitingModifications.add(new TimestampedModification(supplier, timestamp));
		if (waitingModifications.size() == 1) {
			tryToProcessNextUpdate();
		}
	}

	public final synchronized void modify(Consumer<T> modification) {
		final long timestamp = System.nanoTime();
		waitingModifications.add(new TimestampedModification(modification, timestamp));
		if (waitingModifications.size() == 1) {
			tryToProcessNextUpdate();
		}
	}

	public final synchronized T get() {
		assert val != null;
		return val;
	}

	private final void tryToProcessNextUpdate() {
		if (pendingAcks == 0 && !waitingModifications.isEmpty()) {
			// In the case of complete glitch freedom or atomic consistency, we
			// possibly need to acquire a lock before processing the update
			if (Consts.consistencyType == ConsistencyType.COMPLETE_GLITCH_FREE || //
					Consts.consistencyType == ConsistencyType.ATOMIC) {
				final boolean lockRequired = forwarder.sendReadWriteLockRequest(object + "@" + host, this);
				if (!lockRequired) {
					processNextUpdate(UUID.randomUUID());
				}
			}
			// Otherwise the update can be immediately processed
			else {
				processNextUpdate(UUID.randomUUID());
			}
		}
	}

	private final void processNextUpdate(UUID eventId) {
		final TimestampedModification tMod = waitingModifications.poll();
		final Object mod = tMod.getModification();
		final long timestamp = tMod.getTimestamp();

		// Apply modification
		if (mod instanceof Consumer) {
			@SuppressWarnings("unchecked")
			final Consumer<T> consumer = (Consumer<T>) mod;
			consumer.accept(val);
		} else if (mod instanceof Supplier) {
			@SuppressWarnings("unchecked")
			final Supplier<T> supplier = (Supplier<T>) mod;
			val = supplier.get();
		}

		// Propagate modification to local and remote subscribers
		final Event<? extends Serializable> ev = new Event<>(Consts.getHostName(), object, val);
		final String source = ev.getSignature();
		final EventPacket packet = new EventPacket(ev, eventId, source, System.nanoTime());
		packet.setLockReleaseNodes(forwarder.getLockReleaseNodesFor(source));

		final Set<UpdateConsumer> satConsumers = //
				consumers.entrySet().stream().filter(e -> e.getValue().stream().allMatch(constr -> constr.test(val)))//
						.map(e -> e.getKey())//
						.collect(Collectors.toSet());

		pendingAcks = satConsumers.size();
		satConsumers.forEach(c -> c.updateFromProducer(packet, this));

		forwarder.sendEvent(eventId, ev, ev.getSignature(), timestamp);
	}

	@Override
	public UpdateProducer<T> filter(SerializablePredicate<T> constraint) {
		final List<SerializablePredicate<T>> constrList = new ArrayList<>();
		constrList.add(constraint);
		return new FilteredUpdateProducer<T>(this, constrList);
	}

	@Override
	public final void notifyUpdateFinished() {
		pendingAcks--;
		tryToProcessNextUpdate();
	}

	@Override
	public void registerUpdateConsumer(UpdateConsumer consumer, List<SerializablePredicate<T>> constraints) {
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
	public List<SerializablePredicate<T>> getConstraints() {
		return constraints;
	}

	@Override
	public void notifyLockGranted(LockGrantPacket lockGrant) {
		processNextUpdate(lockGrant.getLockID());
	}

	private class TimestampedModification {
		private final Object modification;
		private final long timestamp;

		TimestampedModification(Object modification, long timestamp) {
			super();
			this.modification = modification;
			this.timestamp = timestamp;
		}

		final Object getModification() {
			return modification;
		}

		final long getTimestamp() {
			return timestamp;
		}

	}

}
