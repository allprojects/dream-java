package javareact.financial;

import javareact.common.Consts;
import javareact.common.types.ReactiveChangeListener;
import javareact.common.types.RemoteVar;
import javareact.common.types.Signal;

public class FinancialApp implements ReactiveChangeListener<Integer> {
	private Signal<Integer> f1Signal;
	private Signal<Integer> f2Signal;
	private Signal<Integer> f3Signal;

	public static void main(String[] args) {
		new FinancialApp().start();
	}
	
	public void start() {
		Consts.hostName = "Local";

		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		RemoteVar<Integer> f1 = new RemoteVar<>("Model1", "f1");
		RemoteVar<Integer> f2 = new RemoteVar<>("Model2", "f2");
		RemoteVar<Integer> f3 = new RemoteVar<>("Model3", "f3");
		
		RemoteVar<Integer> model1 = new RemoteVar<>("Model1", "model1");

		f1Signal = new Signal<>("f1Signal", () -> f1.get(), f1);
		f2Signal = new Signal<>("f2Signal", () -> f2.get(), f2);
		f3Signal = new Signal<>("f3Signal", () -> f3.get(), f3);

		f1Signal.addReactiveChangeListener(this);
		f2Signal.addReactiveChangeListener(this);
		f3Signal.addReactiveChangeListener(this);
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		System.out.println(f1Signal.get());
		System.out.println(f2Signal.get());
		System.out.println(f3Signal.get());
		
		System.out.println(model1.get());
	}

	@Override
	public void notifyReactiveChanged(Integer newValue) {
		System.out.println("Value changed");
		if (f1Signal.get() != null && f2Signal.get() != null && f3Signal.get() != null) {
			if ((f1Signal.get() + f2Signal.get() + f3Signal.get()) / 3.0 > 150) {
				System.out.println("Financial Alert!");
			}
		}
	}
}
