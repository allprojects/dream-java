package dream.examples.financial;

import java.util.Random;

import dream.client.Var;
import dream.examples.util.Client;

public class InputModel extends Client {

	public InputModel() {
		super("InputModel");
	}

	public static void main(String[] args) {
		new InputModel().start();
	}

	public void start() {
		final Var<Integer> marketIndex = new Var<>("marketIndex", 1);
		final Var<Integer> stockOpts = new Var<>("stockOpts", 1);
		final Var<Integer> news = new Var<>("news", 1);

		final Random random = new Random();

		while (true) {
			marketIndex.set(random.nextInt(100));
			stockOpts.set(random.nextInt(100));
			news.set(random.nextInt(100));

			System.out.println("New values: " + marketIndex.get() + ", " + stockOpts.get() + ", " + news.get());

			try {
				Thread.sleep(1000);
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
