package dream.examples.form;

import dream.client.Signal;
import dream.client.Var;

public class Boss extends FormClient {

	private Var<Double> eph;

	public Boss() {
		super("Boss");
		eph = new Var<>("euro_per_hour", 8.5);
		new Signal<>("helper", () -> eph.get(), eph);
	}

	@Override
	public void typedText(String typedText) {
		System.out.println("Boss: \"" + typedText + "\"");
		Double value = Double.valueOf(typedText);
		System.out.println("Boss: " + value);
		eph.set(value);
	}

	public static void main(String[] args) {
		Boss b = new Boss();
		b.init("Boss");
	}

}
