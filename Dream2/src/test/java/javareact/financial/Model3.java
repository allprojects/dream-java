package javareact.financial;

import javareact.common.Consts;
import javareact.common.types.RemoteVar;
import javareact.common.types.Signal;

public class Model3 {
	public void start() {
		Consts.hostName = "Model3";

		RemoteVar<Integer> marketIndex = new RemoteVar<>("InputModel", "marketIndex");
		RemoteVar<Integer> news = new RemoteVar<>("InputModel", "news");

		Signal<Integer> f3 = new Signal<>("f3", () -> {
			if (marketIndex.get() == null || news.get() == null) {
				return null;
			} else {
				return marketIndex.get() + news.get();
			}
		}, marketIndex, news);

		f3.change().addHandler((oldValue, newValue) -> System.out.println("New value for f3: " + newValue));
	}

	public static void main(String[] args) {
		new Model3().start();
	}
}
