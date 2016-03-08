package dream.common.packets.locking;

import java.io.Serializable;
import java.util.UUID;

import protopeer.network.Message;
import protopeer.util.quantities.Data;

public class LockGrantPacket extends Message implements Serializable {
	private static final long serialVersionUID = -3499224800050816098L;
	public static final String subject = "__DREAM_LOCK_GRANT_PACKET_SUBJECT";

	private final UUID lockID;
	private final LockType type;

	public LockGrantPacket(LockRequestPacket reqPkt) {
		this.lockID = reqPkt.getLockID();
		this.type = reqPkt.getType();
	}

	public final UUID getLockID() {
		return lockID;
	}

	public final LockType getType() {
		return type;
	}

	@Override
	public Data getSize() {
		// TODO: estimate the real size
		return Data.inKByte(1);
	}

	@Override
	public String toString() {
		return "LockGrantPacket [lockID=" + lockID + "]";
	}

}
