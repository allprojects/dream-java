package dream.generator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import dream.common.Consts;
import dream.common.utils.AtomicDependencyDetector;
import dream.common.utils.CompleteGlitchFreeDependencyDetector;
import dream.common.utils.DependencyGraph;
import dream.common.utils.FinalNodesDetector;
import dream.common.utils.IntraSourceDependencyDetector;
import dream.experiments.DreamConfiguration;

public class GraphGenerator {
  private static GraphGenerator instance;

  private final DependencyGraph depGraph = DependencyGraph.instance;
  private final Set<String> existingNodes = new HashSet<>();

  private final DreamConfiguration config = DreamConfiguration.get();
  private final Random random = RandomGenerator.get();

  private final Map<String, GraphGeneratorListener> listeners = new HashMap<>();

  public static final GraphGenerator get() {
    if (instance == null) {
      instance = new GraphGenerator();
    }
    return instance;
  }

  private GraphGenerator() {
    // Nothing to do
  }

  public void clean() {
    depGraph.clear();
    existingNodes.clear();
    listeners.clear();
  }

  public final void generateGraphs(int id) {
    // Only the first client triggers a graph generation
    if (id == 0) {
      IntStream.range(0, config.graphNumSources).forEach(i -> generateVar());
      IntStream.range(0, config.graphNumInnerNodes).forEach(i -> generateSignal());
    }
  }

  public final void addGraphGeneratorListener(GraphGeneratorListener listener, int id) {
    final String hostName = Consts.hostPrefix + id;
    listeners.put(hostName, listener);
  }

  public final void notifyListeners(int id) {
    // Only the first client triggers listeners notification
    if (id == 0) {
      // Consolidates the data structures used during the processing of the
      // events
      IntraSourceDependencyDetector.instance.consolidate();
      CompleteGlitchFreeDependencyDetector.instance.consolidate();
      AtomicDependencyDetector.instance.consolidate();
      FinalNodesDetector.instance.consolidate();

      depGraph.getSources().forEach(s -> {
        final String hostId = s.split("@")[1];
        listeners.get(hostId).notifyVar(s);
      });

      depGraph.getGraph().entrySet().forEach(e -> {
        final String name = e.getKey();
        final String hostId = name.split("@")[1];
        final Set<String> deps = e.getValue().stream().collect(Collectors.toSet());
        listeners.get(hostId).notifySignal(name, deps);
      });
    }
  }

  private final void generateVar() {
    final String host = selectRandomHost();
    final String name = Consts.objPrefix + existingNodes.size();
    final String node = name + "@" + host;
    existingNodes.add(node);
    depGraph.addVar(node);
  }

  private final void generateSignal() {
    final String name = Consts.objPrefix + existingNodes.size();
    final Set<String> depNodes = selectDepNodes();
    final String host = random.nextDouble() < config.graphLocality//
        ? depNodes.stream().findAny().get().split("@")[1] //
        : selectRandomHost();

    final String node = name + "@" + host;
    existingNodes.add(node);
    depGraph.addSignal(node, depNodes);
  }

  private final Set<String> selectDepNodes() {
    final int numDeps = config.graphMinDepPerNode + random.nextInt(config.graphMaxDepPerNode - config.graphMinDepPerNode + 1);
    return selectRandomNodes(numDeps);
  }

  private final Set<String> selectRandomNodes(int numNodes) {
    final List<String> shuffleList = new ArrayList<>(existingNodes);
    Collections.shuffle(shuffleList);
    return shuffleList.stream().limit(numNodes).collect(Collectors.toSet());
  }

  private final String selectRandomHost() {
    final int id = random.nextInt(config.numberOfClients);
    return Consts.hostPrefix + id;
  }

}
