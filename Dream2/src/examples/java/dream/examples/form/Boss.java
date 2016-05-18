package dream.examples.form;

import dream.client.Var;

public class Boss extends FormClient {

	private Var<Double> eph;

	public Boss() {
		super("Boss", "Euro/Hour");
	}

	@Override
	protected void init() {
		eph = new Var<>("euro_per_hour", 8.5);
	}

	@Override
	public void typedText(String typedText) {
		Double value = Double.valueOf(typedText);
		eph.set(value);
		logger.fine("Set Euro_Per_Hour to " + value);
	}

	public static void main(String[] args) {
		Boss b = new Boss();
		b.start();
	}

}
