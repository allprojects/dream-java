package protopeer.util.quantities;

public class Data extends Quantity {
	
	protected Data(double value, DataUnit unit) {
		super(value * unit.getRelationToBaseUnit());
	}
	
	protected Data(double value) {
		super(value);
	}
	
	public Data add(Data data) {
		return new Data(valueInBaseUnit() + data.valueInBaseUnit());
	}
	
	public Data subtract(Data data) {
		return new Data(valueInBaseUnit() - data.valueInBaseUnit());
	}
	
	public Data multiply(double factor) {
		return new Data(valueInBaseUnit() * factor);
	}
	
	public Data divideBy(double divisor) {
		return multiply(1/divisor);
	}
	
	
	/*
	 * The data units ...
	 */
	private static abstract class DataUnit extends Unit {
		// base unit is bit
		private DataUnit(double numberOfBits) {
			super(numberOfBits);
		}
	}
	
	private static final DataUnit Bit = new Bit();
	private static class Bit extends DataUnit {
		private Bit() { super(1); }
	}
	
	private static final DataUnit KBit = new KBit();
	private static class KBit extends DataUnit {
		private KBit() { super(1e3); }
	}

	private static final DataUnit MBit = new MBit();
	private static class MBit extends DataUnit {
		private MBit() { super(1e6); }
	}
	
	private static final DataUnit GBit = new GBit();
	private static class GBit extends DataUnit {
		private GBit() { super(1e9); }
	}
	
	private static final DataUnit Byte = new Byte();
	private static class Byte extends DataUnit {
		private Byte() { super(8); }
	}
	
	private static final DataUnit KByte = new KByte();
	private static class KByte extends DataUnit {
		private KByte() { super(8e3); }
	}

	private static final DataUnit MByte = new MByte();
	private static class MByte extends DataUnit {
		private MByte() { super(8e6); }
	}
	
	private static final DataUnit GByte = new GByte();
	private static class GByte extends DataUnit {
		private GByte() { super(8e9); }
	}

	/*
	 * Access methods 
	 */
	
	/**
	 * Represents the data 0 
	 */
	public static final Data ZERO = new Data(0);
	public static final Data MAX_VALUE = new Data(Double.MAX_VALUE);
	public static final Data MIN_VALUE = new Data(Double.MIN_VALUE);

	/**
	 * Returns a <code>Data</code> object representing
	 * <code>value</code> Bit.
	 * 
	 * @param value the amount of data in Bit
	 * @return a <code>Data</code> object representing
	 *         <code>value</code> Bit
	 */
	public static Data inBit(double value) {
		return new Data(value, Data.Bit);
	}

	/**
	 * Returns the amount of <code>data</code> in Bit.
	 * 
	 * @return the amount of <code>data</code> in Bit
	 */
	public static double inBit(Data data) {
		return data.getValue(Data.Bit);
	}

	/**
	 * Returns a <code>Data</code> object representing
	 * <code>value</code> KBit.
	 * 
	 * @param value the amount of data in KBit
	 * @return a <code>Data</code> object representing
	 *         <code>value</code> KBit
	 */
	public static Data inKBit(double value) {
		return new Data(value, Data.KBit);
	}

	/**
	 * Returns the amount of <code>data</code> in KBit.
	 * 
	 * @return the amount of <code>data</code> in KBit
	 */
	public static double inKBit(Data data) {
		return data.getValue(Data.KBit);
	}
	
	/**
	 * Returns a <code>Data</code> object representing
	 * <code>value</code> MBit.
	 * 
	 * @param value the amount of data in MBit
	 * @return a <code>Data</code> object representing
	 *         <code>value</code> MBit
	 */
	public static Data inMBit(double value) {
		return new Data(value, Data.MBit);
	}

	/**
	 * Returns the amount of <code>data</code> in MBit.
	 * 
	 * @return the amount of <code>data</code> in MBit
	 */
	public static double inMBit(Data data) {
		return data.getValue(Data.MBit);
	}
	
	/**
	 * Returns a <code>Data</code> object representing
	 * <code>value</code> GBit.
	 * 
	 * @param value the amount of data in GBit
	 * @return a <code>Data</code> object representing
	 *         <code>value</code> GBit
	 */
	public static Data inGBit(double value) {
		return new Data(value, Data.GBit);
	}

	/**
	 * Returns the amount of <code>data</code> in GBit.
	 * 
	 * @return the amount of <code>data</code> in GBit
	 */
	public static double inGBit(Data data) {
		return data.getValue(Data.GBit);
	}

	/**
	 * Returns a <code>Data</code> object representing
	 * <code>value</code> Byte.
	 * 
	 * @param value the amount of data in Byte
	 * @return a <code>Data</code> object representing
	 *         <code>value</code> Byte
	 */
	public static Data inByte(double value) {
		return new Data(value, Data.Byte);
	}

	/**
	 * Returns the amount of <code>data</code> in Byte.
	 * 
	 * @return the amount of <code>data</code> in Byte
	 */
	public static double inByte(Data data) {
		return data.getValue(Data.Byte);
	}

	/**
	 * Returns a <code>Data</code> object representing
	 * <code>value</code> KByte.
	 * 
	 * @param value the amount of data in KByte
	 * @return a <code>Data</code> object representing
	 *         <code>value</code> KByte
	 */
	public static Data inKByte(double value) {
		return new Data(value, Data.KByte);
	}

	/**
	 * Returns the amount of <code>data</code> in KByte.
	 * 
	 * @return the amount of <code>data</code> in KByte
	 */
	public static double inKByte(Data data) {
		return data.getValue(Data.KByte);
	}
	
	/**
	 * Returns a <code>Data</code> object representing
	 * <code>value</code> MByte.
	 * 
	 * @param value the amount of data in MByte
	 * @return a <code>Data</code> object representing
	 *         <code>value</code> MByte
	 */
	public static Data inMByte(double value) {
		return new Data(value, Data.MByte);
	}

	/**
	 * Returns the amount of <code>data</code> in MByte.
	 * 
	 * @return the amount of <code>data</code> in MByte
	 */
	public static double inMByte(Data data) {
		return data.getValue(Data.MByte);
	}
	
	/**
	 * Returns a <code>Data</code> object representing
	 * <code>value</code> GByte.
	 * 
	 * @param value the amount of data in GByte
	 * @return a <code>Data</code> object representing
	 *         <code>value</code> GByte
	 */
	public static Data inGByte(double value) {
		return new Data(value, Data.GByte);
	}

	/**
	 * Returns the amount of <code>data</code> in GByte.
	 * 
	 * @return the amount of <code>data</code> in GByte
	 */
	public static double inGByte(Data data) {
		return data.getValue(Data.GByte);
	}

}
