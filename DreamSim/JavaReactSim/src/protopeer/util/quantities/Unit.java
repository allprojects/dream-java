package protopeer.util.quantities;

public abstract class Unit {
	
	public final double relationToBaseUnit;
	
	public Unit(double relationToBaseUnit) {
		this.relationToBaseUnit = relationToBaseUnit;
	}
	
	public double getRelationToBaseUnit() {
		return relationToBaseUnit;
	}
}
