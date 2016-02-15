package dream.examples.local;

import dream.client.Signal;
import dream.client.Var;

public class Example1 {

	public static void main(String args[]) {
		final Var<Integer> varInt = new Var<>("varInt", 1);
		final Var<Double> varDouble = new Var<>("varDouble", 1.0);
		final Var<Boolean> varBool = new Var<>("varBool", false);
		final Var<String> varString1 = new Var<>("varString1", "");
		final Var<String> varString2 = new Var<>("varString2", "");

		final Signal<Integer> signalInt = new Signal<Integer>("signalInt",
				() -> 10 - 2 + (varInt.get() * 2 + varInt.get()) / 2, varInt);
		final Signal<Double> signalDouble = new Signal<Double>("signalDouble",
				() -> varDouble.get() + varDouble.get() * 2, varDouble);
		final Signal<Boolean> signalBool = new Signal<Boolean>("signalBool", () -> !varBool.get(), varBool);
		final Signal<String> signalString = new Signal<String>("signalString",
				() -> varString1.get() + varString2.get(), varString1, varString2);

		signalInt.addValueChangeListener(val -> System.out.println("signalInt: " + val + " (correct value: 158)"));
		signalDouble
				.addValueChangeListener(val -> System.out.println("signalDouble: " + val + " (correct value: 4.8)"));
		signalBool.addValueChangeListener(val -> System.out.println("signalBool: " + val + " (correct value: false)"));
		signalString.addValueChangeListener(
				val -> System.out.println("signalString: " + val + " (correct value: Hello World!)"));

		try {
			Thread.sleep(500);
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}

		varInt.set(100);
		varDouble.set(1.6);
		varBool.set(true);
		varString1.set("Hello ");
		varString2.set("World!");

	}
}
