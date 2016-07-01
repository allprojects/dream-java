package dream.examples.form.complete_glitchfree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;
import java.util.logging.Level;

import dream.client.DreamClient;
import dream.client.RemoteVar;
import dream.client.Signal;
import dream.client.Var;
import dream.examples.util.Client;
import dream.examples.util.Pair;

public class LockManager extends Client {

	public static final String VAR_lock = "lock";
	public static final String VAR_requestLock = "requestLock";
	public static final String VAR_clients = "clients";
	public static final String NAME = "LockManager";

	private Var<Lock> lock;
	private final Var<ArrayList<Variable>> clients;
	private LinkedList<LockRequest> lockRequests;

	public LockManager() {
		super(NAME);
		clients = new Var<>(VAR_clients, new ArrayList<>());
		lock = new Var<>(VAR_lock, new Lock());
		lockRequests = new LinkedList<>();
		detectNewSession();
	}

	private void detectNewSession() {
		Set<String> vars = DreamClient.instance.listVariables();
		vars.stream().map(x -> new Variable(x.split("@")[1], x.split("@")[0])).// Pair(Host,Var)
				filter(x -> !clients.get().contains(x) && x.getVar().equalsIgnoreCase(VAR_requestLock)).//
				forEach(x -> createNewSessionFor(x));
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			logger.log(Level.SEVERE, "Failed to sleep for 0.5 seconds", e);
		}

		detectNewSession();
	}

	private void createNewSessionFor(Variable x) {
		RemoteVar<LockRequest> rv = new RemoteVar<>(x.getHost(), x.getVar());
		Signal<LockRequest> s = new Signal<>(x.getHost() + "request", () -> {
			if (rv.get() != null)
				return rv.get();
			else
				return null;
		}, rv);
		s.change().addHandler((oldValue, newValue) -> {
			if (newValue.isLockRequest()) {
				// client requesting a lock
				if (!lock.get().isLocked(newValue.getVars())) {
					lock.set(new Lock(lock.get(), newValue));
				} else {
					// already locked, adding to queue
					lockRequests.add(newValue);
				}
			} else {
				// client trying to release a lock
				lock.set(new Lock(lock.get(), newValue));
				removeLockRequestsFor(x.getHost());
				processNextRequest();
			}
		});
		clients.modify(old -> old.add(x));
	}

	private void processNextRequest() {
		for (LockRequest req : lockRequests) {
			if (!lock.get().isLocked(req.getVars())) {
				lock.set(new Lock(lock.get(), req));
				lockRequests.remove(req);
				break;
			}
		}
	}

	private void removeLockRequestsFor(String host) {
		for (LockRequest req : lockRequests) {
			if (req.getClient().equals(host)) {
				lockRequests.remove(req);
			}
		}
	}

	public static void main(String[] args) {
		new LockManager();
	}
}

class Variable extends Pair<String, String> {
	private static final long serialVersionUID = 4689891891604406371L;

	public Variable(String host, String var) {
		super(host, var);
	}

	public String getHost() {
		return getFirst();
	}

	public String getVar() {
		return getVar();
	}

	public static Variable get(Var<?> v) {
		return new Variable(v.getHost(), v.getObject());
	}

}

class LockRequest implements Serializable {
	private static final long serialVersionUID = -7166632148414861582L;
	private Variable[] vars;
	private String client;

	public LockRequest(String client, Variable... vars) {
		this.vars = vars;
		this.client = client;
	}

	public Variable[] getVars() {
		return vars;
	}

	public String getClient() {
		return client;
	}

	public boolean isLockRequest() {
		return vars != null;
	}
}

class Lock extends HashMap<Variable, String> {
	private static final long serialVersionUID = -4195570287637533298L;

	public Lock() {

	}

	public Lock(Lock lock, LockRequest req) {
		this.putAll(lock);
		if (!req.isLockRequest()) {
			// lock release
			for (Variable v : keySet()) {
				remove(v, req.getClient());
			}
		} else {
			for (int i = 0; i < req.getVars().length; i++) {
				put(req.getVars()[i], req.getClient());
			}
		}
	}

	public boolean isLocked(Variable[] vars) {
		return !Arrays.asList(vars).stream().anyMatch(x -> containsKey(x));
	}

}