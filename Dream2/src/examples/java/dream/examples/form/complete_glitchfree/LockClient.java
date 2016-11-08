package dream.examples.form.complete_glitchfree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dream.client.RemoteVar;
import dream.client.Signal;
import dream.client.Var;
import dream.examples.util.Client;
import dream.examples.util.Pair;

public abstract class LockClient extends Client {
	private boolean setup = false;
	private Var<LockRequest> lockRequest;
	private List<Variable> hasLock;

	public LockClient(String name) {
		super(name);

		// Establish new session with LockManager
		RemoteVar<ArrayList<Pair<String, String>>> registeredClients = new RemoteVar<>(LockManager.NAME,
				LockManager.VAR_clients);
		Signal<ArrayList<Pair<String, String>>> s = new Signal<>("s", () -> {
			if (registeredClients.get() == null)
				return new ArrayList<Pair<String, String>>();
			else
				return registeredClients.get();
		}, registeredClients);
		s.change().addHandler((o, n) -> {
			if (n.contains(new Pair<>(this.getHostName(), LockManager.VAR_requestLock)) && setup == false)
				lockSetup();
		});

		hasLock = new ArrayList<>();
		lockRequest = new Var<>(LockManager.VAR_requestLock, null);
		logger.fine("Setup: Waiting for Registration to LockManager ...");
	}

	private void lockSetup() {
		setup = true;
		RemoteVar<Lock> lock = new RemoteVar<>(LockManager.NAME, LockManager.VAR_lock);
		Signal<Lock> sLock = new Signal<>("lock", () -> {
			return lock.get();
		}, lock);
		sLock.change().addHandler((oldValue, newValue) -> {
			newValue.forEach((var, client) -> {
				if (client.equals(getHostName()))
					hasLock.add(var);
				else
					hasLock.remove(var);
			});
			synchronized (this) {
				this.notify();
			}
		});

		setup();
	}

	protected abstract void setup();

	public void lock(Variable... vars) {
		lockRequest.set(new LockRequest(getHostName(), vars));
		synchronized (this) {
			while (!hasLock.containsAll(Arrays.asList(vars))) {
				try {
					this.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void unlock() {
		lockRequest.set(new LockRequest(getHostName()));
	}

}
