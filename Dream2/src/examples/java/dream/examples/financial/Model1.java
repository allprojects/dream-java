package dream.examples.financial;

import java.util.Arrays;
import java.util.List;

import dream.client.RemoteVar;
import dream.client.Signal;
import dream.examples.util.Client;

public class Model1 extends Client {
	public Model1() {
		super("Model1");
	}

	@Override
	protected List<String> waitForVars() {
		return Arrays.asList("marketIndex@InputModel", "stockOpts@InputModel");
	}

	public void start() {
		final RemoteVar<Integer> marketIndex = new RemoteVar<>("InputModel", "marketIndex");
		final RemoteVar<Integer> stockOpts = new RemoteVar<>("InputModel", "stockOpts");

		final Signal<Integer> f1 = new Signal<>("f1", () -> {
			if (marketIndex.get() == null || stockOpts.get() == null) {
				return null;
			} else {
				return marketIndex.get() * 2 + stockOpts.get();
			}
		}, 1, marketIndex, stockOpts);

		f1.change().addHandler((oldVal, newVal) -> System.out.println("New value for f1: " + newVal));
	}

	public static void main(String[] args) {
		new Model1().start();
	}
}
