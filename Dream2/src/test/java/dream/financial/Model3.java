package dream.financial;

import dream.common.Consts;
import dream.common.datatypes.RemoteVar;
import dream.common.datatypes.Signal;
import dream.common.datatypes.ValueChangeListener;

public class Model3 implements ValueChangeListener<Integer> {
	public void start() {
		Consts.hostName = "Model3";
		
		RemoteVar<Integer> marketIndex = new RemoteVar<>("InputModel", "marketIndex");
		RemoteVar<Integer> news = new RemoteVar<>("InputModel", "news");

		Signal<Integer> f3 = new Signal<>("f3", () -> {
			if (marketIndex.get() == null || news.get() == null) { return null; }
			else { return marketIndex.get() + news.get(); }
		}, marketIndex, news);
		
		f3.addValueChangeListener(this);
	}

	@Override
	public void notifyValueChanged(Integer newValue) {
		System.out.println("New value for f3: " + newValue);
	}
	
	public static void main(String[] args) {
		new Model3().start();
	}
}
