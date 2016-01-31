package dream.locking;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import dream.common.packets.locking.LockReleasePacket;
import dream.common.packets.locking.LockRequestPacket;

class SidUpLockManager implements LockManager {
  private final Queue<PendingRequest> pendingRequests = new LinkedList<>();

  @Override
  public final boolean processLockRequest(LockRequestPacket request) {
    pendingRequests.add(new PendingRequest(request));
    return pendingRequests.size() == 1;
  }

  @Override
  public final Set<LockRequestPacket> processLockRelease(LockReleasePacket release) {
    assert !pendingRequests.isEmpty();
    final PendingRequest req = pendingRequests.peek();
    assert req.getRequest().getLockID().equals(release.getLockID());
    final Set<LockRequestPacket> result = new HashSet<>();
    if (req.decreaseCount()) {
      pendingRequests.poll();
      result.add(pendingRequests.peek().getRequest());
    }
    return result;
  }

  private class PendingRequest {
    private final LockRequestPacket request;
    private int count;

    PendingRequest(LockRequestPacket request) {
      super();
      this.request = request;
      count = request.getUnlockNodes().size();
    }

    final LockRequestPacket getRequest() {
      return request;
    }

    boolean decreaseCount() {
      count--;
      return count == 0;
    }

  }

}
