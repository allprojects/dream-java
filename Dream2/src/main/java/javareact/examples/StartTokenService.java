package javareact.examples;

import java.util.ArrayList;
import java.util.Collection;
import java.util.StringTokenizer;

import javareact.token_service.TokenServiceLauncher;

public class StartTokenService {
  private static final String addressDelim = "#";

  public static void main(String[] args) {
    if (args.length < 1) {
      err();
    }
    Collection<String> addresses = getBrokerAddresses(args[0]);
    TokenServiceLauncher.start(addresses);
  }

  private static final void err() {
    System.out.println("Usage: StartTokenService <brokerAddr>[" + addressDelim + "<brokerAddr]*");
    System.exit(-1);
  }

  private static Collection<String> getBrokerAddresses(String args) {
    Collection<String> result = new ArrayList<String>();
    StringTokenizer tokenizer = new StringTokenizer(args, addressDelim);
    while (tokenizer.hasMoreTokens()) {
      result.add(tokenizer.nextToken());
    }
    return result;
  }
}
