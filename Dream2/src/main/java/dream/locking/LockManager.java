package dream.locking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import dream.common.packets.locking.LockReleasePacket;
import dream.common.packets.locking.LockRequestPacket;
import dream.common.packets.locking.LockType;

class LockManager {
  private final List<LockRequestPacket> pendingRequests = new ArrayList<>();
  private final Map<String, Integer> readLocks = new HashMap<>();
  private final Set<String> writeLocks = new HashSet<>();

  /**
   * Process the given request. Returns true if the lock can be granted.
   *
   * @param request
   *          the request.
   * @return true if the lock is granted, false otherwise.
   */
  final boolean processLockRequest(LockRequestPacket request) {
    if (canBeGranted(request)) {
      pendingRequests.add(request);
      return false;
    } else {
      lock(request);
      return true;
    }
  }

  /**
   * Process the given release. Returns the set of pending requests that can now
   * obtain a lock.
   *
   * @param release
   *          the release.
   * @return the set of requests that obtain the lock.
   */
  final Set<LockRequestPacket> processLockRelease(LockReleasePacket release) {
    unlock(release);
    final Set<LockRequestPacket> result = new HashSet<>();
    final Iterator<LockRequestPacket> it = pendingRequests.iterator();
    while (it.hasNext()) {
      final LockRequestPacket request = it.next();
      if (canBeGranted(request)) {
        lock(request);
        result.add(request);
        it.remove();
      }
    }
    return result;
  }

  private final boolean canBeGranted(LockRequestPacket request) {
    final LockType type = request.getType();
    final Set<String> nodes = request.getNodes();
    return nodes.stream().anyMatch(n -> writeLocks.contains(n)) || //
        type == LockType.READ_WRITE && nodes.stream().anyMatch(n -> readLocks.containsKey(n));
  }

  private final void lock(LockRequestPacket request) {
    final LockType type = request.getType();
    final Set<String> nodes = request.getNodes();
    switch (type) {
    case READ_ONLY:
      nodes.forEach(n -> {
        final Integer count = readLocks.get(n);
        if (count == null) {
          readLocks.put(n, 1);
        } else {
          readLocks.put(n, count + 1);
        }
      });
      break;
    case READ_WRITE:
      writeLocks.addAll(nodes);
      break;
    default:
      assert false : type;
      break;
    }
  }

  private final void unlock(LockReleasePacket release) {
    final LockType type = release.getType();
    final Set<String> nodes = release.getNodes();
    switch (type) {
    case READ_ONLY:
      nodes.forEach(n -> {
        final int newCount = readLocks.get(n) - 1;
        if (newCount == 0) {
          readLocks.remove(n);
        } else {
          readLocks.put(n, newCount);
        }
      });
      break;
    case READ_WRITE:
      writeLocks.removeAll(nodes);
      break;
    default:
      assert false : type;
      break;
    }
  }

}
