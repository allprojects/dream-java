package dream.eval;

import java.util.Random;

import dream.client.DreamClient;
import dream.client.Var;
import dream.common.Consts;

public class EvalVarClient {

	public static void main(String args[]) {
		if (args.length < 5) {
			System.out.println("Usage: EvalVarClient <serverAddr> <lockMgrAddr> <hostName> <varName> <sleepTime>");
			System.exit(1);
		}

		final String serverAddr = args[0];
		final String lockMgrAddr = args[1];
		final String hostName = args[2];
		final String varName = args[3];
		final int sleepTime = Integer.parseInt(args[4]);

		Consts.serverAddr = serverAddr;
		Consts.lockManagerAddr = lockMgrAddr;

		Consts.setHostName(hostName);

		final DreamClient client = DreamClient.instance;
		client.connect();

		final Var<Integer> remoteInt = new Var<Integer>(varName, 1);
		final Random random = new Random();

		try {
			Thread.sleep(10000);
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
