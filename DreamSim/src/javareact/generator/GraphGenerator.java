package javareact.generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javareact.common.Consts;
import javareact.common.types.Proxy;
import javareact.common.types.RemoteVar;
import javareact.common.types.Types;
import javareact.common.types.Var;
import javareact.experiments.JavaReactConfiguration;

class GraphGenerator {
  private final JavaReactConfiguration config;
  private final Random random;

  private final int graphId;
  private int count;

  // Node -> List of nodes it depends from
  private final Map<Node, List<Node>> dependencyMap = new HashMap<>();
  private final List<Node> existingNodes = new ArrayList<>();

  // NodeId -> Associated listener
  private final Map<String, GraphGeneratorListener> listeners = new HashMap<>();

  GraphGenerator(int graphId) {
    this.graphId = graphId;
    count = 0;
    config = JavaReactConfiguration.getSingleton();
    random = RandomGenerator.get();
  }

  final void addGraphGeneratorListener(GraphGeneratorListener listener, int id) {
    String hostName = Consts.hostPrefix + id;
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
    // Guarantees that existing nodes are iterated in insertion order
    for (Node node : existingNodes) {
      boolean isObservable = dependencyMap.get(node).isEmpty();
      String hostId = node.getHostName();
      if (isObservable) {
        listeners.get(hostId).notifyObservable(node.getName(), Types.INT);
      } else {
    	Function<List<RemoteVar<Integer>>, Integer> expression = generateExpression(node);
        listeners.get(hostId).notifyReactive(node.getName(), expression, dependencyMap.get(node).stream().map(Node::getName).collect(Collectors.toList()));
      }
    }
  }

  private final String generateNode(String previousHost) {
    String hostName = selectHost(previousHost);
    String name = Consts.objPrefix + graphId + "x" + count;
    count++;
    Node node = new Node(hostName, name);
    List<Node> dependingFrom = selectNodes();
    dependencyMap.put(node, dependingFrom);
    existingNodes.add(node);
    return hostName;
  }

  private final String selectHost(String previousHost) {
    float prob = random.nextFloat();
    if (prob > config.locality || previousHost == null) {
      int numClients = config.numberOfComponents;
      int id = random.nextInt(numClients);
      return Consts.hostPrefix + id;
    } else {
      return previousHost;
    }
  }

  private final List<Node> selectNodes() {
    List<Node> result = new ArrayList<Node>();
    int numNodesToSelect = Math.min(existingNodes.size(), config.numGraphDependencies);
    for (int i = 0; i < numNodesToSelect; i++) {
      addNode(result);
    }
    return result;
  }

  private final void addNode(List<Node> selectedNodes) {
    int numNodes = existingNodes.size();
    Node selectedNode = null;
    do {
      int nodeId = random.nextInt(numNodes);
      selectedNode = existingNodes.get(nodeId);
    } while (selectedNodes.contains(selectedNode));
    selectedNodes.add(selectedNode);
  }

  private final Function<List<RemoteVar<Integer>>, Integer> generateExpression(Node node) {
	return (vars) -> {
		Integer result = 0;
		for (RemoteVar<Integer> v : vars) {
			if (v.get() != null) {
				result += v.get();
			}
		}
		return result;
//		return vars.stream().reduce(0, (acc, v) -> {
//			return acc + v.get();
//		}, (a, b) -> a + b);
	};
  }

}
