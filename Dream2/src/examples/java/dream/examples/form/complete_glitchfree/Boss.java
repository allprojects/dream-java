package dream.examples.form.complete_glitchfree;

import dream.client.Var;
import dream.examples.form.core.FormClient;
import dream.examples.util.Pair;

public class Boss extends FormClient {

	public static final String NAME = "Boss";
	public static final String EuroPerHour = "euro_per_hour";
	public static final String RequiredHours = "required_hours";

	private Var<Double> eph;
	private Var<Pair<Integer, Integer>> rh;

	public Boss() {
		super(NAME, "Euro/Hour", "Minimum Hours", "Maximum Hours");
		setInitValues(Double.toString(8.5), Integer.toString(10), Integer.toString(60));
	}

	@Override
	protected void init() {
		eph = new Var<>(EuroPerHour, 8.5);
		rh = new Var<>(RequiredHours, new Pair<>(10, 60));
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
			rh.set(new Pair<>(value2, rh.get().getSecond()));
			logger.fine("Set minimum @ Required_Hours to " + value2);
			break;
		case 2:
			Integer value3 = Integer.valueOf(typedText);
			rh.set(new Pair<>(rh.get().getSecond(), value3));
			logger.fine("Set maximum @ Required_Hours to " + value3);
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
