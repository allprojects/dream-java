package protopeer.util.quantities;

import protopeer.*;

public class Time extends Quantity {

	private Time(double value, TimeUnit unit) {
		super(value * unit.getRelationToBaseUnit());
	}

	/**
	 * Internal methods can use the faster constructor without the
	 * multiplication.
	 * 
	 * @param value
	 */
	private Time(double valueInBaseUnit) {
		super(valueInBaseUnit);
	}

	public Time add(Time time) {
		return new Time(valueInBaseUnit() + time.valueInBaseUnit());
	}

	public Time subtract(Time time) {
		return new Time(valueInBaseUnit() - time.valueInBaseUnit());
	}

	public Time multiply(double factor) {
		return new Time(valueInBaseUnit() * factor);
	}

	public Time divideBy(double divisor) {
		return multiply(1 / divisor);
	}

	public static final Time ZERO = new Time(0);
	public static final Time MAX_VALUE = new Time(Double.MAX_VALUE);
	public static final Time MIN_VALUE = new Time(Double.MIN_VALUE);
	
	/*
	 * All the different time units ...
	 */

	private static abstract class TimeUnit extends Unit {
		// base unit is milliseconds
		private TimeUnit(double numberOfMilliseconds) {
			super(numberOfMilliseconds);
		}
	}

	private static Milliseconds MILLISECOND = new Milliseconds();

	private static class Milliseconds extends TimeUnit {
		private Milliseconds() {
			super(1);
		}
	}

	private static Seconds SECOND = new Seconds();

	private static class Seconds extends TimeUnit {
		private Seconds() {
			super(1e3);
		}
	}

	private static Minutes MINUTE = new Minutes();

	private static class Minutes extends TimeUnit {
		private Minutes() {
			super(60e3);
		}
	}

	private static Hours HOUR = new Hours();

	private static class Hours extends TimeUnit {
		private Hours() {
			super(3600e3);
		}
	}

	private static Days DAY = new Days();

	private static class Days extends TimeUnit {
		private Days() {
			super(86400e3);
		}
	}

	private static MeasurementEpoch MEASUREMENT_EPOCH = new MeasurementEpoch();

	private static class MeasurementEpoch extends TimeUnit {
		private MeasurementEpoch() {
			super(MainConfiguration.getSingleton().measurementEpochDuration);
		}
	}

	/*
	 * Access methods ...
	 */

	/**
	 * Returns a <code>Time</code> object representing <code>value</code>
	 * milliseconds.
	 * 
	 * @param value
	 *            the time in milliseconds.
	 * @return a <code>Time</code> object representing <code>value</code>
	 *         milliseconds
	 */
	public static Time inMilliseconds(double value) {
		return new Time(value, Time.MILLISECOND);
	}

	/**
	 * Returns the <code>time</code> in milliseconds.
	 * 
	 * @return the <code>time</code> in milliseconds
	 */
	public static double inMilliseconds(Time time) {
		return time.getValue(Time.MILLISECOND);
	}

	/**
	 * Returns a <code>Time</code> object representing <code>value</code>
	 * seconds.
	 * 
	 * @param value
	 *            the time in seconds.
	 * @return a <code>Time</code> object representing <code>value</code>
	 *         seconds
	 */
	public static Time inSeconds(double value) {
		return new Time(value, Time.SECOND);
	}

	/**
	 * Returns the <code>time</code> in seconds.
	 * 
	 * @return the <code>time</code> in seconds
	 */
	public static double inSeconds(Time time) {
		return time.getValue(Time.SECOND);
	}

	/**
	 * Returns a <code>Time</code> object representing <code>value</code>
	 * minutes.
	 * 
	 * @param value
	 *            the time in minutes.
	 * @return a <code>Time</code> object representing <code>value</code>
	 *         minutes
	 */
	public static Time inMinutes(double value) {
		return new Time(value, Time.MINUTE);
	}

	/**
	 * Returns the <code>time</code> in minutes.
	 * 
	 * @return the <code>time</code> in minutes
	 */
	public static double inMinutes(Time time) {
		return time.getValue(Time.MINUTE);
	}

	/**
	 * Returns a <code>Time</code> object representing <code>value</code> hours.
	 * 
	 * @param value
	 *            the time in hours.
	 * @return a <code>Time</code> object representing <code>value</code> hours
	 */
	public static Time inHours(double value) {
		return new Time(value, Time.HOUR);
	}

	/**
	 * Returns the <code>time</code> in hours.
	 * 
	 * @return the <code>time</code> in hours
	 */
	public static double inHours(Time time) {
		return time.getValue(Time.HOUR);
	}

	/**
	 * Returns a <code>Time</code> object representing <code>value</code> days.
	 * 
	 * @param value
	 *            the time in days.
	 * @return a <code>Time</code> object representing <code>value</code> days
	 */
	public static Time inDays(double value) {
		return new Time(value, Time.DAY);
	}

	/**
	 * Returns the <code>time</code> in days.
	 * 
	 * @return the <code>time</code> in days
	 */
	public static double inDays(Time time) {
		return time.getValue(Time.DAY);
	}

	/**
	 * Returns a <code>Time</code> object representing <code>value</code> in
	 * measurement epochs.
	 * 
	 * @param value
	 *            the time in measurement epochs.
	 * @return a <code>Time</code> object representing <code>value</code> days
	 */
	public static Time inMeasurementEpochs(double value) {
		return new Time(value, Time.MEASUREMENT_EPOCH);
	}

	/**
	 * Returns the <code>time</code> in measurement epochs.
	 * 
	 * @return the <code>time</code> in measurement epochs.
	 */
	public static double inMeasurementEpochs(Time time) {
		return time.getValue(Time.MEASUREMENT_EPOCH);
	}

}
