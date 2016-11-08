package dream.examples.scrumBoard.atomic;

import java.util.ArrayList;
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

	/**
	 * lock:<br>
	 * "" -> no lock<br>
	 * [ClientName] -> graph is locked for this Client
	 */
	private Var<String> lock;
	private final Var<ArrayList<Pair<String, String>>> clients;
	private LinkedList<String> lockRequests;

	public LockManager() {
		super(NAME);
		clients = new Var<>(VAR_clients, new ArrayList<>());
		lock = new Var<>(VAR_lock, "");
		lockRequests = new LinkedList<>();
		detectNewSession();
	}

	private void detectNewSession() {
		Set<String> vars = DreamClient.instance.listVariables();
		vars.stream().map(x -> new Pair<String, String>(x.split("@")[1], x.split("@")[0])).// Pair(Host,Var)
				filter(x -> !clients.get().contains(x) && x.getSecond().equalsIgnoreCase(VAR_requestLock)).//
				forEach(x -> createNewSessionFor(x));
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			logger.log(Level.SEVERE, "Failed to sleep for 0.5 seconds", e);
		}

		detectNewSession();
	}

	private void createNewSessionFor(Pair<String, String> x) {
		RemoteVar<Boolean> rv = new RemoteVar<>(x.getFirst(), x.getSecond());
		Signal<Boolean> s = new Signal<>(x.getFirst() + "request", () -> {
			if (rv.get() != null)
				return rv.get();
			else
				return false;
		}, rv);
		s.change().addHandler((oldValue, newValue) -> {
			if (newValue) {
				// client requesting a lock
				if (lock.get().equals("")) {
					// no lock present, granting
					lock.set(x.getFirst());
					logger.fine("Currently not locked, granting lock for: " + x.getFirst());
				} else {
					// already locked, adding to queue
					lockRequests.add(x.getFirst());
					logger.fine("Already locked for \"" + lock.get() + "\". Adding \"" + x.getFirst() + "\"to queue");
				}
			} else if (!newValue) {
				// client trying to release a lock
				if (lock.get().equals(x.getFirst())) {
					// client had the lock, releasing
					lock.set("");
					logger.fine("Releasing lock for:" + x.getFirst());
					// granting lock request for next client
					if (!lockRequests.isEmpty()) {
						lock.set(lockRequests.poll());
						logger.fine("Processing next in queue");
					}
				} else {
					// client didn't have the lock -> withdrawing lock request
					lockRequests.remove(x.getFirst());
					logger.fine("\"" + x.getFirst() + "\" withdraw lock request");
				}
			}
		});
		clients.modify(old -> old.add(x));
	}

	public static void main(String[] args) {
		new LockManager();
	}
}
