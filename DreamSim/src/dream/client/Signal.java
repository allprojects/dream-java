package dream.client;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

import dream.common.packets.EventPacket;
import dream.common.packets.content.Advertisement;
import dream.common.packets.content.Event;
import dream.common.packets.content.Subscription;
import dream.common.packets.locking.LockGrantPacket;
import dream.experiments.DreamConfiguration;
import dream.measurement.MeasurementLogger;
import protopeer.Peer;

public class Signal implements LockApplicant, Subscriber {
  private final ClientEventForwarder forwarder;
  private final QueueManager queueManager = new QueueManager();
  DreamConfiguration conf = DreamConfiguration.get();

  private final Peer peer;
  private final String host;
  private final String object;

  private final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

  public Signal(Peer peer, String host, String object, Set<Subscription> subs) {
    this.peer = peer;
    this.host = host;
    this.object = object;
    forwarder = (ClientEventForwarder) peer.getPeerletOfType(ClientEventForwarder.class);
    forwarder.advertise(new Advertisement(host, object), subs);
    subs.forEach(s -> forwarder.addSubscription(this, s));
  }

  @Override
  public void notifyEventReceived(EventPacket evPkt) {
    logger.finest("processTask method invoked with " + evPkt);
    final List<EventPacket> eventsList = queueManager.processEventPacket(evPkt, object + "@" + host);
    logger.finest("The queueManager returned the following pairs " + eventsList);

    if (!eventsList.isEmpty()) {
      logger.finest("Actual update");
      // Extract information from any of the packets
      final EventPacket anyPkt = eventsList.stream().findAny().get();

      // Save event delay
      // TODO: check we are using the intended delay semantics
      eventsList.forEach(ev -> {
        final double delay = peer.getClock().getCurrentTime() - ev.getCreationTime();
        MeasurementLogger.getLogger().saveDelay(delay);
      });

      // Notify local and remote dependent objects
      logger.finest("Sending event to dependent objects.");
      final Event event = new Event(host, object);
      // Notify remote subscribers
      forwarder.sendEvent(anyPkt.getId(), event, anyPkt.getCreationTime(), anyPkt.getSource());

      // Release locks, if needed
      if ((conf.consistencyType == DreamConfiguration.COMPLETE_GLITCH_FREE || //
          conf.consistencyType == DreamConfiguration.ATOMIC) && //
          anyPkt.getLockReleaseNodes().contains(object + "@" + host)) {
        forwarder.sendLockRelease(anyPkt.getId());
      }

    } else {
      logger.finest(object + ": update call but waiting: " + evPkt);
    }
  }

  public void atomicRead() {
    acquireLock();
  }

  private final synchronized void acquireLock() {
    if (conf.consistencyType != DreamConfiguration.ATOMIC) {
      return;
    }
    forwarder.sendReadOnlyLockRequest(object + "@" + host, this);
  }

  private final synchronized void releaseLock(UUID lockID) {
    if (conf.consistencyType != DreamConfiguration.ATOMIC) {
      return;
    }
    forwarder.sendLockRelease(lockID);
  }

  @Override
  public final synchronized void notifyLockGranted(LockGrantPacket lockGrant) {
    final UUID lockID = lockGrant.getLockID();
    // TODO: properly implement delay
    releaseLock(lockID);
  }

}
