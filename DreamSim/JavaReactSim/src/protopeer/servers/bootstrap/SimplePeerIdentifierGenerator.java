package protopeer.servers.bootstrap;

import protopeer.*;
import protopeer.network.*;
import protopeer.util.*;
import cern.jet.random.*;

public class SimplePeerIdentifierGenerator implements PeerIdentifierGenerator {

	public PeerIdentifier generatePeerIdentifier(NetworkAddress networkAddress) {
		// int dimensions = MainConfiguration.getSingleton().spaceDimensions;
		// if (dimensions == 1) {
		if (MainConfiguration.getSingleton().identifierInitialization == MainConfiguration.IdentifierInitialization.UNIFORM) {
			return new RingIdentifier(RandomnessSource.getNextTopologyDouble());
		} else if (MainConfiguration.getSingleton().identifierInitialization == MainConfiguration.IdentifierInitialization.GAUSSIAN_MIXTURE) {
			return new RingIdentifier(getNextGaussianMixture());
		} else if (MainConfiguration.getSingleton().identifierInitialization == MainConfiguration.IdentifierInitialization.EXPONENTIAL) {
			double pickedID = -1;
			Exponential distro = new Exponential(3, RandomnessSource.getGeneralRandomEngine());
			while (pickedID > 1.0 || pickedID < 0) {
				pickedID = distro.nextDouble();
			}
			return new RingIdentifier(pickedID);
		}
		// } else if (dimensions > 1) {
		// double coords[] = new double[dimensions];
		// for (int i = 0; i < coords.length; i++) {
		// if (MainConfiguration.getSingleton().identifierInitialization ==
		// MainConfiguration.IdentifierInitialization.UNIFORM) {
		// coords[i] = RandomnessSource.getNextTopologyDouble();
		// } else if (MainConfiguration.getSingleton().identifierInitialization
		// == MainConfiguration.IdentifierInitialization.BINARY) {
		// coords[i] = RandomnessSource.getNextTopologyDouble() < 0.5 ? 0.0 :
		// 1.0;
		// }
		// }
		// return new MultiDimensionalPeerIdentifier(coords);
		// }
		return null;
	}

	private static double getNextGaussianMixture() {
		double out = -1;
		while (out > 1.0 || out < 0.0) {
			double choose = RandomnessSource.getNextTopologyDouble();
			if (choose < 0.3) {
				out = (new Normal(0.2, 0.1, RandomnessSource.getGeneralRandomEngine())).nextDouble();
			}
			if (choose < 0.7) {
				out = (new Normal(0.7, 0.001, RandomnessSource.getGeneralRandomEngine())).nextDouble();
			}
			if (choose < 0.9) {
				out = (new Normal(0.5, 0.01, RandomnessSource.getGeneralRandomEngine())).nextDouble();
			} else {
				out = (new Normal(0.6, 0.2, RandomnessSource.getGeneralRandomEngine())).nextDouble();
			}
		}
		return out;
	}

}
