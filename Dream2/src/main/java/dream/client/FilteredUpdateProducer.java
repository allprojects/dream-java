package dream.client;

import java.util.List;

import dream.common.SerializablePredicate;

class FilteredUpdateProducer<T> implements UpdateProducer<T> {

  private final UpdateProducer<T> producer;
  private final List<SerializablePredicate<T>> constraints;

  public FilteredUpdateProducer(UpdateProducer<T> producer, List<SerializablePredicate<T>> constraints) {
    super();
    this.producer = producer;
    this.constraints = constraints;
  }

  @Override
  public void notifyUpdateFinished() {
    producer.notifyUpdateFinished();
  }

  @Override
  public void registerUpdateConsumer(UpdateConsumer consumer, List<SerializablePredicate<T>> constraints) {
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
  public List<SerializablePredicate<T>> getConstraints() {
    return constraints;
  }

}
