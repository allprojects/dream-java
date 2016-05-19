package dream.examples.financial;

import java.util.Arrays;
import java.util.List;

import dream.client.ChangeEventHandler;
import dream.client.RemoteVar;
import dream.client.Signal;
import dream.examples.util.Client;

public class FinancialApp extends Client implements ChangeEventHandler<Integer> {

	public FinancialApp() {
		super("Local");
	}

	private Signal<Integer> f1Signal;
	private Signal<Integer> f2Signal;
	private Signal<Integer> f3Signal;

	private RemoteVar<Integer> f1;
	private RemoteVar<Integer> f2;
	private RemoteVar<Integer> f3;

	public static void main(String[] args) {
		new FinancialApp().start();
	}

	@Override
	protected List<String> waitForVars() {
		return Arrays.asList("f1@Model1", "f2@Model2", "f3@Model3");
	}

	public void start() {
		f1 = new RemoteVar<>("Model1", "f1");
		f2 = new RemoteVar<>("Model2", "f2");
		f3 = new RemoteVar<>("Model3", "f3");

		f1Signal = new Signal<>("f1Signal", () -> f1.get(), f1);
		f2Signal = new Signal<>("f2Signal", () -> f2.get(), f2);
		f3Signal = new Signal<>("f3Signal", () -> f3.get(), f3);

		f1Signal.change().addHandler(this);
		f2Signal.change().addHandler(this);
		f3Signal.change().addHandler(this);

		try {
			Thread.sleep(2000);
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}

		System.out.println(f1.get());
		System.out.println(f2.get());
		System.out.println(f3.get());
	}

	@Override
	public void handle(Integer oldVal, Integer newVal) {
		System.out.println("Value changed");

		if (f1.get() != null && f2.get() != null && f3.get() != null) {
			if ((f1.get() + f2.get() + f3.get()) / 3.0 > 150) {
				System.out.println("Financial Alert!");
			}
		}
	}
}
