package dream.examples.financial;

import java.util.Arrays;
import java.util.List;

import dream.client.RemoteVar;
import dream.client.Signal;
import dream.examples.util.Client;

public class Model2 extends Client {
	public Model2() {
		super("Model2");
	}

	@Override
	protected List<String> waitForVars() {
		return Arrays.asList("marketIndex@InputModel", "stockOpts@InputModel");
	}

	public void start() {
		final RemoteVar<Integer> marketIndex = new RemoteVar<>("InputModel", "marketIndex");
		final RemoteVar<Integer> stockOpts = new RemoteVar<>("InputModel", "stockOpts");

		final Signal<Integer> f2 = new Signal<>("f2", () -> {
			if (marketIndex.get() == null || stockOpts.get() == null) {
				return null;
			} else {
				return marketIndex.get() + stockOpts.get() * 2;
			}
		} , marketIndex, stockOpts);

		f2.change().addHandler((oldVal, newVal) -> System.out.println("New value for f2: " + newVal));
	}

	public static void main(String[] args) {
		new Model2().start();
	}
}
