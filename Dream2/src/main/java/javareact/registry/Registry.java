package javareact.registry;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

import javareact.common.packets.EventPacket;
import javareact.common.packets.SubscriptionPacket;
import javareact.common.packets.content.Subscription;
import polimi.reds.NodeDescriptor;
import polimi.reds.broker.routing.Outbox;
import polimi.reds.broker.routing.PacketForwarder;

public class Registry implements PacketForwarder {
	// HostId -> ObservableId -> List of Events
	private final Map<String, Map<String, Collection<EventPacket>>> lastEvents = new HashMap<String, Map<String, Collection<EventPacket>>>();
	private final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	@Override
	public Collection<NodeDescriptor> forwardPacket(String subject, NodeDescriptor sender, Serializable packet,
			Collection<NodeDescriptor> neighbors, Outbox outbox) {
		if (subject.equals(EventPacket.subject)) {
			assert (packet instanceof EventPacket);
			EventPacket evPkt = (EventPacket) packet;
			logger.finer("Received an event packet " + evPkt);
			processEvent(evPkt);
		} else if (subject.equals(SubscriptionPacket.subject)) {
			assert (packet instanceof SubscriptionPacket);
			SubscriptionPacket subPkt = (SubscriptionPacket) packet;
			logger.fine("Received a subscription packet " + subPkt);
			EventPacket ev = getInformationFor(subPkt.getSubscription());
			if (ev != null) {
				logger.fine("Found information for packet " + subPkt + ". Replying with " + ev);
				Collection<NodeDescriptor> replyTo = new ArrayList<NodeDescriptor>();
				replyTo.add(sender);
				outbox.add(EventPacket.subject, ev, replyTo);
			}
		} else {
			assert false : subject;
		}
		return new ArrayList<NodeDescriptor>();
	}

	/**
	 * Stores the given event and deletes events that it overrides.
	 */
	private final void processEvent(EventPacket ev) {
		String hostId = ev.getEvent().getHostId();
		String observableId = ev.getEvent().getObservableId();
		Map<String, Collection<EventPacket>> observableIdMap = lastEvents.get(hostId);
		if (observableIdMap == null) {
			observableIdMap = new HashMap<String, Collection<EventPacket>>();
			lastEvents.put(hostId, observableIdMap);
		}
		Collection<EventPacket> events = observableIdMap.get(observableId);
		if (events == null) {
			events = new ArrayList<EventPacket>();
			observableIdMap.put(observableId, events);
		}
		Iterator<EventPacket> eventsIt = events.iterator();
		while (eventsIt.hasNext()) {
			EventPacket storedEvent = eventsIt.next();
			if (storedEvent.getEvent().containsTheSameInformationAs(ev.getEvent())) {
				eventsIt.remove();
			}
		}
		events.add(ev);
	}

	/**
	 * Returns the last event storing the information required by sub, if any,
	 * and null otherwise.
	 */
	private final EventPacket getInformationFor(Subscription sub) {
		Map<String, Collection<EventPacket>> observableIdMap = lastEvents.get(sub.getHostId());
		if (observableIdMap == null) {
			return null;
		}
		Collection<EventPacket> events = observableIdMap.get(sub.getObservableId());
		if (events == null) {
			return null;
		}
		for (EventPacket ev : events) {
			if (sub.isSatisfiedBy(ev.getEvent())) {
				return ev;
			}
		}
		return null;
	}

}
