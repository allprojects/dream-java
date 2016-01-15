package dream.common.packets.locking;

import java.io.Serializable;

/**
 * Manages the requests and release of locks. It is not thread safe, so it
 * assumes that requests are sequential (e.g., generated from a single server).
 */
public class LockReleasePacket implements Serializable {
  private static final long serialVersionUID = -1523880233653918696L;
  public static final String subject = "__DREAM_LOCK_RELEASE_PACKET_SUBJECT";

  private final Lock lock;

  public LockReleasePacket(Lock lock) {
    this.lock = lock;
  }

  public final Lock getLock() {
    return lock;
  }

  @Override
  public String toString() {
    return "LockReleasePacket [" + lock + "]";
  }

}
