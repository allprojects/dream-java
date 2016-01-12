package dream.common.packets;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import dream.common.packets.content.Event;

/**
 * Packet used to deliver events, which notify about some state change.
 */
public class EventPacket implements Serializable {
  private static final long serialVersionUID = 8208653909787190211L;
  public static final String subject = "__DREAM_PUBLICATION_PACKET_SUBJECT";

  private final Event event;
  private final UUID id;
  private final String initialVar;
  private boolean approvedByTokenService = false;

  // Final expressions are used in the atomic protocol to determine when the
  // token can be released
  // Upon receiving a final expression, the server acknowledges the token
  // manager
  private final Set<String> finalExpressions = new HashSet<String>();

  public EventPacket(Event event, UUID id, String initialVar, boolean approvedByTokenService) {
    this.event = event;
    this.id = id;
    this.initialVar = initialVar;
    this.approvedByTokenService = approvedByTokenService;
  }

  public final Event getEvent() {
    return event;
  }

  public final UUID getId() {
    return id;
  }

  public final String getInitialVar() {
    return initialVar;
  }

  public final void addFinalExpression(String expression) {
    finalExpressions.add(expression);
  }

  public final Set<String> getFinalExpressions() {
    return finalExpressions;
  }

  public final boolean isFinal() {
    return finalExpressions.contains(event.getSignature());
  }

  public final EventPacket dup() {
    final EventPacket result = new EventPacket(event, id, initialVar, approvedByTokenService);
    result.finalExpressions.addAll(finalExpressions);
    return result;
  }

  public final void tokenServiceApproves() {
    approvedByTokenService = true;
  }

  public final boolean isApprovedByTokenService() {
    return approvedByTokenService;
  }

  @Override
  public String toString() {
    return "EventPacket [event=" + event + ", id=" + id + ", initialVar=" + initialVar + ", finalExpressions=" + finalExpressions + "]";
  }

}
