package dream.examples.form;

import dream.client.Var;

public class Secretary extends FormClient {

	private Var<Integer> wh;

	public Secretary() {
		super("Secretary", "Working Hours");
	}

	@Override
	protected void init() {
		wh = new Var<>("working_hours", 5);
	}

	@Override
	public void typedText(String typedText) {
		Integer value = Integer.valueOf(typedText);
		wh.set(value);
		logger.fine("Set Working_Hours to " + value);
	}

	public static void main(String[] args) {
		Secretary s = new Secretary();
		s.start();
	}

}
