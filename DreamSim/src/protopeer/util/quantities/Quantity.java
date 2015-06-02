package protopeer.util.quantities;

public abstract class Quantity implements Comparable<Quantity> {

	private final double valueInBaseUnit;

	public Quantity(double value) {
		this.valueInBaseUnit = value;
	}

	protected double valueInBaseUnit() {
		return valueInBaseUnit;
	}

	protected double getValue(Unit unit) {
		return valueInBaseUnit / unit.getRelationToBaseUnit();
	}

	public int compareTo(Quantity o) {
		if (getClass().equals(o.getClass())) {
			if (equals(o)) {
				return 0;
			} else if (valueInBaseUnit < o.valueInBaseUnit) {
				return -1;
			} else {
				return 1;
			}
		} else {
			throw new NotComparableException();
		}
	}

	public boolean isGreaterThan(Quantity o) {
		return compareTo(o) > 0;
	}

	public boolean isGreaterOrEqualTo(Quantity o) {
		return compareTo(o) >= 0;
	}

	public boolean isLowerThan(Quantity o) {
		return compareTo(o) < 0;
	}

	public boolean isLowerOrEqualTo(Quantity o) {
		return compareTo(o) <= 0;
	}

	@Override
	public boolean equals(Object arg0) {
		if(arg0 == null){
			return false;
		}
		if(getClass().equals(arg0.getClass())) {
			return valueInBaseUnit == ((Quantity)arg0).valueInBaseUnit;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return new Double(valueInBaseUnit).hashCode();
	}

}
