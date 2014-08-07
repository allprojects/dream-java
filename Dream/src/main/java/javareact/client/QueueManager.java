package javareact.client;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javareact.common.packets.EventPacket;
import javareact.common.packets.content.Event;
import javareact.server.WaitRecommendations;

/**
 * This class is responsible for temporarily accumulating events before delivery to make sure that no glitch can occur.
 */
public class QueueManager {
  private final Map<UUID, WaitingElement> waitingElements = new HashMap<UUID, WaitingElement>();

  public final Set<EventPacket> processEventPacket(EventPacket evPkt, String expression) {
    Event ev = evPkt.getEvent();
    UUID id = evPkt.getId();
    Set<String> computedFrom = evPkt.getComputedFrom();
    Set<WaitRecommendations> waitingRecommendations = evPkt.hasRecommendationsFor(expression) ? evPkt.getRecommendationsFor(expression) : new HashSet<WaitRecommendations>();
    Set<EventPacket> results = new HashSet<EventPacket>();
    if (waitingElements.containsKey(id)) {
      WaitingElement elem = waitingElements.get(id);
      elem.processEvent(ev, computedFrom);
      if (elem.hasFinishedWaiting()) {
        for (Event eventToDeliver : elem.getReceivedEvents()) {
          EventPacket eventPktToDeliver = new EventPacket(eventToDeliver, id, elem.getComputedFrom(), true);
          results.add(eventPktToDeliver);
        }
        waitingElements.remove(id);
      }
    } else {
      Set<String> expressionsToWaitFrom = getExpressionsToWaitFrom(waitingRecommendations);
      if (!expressionsToWaitFrom.isEmpty()) {
        WaitingElement elem = new WaitingElement(expressionsToWaitFrom, computedFrom, ev);
        waitingElements.put(id, elem);
      } else {
        results.add(new EventPacket(ev, id, computedFrom, true));
      }
    }
    return results;
  }

  private final Set<String> getExpressionsToWaitFrom(Set<WaitRecommendations> recommendations) {
    Set<String> result = new HashSet<String>();
    for (WaitRecommendations wr : recommendations) {
      result.addAll(wr.getRecommendations());
    }
    return result;
  }

  private class WaitingElement {
    // Set of expressions we are waiting for before delivering the events with the given id
    private final Set<String> waitingFor = new HashSet<String>();
    // Set of events received with the given id
    private final Set<Event> receivedEvents = new HashSet<Event>();
    // Set of computations that triggered the occurrence of the event
    private final Set<String> computedFrom = new HashSet<String>();

    WaitingElement(Set<String> waitingFor, Set<String> computedFrom, Event ev) {
      this.waitingFor.addAll(waitingFor);
      this.computedFrom.addAll(computedFrom);
      receivedEvents.add(ev);
    }

    final void processEvent(Event event, Set<String> computedFrom) {
      String signature = event.getSignature();
      assert (waitingFor.contains(signature));
      waitingFor.remove(signature);
      receivedEvents.add(event);
      this.computedFrom.addAll(computedFrom);
    }

    final boolean hasFinishedWaiting() {
      return waitingFor.isEmpty();
    }

    final Set<Event> getReceivedEvents() {
      return receivedEvents;
    }

    final Set<String> getComputedFrom() {
      return computedFrom;
    }
  }
}
