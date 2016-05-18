package dream.examples.financial;

import dream.client.DreamClient;
import dream.client.RemoteVar;
import dream.client.Signal;
import dream.common.Consts;

public class Model2 {
	public void start() {
		Consts.hostName = "Model2";
		DreamClient.instance.connect();

		final RemoteVar<Integer> marketIndex = new RemoteVar<>("InputModel", "marketIndex");
		final RemoteVar<Integer> stockOpts = new RemoteVar<>("InputModel", "stockOpts");

		final Signal<Integer> f2 = new Signal<>("f2", () -> {
			if (marketIndex.get() == null || stockOpts.get() == null) {
				return null;
			} else {
				return marketIndex.get() + stockOpts.get() * 2;
			}
		}, marketIndex, stockOpts);

		f2.change().addHandler((oldVal, newVal) -> System.out.println("New value for f2: " + newVal));
	}

	public static void main(String[] args) {
		new Model2().start();
	}
}
