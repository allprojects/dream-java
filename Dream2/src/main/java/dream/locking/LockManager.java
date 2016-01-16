package dream.locking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import dream.common.packets.locking.LockReleasePacket;
import dream.common.packets.locking.LockRequestPacket;
import dream.common.packets.locking.LockType;

class LockManager {
  // Pending requests
  private final List<LockRequestPacket> pendingRequests = new ArrayList<>();

  // All stored locks
  private final Map<UUID, LockRequestPacket> activeLocks = new HashMap<>();

  // Stored read and write locks
  private final Map<String, Integer> readLocks = new HashMap<>();
  private final WriteLockManager writeLocks = new WriteLockManager();

  /**
   * Process the given request. Returns true if the lock can be granted.
   *
   * @param request
   *          the request.
   * @return true if the lock is granted, false otherwise.
   */
  final boolean processLockRequest(LockRequestPacket request) {
    if (canBeGranted(request)) {
      lock(request);
      return true;
    } else {
      pendingRequests.add(request);
      return false;
    }
  }

  /**
   * Process the given release. Returns the set of pending requests that can now
   * obtain a lock.
   *
   * @param release
   *          the release.
   * @return the set of granted locks.
   */
  final Set<LockRequestPacket> processLockRelease(LockReleasePacket release) {
    final UUID lockID = release.getLockID();
    assert activeLocks.containsKey(lockID);
    final LockRequestPacket reqPkt = activeLocks.get(lockID);

    final Set<LockRequestPacket> result = new HashSet<>();
    if (release(reqPkt.getLockID(), reqPkt.getLockNodes(), reqPkt.getType())) {
      final Iterator<LockRequestPacket> it = pendingRequests.iterator();
      while (it.hasNext()) {
        final LockRequestPacket request = it.next();
        if (canBeGranted(request)) {
          lock(request);
          result.add(request);
          it.remove();
        }
      }
    }
    return result;
  }

  private final boolean canBeGranted(LockRequestPacket request) {
    final LockType type = request.getType();
    final Set<String> lockNodes = request.getLockNodes();
    final boolean writeConflicts = lockNodes.stream()//
        .anyMatch(n -> writeLocks.isLocked(n));
    if (writeConflicts) {
      return false;
    }
    final boolean readConflicts = type == LockType.READ_WRITE && //
        lockNodes.stream().anyMatch(n -> readLocks.containsKey(n));
    return !readConflicts;
  }

  private final void lock(LockRequestPacket request) {
    final LockType type = request.getType();
    final Set<String> lockNodes = request.getLockNodes();
    switch (type) {
    case READ_ONLY:
      lockNodes.forEach(n -> {
        final Integer count = readLocks.get(n);
        if (count == null) {
          readLocks.put(n, 1);
        } else {
          readLocks.put(n, count + 1);
        }
      });
      break;
    case READ_WRITE:
      writeLocks.grant(request);
      break;
    default:
      assert false : type;
      break;
    }
  }

  /**
   * Return true if the packet released at least one node
   */
  private final boolean release(UUID lockId, Set<String> lockNodes, LockType type) {
    boolean result = false;
    switch (type) {
    case READ_ONLY:
      for (final String lockNode : lockNodes) {
        final int newCount = readLocks.get(lockNode) - 1;
        if (newCount == 0) {
          readLocks.remove(lockNode);
          activeLocks.remove(lockId);
          result = true;
        } else {
          readLocks.put(lockNode, newCount);
        }
      }
      break;
    case READ_WRITE:
      if (writeLocks.release(lockId)) {
        activeLocks.remove(lockId);
        result = true;
      }
      break;
    default:
      assert false : type;
      break;
    }
    return result;
  }

  /**
   * Store information about the write locks that have been granted.
   */
  private class WriteLockManager {
    private final Map<UUID, Integer> grantedLocks = new HashMap<>();
    private final Map<UUID, Set<String>> lockedNodesMap = new HashMap<>();
    private final Set<String> lockedNodes = new HashSet<>();

    /**
     * Store the lock coming from the given source and having the given lockId.
     */
    void grant(LockRequestPacket request) {
      final UUID lockId = request.getLockID();
      final Set<String> nodesToLock = request.getLockNodes();
      assert!grantedLocks.containsKey(lockId);
      assert!lockedNodesMap.containsKey(lockId);

      grantedLocks.put(lockId, request.getUnlockNodes().size());
      lockedNodesMap.put(lockId, new HashSet<>(nodesToLock));
      lockedNodes.addAll(nodesToLock);
    }

    /**
     * Return true if the given node is already locked.
     */
    boolean isLocked(String node) {
      return lockedNodes.contains(node);
    }

    /**
     * Return true if the lock has been entirely released.
     */
    boolean release(UUID lockId) {
      assert grantedLocks.containsKey(lockId);
      Integer count = grantedLocks.get(lockId);
      if (--count == 0) {
        grantedLocks.remove(lockId);
        lockedNodesMap.get(lockId).forEach(lockedNodes::remove);
        lockedNodesMap.remove(lockId);
        return true;
      } else {
        grantedLocks.put(lockId, count);
        return false;
      }
    }

  }

}
