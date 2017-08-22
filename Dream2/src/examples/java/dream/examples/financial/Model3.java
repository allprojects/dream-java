package dream.examples.financial;

import java.util.Arrays;
import java.util.List;

import dream.client.RemoteVar;
import dream.client.Signal;
import dream.examples.util.Client;

public class Model3 extends Client {

	public Model3() {
		super("Model3");
	}

	@Override
	protected List<String> waitForVars() {
		return Arrays.asList("marketIndex@InputModel", "news@InputModel");
	}

	public void start() {
		final RemoteVar<Integer> marketIndex = new RemoteVar<>("InputModel", "marketIndex");
		final RemoteVar<Integer> news = new RemoteVar<>("InputModel", "news");

		final Signal<Integer> f3 = new Signal<>("f3", () -> {
			if (marketIndex.get() == null || news.get() == null) {
				return null;
			} else {
				return marketIndex.get() + news.get();
			}
		}, 1, marketIndex, news);

		f3.change().addHandler((oldVal, newVal) -> System.out.println("New value for f3: " + newVal));
	}

	public static void main(String[] args) {
		new Model3().start();
	}
}
