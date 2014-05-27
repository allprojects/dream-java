package protopeer;

/**
 * The implementation of the Chord-like unit ring peer identifier space.
 * 
 *
 */
public class RingIdentifier extends PeerIdentifier {

	private double position;

	public RingIdentifier(double position) {
		this.position = position;
	}

	public double distanceTo(PeerIdentifier id) {
		RingIdentifier otherID = (RingIdentifier) id;
		return distanceBetween(position, otherID.position);
	}

	private double distanceBetween(double position1, double position2) {
		double vector=shortestDistanceVectorBetween(position1, position2);
		return (vector>0 ? vector : -vector);
	}

	public static double shortestDistanceVectorBetween(RingIdentifier start, RingIdentifier end) {
		return shortestDistanceVectorBetween(start.position,end.position);
	}
	
	public static double shortestPositiveDistanceVectorBetween(RingIdentifier start, RingIdentifier end) {
		return shortestPositiveDistanceVectorBetween(start.position,end.position);
	}
	
	private static double shortestDistanceVectorBetween(double startPosition, double endPosition) {
		double minVector = Double.MAX_VALUE;
		for (int offset = -1; offset <= 1; offset++) {
			double vector = offset + endPosition - startPosition;
			//Math.abs is a bit slower than the ? : operator
			if ((vector>0 ? vector : -vector) < (minVector>0 ? minVector : -minVector)) {
				minVector = vector;
			}
		}
		return minVector;
	}

	private static double shortestPositiveDistanceVectorBetween(double startPosition, double endPosition) {
		double minVector = Double.MAX_VALUE;
		for (int offset = -1; offset <= 1; offset++) {
			double vector = offset + endPosition - startPosition;
			if (vector>=0 && vector<minVector) {			
				minVector = vector;
			}
		}
		return minVector;
	}

	
	public int compareTo(PeerIdentifier id) {
		RingIdentifier otherID = (RingIdentifier) id;
		if (this.position < otherID.position) {
			return -1;
		}
		if (this.position > otherID.position) {
			return 1;
		}
		return 0;
	}

	public String toString() {
		return "" + position;
	}

	public double getPosition() {
		return position;
	}

	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(position);
		result = PRIME * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		//		if (obj == null)
		//			return false;
		//		if (getClass() != obj.getClass())
		//			return false;
		final RingIdentifier other = (RingIdentifier) obj;
		if (Double.doubleToLongBits(position) != Double.doubleToLongBits(other.position))
			return false;
		return true;
	}

	public double getMaxDistance() {
		return 0.5;
	}
}
