package javareact.measurement;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javareact.experiments.JavaReactConfiguration;
import protopeer.Experiment;
import protopeer.network.Message;
import protopeer.util.quantities.Data;

/**
 * This class is used to store and print all the information about the experiment.
 * 
 * @author Alessandro Margara
 */
public class MeasurementLogger {
  private final Map<String, Integer> storedMsgs = new HashMap<String, Integer>();
  private final Map<String, Double> storedMsgsSize = new HashMap<String, Double>();
  private final Map<Integer, List<Double>> storedDelays = new HashMap<Integer, List<Double>>();
  private final int epochDuration;
  private final int simulationTime;
  private static MeasurementLogger logger = null;

  public static MeasurementLogger getLogger() {
    if (logger == null) {
      logger = new MeasurementLogger();
    }
    return logger;
  }

  private MeasurementLogger() {
    // Private constructor
    simulationTime = JavaReactConfiguration.getSingleton().simulationTimeInSeconds;
    epochDuration = JavaReactConfiguration.getSingleton().epochDuration;
  }

  public void resetCounters() {
    storedMsgs.clear();
    storedMsgsSize.clear();
    storedDelays.clear();
  }

  public void saveMessage(Message msg) {
    String name = msg.getClass().getName();
    addMessage(name, msg);
  }

  private void addMessage(String name, Message msg) {
    if (!storedMsgs.containsKey(name)) {
      storedMsgs.put(name, 1);
      storedMsgsSize.put(name, Data.inByte(msg.getSize()));
    } else {
      int previousCount = storedMsgs.remove(name);
      storedMsgs.put(name, previousCount + 1);
      double previousSize = storedMsgsSize.remove(name);
      storedMsgsSize.put(name, previousSize + Data.inByte(msg.getSize()));
    }
  }

  public void saveDelay(double delay) {
    double time = Experiment.getSingleton().getClock().getCurrentTime();
    Integer epoch = (int) (time / epochDuration);
    if (!storedDelays.containsKey(epoch)) {
      List<Double> content = new ArrayList<Double>();
      storedDelays.put(epoch, content);
    }
    storedDelays.get(epoch).add(delay);
  }

  public void printResults(String filename) {
    printTraffic(filename);
    printDelay(filename);
    printDelayAvg(filename);
  }

  private void printTraffic(String filename) {
    try {
      FileOutputStream traffic = new FileOutputStream("results/" + filename + "Traffic");
      FileOutputStream trafficByte = new FileOutputStream("results/" + filename + "TrafficByte");
      for (String s : storedMsgs.keySet()) {
        int val = storedMsgs.get(s).intValue();
        traffic.write((s + "\t" + val + "\t" + val / simulationTime + "\n").getBytes());
      }
      for (String s : storedMsgsSize.keySet()) {
        int val = storedMsgsSize.get(s).intValue();
        trafficByte.write((s + "\t" + val + "\t" + val / simulationTime + "\n").getBytes());
      }
      traffic.close();
      trafficByte.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void printDelay(String filename) {
    try {
      FileOutputStream delay = new FileOutputStream("results/" + filename + "Delay");
      List<Integer> keyList = new ArrayList<Integer>(storedDelays.keySet());
      Collections.sort(keyList);
      for (Integer label : keyList) {
        double sum = 0;
        double size = storedDelays.get(label).size();
        for (Double val : storedDelays.get(label)) {
          sum += val;
        }
        double avgVal = sum / size;
        delay.write(((label * epochDuration / 1000) + "\t" + avgVal + "\n").getBytes());
      }
      delay.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void printDelayAvg(String filename) {
    try {
      FileOutputStream delay = new FileOutputStream("results/" + filename + "DelayAvg");
      double sum = 0;
      double count = 0;
      for (Integer key : storedDelays.keySet()) {
        List<Double> values = storedDelays.get(key);
        for (Double val : values) {
          sum += val;
          count++;
        }
      }
      double avgVal = sum / count;
      delay.write((String.valueOf(avgVal) + "\n").getBytes());
      delay.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
