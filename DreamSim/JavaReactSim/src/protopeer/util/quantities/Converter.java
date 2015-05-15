package protopeer.util.quantities;

public class Converter {
	
	public static Bandwidth getBandwidth(Data data, Time time) {
		double dataValue = Data.inBit(data);
		double timeValue = Time.inMilliseconds(time);
		return Bandwidth.inBitPerMillisecond(dataValue / timeValue);
	}
	
	public static Time getTime(Data data, Bandwidth bandwidth) {
		double value = Data.inBit(data) / Bandwidth.inBitPerMillisecond(bandwidth);
		return Time.inMilliseconds(value);
	}
	
	public static Data getData(Bandwidth bandwidth, Time time) {
		double value = Bandwidth.inBitPerMillisecond(bandwidth) * 
				Time.inMilliseconds(time); 
		return Data.inBit(value);
	}
}
