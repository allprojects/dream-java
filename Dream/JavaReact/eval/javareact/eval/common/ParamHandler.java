package javareact.eval.common;

import javareact.common.ConsistencyType;

public class ParamHandler {
  private static ParamHandler instance = null;

  private boolean burst;
  private int numOperations;
  private ConsistencyType consistencyType;

  private ParamHandler() {
    resetToDefault();
  }

  public final void resetToDefault() {
    burst = false;
    numOperations = 1;
    consistencyType = ConsistencyType.GLITCH_FREE;
  }

  public static final ParamHandler getInstance() {
    if (instance == null) {
      instance = new ParamHandler();
    }
    return instance;
  }

  public final boolean getBurst() {
    return burst;
  }

  public final void setBurst(boolean burst) {
    this.burst = burst;
  }

  public final int getNumOperations() {
    return numOperations;
  }

  public final void setNumOperations(int numOperations) {
    this.numOperations = numOperations;
  }

  public final ConsistencyType getConsistencyType() {
    return consistencyType;
  }

  public final void setConsistencyType(ConsistencyType consistencyType) {
    this.consistencyType = consistencyType;
  }

}
