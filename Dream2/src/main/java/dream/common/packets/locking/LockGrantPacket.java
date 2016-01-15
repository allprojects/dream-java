package dream.common.packets.locking;

import java.io.Serializable;
import java.util.UUID;

public class LockGrantPacket implements Serializable {
  private static final long serialVersionUID = -3499224800050816098L;
  public static final String subject = "__DREAM_LOCK_GRANT_PACKET_SUBJECT";

  private final UUID lockID;

  public LockGrantPacket(LockRequestPacket reqPkt) {
    this.lockID = reqPkt.getLockID();
  }

  final UUID getLockID() {
    return lockID;
  }

  @Override
  public String toString() {
    return "LockGrantPacket [lockID=" + lockID + "]";
  }

}
