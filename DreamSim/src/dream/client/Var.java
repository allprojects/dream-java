package dream.client;

import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;

import dream.common.packets.content.Advertisement;
import dream.common.packets.content.Event;
import dream.common.packets.locking.LockGrantPacket;
import dream.experiments.DreamConfiguration;
import protopeer.Peer;

public class Var implements LockApplicant {
  private final ClientEventForwarder forwarder;
  DreamConfiguration conf;

  private final Peer peer;
  private final String host;
  private final String object;

  private final Queue<PendingEvent> pendingEvents = new LinkedList<>();

  public Var(Peer peer, String host, String object) {
    this.peer = peer;
    this.forwarder = (ClientEventForwarder) peer.getPeerletOfType(ClientEventForwarder.class);
    this.host = host;
    this.object = object;
    conf = DreamConfiguration.get();
    forwarder.advertise(new Advertisement(host, object));
  }

  public final void modify() {
    final PendingEvent ev = new PendingEvent(new Event(host, object), peer.getClock().getCurrentTime());
    pendingEvents.add(ev);
    if (pendingEvents.size() == 1) {
      processNextEvent();
    }
  }

  private final void processNextEvent() {
    if (!pendingEvents.isEmpty()) {
      // In the case of complete glitch freedom or atomic consistency, we
      // possibly need to acquire a lock before processing the update
      if (conf.consistencyType == DreamConfiguration.COMPLETE_GLITCH_FREE || //
          conf.consistencyType == DreamConfiguration.ATOMIC) {
        final boolean lockRequired = forwarder.sendReadWriteLockRequest(object + "@" + host, this);
        if (!lockRequired) {
          sendNextEventPacket(UUID.randomUUID());
          processNextEvent();
        }
      }
      // Otherwise the update can be immediately processed
      else {
        sendNextEventPacket(UUID.randomUUID());
        processNextEvent();
      }
    }
  }

  private final void sendNextEventPacket(UUID eventId) {
    assert!pendingEvents.isEmpty();
    final PendingEvent p = pendingEvents.poll();
    forwarder.sendEvent(eventId, p.getEvent(), p.getCreationTime(), p.getEvent().getSignature());
  }

  @Override
  public void notifyLockGranted(LockGrantPacket lockGrant) {
    assert!pendingEvents.isEmpty();
    sendNextEventPacket(lockGrant.getLockID());
    processNextEvent();
  }

  private final class PendingEvent {
    private final Event event;
    private final double creationTime;

    PendingEvent(Event event, double creationTime) {
      super();
      this.event = event;
      this.creationTime = creationTime;
    }

    final Event getEvent() {
      return event;
    }

    final double getCreationTime() {
      return creationTime;
    }

  }

}
