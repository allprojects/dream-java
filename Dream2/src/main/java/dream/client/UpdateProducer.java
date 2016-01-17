package dream.client;

import java.util.List;

import dream.common.SerializablePredicate;

/**
 * An UpdateProducer generates an update task and gets notified about the
 * lifetime events of a task.
 */
public interface UpdateProducer<T> {

  /**
   * The method is invoked when an update procedure finishes.
   */
  void notifyUpdateFinished();

  /**
   * Register a new consumer for the updates of this producer.
   *
   * @param consumer
   *          the consumer.
   */
  void registerUpdateConsumer(UpdateConsumer consumer);

  /**
   * Unregister the consumer from the updates of this producer.
   *
   * @param consumer
   *          the consumer.
   */
  void unregisterUpdateConsumer(UpdateConsumer consumer);

  /**
   * Return an UpdateProducer with the given filter.
   *
   * @param constraint
   *          the constraint used to filter.
   * @return an UpdateProducer for the filtered object.
   */
  public UpdateProducer<T> filter(SerializablePredicate<T> constraint);

  /**
   * Return the host of the object the producer refers to.
   *
   * @return the host of the object.
   */
  String getHost();

  /**
   * Return the name of the object the producer refers to.
   *
   * @return the name of the object.
   */
  String getObject();

  /**
   * Returns the constraints of the producer.
   *
   * @return the constraints of the producer.
   */
  List<SerializablePredicate> getConstraints();

}
