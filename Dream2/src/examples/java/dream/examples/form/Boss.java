package dream.examples.form;

import dream.client.Var;

public class Boss extends FormClient {

	private Var<Double> eph;
	private Var<Integer> rmh;

	public Boss() {
		super("Boss", "Euro/Hour", "Minimum Hours");
		setInitValues(Double.toString(8.5), Integer.toString(10));
	}

	@Override
	protected void init() {
		eph = new Var<>("euro_per_hour", 8.5);
		rmh = new Var<>("required_minimum_hours", 10);
	}

	@Override
	public void typedText(int i, String typedText) {
		switch (i) {
		case 0:
			Double value = Double.valueOf(typedText);
			eph.set(value);
			logger.fine("Set Euro_Per_Hour to " + value);
			break;
		case 1:
			Integer value2 = Integer.valueOf(typedText);
			rmh.set(value2);
			logger.fine("Set Required_Minimum_Hours to " + value2);
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
