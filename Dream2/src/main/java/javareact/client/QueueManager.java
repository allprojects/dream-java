package javareact.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javareact.common.packets.EventPacket;
import javareact.common.packets.content.Event;
import javareact.common.types.EventProxyPair;
import javareact.common.types.Proxy;
import javareact.server.WaitRecommendations;

/**
 * This class is responsible for temporarily accumulating events before delivery
 * to make sure that no glitch can occur.
 */
public class QueueManager {
	// WaitingElements, partitioned by id
	private final Map<UUID, WaitingElement> waitingElements = new HashMap<UUID, WaitingElement>();
	// Candidate results to deliver (they can be effectively delivered only
	// there are no waiting elements).
	private final Set<EventProxyPair> pendingResults = new HashSet<EventProxyPair>();

	public final List<EventProxyPair> processEventPacket(EventProxyPair eventProxyPair, String expression) {
		EventPacket evPkt = eventProxyPair.getEventPacket();
		Proxy proxy = eventProxyPair.getProxy();

		UUID id = evPkt.getId();
		Set<WaitRecommendations> waitingRecommendations = evPkt.hasRecommendationsFor(expression) ? evPkt
				.getRecommendationsFor(expression) : new HashSet<WaitRecommendations>();

		if (waitingElements.containsKey(id)) {
			WaitingElement elem = waitingElements.get(id);
			elem.processEvent(evPkt, proxy);
			if (elem.hasFinishedWaiting()) {
				Map<EventPacket, Proxy> eventProxyMap = elem.getReceivedEvents();
				for (EventPacket eventToDeliver : eventProxyMap.keySet()) {
					Proxy proxyToDeliver = eventProxyMap.get(eventToDeliver);
					pendingResults.add(new EventProxyPair(eventToDeliver, proxyToDeliver));
				}
				waitingElements.remove(id);
			}
		} else {
			Set<String> expressionsToWaitFrom = getExpressionsToWaitFrom(waitingRecommendations);
			if (!expressionsToWaitFrom.isEmpty()) {
				WaitingElement elem = new WaitingElement(expressionsToWaitFrom, evPkt, proxy);
				waitingElements.put(id, elem);
			} else {
				pendingResults.add(new EventProxyPair(evPkt, proxy));
			}
		}

		List<EventProxyPair> result = new ArrayList<EventProxyPair>();
		if (waitingElements.isEmpty()) {
			result.addAll(pendingResults);
			pendingResults.clear();
		}
		return result;
	}

	private final Set<String> getExpressionsToWaitFrom(Set<WaitRecommendations> recommendations) {
		Set<String> result = new HashSet<String>();
		for (WaitRecommendations wr : recommendations) {
			result.addAll(wr.getRecommendations());
		}
		return result;
	}

	private class WaitingElement {
		// Set of expressions we are waiting for before delivering the events
		// with the given id
		private final Set<String> waitingFor = new HashSet<String>();
		// Set of events received with the given id
		private final Map<EventPacket, Proxy> receivedEvents = new HashMap<EventPacket, Proxy>();

		WaitingElement(Set<String> waitingFor, EventPacket evPkt, Proxy proxy) {
			this.waitingFor.addAll(waitingFor);
			receivedEvents.put(evPkt, proxy);
		}

		final void processEvent(EventPacket evPkt, Proxy proxy) {
			Event ev = evPkt.getEvent();
			String signature = ev.getSignature();
			assert (waitingFor.contains(signature));
			waitingFor.remove(signature);
			receivedEvents.put(evPkt, proxy);
		}

		final boolean hasFinishedWaiting() {
			return waitingFor.isEmpty();
		}

		final Map<EventPacket, Proxy> getReceivedEvents() {
			return receivedEvents;
		}
	}
}
