package javareact.common.packets.token_service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import protopeer.network.Message;
import protopeer.util.quantities.Data;

/**
 * This packet is used to notify the token service about the forwarding of events. It contains a final expression a
 * server has processed and the number of times it has been delivered to a client.
 */
public class TokenAckPacket extends Message {
  private static final long serialVersionUID = -755350171061415780L;
  public static final String subject = "__JAVA_REACT_TOKEN_ACK_PACKET_SUBJECT";

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

  @Override
  public Data getSize() {
    ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
    ObjectOutputStream objectOutputStream;
    try {
      objectOutputStream = new ObjectOutputStream(byteOutputStream);
      objectOutputStream.writeObject(this);
      objectOutputStream.flush();
      objectOutputStream.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return Data.inByte(byteOutputStream.toByteArray().length);
  }

}
