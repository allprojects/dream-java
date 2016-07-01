package dream.examples.form.complete_glitchfree;

import java.util.ArrayList;

import dream.client.RemoteVar;
import dream.client.Signal;
import dream.client.Var;
import dream.examples.form.core.FormClient;
import dream.examples.util.Pair;

public abstract class LockClient extends FormClient {
	private boolean setup = false;
	private Var<LockRequest> lockRequest;
	private boolean hasLock;

	public LockClient(String name, String... labelText) {
		super(name, labelText);

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

		lockRequest = new Var<>(LockManager.VAR_requestLock, null);
		logger.fine("Setup: Waiting for Registration to LockManager ...");
	}

	private void lockSetup() {
		setup = true;
		RemoteVar<String> lock = new RemoteVar<>(LockManager.NAME, LockManager.VAR_lock);
		Signal<String> sLock = new Signal<>("lock", () -> {
			return lock.get();
		}, lock);
		sLock.change().addHandler((oldValue, newValue) -> {
			if (newValue.equals(getHostName()))
				hasLock = true;
			else
				hasLock = false;
			synchronized (this) {
				this.notify();
			}
		});

		setup();
	}

	protected abstract void setup();

	public void lock(Variable... vars) {
		// TODO: add dependencies as well
		lockRequest.set(new LockRequest(getHostName(), vars));
		synchronized (this) {
			while (!hasLock)
				try {
					this.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
		}
	}

	public void unlock() {
		lockRequest.set(new LockRequest(getHostName()));
	}

}
