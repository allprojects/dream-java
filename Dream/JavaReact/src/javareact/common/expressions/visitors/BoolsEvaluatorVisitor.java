package javareact.common.expressions.visitors;

import java.util.Map;

import javareact.common.Consts;
import javareact.common.expressions.antlr_grammars.BoolsBaseVisitor;
import javareact.common.expressions.antlr_grammars.BoolsParser;
import javareact.common.packets.content.Value;

public class BoolsEvaluatorVisitor extends BoolsBaseVisitor<Value> {
  private final Map<String, Value> values;

  public BoolsEvaluatorVisitor(Map<String, Value> values) {
    super();
    this.values = values;
  }

  @Override
  public Value visitIdentifier(BoolsParser.IdentifierContext ctx) {
    String hostId = (ctx.hostId() == null) ? Consts.hostName : ctx.hostId().getText();
    String observableId = ctx.observableId().getText();
    String method = ctx.method().getText();
    String id = hostId + "." + observableId + "." + method;
    return values.get(id);
  }

  @Override
  public Value visitParens(BoolsParser.ParensContext ctx) {
    return visit(ctx.boolExpr());
  }

  @Override
  public Value visitNot(BoolsParser.NotContext ctx) {
    boolean val = !visit(ctx.boolExpr()).boolVal();
    return new Value(val);
  }

  @Override
  public Value visitId(BoolsParser.IdContext ctx) {
    return visit(ctx.identifier());
  }

  @Override
  public Value visitBool(BoolsParser.BoolContext ctx) {
    return new Value(Boolean.parseBoolean(ctx.getText()));
  }

  @Override
  public Value visitAndOr(BoolsParser.AndOrContext ctx) {
    boolean left = visit(ctx.boolExpr(0)).boolVal();
    boolean right = visit(ctx.boolExpr(1)).boolVal();
    if (ctx.op.getType() == BoolsParser.AND) {
      return new Value(left && right);
    } else {
      return new Value(left || right);
    }
  }
}
