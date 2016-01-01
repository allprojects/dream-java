package javareact.financial;

import javareact.common.Consts;
import javareact.common.types.RemoteVar;
import javareact.common.types.Signal;

public class Model1 {
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
		}, marketIndex, stockOpts);

		f1.change().addHandler((oldValue, newValue) -> System.out.println("New value for f1: " + newValue));
	}

	public static void main(String[] args) {
		new Model1().start();
	}
}
