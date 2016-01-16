package dream.common.datatypes;

import dream.common.packets.locking.LockGrantPacket;

/**
 * A LockApplicany can apply for locks and is notified when the lock is granted.
 */
public interface LockApplicant {

  /**
   * Method invoked when a lock is granted.
   *
   * @param lockGrant
   *          the granted lock.
   */
  public void notifyLockGranted(LockGrantPacket lockGrant);

}
