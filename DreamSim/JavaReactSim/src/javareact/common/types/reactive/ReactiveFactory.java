package javareact.common.types.reactive;

import javareact.common.packets.content.Value;
import protopeer.Peer;

public final class ReactiveFactory {

  public static final ReactiveInteger getInteger(Peer peer, String expression, int startingValue, String name, boolean isPublic) {
    ReactiveInteger result = new ReactiveIntegerImpl(peer, expression, new Value(startingValue), name, isPublic);
    return result;
  }

  public static final ReactiveInteger getInteger(Peer peer, String expression, int startingValue, String name) {
    ReactiveInteger result = new ReactiveIntegerImpl(peer, expression, new Value(startingValue), name, true);
    return result;
  }

  public static final ReactiveInteger getInteger(Peer peer, String expression, String name, boolean isPublic) {
    ReactiveInteger result = new ReactiveIntegerImpl(peer, expression, name, isPublic);
    return result;
  }

  public static final ReactiveInteger getInteger(Peer peer, String expression, String name) {
    ReactiveInteger result = new ReactiveIntegerImpl(peer, expression, name, true);
    return result;
  }

  public static final ReactiveDouble getDouble(Peer peer, String expression, double startingValue, String name, boolean isPublic) {
    ReactiveDouble result = new ReactiveDoubleImpl(peer, expression, new Value(startingValue), name, isPublic);
    return result;
  }

  public static final ReactiveDouble getDouble(Peer peer, String expression, double startingValue, String name) {
    ReactiveDouble result = new ReactiveDoubleImpl(peer, expression, new Value(startingValue), name, true);
    return result;
  }

  public static final ReactiveDouble getDouble(Peer peer, String expression, String name, boolean isPublic) {
    ReactiveDouble result = new ReactiveDoubleImpl(peer, expression, name, isPublic);
    return result;
  }

  public static final ReactiveDouble getDouble(Peer peer, String expression, String name) {
    ReactiveDouble result = new ReactiveDoubleImpl(peer, expression, name, true);
    return result;
  }

  public static final ReactiveBool getBool(Peer peer, String expression, boolean startingValue, String name, boolean isPublic) {
    ReactiveBool result = new ReactiveBoolImpl(peer, expression, new Value(startingValue), name, isPublic);
    return result;
  }

  public static final ReactiveBool getBool(Peer peer, String expression, boolean startingValue, String name) {
    ReactiveBool result = new ReactiveBoolImpl(peer, expression, new Value(startingValue), name, true);
    return result;
  }

  public static final ReactiveBool getBool(Peer peer, String expression, String name, boolean isPublic) {
    ReactiveBool result = new ReactiveBoolImpl(peer, expression, name, isPublic);
    return result;
  }

  public static final ReactiveBool getBool(Peer peer, String expression, String name) {
    ReactiveBool result = new ReactiveBoolImpl(peer, expression, name, true);
    return result;
  }

  public static final ReactiveString getString(Peer peer, String expression, String startingValue, String name, boolean isPublic) {
    ReactiveString result = new ReactiveStringImpl(peer, expression, new Value(startingValue), name, isPublic);
    return result;
  }

  public static final ReactiveString getString(Peer peer, String expression, String startingValue, String name) {
    ReactiveString result = new ReactiveStringImpl(peer, expression, new Value(startingValue), name, true);
    return result;
  }

  public static final ReactiveString getString(Peer peer, String expression, String name, boolean isPublic) {
    ReactiveString result = new ReactiveStringImpl(peer, expression, name, isPublic);
    return result;
  }

  public static final ReactiveString getString(Peer peer, String expression, String name) {
    ReactiveString result = new ReactiveStringImpl(peer, expression, name, true);
    return result;
  }

}
