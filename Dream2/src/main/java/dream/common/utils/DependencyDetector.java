package dream.common.utils;

/**
 * A dependency detector is responsible for detecting dependencies in the
 * propagation graph. There exist two types of dependency detectors:
 * intra-source dependency detectors detect dependencies in propagations that
 * start from a single source, while inter-source dependency detectors consider
 * dependencies that involve multiple sources.
 */
public interface DependencyDetector {

  /**
   * Compute the accessory data structures required to speed-up the query
   * process. It needs to be invoked when the dependency graph changes.
   */
  public void consolidate();

}
