package javareact.common.types;

public abstract class ReactiveDouble extends AbstractReactive<Double> {
	public ReactiveDouble(String name, Proxy... proxies) {
		super(name, proxies);
		val = 0.0;
	}

	@Override
	public final synchronized DoubleProxy getProxy() {
		return (DoubleProxy) super.getProxy();
	}
}