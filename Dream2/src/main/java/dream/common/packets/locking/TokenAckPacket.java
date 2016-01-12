package dream.common.packets.locking;

import java.io.Serializable;

/**
 * This packet is used to notify the token service about the forwarding of
 * events. It contains a final expression a server has processed and the number
 * of times it has been delivered to a client.
 */
public class TokenAckPacket implements Serializable {
  private static final long serialVersionUID = -755350171061415780L;
  public static final String subject = "__DREAM_TOKEN_ACK_PACKET_SUBJECT";

  private final String finalExpression;
  private final int count;

  public TokenAckPacket(String finalExpression, int count) {
    this.finalExpression = finalExpression;
    this.count = count;
  }

  public final String getFinalExpression() {
    return finalExpression;
  }

  public final int getCount() {
    return count;
  }

}
