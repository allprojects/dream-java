package protopeer.measurement;

import java.io.*;
import java.util.*;

/**
 * Represents a set of aggregated measured values. Each value is a
 * <code>double</code>. Basic statistics on the set of values can be computer
 * (avg, min, max, etc.)
 * 
 * 
 */
public class Aggregate implements Cloneable, Serializable {

	private static final long serialVersionUID = 9185547673353467107L;

	private int numValues = 0;

	private double average = Double.NaN;

	private double minValue = Double.NaN;

	private double maxValue = Double.NaN;

	private double sumValue = 0.0;

	private double sumSquaredValue = 0.0;

	private ArrayList<Double> values;

	private final boolean storeValues;

	public boolean isStoreValues() {
		return storeValues;
	}

	private Aggregate() {
		this.storeValues = false;
	}

	/**
	 * Constructs an empty set of values.
	 * 
	 * @param storeValues
	 *            tells the aggregate whether to keep all the values or only the
	 *            aggregates, note: keeping all the values requires much more
	 *            memory, but is necessary for computing the percentiles
	 */
	Aggregate(boolean storeValues) {
		if (storeValues) {
			this.values = new ArrayList<Double>();
		}
		this.storeValues = storeValues;
	}

	/**
	 * Gets the average of the aggregated values.
	 * 
	 * @return
	 */
	public synchronized double getAverage() {
		return average;
	}

	/**
	 * Gets the number of aggregated values.
	 * 
	 * @return
	 */
	public synchronized int getNumValues() {
		return numValues;
	}

	/**
	 * Gets the minimum of the aggregated values
	 * 
	 * @return
	 */
	public synchronized double getMin() {
		return minValue;
	}

	/**
	 * Gets the maximum of the aggregated values
	 * 
	 * @return
	 */
	public synchronized double getMax() {
		return maxValue;
	}

	/**
	 * Gets the sum of the aggregated values
	 * 
	 * @return
	 */
	public synchronized double getSum() {
		return sumValue;
	}

	/**
	 * Gets the sum of the squares of the aggregated values
	 * 
	 * @return
	 */
	public synchronized double getSumSquared() {
		return sumSquaredValue;
	}

	/**
	 * Adds the <code>value</code> to the aggregate.
	 * 
	 * @param value
	 */
	public synchronized void addValue(double value) {
		if (numValues == 0) {
			average = value;
			sumValue = value;
			minValue = value;
			maxValue = value;
			sumSquaredValue = value * value;
		} else {
			average = (sumValue + value) / (numValues + 1);
			sumValue += value;
			sumSquaredValue += value * value;
			minValue = Math.min(value, minValue);
			maxValue = Math.max(value, maxValue);
		}
		if (isStoreValues()) {
			values.add(value);
		}

		numValues++;
	}

	/**
	 * Returns the median of the aggregated values, requires
	 * <code>storeValues</code> to be true.
	 * 
	 * @return
	 */
	public synchronized double getMedian() {
		return getPercentile(50);
	}
	
	
	/**
	 * Returns the standard deviation of the aggregated values.
	 * 
	 * @return
	 */
	public synchronized double getStdDev() {
		return Math.sqrt((sumSquaredValue/numValues) - (sumValue/numValues)*(sumValue/numValues));
	}
	
	/**
	 * Returns the <code>percentile</code>-th percentile of the aggregated
	 * values, requires <code>storeValues</code> to be true.
	 * 
	 * @return
	 */
	public synchronized double getPercentile(double percentile) {
		if (values == null || values.size() == 0) {
			return Double.NaN;
		}
		Collections.sort(values);
		return values.get((int) (values.size() * percentile / 100.0));
	}

	/**
	 * Merges the values from the <code>otherAggregate</code> into this
	 * aggregate.
	 * 
	 * @param otherAggregate
	 */
	public synchronized void mergeWith(Aggregate otherAggregate) {
		if (this.numValues == 0) {
			minValue = otherAggregate.minValue;
			maxValue = otherAggregate.maxValue;
			average = otherAggregate.average;
			sumValue = otherAggregate.sumValue;
			sumSquaredValue = otherAggregate.sumSquaredValue;
			numValues = otherAggregate.numValues;
		} else if (otherAggregate.numValues == 0) {
			return;
		} else {
			minValue = Math.min(this.minValue, otherAggregate.minValue);
			maxValue = Math.max(this.maxValue, otherAggregate.maxValue);
			average = (this.sumValue + otherAggregate.sumValue) / (this.numValues + otherAggregate.numValues);
			sumValue = this.sumValue + otherAggregate.sumValue;
			sumSquaredValue = this.sumSquaredValue + otherAggregate.sumSquaredValue;
			numValues = this.numValues + otherAggregate.numValues;
		}
		if (otherAggregate.isStoreValues() && this.isStoreValues()) {
			values.addAll(otherAggregate.values);
		}
	}

	public Collection<Double> getValues() {
		if (values == null) {
			return null;
		}
		return Collections.unmodifiableCollection(values);
	}

	@Override
	public synchronized Aggregate clone() {
		try {
			Aggregate twin = (Aggregate) super.clone();
			if (this.isStoreValues()) {
				twin.values = new ArrayList<Double>(this.values);
			}
			return twin;
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

	@Override
	public String toString() {
		return "(#=" + numValues + ", sum=" + sumValue + ", avg=" + average + ")";
	}

}
