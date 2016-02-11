package dream.locking;

import java.util.Set;

import dream.common.packets.locking.LockReleasePacket;
import dream.common.packets.locking.LockRequestPacket;

interface LockManager {

  /**
   * Process the given request. Returns true if the lock can be granted.
   *
   * @param request
   *          the request.
   * @return true if the lock is granted, false otherwise.
   */
  boolean processLockRequest(LockRequestPacket request);

  /**
   * Process the given release. Returns the set of pending requests that can now
   * obtain a lock.
   *
   * @param release
   *          the release.
   * @return the set of granted locks.
   */
  Set<LockRequestPacket> processLockRelease(LockReleasePacket release);

}