package dream.eval;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import dream.client.DreamClient;
import dream.client.RemoteVar;
import dream.client.Signal;
import dream.client.UpdateProducer;
import dream.common.Consts;

public class EvalSignalClient {

	public static void main(String args[]) {
		if (args.length < 4) {
			System.out
					.println("Usage: EvalSignalClient <serverAddr> <hostName> <signalName> <remoteVar>[;<remoteVar>]+");
			System.exit(0);
		}

		final String serverAddr = args[0];
		final String hostName = args[1];
		final String signalName = args[2];
		final String deps = args[3];

		Consts.serverAddr = serverAddr;
		Consts.setHostName(hostName);

		final DreamClient client = DreamClient.instance;
		client.connect();

		StringTokenizer tokenizer = new StringTokenizer(deps, ";");
		final List<String> relevantRemoteVars = new ArrayList<>();

		while (tokenizer.hasMoreTokens()) {
			relevantRemoteVars.add(tokenizer.nextToken());
		}

		// Wait until all remote vars have been notified
		while (!client.listVariables().containsAll(relevantRemoteVars)) {
			try {
				Thread.sleep(100);
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
		}

		// Create RemoteVar objects
		final List<UpdateProducer<?>> remoteVars = new ArrayList<>();
		for (String remoteVar : relevantRemoteVars) {
			remoteVars.add(new RemoteVar<Integer>(remoteVar.split("@")[1], remoteVar.split("@")[0]));
		}

		@SuppressWarnings("unchecked")
		final Signal<Integer> signal = new Signal<Integer>(signalName, () -> {
			int result = 0;
			for (UpdateProducer<?> remoteVar : remoteVars) {
				result += ((RemoteVar<Integer>) remoteVar).get();
			}
			return result;
		}, remoteVars);

		signal.change().addHandler((oldVal, val) -> System.out.println("Signal: " + val));

	}
}
