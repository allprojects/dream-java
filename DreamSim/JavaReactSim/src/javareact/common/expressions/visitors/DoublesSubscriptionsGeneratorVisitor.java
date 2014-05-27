package javareact.common.expressions.visitors;

import java.util.HashSet;
import java.util.Set;

import javareact.client.TrafficGeneratorPeerlet;
import javareact.common.Consts;
import javareact.common.expressions.antlr_grammars.DoublesBaseVisitor;
import javareact.common.expressions.antlr_grammars.DoublesParser;
import javareact.common.packets.content.Constraint;
import javareact.common.packets.content.Subscription;
import protopeer.Peer;

public class DoublesSubscriptionsGeneratorVisitor extends DoublesBaseVisitor<Void> {
  private final Set<Subscription> subscriptions = new HashSet<Subscription>();
  private final Peer peer;

  public DoublesSubscriptionsGeneratorVisitor(Peer peer) {
    super();
    this.peer = peer;
  }

  @Override
  public Void visitIdentifier(DoublesParser.IdentifierContext ctx) {
    int clientId = ((TrafficGeneratorPeerlet) peer.getPeerletOfType(TrafficGeneratorPeerlet.class)).getClientId();
    String hostId = (ctx.hostId() == null) ? Consts.hostPrefix + clientId : ctx.hostId().getText();
    String observableId = ctx.observableId().getText();
    String method = ctx.method().getText();
    Subscription sub = new Subscription(observableId, hostId, new Constraint(method));
    subscriptions.add(sub);
    return null;
  }

  public Set<Subscription> getSubscriptions() {
    return subscriptions;
  }

}
