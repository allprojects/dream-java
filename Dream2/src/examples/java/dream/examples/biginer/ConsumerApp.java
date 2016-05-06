package dream.examples.biginer;

import dream.client.RemoteVar;
import dream.client.Signal;
import dream.common.Consts;

/**
 * An application which consumes the variables
 */
public class ConsumerApp {

	public ConsumerApp() throws Exception {
		// App will be running on host different from the producer
		Consts.hostName = "Host2";

		// Register a Subscription
		RemoteVar<String> rv = new RemoteVar<String>("Host1", "exVar");
		System.out.println("Consumer has started\n Please wait..initial communication may take upto 10 seconds");
		// On every change in remote variable rv create a signal which could
		// trigger appropriate action
		Signal<String> s = new Signal<String>("s", () -> {
			return rv.get() + "ABC";
		} , rv);

		// Register a handler which will be executed upon receiving the signal
		s.change().addHandler((oldVal, val) -> System.out.println("Signal1: " + val));
	}

	public static void main(String args[]) {
		try {
			// Start Consumer
			new ConsumerApp();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
