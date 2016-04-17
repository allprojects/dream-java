package dream.examples.chat_fifo;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

/**
 * Implements a VectorClock that records the time stamps of all send and receive
 * events. It contains functions to compare and merge two VectorClocks.
 * 
 * @author Frits de Nijs
 * @author Peter Dijkshoorn
 */
public class VectorClock extends HashMap<String, Integer> implements Serializable, Comparator<VectorClock> {
	// Unique Serial.
	private static final long serialVersionUID = 6668164199894268488L;

	public VectorClock() {

	}

	public VectorClock(String initString) {
		fromString(this, initString);
	}

	/**
	 * Increases the component of pUnit by 1.
	 * 
	 * @param pUnit
	 *            - The ID of the vector element being increased.
	 */
	public void incrementClock(String pUnit) {
		// If we have it in the vector, increment.
		if (this.containsKey(pUnit)) {
			this.put(pUnit, this.get(pUnit).intValue() + 1);
		}
		// Else, store with value 1 (starts at 0, +1).
		else {
			this.put(pUnit, 1);
		}
	}

	/**
	 * GUI operation, returns the IDs in some neat order.
	 * 
	 * @return The IDs of the elements in the Clock.
	 */
	public String[] getOrderedIDs() {
		String[] lResult = new String[this.size()];

		lResult = this.keySet().toArray(lResult);

		Arrays.sort(lResult);

		return lResult;
	}

	/**
	 * GUI operation, returns the values in some neat order.
	 * 
	 * @return The Values of the elements in the Clock.
	 */
	public Integer[] getOrderedValues() {
		Integer[] lResult = new Integer[this.size()];
		String[] lKeySet = this.getOrderedIDs();

		int i = 0;
		for (String lKey : lKeySet) {
			lResult[i] = this.get(lKey);
			i++;
		}

		return lResult;
	}

	@Override
	public Integer get(Object key) {
		Integer lResult = super.get(key);

		if (lResult == null)
			lResult = 0;

		return lResult;
	}

	@Override
	public VectorClock clone() {
		return (VectorClock) super.clone();
	}

	@Override
	public String toString() {
		return toString(this);
	}

	/**
	 * VectorClock merging operation. Creates a new VectorClock with the maximum
	 * for each element in either clock. Used in Buffer and Process to
	 * manipulate clocks.
	 * 
	 * @param pOne
	 *            - First Clock being merged.
	 * @param pTwo
	 *            - Second Clock being merged.
	 * 
	 * @return A new VectorClock with the maximum for each element in either
	 *         clock.
	 */
	public static VectorClock max(VectorClock pOne, VectorClock pTwo) {
		// Create new Clock.
		VectorClock lResult = new VectorClock();

		// Go over all elements in clock One, put them in the new clock.
		for (String lEntry : pOne.keySet()) {
			lResult.put(lEntry, pOne.get(lEntry));
		}

		// Go over all elements in clock Two,
		for (String lEntry : pTwo.keySet()) {
			// Insert the Clock Two value if it is not present in One, or if it
			// is higher.
			if (!lResult.containsKey(lEntry) || lResult.get(lEntry) < pTwo.get(lEntry)) {
				lResult.put(lEntry, pTwo.get(lEntry));
			}
		}

		// Return the merged clock.
		return lResult;
	}

	public int compare(VectorClock v1, VectorClock v2) {
		switch (order(v1, v2)) {
		case GREATER:
			return 1;
		case SMALLER:
			return -1;
		default:
			return 0;
		}
	}

	/**
	 * VectorClock compare operation. Returns one of four possible values
	 * indicating how clock one relates to clock two:
	 * 
	 * VectorComparison.GREATER If One > Two. VectorComparison.EQUAL If One =
	 * Two. VectorComparison.SMALLER If One < Two. VectorComparison.SIMULTANEOUS
	 * If One <> Two.
	 * 
	 * @param pOne
	 *            - First Clock being compared.
	 * @param pTwo
	 *            - Second Clock being compared.
	 * 
	 * @return VectorComparison value indicating how One relates to Two.
	 */
	public static VectorComparison order(VectorClock pOne, VectorClock pTwo) {
		// Initially we assume it is all possible things.
		boolean lEqual = true;
		boolean lGreater = true;
		boolean lSmaller = true;

		// Go over all elements in Clock one.
		for (String lEntry : pOne.keySet()) {
			// Compare if also present in clock two.
			if (pTwo.containsKey(lEntry)) {
				// If there is a difference, it can never be equal.
				// Greater / smaller depends on the difference.
				if (pOne.get(lEntry) < pTwo.get(lEntry)) {
					lEqual = false;
					lGreater = false;
				}
				if (pOne.get(lEntry) > pTwo.get(lEntry)) {
					lEqual = false;
					lSmaller = false;
				}
			}
			// Else assume zero (default value is 0).
			else if (pOne.get(lEntry) != 0) {
				lEqual = false;
				lSmaller = false;
			}
		}

		// Go over all elements in Clock two.
		for (String lEntry : pTwo.keySet()) {
			// Only elements we have not found in One still need to be checked.
			if (!pOne.containsKey(lEntry) && (pTwo.get(lEntry) != 0)) {
				lEqual = false;
				lGreater = false;
			}
		}

		// Return based on determined information.
		if (lEqual) {
			return VectorComparison.EQUAL;
		} else if (lGreater && !lSmaller) {
			return VectorComparison.GREATER;
		} else if (lSmaller && !lGreater) {
			return VectorComparison.SMALLER;
		} else {
			return VectorComparison.SIMULTANEOUS;
		}
	}

	public static String toString(VectorClock clock) {
		StringBuilder sb = new StringBuilder();
		String[] lIDs = clock.getOrderedIDs();
		Integer[] lRequests = clock.getOrderedValues();
		sb.append("[");
		for (int i = 0; i < lRequests.length; i++) {
			sb.append(lIDs[i]).append("=").append(lRequests[i].toString());
			if (i + 1 < lRequests.length) {
				sb.append(",");
			}
		}
		sb.append("]");
		return sb.toString();
	}

	public static VectorClock fromString(String clockString) {
		return fromString(new VectorClock(), clockString);
	}

	private static VectorClock fromString(VectorClock vc, String clockString) {
		if (clockString.startsWith("[") && clockString.endsWith("]")) {
			clockString = clockString.substring(1, clockString.length() - 1);
			String[] pairs = clockString.split(",");
			for (int i = 0; i < pairs.length; i++) {
				if (pairs[i].length() > 0) {
					String[] temp = pairs[i].split("=");
					String key = temp[0];
					int value = Integer.valueOf(temp[1]);
					vc.put(key, value);
				}
			}
			return vc;
		} else
			throw new IllegalArgumentException("String is not a VectorClock: " + clockString);
	}
}

/**
 * Enumerates the four different outcomes of comparing two VectorClocks.
 * 
 * @author Frits de Nijs
 * @author Peter Dijkshoorn
 */
enum VectorComparison {
	GREATER, EQUAL, SMALLER, SIMULTANEOUS;
}