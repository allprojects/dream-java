package dream.examples.local;

import dream.client.Signal;
import dream.client.Var;

public class ExampleFilter {
	public static void main(String args[]) {

		final Var<Integer> varInt = new Var<>("varInt", 1);
		final Signal<Integer> signalInt = new Signal<>("signalInt", () -> varInt.get() + 1, 1,
				varInt.filter(val -> val > 10));
		final Signal<Integer> signalInt2 = new Signal<>("signalInt2", () -> signalInt.get() + 1, 1,
				signalInt.filter(val -> val > 20));

		signalInt.change().addHandler((oldVal, val) -> System.out.println("SignalInt: " + val));
		signalInt2.change().addHandler((oldVal, val) -> System.out.println("SignalInt2: " + val));

		try {
			Thread.sleep(500);
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}

		System.out.println("Expected results:");
		System.out.println("SignalInt: 12");
		System.out.println("SignalInt: 21");
		System.out.println("SignalInt2: 22");
		System.out.println();

		varInt.set(1);
		varInt.set(2);
		varInt.set(11);
		varInt.set(20);
		varInt.set(10);

	}
}
