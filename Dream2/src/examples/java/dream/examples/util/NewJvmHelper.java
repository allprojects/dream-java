/**
 * 
 */
package dream.examples.util;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * @author Ram
 *
 */
public class NewJvmHelper {

	private static Logger logger = Logger.getGlobal();

	/**
	 * 
	 * @param c
	 *            class name which needs to be executed in new JVM
	 * @param args
	 *            arguments which needs to be passed as a run time argument to
	 *            the JVM
	 * @return process, which needs to be cleaned by the class which initializes
	 *         the new JVM.
	 */
	public static Process startNewJVM(Class<?> c, String... args) {
		logger.fine("Starting " + c.getName() + " ...");
		String separator = System.getProperty("file.separator");
		String classpath = System.getProperty("java.class.path");
		String path = System.getProperty("java.home") + separator + "bin" + separator + "java";
		String[] arguments = new String[args.length + 4];
		arguments[0] = path;
		arguments[1] = "-cp";
		arguments[2] = classpath;
		arguments[3] = c.getName();
		for (int i = 0; i < args.length; i++) {
			arguments[i + 4] = args[i];
		}
		ProcessBuilder processBuilder = new ProcessBuilder(arguments).inheritIO();
		Process process = null;
		try {
			process = processBuilder.start();
			process.waitFor(1, TimeUnit.SECONDS);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		logger.fine(c.getName() + " started!");
		return process;
	}
}
