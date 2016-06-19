/**
 * 
 */
package dream.examples.util;

import java.util.HashMap;

/**
 * @author Ram
 *
 */
public class StringToList {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String input = "p1:2@p2:3";
		String seperator = "@";
		StringToList.getClockFromString(input, seperator);

	}

	public static HashMap<String, Integer> getClockFromString(String inputString, String seperator) {
		HashMap<String, Integer> clock = new HashMap<>();

		String[] tokens = inputString.split(seperator);
		int tokenCount = tokens.length;
		for (int j = 0; j < tokenCount; j++) {
			String kString = tokens[j].substring(0, tokens[j].indexOf(':'));
			Integer value = new Integer(
					Integer.parseInt(tokens[j].substring(tokens[j].indexOf(':') + 1, tokens[j].length())));
			System.out.println(tokens[j] + " " + kString + " " + value);
		}
		return clock;

	}
}
