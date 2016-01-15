package dream.locking;

import dream.common.packets.locking.Lock;
import polimi.reds.NodeDescriptor;

final class LockApplicantPair {
  private final NodeDescriptor applicant;
  private final Lock lock;

  LockApplicantPair(NodeDescriptor applicant, Lock lock) {
    this.applicant = applicant;
    this.lock = lock;
  }

  final NodeDescriptor getApplicant() {
    return applicant;
  }

  final Lock getLock() {
    return lock;
  }

  @Override
  public String toString() {
    return "LockApplicantPair [" + applicant + ", " + lock + "]";
  }

}
