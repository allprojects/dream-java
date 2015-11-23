package javareact.financial;

import javareact.common.Consts;
import javareact.common.types.ReactiveChangeListener;
import javareact.common.types.RemoteVar;
import javareact.common.types.Signal;
import javareact.common.types.Var;

public class Model1 implements ReactiveChangeListener<Integer> {
	public void start() {
		Consts.hostName = "Model1";
		
		RemoteVar<Integer> marketIndex = new RemoteVar<>("InputModel", "marketIndex");
		RemoteVar<Integer> stockOpts = new RemoteVar<>("InputModel", "stockOpts");
		
	
		
		Signal<Integer> f1 = new Signal<>("f1", () -> {
			if (marketIndex.get() == null || stockOpts.get() == null) { return null; }
			else { return marketIndex.get() * 2 + stockOpts.get();
		} }, marketIndex, stockOpts);
		
		f1.addReactiveChangeListener(this);
	}

	@Override
	public void notifyReactiveChanged(Integer oldValue, Integer newValue, String host) {
		System.out.println("New value for f1: " + newValue);
	}
	
	public static void main(String[] args) {
		new Model1().start();
	}
}
