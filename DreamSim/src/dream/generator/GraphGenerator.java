package dream.generator;

import java.util.ArrayList;
import java.util.HashMap;
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

class GraphGenerator {
  private final DreamConfiguration config = DreamConfiguration.get();
  private final Random random = RandomGenerator.get();

  private final int graphId;
  private int count;

  // Node -> List of nodes it depends on
  private final Map<String, List<String>> dependencyMap = new HashMap<>();
  private final List<String> existingNodes = new ArrayList<>();

  // NodeId -> Associated listener
  private final Map<String, GraphGeneratorListener> listeners = new HashMap<>();

  GraphGenerator(int graphId) {
    this.graphId = graphId;
    count = 0;
  }

  final void addGraphGeneratorListener(GraphGeneratorListener listener, int id) {
    final String hostName = Consts.hostPrefix + id;
    listeners.put(hostName, listener);
  }

  final void removeGraphGeneratorListener(int id) {
    listeners.remove(id);
  }

  final void generateGraph() {
    String previousHost = null;
    for (int i = 0; i < config.numGraphNodes; i++) {
      previousHost = generateNode(previousHost);
    }
  }

  final void notifyListeners() {
    // Consolidates the data structures used during the processing of the events
    final DependencyGraph graph = DependencyGraph.instance;
    existingNodes.forEach(node -> {
      final List<String> deps = dependencyMap.get(node);
      if (deps.isEmpty()) {
        graph.addVar(node);
      } else {
        graph.addSignal(node, deps);
      }
    });
    IntraSourceDependencyDetector.instance.consolidate();
    CompleteGlitchFreeDependencyDetector.instance.consolidate();
    AtomicDependencyDetector.instance.consolidate();
    FinalNodesDetector.instance.consolidate();

    // Guarantees that existing nodes are iterated in insertion order
    existingNodes.forEach(node -> {
      final boolean isVar = dependencyMap.get(node).isEmpty();
      final String hostId = node.split("@")[1];
      if (isVar) {
        listeners.get(hostId).notifyVar(node);
      } else {
        final Set<String> deps = dependencyMap.get(node).stream()//
            .collect(Collectors.toSet());
        listeners.get(hostId).notifySignal(node, deps);
      }
    });
  }

  private final String generateNode(String previousHost) {
    final String hostName = selectHost(previousHost);
    final String name = Consts.objPrefix + graphId + "x" + count;
    count++;
    final String node = name + "@" + hostName;
    final List<String> dependingFrom = selectNodes();
    dependencyMap.put(node, dependingFrom);
    existingNodes.add(node);
    return hostName;
  }

  private final String selectHost(String previousHost) {
    final float prob = random.nextFloat();
    if (prob > config.locality || previousHost == null) {
      final int id = random.nextInt(config.numberOfClients);
      return Consts.hostPrefix + id;
    } else {
      return previousHost;
    }
  }

  private final List<String> selectNodes() {
    final List<String> result = new ArrayList<String>();
    final int numNodesToSelect = Math.min(existingNodes.size(), config.numGraphDependencies);
    IntStream.range(0, numNodesToSelect).forEach(i -> addNode(result));
    return result;
  }

  private final void addNode(List<String> selectedNodes) {
    final int numNodes = existingNodes.size();
    String selectedNode = null;
    do {
      final int nodeId = random.nextInt(numNodes);
      selectedNode = existingNodes.get(nodeId);
    } while (selectedNodes.contains(selectedNode));
    selectedNodes.add(selectedNode);
  }

}
