package dream.eval;

import java.util.Random;

import dream.client.DreamClient;
import dream.client.Var;
import dream.common.Consts;

public class EvalVarClient {

	public static void main(String args[]) {
		if (args.length < 4) {
			System.out.println("Usage: EvalVarClient <serverAddr> <hostName> <varName> <sleepTime>");
			System.exit(1);
		}

		final String serverAddr = args[0];
		final String hostName = args[1];
		final String varName = args[2];
		final int sleepTime = Integer.parseInt(args[3]);

		Consts.serverAddr = serverAddr;
		Consts.hostName = hostName;

		final DreamClient client = DreamClient.instance;
		client.connect();

		final Var<Integer> remoteInt = new Var<Integer>(varName, 1);
		final Random random = new Random();

		try {
			Thread.sleep(5000);
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}

		while (true) {
			remoteInt.set(random.nextInt(1000));
			try {
				Thread.sleep(sleepTime);
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

}
