package dream;

import dream.experiments.SimulatedExperimentRunner;

public class SimulationStarter {

  public static void main(String[] args) {
    final SimulatedExperimentRunner experimentRunner = new SimulatedExperimentRunner();
    experimentRunner.runExperiments();
  }

}
