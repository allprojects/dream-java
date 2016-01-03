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
  // Candidate results to deliver (they can be effectively delivered only there
  // are no waiting elements).
  private final Set<EventProxyPair> pendingResults = new HashSet<EventProxyPair>();

  public final List<EventProxyPair> processEventPacket(EventProxyPair eventProxyPair, String expression) {
    final EventPacket evPkt = eventProxyPair.getEventPacket();
    final Proxy proxy = eventProxyPair.getProxy();

    final UUID id = evPkt.getId();
    final Set<WaitRecommendations> waitingRecommendations = evPkt.hasRecommendationsFor(expression) ? evPkt.getRecommendationsFor(expression) : new HashSet<WaitRecommendations>();

    if (waitingElements.containsKey(id)) {
      final WaitingElement elem = waitingElements.get(id);
      elem.processEvent(evPkt, proxy);
      if (elem.hasFinishedWaiting()) {
        final Map<EventPacket, Proxy> eventProxyMap = elem.getReceivedEvents();
        eventProxyMap.entrySet().//
            forEach(e -> pendingResults.add(new EventProxyPair(e.getKey(), e.getValue())));
        waitingElements.remove(id);
      }
    } else {
      final Set<String> expressionsToWaitFrom = getExpressionsToWaitFrom(waitingRecommendations);
      if (!expressionsToWaitFrom.isEmpty()) {
        final WaitingElement elem = new WaitingElement(expressionsToWaitFrom, evPkt, proxy);
        waitingElements.put(id, elem);
      } else {
        pendingResults.add(new EventProxyPair(evPkt, proxy));
      }
    }

    final List<EventProxyPair> result = new ArrayList<EventProxyPair>();
    if (waitingElements.isEmpty()) {
      result.addAll(pendingResults);
      pendingResults.clear();
    }
    return result;
  }

  private final Set<String> getExpressionsToWaitFrom(Set<WaitRecommendations> recommendations) {
    return recommendations.stream().//
        map(wr -> wr.getRecommendations()).//
        collect(HashSet::new, HashSet::addAll, HashSet::addAll);
  }

  private class WaitingElement {
    // Set of expressions we are waiting for before delivering the events with
    // the given id
    private final Set<String> waitingFor = new HashSet<String>();
    // Set of events received with the given id
    private final Map<EventPacket, Proxy> receivedEvents = new HashMap<EventPacket, Proxy>();

    WaitingElement(Set<String> waitingFor, EventPacket evPkt, Proxy proxy) {
      this.waitingFor.addAll(waitingFor);
      receivedEvents.put(evPkt, proxy);
    }

    final void processEvent(EventPacket evPkt, Proxy proxy) {
      final Event ev = evPkt.getEvent();
      final String signature = ev.getSignature();
      assert waitingFor.contains(signature);
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
