package protopeer.util.quantities;

public class Bandwidth extends Quantity {

	public Bandwidth(double value, BWUnit unit) {
		super(value * unit.getRelationToBaseUnit());
	}

	private Bandwidth(double value) {
		super(value);
	}

	public double getValue(BWUnit unit) {
		return super.getValue(unit);
	}
	
	public Bandwidth add(Bandwidth bandwidth) {
		return new Bandwidth(valueInBaseUnit() + bandwidth.valueInBaseUnit());
	}
	
	public Bandwidth subtract(Bandwidth bandwidth) {
		return new Bandwidth(valueInBaseUnit() - bandwidth.valueInBaseUnit());
	}
	
	public Bandwidth multiply(double factor) {
		return new Bandwidth(valueInBaseUnit() * factor);
	}
	
	public Bandwidth divideBy(double divisor) {
		return multiply(1/divisor);
	}
	

	
	/*
	 * All the bandwidth units
	 */
	private static abstract class BWUnit extends Unit {
		// base unit is bits/millisecond
		private BWUnit(double relationToBitPerSecond) {
			super(relationToBitPerSecond);
		}
	}
	
	private static final BWUnit BitPerMillisecond = new BitsPerMillisecond();
	private static class BitsPerMillisecond extends BWUnit {
		private BitsPerMillisecond() { super(1); }
	}
	
	private static final BWUnit BitPerSecond = new BitsPerSecond();
	private static class BitsPerSecond extends BWUnit {
		private BitsPerSecond() { super(1e-3); }
	}
	
	private static final BWUnit KBitPerSecond = new KBitsPerSecond();
	private static class KBitsPerSecond extends BWUnit {
		private KBitsPerSecond() { super(1); }
	}
	
	private static final BWUnit MBitPerSecond = new MBitsPerSecond();
	private static class MBitsPerSecond extends BWUnit {
		private MBitsPerSecond() { super(1e3); }
	}
	
	private static final BWUnit GBitPerSecond = new GBitsPerSecond();
	private static class GBitsPerSecond extends BWUnit {
		private GBitsPerSecond() { super(1e6); }
	}

	private static final BWUnit BytePerSecond = new BytePerSecond();
	private static class BytePerSecond extends BWUnit {
		private BytePerSecond() { super(8e-3); }
	}
	
	private static final BWUnit KBytePerSecond= new KBytePerSecond();
	private static class KBytePerSecond extends BWUnit {
		private KBytePerSecond() { super(8); }
	}
	
	private static final BWUnit MBytePerSecond = new MBytePerSecond();
	private static class MBytePerSecond extends BWUnit {
		private MBytePerSecond() { super(8e3); }
	}
	
	private static final BWUnit GBytePerSecond = new GBytePerSecond();
	private static class GBytePerSecond extends BWUnit {
		private GBytePerSecond() { super(8e6); }
	}
	
	/**
	 * Represents the bandwidth 0 
	 */
	public static final Bandwidth ZERO = new Bandwidth(0);
	public static final Bandwidth MAX_VALUE = new Bandwidth(Double.MAX_VALUE);
	public static final Bandwidth MIN_VALUE = new Bandwidth(Double.MAX_VALUE);

	/**
	 * Returns a <code>Bandwidth</code> object representing
	 * <code>value</code> BitPerMillisecond.
	 * 
	 * @param value the bandwidth in BitPerMillisecond
	 * @return a <code>Bandwidth</code> object representing
	 *         <code>value</code> BitPerMillisecond
	 */
	public static Bandwidth inBitPerMillisecond(double value) {
		return new Bandwidth(value, Bandwidth.BitPerMillisecond);
	}

	/**
	 * Returns the <code>bandwidth</code> in BitPerMillisecond.
	 * 
	 * @return the <code>bandwidth</code> in BitPerMillisecond
	 */
	public static double inBitPerMillisecond(Bandwidth bandwidth) {
		return bandwidth.getValue(Bandwidth.BitPerMillisecond);
	}

	/**
	 * Returns a <code>Bandwidth</code> object representing
	 * <code>value</code> BitPerSecond.
	 * 
	 * @param value the bandwidth in BitPerSecond
	 * @return a <code>Bandwidth</code> object representing
	 *         <code>value</code> BitPerSecond
	 */
	public static Bandwidth inBitPerSecond(double value) {
		return new Bandwidth(value, Bandwidth.BitPerSecond);
	}

	/**
	 * Returns the <code>bandwidth</code> in BitPerSecond.
	 * 
	 * @return the <code>bandwidth</code> in BitPerSecond
	 */
	public static double inBitPerSecond(Bandwidth bandwidth) {
		return bandwidth.getValue(Bandwidth.BitPerSecond);
	}

	/**
	 * Returns a <code>Bandwidth</code> object representing
	 * <code>value</code> KBitPerSecond.
	 * 
	 * @param value the bandwidth in KBitPerSecond
	 * @return a <code>Bandwidth</code> object representing
	 *         <code>value</code> KBitPerSecond
	 */
	public static Bandwidth inKBitPerSecond(double value) {
		return new Bandwidth(value, Bandwidth.KBitPerSecond);
	}

	/**
	 * Returns the <code>bandwidth</code> in KBitPerSecond.
	 * 
	 * @return the <code>bandwidth</code> in KBitPerSecond
	 */
	public static double inKBitPerSecond(Bandwidth bandwidth) {
		return bandwidth.getValue(Bandwidth.KBitPerSecond);
	}
	
	/**
	 * Returns a <code>Bandwidth</code> object representing
	 * <code>value</code> MBitPerSecond.
	 * 
	 * @param value the bandwidth in MBitPerSecond
	 * @return a <code>Bandwidth</code> object representing
	 *         <code>value</code> MBitPerSecond
	 */
	public static Bandwidth inMBitPerSecond(double value) {
		return new Bandwidth(value, Bandwidth.MBitPerSecond);
	}

	/**
	 * Returns the <code>bandwidth</code> in MBitPerSecond.
	 * 
	 * @return the <code>bandwidth</code> in MBitPerSecond
	 */
	public static double inMBitPerSecond(Bandwidth bandwidth) {
		return bandwidth.getValue(Bandwidth.MBitPerSecond);
	}
	
	/**
	 * Returns a <code>Bandwidth</code> object representing
	 * <code>value</code> GBitPerSecond.
	 * 
	 * @param value the bandwidth in GBitPerSecond
	 * @return a <code>Bandwidth</code> object representing
	 *         <code>value</code> GBitPerSecond
	 */
	public static Bandwidth inGBitPerSecond(double value) {
		return new Bandwidth(value, Bandwidth.GBitPerSecond);
	}

	/**
	 * Returns the <code>bandwidth</code> in GBitPerSecond.
	 * 
	 * @return the <code>bandwidth</code> in GBitPerSecond
	 */
	public static double inGBitPerSecond(Bandwidth bandwidth) {
		return bandwidth.getValue(Bandwidth.GBitPerSecond);
	}

	/**
	 * Returns a <code>Bandwidth</code> object representing
	 * <code>value</code> BytePerSecond.
	 * 
	 * @param value the bandwidth in BytePerSecond
	 * @return a <code>Bandwidth</code> object representing
	 *         <code>value</code> BytePerSecond
	 */
	public static Bandwidth inBytePerSecond(double value) {
		return new Bandwidth(value, Bandwidth.BytePerSecond);
	}

	/**
	 * Returns the <code>bandwidth</code> in BytePerSecond.
	 * 
	 * @return the <code>bandwidth</code> in BytePerSecond
	 */
	public static double inBytePerSecond(Bandwidth bandwidth) {
		return bandwidth.getValue(Bandwidth.BytePerSecond);
	}

	/**
	 * Returns a <code>Bandwidth</code> object representing
	 * <code>value</code> KBytePerSecond.
	 * 
	 * @param value the bandwidth in KBytePerSecond
	 * @return a <code>Bandwidth</code> object representing
	 *         <code>value</code> KBytePerSecond
	 */
	public static Bandwidth inKBytePerSecond(double value) {
		return new Bandwidth(value, Bandwidth.KBytePerSecond);
	}

	/**
	 * Returns the <code>bandwidth</code> in KBytePerSecond.
	 * 
	 * @return the <code>bandwidth</code> in KBytePerSecond
	 */
	public static double inKBytePerSecond(Bandwidth bandwidth) {
		return bandwidth.getValue(Bandwidth.KBytePerSecond);
	}
	
	/**
	 * Returns a <code>Bandwidth</code> object representing
	 * <code>value</code> MBytePerSecond.
	 * 
	 * @param value the bandwidth in MBytePerSecond
	 * @return a <code>Bandwidth</code> object representing
	 *         <code>value</code> MBytePerSecond
	 */
	public static Bandwidth inMBytePerSecond(double value) {
		return new Bandwidth(value, Bandwidth.MBytePerSecond);
	}

	/**
	 * Returns the <code>bandwidth</code> in MBytePerSecond.
	 * 
	 * @return the <code>bandwidth</code> in MBytePerSecond
	 */
	public static double inMBytePerSecond(Bandwidth bandwidth) {
		return bandwidth.getValue(Bandwidth.MBytePerSecond);
	}
	
	/**
	 * Returns a <code>Bandwidth</code> object representing
	 * <code>value</code> GBytePerSecond.
	 * 
	 * @param value the bandwidth in GBytePerSecond
	 * @return a <code>Bandwidth</code> object representing
	 *         <code>value</code> GBytePerSecond
	 */
	public static Bandwidth inGBytePerSecond(double value) {
		return new Bandwidth(value, Bandwidth.GBytePerSecond);
	}

	/**
	 * Returns the <code>bandwidth</code> in GBytePerSecond.
	 * 
	 * @return the <code>bandwidth</code> in GBytePerSecond
	 */
	public static double inGBytePerSecond(Bandwidth bandwidth) {
		return bandwidth.getValue(Bandwidth.GBytePerSecond);
	}


}
