package dream.financial;

import dream.common.datatypes.Signal;

public interface FinancialModel {
	public Signal<Integer> compute();
}
