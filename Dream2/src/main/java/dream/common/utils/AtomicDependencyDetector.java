package dream.common.utils;

import java.util.Set;

public class AtomicDependencyDetector extends InterSourceDependencyDetector {

  @Override
  protected void computeDataStructs() {
    // Nothing to do
  }

  @Override
  public Set<String> getNodesToLockFor(String source) {
    return dependencyClosure.get(source);
  }

}
