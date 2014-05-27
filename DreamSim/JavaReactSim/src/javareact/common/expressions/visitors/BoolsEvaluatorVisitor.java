package javareact.common.expressions.visitors;

import java.util.Map;

import javareact.client.TrafficGeneratorPeerlet;
import javareact.common.Consts;
import javareact.common.expressions.antlr_grammars.BoolsBaseVisitor;
import javareact.common.expressions.antlr_grammars.BoolsParser;
import javareact.common.packets.content.Value;
import protopeer.Peer;

public class BoolsEvaluatorVisitor extends BoolsBaseVisitor<Value> {
  private final Map<String, Value> values;
  private final Peer peer;

  public BoolsEvaluatorVisitor(Peer peer, Map<String, Value> values) {
    super();
    this.peer = peer;
    this.values = values;
  }

  @Override
  public Value visitIdentifier(BoolsParser.IdentifierContext ctx) {
    int clientId = ((TrafficGeneratorPeerlet) peer.getPeerletOfType(TrafficGeneratorPeerlet.class)).getClientId();
    String hostId = (ctx.hostId() == null) ? Consts.hostPrefix + clientId : ctx.hostId().getText();
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
