package protopeer.network.delayloss;

import java.io.*;
import java.util.*;

import org.apache.log4j.*;

import protopeer.network.*;
import protopeer.util.*;

/**
 * A lossless network model in which the complete source-destination matrix of
 * delays can be specified. The (i,j) cell of the matrix contains the RTT delay
 * in microseconds from address i to address j.
 * 
 * 
 */
public class MatrixNetworkModel implements DelayLossNetworkModel {
	private static final Logger logger = Logger.getLogger(MatrixNetworkModel.class);

	private int latencies[][];

	Vector<IntegerNetworkAddress> availableAddresses = new Vector<IntegerNetworkAddress>();

	HashSet<IntegerNetworkAddress> allocatedAddresses = new HashSet<IntegerNetworkAddress>();

	/**
	 * Allocates a new address uniformly randomly from the set of available
	 * ones. Returns null and logs an error when there are no available
	 * addresses.
	 */
	public synchronized NetworkAddress allocateAddress() {
		if (availableAddresses.isEmpty()) {
			logger.error("ran out of addresses");
			return null;
		}
		IntegerNetworkAddress allocatedAddress = availableAddresses
				.elementAt((int) (availableAddresses.size() * RandomnessSource.getNextNetworkDouble()));
		availableAddresses.remove(allocatedAddress);
		allocatedAddresses.add(allocatedAddress);
		return allocatedAddress;
	}
	
	/**
	 *  see the source, not sure about the spec yet :)
	 */
	public double getDelay(NetworkAddress sourceAddress, NetworkAddress destinationAddress, Message message) {
		int sourceIndex = ((IntegerNetworkAddress) sourceAddress).getIntValue();
		int destinationIndex = ((IntegerNetworkAddress) destinationAddress).getIntValue();
		// convert from microseconds to milisecodns
		// end convert RTT to latency (1/2 factor)
		double randomDelta = RandomnessSource.getNextGeneralDouble() + 0.5;
		return ((double) latencies[sourceIndex][destinationIndex]) / 1e3 / 2 * randomDelta;
	}

	public synchronized int getNumAvailableAddresses() {
		return availableAddresses.size();
	}

	/**
	 * Loads the delay matrix from a file. Each line in the file corresponds to
	 * a single row in the matrix and consists of a space delimited list of
	 * floating point values (delays in microseconds).
	 * 
	 * @param filename
	 * @param numNodes
	 *            number of rows and columns in the matrix (the matrix must be
	 *            square)
	 */
	public synchronized void loadFromMatrix(String filename, int numNodes) {
		latencies = new int[numNodes][numNodes];
		availableAddresses = new Vector<IntegerNetworkAddress>();
		allocatedAddresses = new HashSet<IntegerNetworkAddress>();
		for (int i = 0; i < numNodes; i++) {
			availableAddresses.add(new IntegerNetworkAddress(i));
		}

		try {
			LineNumberReader reader = new LineNumberReader(new InputStreamReader(new FileInputStream(filename)));
			int lineNumber = 0;
			while (true) {
				String line = reader.readLine();
				if (line == null) {
					break;
				}

				StringTokenizer tokenizer = new StringTokenizer(line, " ", false);
				int abnormalValues = 0;
				int totalValues = 0;
				while (tokenizer.hasMoreTokens()) {
					String valueString = tokenizer.nextToken();
					int value = Integer.parseInt(valueString);
					if (value < 0) {
						abnormalValues++;
						value = (int) 5e6;
					}
					latencies[lineNumber][totalValues] = value;
					totalValues++;
				}
				assert (totalValues == numNodes);
				// System.out.println(lineNumber + " " + abnormalValues + " " +
				// totalValues);
				lineNumber++;
			}
			assert (lineNumber == numNodes);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public synchronized void deallocateAddress(NetworkAddress address) {
		availableAddresses.add((IntegerNetworkAddress) address);
	}

	/**
	 * Lossless model, always returns false.
	 */
	public boolean getLoss(NetworkAddress sourceAddress, NetworkAddress destinationAddress, Message message) {
		return false;
	}
	
	/**
	 * Returns all allocated addressess as reachable by broadcast from any <code>sourceAddress</code>.
	 */
	public synchronized Collection<NetworkAddress> getAddressesReachableByBroadcast(NetworkAddress srouceAddress) {
		return new LinkedList<NetworkAddress>(allocatedAddresses);
	}
}
