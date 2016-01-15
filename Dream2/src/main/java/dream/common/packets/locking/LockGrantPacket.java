package dream.common.packets.locking;

import java.io.Serializable;

public class LockGrantPacket implements Serializable {
  private static final long serialVersionUID = -3499224800050816098L;
  public static final String subject = "__DREAM_LOCK_GRANT_PACKET_SUBJECT";

  private final Lock lock;

  public LockGrantPacket(Lock lock) {
    this.lock = lock;
  }

  public final Lock getLock() {
    return lock;
  }

  @Override
  public String toString() {
    return "LockGrantPacket [" + lock + "]";
  }

}
