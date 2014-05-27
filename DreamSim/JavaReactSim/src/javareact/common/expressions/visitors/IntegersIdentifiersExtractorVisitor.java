package javareact.common.expressions.visitors;

import java.util.ArrayList;
import java.util.Collection;

import javareact.client.TrafficGeneratorPeerlet;
import javareact.common.Consts;
import javareact.common.expressions.antlr_grammars.IntegersBaseVisitor;
import javareact.common.expressions.antlr_grammars.IntegersParser;
import protopeer.Peer;

public class IntegersIdentifiersExtractorVisitor extends IntegersBaseVisitor<Void> {
  private final Collection<String> identifiers = new ArrayList<String>();
  private final Peer peer;

  public IntegersIdentifiersExtractorVisitor(Peer peer) {
    super();
    this.peer = peer;
  }

  @Override
  public Void visitIdentifier(IntegersParser.IdentifierContext ctx) {
    int clientId = ((TrafficGeneratorPeerlet) peer.getPeerletOfType(TrafficGeneratorPeerlet.class)).getClientId();
    String hostId = (ctx.hostId() == null) ? Consts.hostPrefix + clientId : ctx.hostId().getText();
    String observableId = ctx.observableId().getText();
    String method = ctx.method().getText();
    String id = hostId + "." + observableId + "." + method;
    identifiers.add(id);
    return null;
  }

  public Collection<String> getIdentifiers() {
    return identifiers;
  }

}
