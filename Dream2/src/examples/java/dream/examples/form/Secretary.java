package dream.examples.form;

import dream.client.Signal;
import dream.client.Var;

public class Secretary extends FormClient {

	private Var<Integer> wh;

	public Secretary() {
		super("Secretary");
		wh = new Var<>("working_hours", 5);
		new Signal<>("helper", () -> wh.get(), wh);
	}

	@Override
	public void typedText(String typedText) {
		System.out.println("Secretary: \"" + typedText + "\"");
		Integer value = Integer.valueOf(typedText);
		System.out.println("Secretary: " + value);
		wh.set(value);
	}

	public static void main(String[] args) {
		new Secretary();
	}

}
