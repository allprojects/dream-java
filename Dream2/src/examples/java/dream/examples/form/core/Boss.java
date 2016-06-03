package dream.examples.form.core;

import dream.client.Var;

public class Boss extends FormClient {

	public static final String NAME = "Boss";
	public static final String EuroPerHour = "euro_per_hour";

	protected Var<Double> eph;

	public Boss() {
		super(NAME, "Euro/Hour");
		setInitValues(Double.toString(8.5));
	}

	@Override
	protected void init() {
		eph = new Var<>(EuroPerHour, 8.5);
	}

	@Override
	public void typedText(int i, String typedText) {
		switch (i) {
		case 0:
			Double value = Double.valueOf(typedText);
			eph.set(value);
			logger.fine("Set Euro_Per_Hour to " + value);
			break;
		default:
			break;
		}

	}

	public static void main(String[] args) {
		Boss b = new Boss();
		b.start();
	}
}
