package protopeer.scenarios;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

import org.apache.log4j.*;

/**
 * Loads the *.scenario files, parses them and returns the {@link Scenario}
 * objects which can be passed to the {@link ScenarioExecutor} as an input.
 * 
 * @author wojtek
 * 
 */
public class ScenarioParser {

	private Logger logger = Logger.getLogger(ScenarioParser.class);

	private LinkedList<ScenarioParserListener> listeners = new LinkedList<ScenarioParserListener>();

	private int currentLine = 0;

	private String currentFilename = "";

	private String getCurrentPrefix() {
		return currentFilename + " line " + currentLine + ": ";
	}

	public ScenarioParser() {
		// add the default log4j listener
		this.addListener(new ScenarioParserListener() {

			public void error(String string) {
				logger.error(string);
			}

			public void warning(String string) {
				logger.warn(string);
			}
		});
	}

	public void addListener(ScenarioParserListener listener) {
		listeners.add(listener);
	}

	private void fireError(String string) {
		for (ScenarioParserListener listener : listeners) {
			listener.error(getCurrentPrefix() + string);
		}
	}

	private void fireWarning(String string) {
		for (ScenarioParserListener listener : listeners) {
			listener.warning(getCurrentPrefix() + string);
		}
	}

	private boolean setPeerIndices(ScenarioEvent event, String string) {
		boolean parsedOK = false;
		try {
			if (string.equals("*")) {
				// "*"
				// start=end=-1 indicates all peers
				event.setStartPeerIndex(-1);
				event.setEndPeerIndex(-1);
				parsedOK = true;
			} else if (string.contains("-")) {
				// "<int>-<int>"
				StringTokenizer tokenizer = new StringTokenizer(string, "-", false);
				String startString = tokenizer.nextToken();
				String endString = tokenizer.nextToken();
				int startInt = Integer.parseInt(startString);
				int endInt = Integer.parseInt(endString);
				if (startInt >= 0 && endInt >= 0 && startInt <= endInt) {
					event.setStartPeerIndex(startInt);
					event.setEndPeerIndex(endInt);
					parsedOK = true;
				}
			} else {
				int peerIndex = Integer.parseInt(string);
				event.setStartPeerIndex(peerIndex);
				event.setEndPeerIndex(peerIndex);
				parsedOK = true;
			}
		} catch (NumberFormatException e) {
			logger.warn("exception while parsing: ", e);
		} catch (NoSuchElementException e) {
			logger.warn("exception while parsing: ", e);
		}
		if (!parsedOK) {
			fireError("problems parsing the peer indices, expecting * or <int> or <int1>-<int2>, where <int>, <int1>, <int2> are >=0 integers, and <int1> <= <int2>, found: "
					+ string);
		}
		return parsedOK;
	}

	private boolean setTimeSpec(ScenarioEvent event, String string) {
		boolean parsedOK = false;
		try {
			double time = Double.parseDouble(string);
			if (time >= 0) {
				event.setExecutionTime(time);
				parsedOK = true;
			}
		} catch (NumberFormatException e) {
			logger.warn("exception while parsing: ", e);
		}
		if (!parsedOK) {
			fireError("problem parsing the execution time, should be a positive double (in milliseconds), found: "
					+ string);
		}
		return parsedOK;
	}

	private boolean setMethodCall(ScenarioEvent event, String methodCallString) {
		boolean parsedOK = false;
		String classNameString = null;
		String methodNameString = null;
		// anything after the last dot is the method name
		try {
			int lastDotIndex = methodCallString.lastIndexOf(".");
			if (lastDotIndex > 0) {
				classNameString = methodCallString.substring(0, lastDotIndex);
				methodNameString = methodCallString.substring(lastDotIndex + 1);
				// method name should end with
				if (methodNameString.endsWith("()")) {
					methodNameString = methodNameString.substring(0, methodNameString.length() - 2);
					// resolve the class name
					Class<?> clazz = Class.forName(classNameString);
					// resolve the method name, look for a zero-argument method
					Method method = clazz.getDeclaredMethod(methodNameString, new Class<?>[0]);
					event.setClazz(clazz);
					event.setMethodToCall(method);
					parsedOK = true;
				}
			}
		} catch (SecurityException e) {
			fireError("SecurityException when resolving the method call: " + e.getMessage());
			return false;
		} catch (ClassNotFoundException e) {
			fireError("class " + classNameString + " not found in classpath");
			return false;
		} catch (NoSuchMethodException e) {
			fireError("method " + methodNameString + " not found in class " + classNameString);
			return false;
		}

		if (!parsedOK) {
			fireError("problem parsing the method call, syntax: [package1[.package[...]].].ClassName.methodName(), found: "
					+ methodCallString);
		}
		return parsedOK;
	}

	private final String USAGE_STRING = "expected format: <peer_indices> <execution_time> <method_call>";

	private ScenarioEvent parseScenarioEvent(String string) {
		StringTokenizer tokenizer = new StringTokenizer(string, "\t ", false);
		ScenarioEvent event = new ScenarioEvent();

		if (!tokenizer.hasMoreTokens()) {
			fireError("peer indices missing, " + USAGE_STRING);
			return null;
		}

		String peerIndicesString = tokenizer.nextToken();
		if (!setPeerIndices(event, peerIndicesString)) {
			return null;
		}

		if (!tokenizer.hasMoreTokens()) {
			fireError("execution time missing, " + USAGE_STRING);
		}

		String timeSpecString = tokenizer.nextToken();
		if (!setTimeSpec(event, timeSpecString)) {
			return null;
		}

		if (!tokenizer.hasMoreTokens()) {
			fireError("method call missing, " + USAGE_STRING);
		}

		String methodCallString = tokenizer.nextToken();
		if (!setMethodCall(event, methodCallString)) {
			return null;
		}

		if (tokenizer.hasMoreTokens()) {
			fireWarning("expecting newline after the method call, found: " + tokenizer.nextToken());
		}
		return event;
	}

	public Scenario parseStream(InputStream in) throws IOException {
		LineNumberReader inLines = new LineNumberReader(new InputStreamReader(in));
		Scenario scenario = new Scenario();

		while (true) {
			String line = inLines.readLine();
			if (line == null) {
				break;
			}
			currentLine = inLines.getLineNumber();

			// trim all white space
			line = line.trim();
			if (line.length() == 0) {
				continue;
			}

			// check if it's a comment
			if (line.charAt(0) == '#') {
				continue;
			}

			// otherwise it's an event spec
			ScenarioEvent scenarioEvent = parseScenarioEvent(line);
			if (scenarioEvent == null) {
				fireError("errors when parsing, ignored event: " + line);
			} else {
				scenario.addEvent(scenarioEvent);
			}
		}
		return scenario;
	}

	public Scenario parseFile(String filename) {
		FileInputStream inFile = null;
		try {
			try {
				inFile = new FileInputStream(filename);
				return parseStream(inFile);

			} finally {
				if (inFile != null) {
					inFile.close();
				}
			}
		} catch (IOException e) {
			logger.error("", e);
		}
		return null;
	}

	public Scenario parseDirectory(String directory) {
		return null;
	}

	public static void main(String[] borgs) {
		ScenarioParser parser = new ScenarioParser();
		parser.addListener(new ScenarioParserListener() {

			public void error(String string) {
				System.out.println(string);
			}

			public void warning(String string) {
				System.out.println(string);
			}
		});
		Scenario scenario = parser
				.parseFile("C:\\_home\\jbuilder_workspace\\protopeer_apps\\data\\scenarios\\example.scenario");
		System.out.println(scenario.getAllEvents().size() + " events loaded");
		System.out.println(scenario.dumpToStringBuffer().toString());
	}

}
