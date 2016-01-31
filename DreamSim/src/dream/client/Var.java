package dream.client;

import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;

import dream.common.packets.EventPacket;
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

  private final Queue<EventPacket> pendingEvents = new LinkedList<>();

  public Var(Peer peer, String host, String object) {
    this.peer = peer;
    this.forwarder = (ClientEventForwarder) peer.getPeerletOfType(ClientEventForwarder.class);
    this.host = host;
    this.object = object;
    conf = DreamConfiguration.get();
    forwarder.advertise(new Advertisement(host, object));
  }

  public final void modify() {
    final EventPacket ev = new EventPacket(new Event(host, object), UUID.randomUUID(), peer.getClock().getCurrentTime(), object + "@" + host);
    pendingEvents.add(ev);
    if (pendingEvents.size() == 1) {
      processNextEvent();
    }
  }

  private final void processNextEvent() {
    if (!pendingEvents.isEmpty()) {
      final EventPacket packet = pendingEvents.peek();
      // In the case of complete glitch freedom or atomic consistency, we
      // possibly need to acquire a lock before processing the update
      if (conf.consistencyType == DreamConfiguration.COMPLETE_GLITCH_FREE || //
          conf.consistencyType == DreamConfiguration.ATOMIC || //
          conf.consistencyType == DreamConfiguration.SIDUP || //
          conf.consistencyType == DreamConfiguration.COMPLETE_GLITCH_FREE_OPTIMIZED && //
              packet.getLockRequestingNode().equals(object + "@" + host)) {
        final boolean lockRequired = forwarder.sendReadWriteLockRequest(packet.getSource(), packet.getId(), this);
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
    forwarder.sendEvent(p.getId(), p.getEvent(), p.getCreationTime(), p.getEvent().getSignature());
  }

  @Override
  public void notifyLockGranted(LockGrantPacket lockGrant) {
    assert !pendingEvents.isEmpty();
    sendNextEventPacket();
    processNextEvent();
  }

}
