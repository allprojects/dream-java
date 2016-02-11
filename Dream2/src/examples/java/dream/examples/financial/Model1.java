package dream.examples.financial;

import dream.client.RemoteVar;
import dream.client.Signal;
import dream.client.ValueChangeListener;
import dream.client.Var;
import dream.common.Consts;

public class Model1 implements ValueChangeListener<Integer> {
	public void start() {
		Consts.hostName = "Model1";

		RemoteVar<Integer> marketIndex = new RemoteVar<>("InputModel", "marketIndex");
		RemoteVar<Integer> stockOpts = new RemoteVar<>("InputModel", "stockOpts");

		Signal<Integer> f1 = new Signal<>("f1", () -> {
			if (marketIndex.get() == null || stockOpts.get() == null) {
				return null;
			} else {
				return marketIndex.get() * 2 + stockOpts.get();
			}
		} , marketIndex, stockOpts);

		f1.addValueChangeListener(this);
	}

	@Override
	public void notifyValueChanged(Integer newValue) {
		System.out.println("New value for f1: " + newValue);
	}

	public static void main(String[] args) {
		new Model1().start();
	}
}
