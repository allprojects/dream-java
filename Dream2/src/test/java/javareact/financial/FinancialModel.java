package javareact.financial;

import javareact.common.types.Signal;

public interface FinancialModel {
	public Signal<Integer> compute();
}
