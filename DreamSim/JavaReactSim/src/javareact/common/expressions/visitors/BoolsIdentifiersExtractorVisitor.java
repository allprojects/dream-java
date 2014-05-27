package javareact.common.expressions.visitors;

import java.util.ArrayList;
import java.util.Collection;

import javareact.client.TrafficGeneratorPeerlet;
import javareact.common.Consts;
import javareact.common.expressions.antlr_grammars.BoolsBaseVisitor;
import javareact.common.expressions.antlr_grammars.BoolsParser;
import protopeer.Peer;

public class BoolsIdentifiersExtractorVisitor extends BoolsBaseVisitor<Void> {
  private final Collection<String> identifiers = new ArrayList<String>();
  private final Peer peer;

  public BoolsIdentifiersExtractorVisitor(Peer peer) {
    super();
    this.peer = peer;
  }

  @Override
  public Void visitIdentifier(BoolsParser.IdentifierContext ctx) {
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
