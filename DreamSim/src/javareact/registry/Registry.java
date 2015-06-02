package javareact.registry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javareact.common.Consts;
import javareact.common.Outbox;
import javareact.common.packets.EventPacket;
import javareact.common.packets.SubscriptionPacket;
import javareact.common.packets.content.AdvType;
import javareact.common.packets.content.Subscription;
import javareact.common.packets.registry.RegistryAdvertisePacket;
import javareact.experiments.JavaReactConfiguration;
import protopeer.BasePeerlet;
import protopeer.Experiment;
import protopeer.Peer;
import protopeer.network.Message;
import protopeer.network.NetworkAddress;
import protopeer.time.Timer;
import protopeer.time.TimerListener;
import protopeer.util.quantities.Time;

public class Registry extends BasePeerlet {
  // HostId -> ObservableId -> List of Events
  private final Map<String, Map<String, Collection<EventPacket>>> lastEvents = new HashMap<String, Map<String, Collection<EventPacket>>>();
  private final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
  private final Set<NetworkAddress> brokers = new HashSet<NetworkAddress>();

  @Override
  public void init(Peer peer) {
    super.init(peer);
    connectToBrokers();
    sendRegistryAdvertisement();
  }

  @Override
  public void handleIncomingMessage(Message packet) {
    Outbox outbox = new Outbox();
    if (packet instanceof EventPacket) {
      EventPacket evPkt = (EventPacket) packet;
      logger.finer("Received an event packet " + evPkt);
      processEvent(evPkt);
    } else if (packet instanceof SubscriptionPacket) {
      SubscriptionPacket subPkt = (SubscriptionPacket) packet;
      logger.fine("Received a subscription packet " + subPkt);
      for (EventPacket ev : getInformationFor(subPkt)) {
        logger.fine("Found information for packet " + subPkt + ". Replying with " + ev);
        Collection<NetworkAddress> replyTo = new ArrayList<NetworkAddress>();
        replyTo.add(subPkt.getSourceAddress());
        outbox.add(EventPacket.subject, ev, replyTo);
      }
    } else {
      assert false;
      logger.warning("Received an unknown packet subject");
    }
    deliverPacketsInOutbox(outbox);
  }

  private final void connectToBrokers() {
    for (int i = 1; i <= JavaReactConfiguration.getSingleton().numberOfBrokers; i++) {
      NetworkAddress address = Experiment.getSingleton().getAddressToBindTo(i);
      brokers.add(address);
    }
  }

  private final void sendRegistryAdvertisement() {
    Timer tokenServiceAdvertisementTimer = getPeer().getClock().createNewTimer();
    tokenServiceAdvertisementTimer.addTimerListener(new TimerListener() {
      @Override
      public void timerExpired(Timer timer) {
        for (NetworkAddress broker : brokers) {
          getPeer().sendMessage(broker, new RegistryAdvertisePacket(AdvType.ADV));
        }
      }
    });
    tokenServiceAdvertisementTimer.schedule(Time.inSeconds(Consts.startRegistryAdvertiseAtSecond));
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

  private final Collection<EventPacket> getInformationFor(Iterable<Subscription> subs) {
    Collection<EventPacket> returnVal = new ArrayList<EventPacket>();
    for (Subscription sub : subs) {
      EventPacket evPkt = getInformationFor(sub);
      if (evPkt != null) {
        returnVal.add(evPkt);
      }
    }
    return returnVal;
  }

  /**
   * Returns the last event storing the information required by sub, if any, and null otherwise.
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
    for (EventPacket evPkt : events) {
      if (sub.isSatisfiedBy(evPkt.getEvent())) {
        return evPkt;
      }
    }
    return null;
  }

  private final void deliverPacketsInOutbox(Outbox outbox) {
    for (Message packet : outbox.getPacketsToSend()) {
      for (NetworkAddress recipient : outbox.getRecipientsFor(packet)) {
        getPeer().sendMessage(recipient, packet);
      }
    }
  }

}
