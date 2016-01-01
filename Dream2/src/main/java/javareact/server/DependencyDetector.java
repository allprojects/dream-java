package javareact.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javareact.common.packets.AdvertisementPacket;
import javareact.common.packets.content.Event;
import javareact.common.packets.content.Subscription;

final class DependencyDetector {
	private final Map<String, Collection<String>> dependencyGraph = new HashMap<String, Collection<String>>();

	// Stores the dependencies for computing expressions
	// Expression (A) -> Changed expression (B) that led to the re-computation
	// of (A) -> Wait recommendations
	private final Map<String, Map<String, Set<WaitRecommendations>>> recommendations = new HashMap<String, Map<String, Set<WaitRecommendations>>>();

	final Set<WaitRecommendations> getWaitRecommendations(Event event, Set<String> computedFrom) {
		String eventSignature = event.getSignature();
		Map<String, Set<WaitRecommendations>> innerMap = recommendations.get(eventSignature);
		Set<WaitRecommendations> result = new HashSet<WaitRecommendations>();
		if (innerMap == null) {
			return result;
		}
		for (String exp : innerMap.keySet()) {
			if (computedFrom.contains(exp)) {
				result.addAll(innerMap.get(exp));
			}
		}
		return result;
	}

	final void processAdvertisementPacket(AdvertisementPacket advPkt) {
		switch (advPkt.getAdvType()) {
		case ADV:
			processAdv(advPkt);
			break;
		case UNADV:
			processUnadv(advPkt);
			break;
		default:
			assert false : advPkt.getAdvType();
		}
	}

	final void consolidate() {
		recommendations.clear();
		computeRecommendations();
	}

	private final void processAdv(AdvertisementPacket advPkt) {
		if (!advPkt.containtsSubscriptions())
			return;
		for (Subscription sub : advPkt.getSubscriptions()) {
			String advSignature = advPkt.getAdvertisement().getSignature();
			String subSignature = sub.getSignature();
			Collection<String> subSignatures = dependencyGraph.get(advSignature);
			if (subSignatures == null) {
				subSignatures = new HashSet<String>();
				dependencyGraph.put(advSignature, subSignatures);
			}
			subSignatures.add(subSignature);
		}
	}

	private final void processUnadv(AdvertisementPacket advPkt) {
		// TODO: manage unadvertisements
	}

	private final void computeRecommendations() {
		for (String expression : dependencyGraph.keySet()) {
			Collection<String> dependingFrom = dependencyGraph.get(expression);
			if (dependingFrom.size() > 1) {
				List<Path> paths = generatePathsFor(expression);
				computeRecommendationsFromPaths(expression, paths);
			}
		}
	}

	private final List<Path> generatePathsFor(String expression) {
		List<Path> results = new ArrayList<Path>();
		generatePathsFor(expression, new Path(), results);
		Collections.sort(results);
		return results;
	}

	private final void generatePathsFor(String expression, Path path, List<Path> paths) {
		path.addFirst(expression);
		if (!dependencyGraph.containsKey(expression)) {
			paths.add(path);
		} else {
			for (String depNode : dependencyGraph.get(expression)) {
				generatePathsFor(depNode, new Path(path), paths);
			}
		}
	}

	private final void computeRecommendationsFromPaths(String expression, List<Path> paths) {
		Path firstPath = paths.get(0);
		Path prefix = firstPath;
		List<Path> consideredPaths = new ArrayList<Path>();
		consideredPaths.add(firstPath);
		for (int i = 1; i < paths.size(); i++) {
			Path path = paths.get(i);
			Path newPrefix = prefix.getCommonPrefix(path);
			// The path is the new element of a new dependency
			if (newPrefix.isEmpty()) {
				// Save the computed dependency
				storeRecommendationsFromPath(prefix, consideredPaths);
				// Initialize variables for new iteration (if any)
				consideredPaths.clear();
				consideredPaths.add(path);
				prefix = path;
			} else {
				// Initialize variables for new iteration (if any)
				prefix = newPrefix;
				consideredPaths.add(path);
				// If it is the last iteration, then save the computed
				// dependency
				if (i == paths.size() - 1) {
					storeRecommendationsFromPath(newPrefix, consideredPaths);
				}
			}
		}
	}

	private final void storeRecommendationsFromPath(Path prefix, List<Path> consideredPaths) {
		String lastCommonExpression = prefix.getLastExpression();
		WaitRecommendations waitRecommendations = getRecommendationsFor(consideredPaths);
		Set<String> recommendationsSet = waitRecommendations.getRecommendations();
		for (String expressionToWaitFor : recommendationsSet) {
			WaitRecommendations recommendationsToStore = waitRecommendations.dup();
			recommendationsToStore.removeExpressionToWaitFor(expressionToWaitFor);
			Map<String, Set<WaitRecommendations>> innerMap = recommendations.get(expressionToWaitFor);
			if (innerMap == null) {
				innerMap = new HashMap<String, Set<WaitRecommendations>>();
				recommendations.put(expressionToWaitFor, innerMap);
			}
			Set<WaitRecommendations> waitSet = innerMap.get(lastCommonExpression);
			if (waitSet == null) {
				waitSet = new HashSet<WaitRecommendations>();
				innerMap.put(lastCommonExpression, waitSet);
			}
			waitSet.add(recommendationsToStore);
		}
	}

	private final WaitRecommendations getRecommendationsFor(List<Path> paths) {
		WaitRecommendations result = null;
		for (Path path : paths) {
			if (result == null) {
				String lastExpression = path.getLastExpression();
				result = new WaitRecommendations(lastExpression);
			}
			String waitForExpression = path.getSecondLastExpression();
			result.addRecommendation(waitForExpression);
		}
		return result;
	}

	/**
	 * Represents a path in the expression graph as a list of expressions.
	 */
	private class Path implements Comparable<Path> {
		private final LinkedList<String> expressions;

		Path() {
			expressions = new LinkedList<String>();
		}

		Path(Path path) {
			expressions = new LinkedList<String>(path.expressions);
		}

		final void addFirst(String expression) {
			expressions.addFirst(expression);
		}

		private final void addLast(String expression) {
			expressions.addLast(expression);
		}

		final Path getCommonPrefix(Path other) {
			Path prefix = new Path();
			for (int i = 0; i < expressions.size(); i++) {
				if (other.expressions.size() <= i) {
					break;
				}
				String myString = expressions.get(i);
				String otherString = other.expressions.get(i);
				if (myString.equals(otherString)) {
					prefix.addLast(myString);
				} else {
					break;
				}
			}
			return prefix;
		}

		final boolean isEmpty() {
			return expressions.isEmpty();
		}

		final String getLastExpression() {
			return expressions.getLast();
		}

		final String getSecondLastExpression() {
			return expressions.get(expressions.size() - 2);
		}

		@Override
		public final int compareTo(Path other) {
			for (int i = 0; i < expressions.size(); i++) {
				if (other.expressions.size() <= i) {
					return 1;
				}
				String myString = expressions.get(i);
				String otherString = other.expressions.get(i);
				int compareStrings = myString.compareTo(otherString);
				if (compareStrings != 0) {
					return compareStrings;
				}
			}
			return 0;
		}

		@Override
		public final String toString() {
			return expressions.toString();
		}

	}

}
