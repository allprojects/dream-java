package dream.generator;

import java.util.Set;

/**
 * A GraphGeneratorListener receives events from a GraphGenerator. In
 * particular, it is notified about the vars and signals defined in a specific
 * node.
 */
public interface GraphGeneratorListener {

	/**
	 * Notifies the presence of a var with the given name.
	 */
	public void notifyVar(String varName);

	/**
	 * Notifies the presence of a signal with the given name and the given
	 * dependencies.
	 */
	public void notifySignal(String signalName, Set<String> dependencies);

}
