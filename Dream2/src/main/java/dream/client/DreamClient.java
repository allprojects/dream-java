package dream.client;

import java.util.HashSet;
import java.util.Set;

import dream.common.utils.DependencyGraph;

/**
 * The DreamClient contains utility methods that can be used by the application
 * to obtain information about the dependency graph.
 */
public enum DreamClient {
	instance;

	private final DependencyGraph depGraph = DependencyGraph.instance;
	private ClientEventForwarder eventForwarder;

	public final void connect() {
		eventForwarder = ClientEventForwarder.get();
	}

	public final Set<String> listVariables() {
		final Set<String> result = new HashSet<>();
		result.addAll(depGraph.getGraph().keySet());
		result.addAll(depGraph.getSources());
		return result;
	}

	public final LockToken readLock(Set<String> varsToLock) {
		LockToken token = new LockToken(varsToLock.size());
		Lock lock = new Lock();
		eventForwarder.sendReadOnlyLockRequest(varsToLock, grant -> {
			synchronized (lock) {
				lock.unlock();
				token.setLockId(grant.getLockID());
				lock.notifyAll();
			}
		});
		synchronized (lock) {
			while (!lock.isUnlocked()) {
				try {
					lock.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		return token;
	}

	public final void unlock(LockToken token) {
		for (int i = 0; i < token.getNumLocks(); i++) {
			eventForwarder.sendLockRelease(token.getLockId());
		}
	}

	private class Lock {
		private boolean unlocked = false;

		final void unlock() {
			unlocked = true;
		}

		final boolean isUnlocked() {
			return unlocked;
		}
	}

}
