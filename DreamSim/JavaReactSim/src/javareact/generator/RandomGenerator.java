package javareact.generator;

import java.util.Random;

import javareact.experiments.JavaReactConfiguration;

public class RandomGenerator {
  private static Random random;

  public static Random get() {
    if (random == null) {
      random = new Random(JavaReactConfiguration.getSingleton().seed);
    }
    return random;
  }

  public static void reset() {
    if (random == null) {
      random = new Random(JavaReactConfiguration.getSingleton().seed);
    } else {
      random.setSeed(JavaReactConfiguration.getSingleton().seed);
    }
  }

}
