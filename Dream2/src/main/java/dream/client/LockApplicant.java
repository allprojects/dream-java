package dream.client;

import dream.common.packets.locking.LockGrantPacket;

/**
 * A LockApplicany can apply for locks and is notified when the lock is granted.
 */
interface LockApplicant {

	/**
	 * Method invoked when a lock is granted.
	 *
	 * @param lockGrant
	 *            the granted lock.
	 */
	void notifyLockGranted(LockGrantPacket lockGrant);

}
