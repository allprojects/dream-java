package dream.generator;

import java.util.ArrayList;
import java.util.Collections;
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
import dream.common.utils.LocalityDetector;
import dream.experiments.DreamConfiguration;

public class GraphGenerator {
	private static GraphGenerator instance;

	private final DependencyGraph depGraph = DependencyGraph.instance;
	// Existing nodes, organized by source
	private final Map<String, List<String>> nodesPerSource = new HashMap<>();
	private final List<String> allNodes = new ArrayList<>();

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
		// Only the first client triggers a graph generation
		if (id == 0) {
			IntStream.range(0, config.graphNumSources).forEach(i -> generateVar());
			nodesPerSource.keySet().forEach(source -> generateGraphFor(source));
			addInterSourceEdges();
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
			LocalityDetector.instance.consolidate();

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
		final List<String> sourceNodes = new ArrayList<>();
		sourceNodes.add(node);
		nodesPerSource.put(node, sourceNodes);
		allNodes.add(node);
		depGraph.addVar(node);
	}

	private final void generateGraphFor(String source) {
		final int numLevels = config.graphDepth;
		// Start from 1 because level 0 is the source
		List<String> previousLevel = new ArrayList<>();
		previousLevel.add(source);
		for (int i = 1; i < numLevels; i++) {
			previousLevel = generateLevel(source, previousLevel);
		}
	}

	private final void addInterSourceEdges() {
		final List<String> sourceList = new ArrayList<>(nodesPerSource.keySet());
		final int numSharedSources = (int) ((sourceList.size() - 1) * config.graphNodeShareProbability);
		for (int i = 0; i < numSharedSources; i++) {
			final String source = sourceList.get(i);
			final String nextSource = sourceList.get(i + 1);
			addInterSourceEdgesBetween(source, nextSource);
		}
	}

	private final void addInterSourceEdgesBetween(String source1, String source2) {
		if (nodesPerSource.get(source1).size() < 3 || nodesPerSource.get(source2).size() < 3) {
			throw new IllegalArgumentException("The number of nodes is too small to generate inter-source edges");
		}
		final List<String> nodes1 = selectRandomSignalsFromSource(source1, 2);
		final List<String> nodes2 = selectRandomSignalsFromSource(source2, 2);

		depGraph.getGraph().get(nodes1.get(0)).add(nodes2.get(0));
		depGraph.getGraph().get(nodes1.get(1)).add(nodes2.get(1));
	}

	private final List<String> generateLevel(String source, List<String> previousLevel) {
		final int numNodes = config.graphMinNodesPerLevel
		    + random.nextInt(config.graphMaxNodesPerLevel - config.graphMinNodesPerLevel + 1);
		final List<String> currentLevel = new ArrayList<>();
		for (int i = 0; i <= numNodes; i++) {
			currentLevel.add(generateSignal(source, previousLevel));
		}
		return currentLevel;
	}

	private final String generateSignal(String source, List<String> previousLevel) {
		final String name = Consts.objPrefix + allNodes.size();
		final List<String> depNodes = selectDepNodes(source, previousLevel);
		final String host = random.nextDouble() < config.graphLocality//
		    ? depNodes.stream().findAny().get().split("@")[1] //
		    : selectRandomHost();

		final String node = name + "@" + host;
		nodesPerSource.get(source).add(node);
		allNodes.add(node);
		depGraph.addSignal(node, depNodes);
		return node;
	}

	private final List<String> selectDepNodes(String source, List<String> previousLevel) {
		final List<String> result = new ArrayList<>();
		final int numDeps = Math.min(1 + random.nextInt(config.graphMaxDependenciesPerNode),
		    nodesPerSource.get(source).size());

		// Always select a node from the previous level of the source graph
		result.add(selectRandomNodeFromPreviousLevel(previousLevel));
		// Select the remaining nodes from the same source
		result.addAll(selectRandomNodesFromSource(source, numDeps - 1));

		return result;
	}

	private final String selectRandomNodeFromPreviousLevel(List<String> previousLevel) {
		return previousLevel.get(random.nextInt(previousLevel.size()));
	}

	private final List<String> selectRandomNodesFromSource(String source, int numNodes) {
		final List<String> sourceNodes = nodesPerSource.get(source);
		Collections.shuffle(sourceNodes);
		return IntStream.range(0, numNodes) //
		    .mapToObj(i -> sourceNodes.get(i)) //
		    .collect(Collectors.toList());
	}

	private final List<String> selectRandomSignalsFromSource(String source, int numSignals) {
		final List<String> sourceNodes = nodesPerSource.get(source);
		Collections.shuffle(sourceNodes);
		final List<String> result = new ArrayList<>();
		int index = 0;
		while (result.size() < numSignals) {
			final String node = sourceNodes.get(index++);
			if (!node.equals(source)) {
				result.add(node);
			}
		}
		return result;
	}

	private final String selectRandomHost() {
		final int id = random.nextInt(config.numberOfClients);
		return Consts.hostPrefix + id;
	}

}
