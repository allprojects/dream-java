package dream.client;

import java.util.UUID;

public class LockToken {
	private UUID lockId;
	private final int numLocks;

	LockToken(int numLocks) {
		this.numLocks = numLocks;
	}

	final void setLockId(UUID lockId) {
		this.lockId = lockId;
	}

	final UUID getLockId() {
		return lockId;
	}

	final int getNumLocks() {
		return numLocks;
	}

}
