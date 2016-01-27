package dream.generator;

import java.util.ArrayList;
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
  // Existing nodes, organized by source
  private final Map<String, Set<String>> nodesPerSource = new HashMap<>();
  private final Set<String> allNodes = new HashSet<>();

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
    nodesPerSource.clear();
    allNodes.clear();
    listeners.clear();
  }

  public final void generateGraphs(int id) {
    if (config.graphNodeShareProbability > 0.95) {
      throw new IllegalArgumentException("graphNodeShareProbability must be below 0.95");
    }
    // Only the first client triggers a graph generation
    if (id == 0) {
      IntStream.range(0, config.graphNumSources).forEach(i -> generateVar());
      nodesPerSource.keySet().forEach(source -> generateGraphFor(source));
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
    final String name = Consts.objPrefix + allNodes.size();
    final String node = name + "@" + host;
    final Set<String> sourceNodes = new HashSet<>();
    sourceNodes.add(node);
    nodesPerSource.put(node, sourceNodes);
    allNodes.add(node);
    depGraph.addVar(node);
  }

  private final void generateGraphFor(String source) {
    final int numLevels = config.graphDepth;
    // Start from 1 because level 0 is the source
    Set<String> previousLevel = new HashSet<>();
    previousLevel.add(source);
    for (int i = 1; i < numLevels; i++) {
      previousLevel = generateLevel(source, previousLevel);
    }
  }

  private final Set<String> generateLevel(String source, Set<String> previousLevel) {
    final int numNodes = config.graphMinNodesPerLevel + random.nextInt(config.graphMaxNodesPerLevel - config.graphMinNodesPerLevel + 1);
    final Set<String> currentLevel = new HashSet<>();
    for (int i = 0; i <= numNodes; i++) {
      currentLevel.add(generateSignal(source, previousLevel));
    }
    return currentLevel;
  }

  private final String generateSignal(String source, Set<String> previousLevel) {
    final String name = Consts.objPrefix + allNodes.size();
    final Set<String> depNodes = selectDepNodes(source, previousLevel);
    final String host = random.nextDouble() < config.graphLocality//
        ? depNodes.stream().findAny().get().split("@")[1] //
        : selectRandomHost();

    final String node = name + "@" + host;
    nodesPerSource.get(source).add(node);
    allNodes.add(node);
    depGraph.addSignal(node, depNodes);
    return node;
  }

  private final Set<String> selectDepNodes(String source, Set<String> previousLevel) {
    final Set<String> result = new HashSet<>();
    int numDeps = Math.min(1 + random.nextInt(config.graphMaxDependenciesPerNode), allNodes.size());
    // To avoid long lasting loops
    if (config.graphNodeShareProbability < 0.3) {
      numDeps = Math.min(numDeps, nodesPerSource.get(source).size());
    }

    // Always select a node from the previous level of the source graph
    result.add(selectRandomNodeFromPreviousLevel(previousLevel));
    while (result.size() < numDeps) {
      if (random.nextDouble() < config.graphNodeShareProbability && nodesPerSource.size() > 1) {
        result.add(selectRandomNodeFromOtherSources(source));
      } else {
        result.add(selectRandomNodeFromSource(source));
      }
    }

    return result;
  }

  private final String selectRandomNodeFromPreviousLevel(Set<String> previousLevel) {
    final List<String> levelList = new ArrayList<>(previousLevel);
    return levelList.get(random.nextInt(levelList.size()));
  }

  private final String selectRandomNodeFromSource(String source) {
    final List<String> sourceNodes = new ArrayList<>(nodesPerSource.get(source));
    return sourceNodes.get(random.nextInt(sourceNodes.size()));
  }

  private final String selectRandomNodeFromOtherSources(String source) {
    final List<String> nodes = nodesPerSource.entrySet().stream()//
        .filter(e -> !e.getKey().equals(source))//
        .map(e -> e.getValue())//
        .collect(ArrayList::new, ArrayList::addAll, ArrayList::addAll);
    return nodes.get(random.nextInt(nodes.size()));
  }

  private final String selectRandomHost() {
    final int id = random.nextInt(config.numberOfClients);
    return Consts.hostPrefix + id;
  }

}
