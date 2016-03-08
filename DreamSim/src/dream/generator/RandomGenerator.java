package dream.generator;

import java.util.Random;

import dream.experiments.DreamConfiguration;

public class RandomGenerator {
	private static Random random;

	public static Random get() {
		if (random == null) {
			random = new Random(DreamConfiguration.get().seed);
		}
		return random;
	}

	public static void reset() {
		if (random == null) {
			random = new Random(DreamConfiguration.get().seed);
		} else {
			random.setSeed(DreamConfiguration.get().seed);
		}
	}

}
