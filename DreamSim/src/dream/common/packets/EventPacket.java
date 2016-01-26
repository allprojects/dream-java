package dream.common.packets;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import dream.common.packets.content.Event;
import dream.common.utils.CompleteGlitchFreeDependencyDetector;
import dream.experiments.DreamConfiguration;
import protopeer.network.Message;
import protopeer.util.quantities.Data;

/**
 * Packet used to deliver events, which notify about some state change.
 */
public class EventPacket extends Message implements Serializable {
  private static final long serialVersionUID = 8208653909787190211L;
  public static final String subject = "__DREAM_PUBLICATION_PACKET_SUBJECT";

  private final Event event;

  // Uniquely identifies a propagation. In the case of complete glitch free and
  // atomic consistency, it is identical to the id of the read lock associated
  // to the propagation.
  private final UUID id;

  // Creation time
  private final double creationTime;

  // Original source of the change
  private final String source;

  // Node that should request the lock for the propagation, if any
  private final String lockRequestingNode;

  // Nodes that should release the lock for the propagation, if any
  private final Set<String> lockReleaseNodes = new HashSet<>();

  public EventPacket(Event event, UUID id, double creationTime, String source) {
    this.event = event;
    this.id = id;
    this.creationTime = creationTime;
    this.source = source;
    this.lockRequestingNode = //
    DreamConfiguration.get().consistencyType == DreamConfiguration.COMPLETE_GLITCH_FREE_OPTIMIZED //
        ? CompleteGlitchFreeDependencyDetector.instance.getLockRequestNodeFor(source) //
        : source;
  }

  public final Event getEvent() {
    return event;
  }

  public final UUID getId() {
    return id;
  }

  public final double getCreationTime() {
    return creationTime;
  }

  public final String getSource() {
    return source;
  }

  public final String getLockRequestingNode() {
    return lockRequestingNode;
  }

  public final void setLockReleaseNodes(Set<String> lockReleaseNodes) {
    this.lockReleaseNodes.addAll(lockReleaseNodes);
  }

  public final Set<String> getLockReleaseNodes() {
    return lockReleaseNodes;
  }

  @Override
  public Data getSize() {
    // TODO: estimate the real size
    return Data.inKByte(1);
  }

  @Override
  public String toString() {
    return "EventPacket [event=" + event + ", id=" + id + ", source=" + source + ", lockReleaseNodes=" + lockReleaseNodes + "]";
  }

}
