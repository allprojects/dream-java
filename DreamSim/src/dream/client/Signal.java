package dream.client;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
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
import protopeer.time.Timer;
import protopeer.util.quantities.Time;

public class Signal implements LockApplicant, Subscriber {
  private final ClientEventForwarder forwarder;
  private final QueueManager queueManager = new QueueManager();
  DreamConfiguration conf = DreamConfiguration.get();

  private final Peer peer;
  private final String host;
  private final String object;

  private final Queue<EventPacket> pendingEvents = new LinkedList<>();

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

      // Notify remote subscribers (acquiring locks if needed)
      final EventPacket packet = new EventPacket(event, anyPkt.getId(), anyPkt.getCreationTime(), anyPkt.getSource());
      pendingEvents.add(packet);
      if (pendingEvents.size() == 1) {
        processNextEvent();
      }

      // Release locks, if needed
      if ((conf.consistencyType == DreamConfiguration.COMPLETE_GLITCH_FREE || //
          conf.consistencyType == DreamConfiguration.COMPLETE_GLITCH_FREE_OPTIMIZED || //
          conf.consistencyType == DreamConfiguration.ATOMIC) && //
          anyPkt.getLockReleaseNodes().contains(object + "@" + host)) {
        forwarder.sendLockRelease(anyPkt.getId());
      }

    } else {
      logger.finest(object + ": update call but waiting: " + evPkt);
    }

  }

  private final void processNextEvent() {
    if (!pendingEvents.isEmpty()) {
      final EventPacket nextPacket = pendingEvents.peek();
      // A signal needs to acquire a lock only in the case of optimized complete
      // glitch freedom
      if (conf.consistencyType == DreamConfiguration.COMPLETE_GLITCH_FREE_OPTIMIZED && //
          nextPacket.getLockRequestingNode().equals(object + "@" + host)) {
        final boolean lockRequired = forwarder.sendReadWriteLockRequest(nextPacket.getSource(), nextPacket.getId(), this);
        if (!lockRequired) {
          sendNextEventPacket();
          processNextEvent();
        }
      }
      // Otherwise the update can be immediately processed
      else {
        sendNextEventPacket();
        processNextEvent();
      }
    }
  }

  private final void sendNextEventPacket() {
    assert !pendingEvents.isEmpty();
    final EventPacket p = pendingEvents.poll();
    forwarder.sendEvent(p.getId(), p.getEvent(), p.getCreationTime(), p.getSource());
  }

  public void atomicRead() {
    acquireReadLock();
  }

  private final synchronized void acquireReadLock() {
    if (conf.consistencyType != DreamConfiguration.ATOMIC) {
      logger.warning("Trying to acquire read lock byt consistency is not atomic");
    } else {
      forwarder.sendReadOnlyLockRequest(object + "@" + host, this);
    }
  }

  private final synchronized void releaseLock(UUID lockID) {
    if (conf.consistencyType != DreamConfiguration.ATOMIC) {
      logger.warning("Trying to acquire read lock byt consistency is not atomic");
    } else {
      forwarder.sendLockRelease(lockID);
    }
  }

  @Override
  public final synchronized void notifyLockGranted(LockGrantPacket lockGrant) {
    final UUID lockID = lockGrant.getLockID();
    switch (lockGrant.getType()) {
    // Reply to a read access to the value of the signal
    case READ_ONLY:
      // This is possible only in the case of atomic consistency
      assert conf.consistencyType == DreamConfiguration.ATOMIC;
      final int lockDuration = DreamConfiguration.get().readLockDurationInMs;
      final Timer timer = peer.getClock().createNewTimer();
      timer.addTimerListener(t -> releaseLock(lockID));
      timer.schedule(Time.inMilliseconds(lockDuration));
      break;
    // Reply to a read-write lock (update to the signal)
    case READ_WRITE:
      // This is possible only in the case of optimized complete glitch freedom
      assert conf.consistencyType == DreamConfiguration.COMPLETE_GLITCH_FREE_OPTIMIZED;
      assert !pendingEvents.isEmpty();
      sendNextEventPacket();
      processNextEvent();
      break;
    default:
      assert false : lockGrant.getType();
    }
  }

}
