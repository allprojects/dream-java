package javareact.eval.common;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public final class EvalParams {
  private static final Properties properties = new Properties();

  public static final int NUM_REPETITIONS = 5500;
  public static final int SKIP_FIRST = 500;
  public static final long SLEEP_TIME = 15;
  public static String evalDir;

  static {
    /**
     * Load properties
     */
    try {
      FileInputStream input = new FileInputStream("eval.properties");
      properties.load(input);
      input.close();
    } catch (IOException e) {
      e.printStackTrace();
    }

    evalDir = properties.getProperty("evalDir", "./");
  }
}
