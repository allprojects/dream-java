/**
 * 
 */
package dream.examples.biginer;

import dream.examples.util.NewJvmHelper;

/**
 * @author Ram
 *
 */
public class AppEntryPoint {
	public static void main(String args[]) {
		Process infra = null;
		Process producer = null;
		Process consumer = null;
		try {
			// Init the infra
			infra = new NewJvmHelper().startNewJVM(StartInfra.class);

			// Start Producer
			producer = new NewJvmHelper().startNewJVM(ProducerApp.class);

			// Start Consumer
			consumer = new NewJvmHelper().startNewJVM(ConsumerApp.class);

			Thread.sleep(5000);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/**
		 * All of the JVMs should be closed before exit
		 */
		finally {
			if (infra != null) {
				infra.destroyForcibly();
			}
			if (producer != null) {
				producer.destroyForcibly();
			}
			if (consumer != null) {
				consumer.destroyForcibly();
			}
		}

	}
}
