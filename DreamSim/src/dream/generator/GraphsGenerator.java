package dream.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import dream.experiments.DreamConfiguration;

public class GraphsGenerator {
  private static GraphsGenerator instance;
  private final List<GraphGenerator> generators = new ArrayList<GraphGenerator>();

  public static final GraphsGenerator get() {
    if (instance == null) {
      instance = new GraphsGenerator();
    }
    return instance;
  }

  private GraphsGenerator() {
    // Nothing to do
  }

  public void clean() {
    generators.clear();
  }

  public final void generateGraphs(int id) {
    // Only the first client triggers a graph generation
    if (id == 0) {
      IntStream.range(0, DreamConfiguration.get().numGraphs)//
          .mapToObj(GraphGenerator::new)//
          .forEach(graphGen -> {
            graphGen.generateGraph();
            generators.add(graphGen);
          });
    }
  }

  public final void addGraphGeneratorListener(GraphGeneratorListener listener, int id) {
    generators.forEach(gen -> gen.addGraphGeneratorListener(listener, id));
  }

  public final void notifyListeners(int id) {
    // Only the first client triggers listeners notification
    if (id == 0) {
      generators.forEach(gen -> gen.notifyListeners());
    }
  }

}
