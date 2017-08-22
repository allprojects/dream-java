package dream.eval.utils;

import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;

import dream.common.Consts;

public class EvalUtils {

	public static final void updateTraffic(Serializable pkt, String subject, final Map<String, Long> trafficPkts,
			final Map<String, Long> trafficBytes) {
		long currentPkts = trafficPkts.containsKey(subject) ? trafficPkts.get(subject) : 0;
		trafficPkts.put(subject, currentPkts + 1);
		long currentBytes = trafficBytes.containsKey(subject) ? trafficBytes.get(subject) : 0;
		trafficBytes.put(subject, currentBytes + sizeof(pkt));
	}

	private static final int sizeof(Serializable pkt) {
		ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
		try {
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteOutputStream);
			objectOutputStream.writeObject(pkt);
			objectOutputStream.flush();
			objectOutputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return byteOutputStream.toByteArray().length;
	}

	public static final void saveTrafficToFile(final Map<String, Long> trafficPkts,
			final Map<String, Long> trafficBytes) {
		try {
			FileWriter writer = new FileWriter(Consts.trafficMeasurementFile, false);
			for (String subject : trafficPkts.keySet()) {
				writer.write(subject + "\t" + trafficPkts.get(subject) + "\t" + trafficBytes.get(subject) + "\n");
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
