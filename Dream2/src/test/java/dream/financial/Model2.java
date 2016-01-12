package dream.financial;

import dream.common.Consts;
import dream.common.datatypes.RemoteVar;
import dream.common.datatypes.Signal;
import dream.common.datatypes.ValueChangeListener;

public class Model2 implements ValueChangeListener<Integer> {
	public void start() {
		Consts.hostName = "Model2";

		RemoteVar<Integer> marketIndex = new RemoteVar<>("InputModel", "marketIndex");
		RemoteVar<Integer> stockOpts = new RemoteVar<>("InputModel", "stockOpts");

		Signal<Integer> f2 = new Signal<>("f2", () -> {
			if (marketIndex.get() == null || stockOpts.get() == null) { return null; }
			else { return marketIndex.get() + stockOpts.get() * 2; }
		}, marketIndex, stockOpts);
		
		f2.addValueChangeListener(this);
	}

	@Override
	public void notifyValueChanged(Integer newValue) {
		System.out.println("New value for f2: " + newValue);
	}
	
	public static void main(String[] args) {
		new Model2().start();
	}
}
