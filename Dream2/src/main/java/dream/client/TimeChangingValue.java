package dream.client;

/**
 * Represents a generic time changing value.
 *
 * In particular, it exposes an evaluate() method which is automatically invoked
 * whenever one of the values it depends on changes.
 *
 * It also exposes methods to register and unregister ValueChangeListener, which
 * are automatically notified when the value changes.
 */
interface TimeChangingValue<T> {

	/**
	 * The evaluate method is automatically invoked whenever one of the values
	 * this object depends on changes.
	 */
	public T evaluate();

	/**
	 * Create an event that fires every time the TimeChangingValue changes. It
	 * fires the tuple (oldVal, newVal) for the change. The first tuple is
	 * (null, newVal)
	 * 
	 * @return a new ChangeEvent
	 */
	public ChangeEvent<T> change();
}
