package dream.examples.atomic;

import java.util.ArrayList;
import java.util.List;

import dream.client.RemoteVar;
import dream.client.Signal;
import dream.common.Consts;

public class TrafficSignal {
	private List<String> listening;

	public static void main(String[] args) {
		new TrafficSignal().start(0);
		new TrafficSignal().start(1);
		new TrafficSignal().start(2);
		new TrafficSignal().start(3);

	}

	public void start(int count) {
		Consts.hostName = "TrafficSignal" + count;

		final RemoteVar<ArrayList<Integer>> durationForGreenLight = new RemoteVar<>(TrafficAnalyzer.NAME,
				TrafficAnalyzer.GREEN_LIGHT_DURATIONS);

		Signal<ArrayList<Integer>> nodeOneSignal = new Signal<ArrayList<Integer>>("getDuration", () -> {
			if (durationForGreenLight.get() == null)
				return new ArrayList<Integer>();
			else
				return durationForGreenLight.get();
		} , durationForGreenLight);
		nodeOneSignal.change()
				.addHandler((oldVal, val) -> System.out.println("Signal" + count + ": " + val.get(count)));
	}

}
