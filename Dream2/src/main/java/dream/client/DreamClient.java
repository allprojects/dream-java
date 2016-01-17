package dream.client;

import java.util.HashSet;
import java.util.Set;

import dream.common.utils.DependencyGraph;

/**
 * The DreamClient contains utility methods that can be used by the application
 * to obtain information about the dependency graph.
 */
public enum DreamClient {
  instance;

  private final DependencyGraph depGraph = DependencyGraph.instance;

  public final void connect() {
    ClientEventForwarder.get();
  }

  public final Set<String> listVariables() {
    final Set<String> result = new HashSet<>();
    result.addAll(depGraph.getGraph().keySet());
    result.addAll(depGraph.getSources());
    return result;
  }

}
