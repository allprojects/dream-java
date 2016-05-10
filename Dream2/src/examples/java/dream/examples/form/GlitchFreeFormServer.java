package dream.examples.form;

import java.util.LinkedList;

import dream.client.Signal;
import dream.examples.util.Pair;

public class GlitchFreeFormServer extends FormServer {

	@Override
	protected void createDependencies() {
		logger.fine("Building Dependencies");

		final UpdateCounter minimumCounter = new UpdateCounter();
		final UpdateCounter maximumCounter = new UpdateCounter();

		final Signal<Pair<Boolean, Integer>> minimumHours = new Signal<>("minimumHours", () -> {
			return new Pair<>(working_hours.get() > 10, minimumCounter.incAndGet());
		}, working_hours);

		final Signal<Pair<Boolean, Integer>> maximumHours = new Signal<>("maximumHours", () -> {
			return new Pair<>(working_hours.get() < 60, maximumCounter.incAndGet());
		}, working_hours);

		final Signal<Boolean> minimumEuroPerHour = new Signal<>("minimumEuroPerHour", () -> {
			return euro_per_hour.get() > 10;
		}, euro_per_hour);

		final LinkedList<Pair<Boolean, Integer>> minimumQueue = new LinkedList<>();
		final LinkedList<Pair<Boolean, Integer>> maximumQueue = new LinkedList<>();
		final OldValue<Boolean> currentValue = new OldValue<>();

		new Signal<>("settingsOkay", () -> {
			if (minimumQueue.getLast().getSecond() < minimumHours.get().getSecond())
				minimumQueue.add(minimumHours.get());
			else if (maximumQueue.getLast().getSecond() < maximumHours.get().getSecond())
				maximumQueue.add(maximumHours.get());

			if (minimumQueue.size() > 0 && maximumQueue.size() > 0)
				currentValue.set(
						minimumQueue.pop().getFirst() && maximumQueue.pop().getFirst() && minimumEuroPerHour.get());
			return currentValue.get();
		}, minimumHours, maximumHours, minimumEuroPerHour);

		new Signal<>("salary", () -> {
			return working_hours.get() * euro_per_hour.get();
		}, working_hours, euro_per_hour);

		logger.fine("Finished building Dependencies");
	}

	public static void main(String[] args) {
		new GlitchFreeFormServer();
	}
}

class UpdateCounter {
	int i = 0;

	public void inc() {
		i += 1;
	}

	public int incAndGet() {
		inc();
		return get();
	}

	public int get() {
		return i;
	}
}

class OldValue<T> {
	T value;

	public T get() {
		return value;
	}

	public void set(T v) {
		value = v;
	}
}
