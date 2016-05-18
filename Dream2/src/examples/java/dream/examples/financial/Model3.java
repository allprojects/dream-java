package dream.examples.financial;

import dream.client.DreamClient;
import dream.client.RemoteVar;
import dream.client.Signal;
import dream.common.Consts;

public class Model3 {
	public void start() {
		Consts.hostName = "Model3";
		DreamClient.instance.connect();

		final RemoteVar<Integer> marketIndex = new RemoteVar<>("InputModel", "marketIndex");
		final RemoteVar<Integer> news = new RemoteVar<>("InputModel", "news");

		final Signal<Integer> f3 = new Signal<>("f3", () -> {
			if (marketIndex.get() == null || news.get() == null) {
				return null;
			} else {
				return marketIndex.get() + news.get();
			}
		}, marketIndex, news);

		f3.change().addHandler((oldVal, newVal) -> System.out.println("New value for f3: " + newVal));
	}

	public static void main(String[] args) {
		new Model3().start();
	}
}
