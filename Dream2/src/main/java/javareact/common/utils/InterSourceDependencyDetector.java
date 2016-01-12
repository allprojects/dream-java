package javareact.common.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * An InterSourceDependencyDetector is a dependency detector used to detect
 * dependencies in the propagations from starting from different sources.
 *
 * For each source, the detector returns the nodes that require to be locked
 * during the propagation.
 *
 * The concrete implementation depends on the specific level of consistency
 * guarantees.
 */
public abstract class InterSourceDependencyDetector {
  protected final DependencyGraph graph = DependencyGraph.instance;

  protected Map<String, Set<String>> dependencyClosure = new HashMap<>();

  public final synchronized void consolidate() {
    dependencyClosure = DependencyGraphUtils.computeDependencyClosure();
    computeDataStructs();
  }

  protected abstract void computeDataStructs();

  /**
   * Returns the nodes that require to be locked during the propagation of an
   * update originated at the given source.
   *
   * @param source
   *          the source.
   * @return the nodes that need to be locked during the propagation.
   */
  public abstract Set<String> getNodesToLockFor(String source);

}
