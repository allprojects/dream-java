package javareact.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javareact.common.packets.EventPacket;
import javareact.common.types.EventProducerPair;
import javareact.common.utils.DependencyDetector;
import javareact.common.utils.WaitRecommendations;

/**
 * This class is responsible for temporarily accumulating events before delivery
 * to make sure that no glitch can occur.
 */
public class QueueManager {
  // WaitingElements, partitioned by id
  private final Map<UUID, WaitingElement> waitingElements = new HashMap<UUID, WaitingElement>();
  // Candidate results to deliver (they can be effectively delivered only there
  // are no waiting elements).
  private final Set<EventProducerPair> pendingResults = new HashSet<>();

  public final List<EventProducerPair> processEventPacket(EventProducerPair event, String expression) {
    final EventPacket evPkt = event.getEventPacket();

    final UUID id = evPkt.getId();
    final Set<WaitRecommendations> waitingRecommendations = DependencyDetector.instance.getWaitRecommendations(evPkt.getEvent(), evPkt.getInitialVar());

    if (waitingElements.containsKey(id)) {
      final WaitingElement elem = waitingElements.get(id);
      elem.processEvent(event);
      if (elem.hasFinishedWaiting()) {
        elem.getReceivedEvents().forEach(pendingResults::add);
        waitingElements.remove(id);
      }
    } else {
      final Set<String> expressionsToWaitFrom = getExpressionsToWaitFrom(waitingRecommendations);
      if (!expressionsToWaitFrom.isEmpty()) {
        final WaitingElement elem = new WaitingElement(expressionsToWaitFrom, event);
        waitingElements.put(id, elem);
      } else {
        pendingResults.add(event);
      }
    }

    final List<EventProducerPair> result = new ArrayList<>();
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
    private final Set<String> waitingFor = new HashSet<>();
    // Set of events received with the given id
    private final Set<EventProducerPair> receivedEvents = new HashSet<>();

    WaitingElement(Set<String> waitingFor, EventProducerPair event) {
      this.waitingFor.addAll(waitingFor);
      receivedEvents.add(event);
    }

    final void processEvent(EventProducerPair event) {
      final String signature = event.getEventPacket().getEvent().getSignature();
      assert waitingFor.contains(signature);
      waitingFor.remove(signature);
      receivedEvents.add(event);
    }

    final boolean hasFinishedWaiting() {
      return waitingFor.isEmpty();
    }

    final Set<EventProducerPair> getReceivedEvents() {
      return receivedEvents;
    }
  }
}
