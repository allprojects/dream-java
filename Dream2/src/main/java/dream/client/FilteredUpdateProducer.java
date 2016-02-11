package dream.client;

import java.util.List;

import dream.common.SerializablePredicate;

class FilteredUpdateProducer<T> implements UpdateProducer<T> {

	private final UpdateProducer<T> producer;
	private final List<SerializablePredicate> constraints;

	public FilteredUpdateProducer(UpdateProducer<T> producer, List<SerializablePredicate> constraints) {
		super();
		this.producer = producer;
		this.constraints = constraints;
	}

	@Override
	public void notifyUpdateFinished() {
		producer.notifyUpdateFinished();
	}

	@Override
	public void registerUpdateConsumer(UpdateConsumer consumer, List<SerializablePredicate> constraints) {
		producer.registerUpdateConsumer(consumer, constraints);
	}

	@Override
	public void unregisterUpdateConsumer(UpdateConsumer consumer) {
		producer.unregisterUpdateConsumer(consumer);
	}

	@Override
	public UpdateProducer<T> filter(SerializablePredicate<T> constraint) {
		constraints.add(constraint);
		return this;
	}

	@Override
	public String getHost() {
		return producer.getHost();
	}

	@Override
	public String getObject() {
		return producer.getObject();
	}

	@Override
	public List<SerializablePredicate> getConstraints() {
		return constraints;
	}

}
