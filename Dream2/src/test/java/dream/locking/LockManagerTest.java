package dream.locking;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.junit.Test;

import dream.common.packets.locking.LockReleasePacket;
import dream.common.packets.locking.LockRequestPacket;
import dream.common.packets.locking.LockType;
import polimi.reds.NodeDescriptor;

public class LockManagerTest {

  @Test
  public void test1() {
    final LockManager manager = new LockManager();

    final Set<String> lockNodes = new HashSet<>();
    lockNodes.add("A");
    lockNodes.add("B");

    final Set<String> unlockNodes = new HashSet<>();
    unlockNodes.add("A");
    unlockNodes.add("B");

    final LockRequestPacket req1 = new LockRequestPacket(new NodeDescriptor(true), lockNodes, unlockNodes, LockType.READ_WRITE);
    assertTrue(manager.processLockRequest(req1));

    final LockRequestPacket req2 = new LockRequestPacket(new NodeDescriptor(true), lockNodes, unlockNodes, LockType.READ_WRITE);
    assertFalse(manager.processLockRequest(req2));

    final UUID reqID1 = req1.getLockID();
    final LockReleasePacket rel1 = new LockReleasePacket(reqID1);
    Set<LockRequestPacket> result = manager.processLockRelease(rel1);
    assertTrue(result.isEmpty());

    final LockRequestPacket req3 = new LockRequestPacket(new NodeDescriptor(true), lockNodes, unlockNodes, LockType.READ_WRITE);
    assertFalse(manager.processLockRequest(req3));

    final LockReleasePacket rel2 = new LockReleasePacket(reqID1);
    result = manager.processLockRelease(rel2);
    assertEquals(1, result.size());
    assertTrue(result.contains(req2));

    final UUID reqID2 = req2.getLockID();
    final LockReleasePacket rel3 = new LockReleasePacket(reqID2);
    result = manager.processLockRelease(rel3);
    assertTrue(result.isEmpty());

    final LockReleasePacket rel4 = new LockReleasePacket(reqID2);
    result = manager.processLockRelease(rel4);
    assertEquals(1, result.size());
    assertTrue(result.contains(req3));
  }

  @Test
  public void test2() {
    final LockManager manager = new LockManager();

    final Set<String> lockNodesA = new HashSet<>();
    lockNodesA.add("A");

    final Set<String> lockNodesB = new HashSet<>();
    lockNodesB.add("B");

    final Set<String> lockNodesAB = new HashSet<>();
    lockNodesAB.add("A");
    lockNodesAB.add("B");

    final Set<String> unlockNodes = new HashSet<>();
    unlockNodes.add("C");

    final LockRequestPacket reqAB = new LockRequestPacket(new NodeDescriptor(true), lockNodesAB, unlockNodes, LockType.READ_WRITE);
    assertTrue(manager.processLockRequest(reqAB));

    final LockRequestPacket reqA = new LockRequestPacket(new NodeDescriptor(true), lockNodesA, unlockNodes, LockType.READ_WRITE);
    assertFalse(manager.processLockRequest(reqA));

    final LockRequestPacket reqB = new LockRequestPacket(new NodeDescriptor(true), lockNodesB, unlockNodes, LockType.READ_WRITE);
    assertFalse(manager.processLockRequest(reqB));

    final UUID id = reqAB.getLockID();
    final LockReleasePacket rel = new LockReleasePacket(id);
    final Set<LockRequestPacket> result = manager.processLockRelease(rel);
    assertEquals(2, result.size());
    assertTrue(result.contains(reqA));
    assertTrue(result.contains(reqB));
  }

  @Test
  public void test3() {
    final LockManager manager = new LockManager();

    final Set<String> lockNodesA = new HashSet<>();
    lockNodesA.add("A");

    final Set<String> lockNodesB = new HashSet<>();
    lockNodesB.add("B");

    final Set<String> lockNodesAB = new HashSet<>();
    lockNodesAB.add("A");
    lockNodesAB.add("B");

    final Set<String> unlockNodes = new HashSet<>();
    unlockNodes.add("C");

    final LockRequestPacket reqAB = new LockRequestPacket(new NodeDescriptor(true), lockNodesAB, unlockNodes, LockType.READ_ONLY);
    assertTrue(manager.processLockRequest(reqAB));

    final LockRequestPacket reqA1 = new LockRequestPacket(new NodeDescriptor(true), lockNodesA, unlockNodes, LockType.READ_ONLY);
    assertTrue(manager.processLockRequest(reqA1));

    final LockRequestPacket reqA2 = new LockRequestPacket(new NodeDescriptor(true), lockNodesA, unlockNodes, LockType.READ_ONLY);
    assertTrue(manager.processLockRequest(reqA2));

    final LockRequestPacket reqB = new LockRequestPacket(new NodeDescriptor(true), lockNodesB, unlockNodes, LockType.READ_WRITE);
    assertFalse(manager.processLockRequest(reqB));

    UUID id = reqA1.getLockID();
    final LockReleasePacket relA1 = new LockReleasePacket(id);
    assertTrue(manager.processLockRelease(relA1).isEmpty());

    id = reqAB.getLockID();
    final LockReleasePacket relB = new LockReleasePacket(id);
    final Set<LockRequestPacket> result = manager.processLockRelease(relB);
    assertEquals(1, result.size());
    assertTrue(result.contains(reqB));
  }
}
