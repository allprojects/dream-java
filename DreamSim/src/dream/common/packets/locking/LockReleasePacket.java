package dream.common.packets.locking;

import java.io.Serializable;
import java.util.UUID;

import protopeer.network.Message;
import protopeer.util.quantities.Data;

/**
 * Manages the requests and release of locks. It is not thread safe, so it
 * assumes that requests are sequential (e.g., generated from a single server).
 */
public class LockReleasePacket extends Message implements Serializable {
  private static final long serialVersionUID = -1523880233653918696L;
  public static final String subject = "__DREAM_LOCK_RELEASE_PACKET_SUBJECT";

  private final UUID lockID;

  public LockReleasePacket(UUID lockID) {
    this.lockID = lockID;
  }

  public final UUID getLockID() {
    return lockID;
  }

  @Override
  public Data getSize() {
    // TODO: estimate the real size
    return Data.inKByte(1);
  }

  @Override
  public String toString() {
    return "LockReleasePacket [lockID=" + lockID + "]";
  }

}
