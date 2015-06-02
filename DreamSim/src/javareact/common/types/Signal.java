package javareact.common.types;

import java.util.function.Supplier;
import protopeer.Peer;

public class Signal<T> extends AbstractReactive<T> {
	private RemoteVar<T> proxy = null;
	private Supplier<T> evaluation;

	private static <T> Proxy[] proxiesFromVars(ProxyGenerator[] vars) {
		Proxy[] proxies = new Proxy[vars.length];
		for (int i = 0; i < vars.length; i++) {
			proxies[i] = vars[i].getProxy();
		}
		return proxies;
	}

	public Signal(Peer peer, String name, Supplier<T> evaluation, Proxy[] proxies) {
		super(peer, name, proxies);
		val = null;
		this.evaluation = evaluation;
	}

	@SafeVarargs
    public Signal(Peer peer, String name, Supplier<T> evaluation, ProxyGenerator... vars) {
		super(peer, name, proxiesFromVars(vars));
		val = null;
		this.evaluation = evaluation;
	}

	@Override
	public synchronized RemoteVar<T> getProxy() {
		if (proxy == null) {
			proxy = new RemoteVar<T>(peer, name);
		}
		return proxy;
	}

	@Override
	public final T evaluate() {
		return evaluation.get();
	}
}
