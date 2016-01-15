package dream.common.packets.locking;

import java.io.Serializable;

public class LockRequestPacket implements Serializable {
  private static final long serialVersionUID = -1523880233653918696L;
  public static final String subject = "__DREAM_LOCK_REQUEST_PACKET_SUBJECT";

  private final Lock lock;

  public LockRequestPacket(Lock lock) {
    this.lock = lock;
  }

  public final Lock getLock() {
    return lock;
  }

  @Override
  public String toString() {
    return "LockRequestPacket [" + lock + "]";
  }

}
