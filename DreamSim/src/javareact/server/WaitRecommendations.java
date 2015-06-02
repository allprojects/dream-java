package javareact.server;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A WaitRecommendations object is stored inside an event E. It is used by the server to prevent glitches while
 * delivering E to the clients. It tells a client C that it must wait for other events before processing E.
 */
public class WaitRecommendations implements Serializable {
  private static final long serialVersionUID = 5700944080087353096L;

  private final String expression;
  private final Set<String> expressionsToWaitFor = new HashSet<String>();

  public WaitRecommendations(String expression) {
    this.expression = expression;
  }

  public final void addRecommendation(String expressionToWaitFor) {
    expressionsToWaitFor.add(expressionToWaitFor);
  }

  public final String getExpression() {
    return expression;
  }

  public final Set<String> getRecommendations() {
    return expressionsToWaitFor;
  }

  public final WaitRecommendations dup() {
    WaitRecommendations result = new WaitRecommendations(expression);
    result.expressionsToWaitFor.addAll(expressionsToWaitFor);
    return result;
  }

  public final void removeExpressionToWaitFor(String expressionToWaitFor) {
    expressionsToWaitFor.remove(expressionToWaitFor);
  }

  @Override
  public String toString() {
    return "WaitRecommendations [expression=" + expression + ", expressionsToWaitFor=" + expressionsToWaitFor + "]";
  }

}
