package dream.examples.financial;

import dream.client.Signal;

public interface FinancialModel {
	public Signal<Integer> compute();
}
