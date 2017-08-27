package dream.eval;

import java.util.Random;
import java.util.logging.Logger;

import dream.client.DreamClient;
import dream.client.Var;
import dream.common.Consts;

public class EvalVarClient {
	private static final int numChanges = 1000;

	public static void main(String args[]) {
		if (args.length < 6) {
			System.out.println(
					"Usage: EvalVarClient <serverAddr> <lockMgrAddr> <hostName> <varName> <startTime> <sleepTime>");
			System.exit(1);
		}

		final String serverAddr = args[0];
		final String lockMgrAddr = args[1];
		final String hostName = args[2];
		final String varName = args[3];
		final int startTime = Integer.parseInt(args[4]);
		final int sleepTime = Integer.parseInt(args[5]);

		Consts.serverAddr = serverAddr;
		Consts.lockManagerAddr = lockMgrAddr;

		Consts.setHostName(hostName);

		final DreamClient client = DreamClient.instance;
		client.connect();

		final Var<Integer> remoteInt = new Var<Integer>(varName, 1);
		final Random random = new Random();

		try {
			Thread.sleep(startTime);
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}

		for (int i = 0; i < numChanges; ++i) {
			remoteInt.set(random.nextInt(1000));
			try {
				Thread.sleep(sleepTime);
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
		}

		final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
		logger.info(hostName + " finished sending updates");
	}

}
