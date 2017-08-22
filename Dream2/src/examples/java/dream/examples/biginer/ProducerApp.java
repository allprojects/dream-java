package dream.examples.biginer;

import dream.client.Var;
import dream.common.Consts;

/**
 * An app which produces the variables
 */
public class ProducerApp {

	public ProducerApp() throws Exception {
		// Mention the host(node) which is producing the value
		Consts.setHostName("Host1");

		// myVar is created and registered as exVar for remote consumption.
		// exVar is initialized to AAA
		Var<String> myVar = new Var<String>("exVar", "AAA");
		System.out.println("Producer has started\n Please wait..initial communication may take upto 10 seconds");
		int iteration = 10;
		Thread.sleep(3000);
		while (iteration > 0) {
			Thread.sleep(100);
			// change value of exVar
			myVar.set("Value-" + (10 - iteration) + "");
			System.out.println("Changed myvar " + myVar.get());
			iteration--;
		}
	}

	public static void main(String args[]) {
		try {

			// Start Producer
			new ProducerApp();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
