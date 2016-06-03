package dream.examples.form;

import dream.client.Var;

public class Secretary extends FormClient {

	public static final String NAME = "Secretary";
	public static final String WorkingHours = "working_hours";

	private Var<Integer> wh;

	public Secretary() {
		super(NAME, "Working Hours");
		setInitValues(Integer.toString(5));
	}

	@Override
	protected void init() {
		wh = new Var<>(WorkingHours, 5);
	}

	@Override
	public void typedText(int i, String typedText) {
		Integer value = Integer.valueOf(typedText);
		wh.set(value);
		logger.fine("Set Working_Hours to " + value);
	}

	public static void main(String[] args) {
		Secretary s = new Secretary();
		s.start();
	}

}
