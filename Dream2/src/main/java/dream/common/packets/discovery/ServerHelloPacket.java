package dream.common.packets.discovery;

import java.io.Serializable;

/**
 * This packet is used by a server to introduce itself whenever a client
 * connects to it.
 */
public class ServerHelloPacket implements Serializable {
  private static final long serialVersionUID = 3429557416466895040L;

  public static final String subject = "__DREAM_SERVER_HELLO_PACKET_SUBJECT";
}
