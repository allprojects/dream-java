package protopeer.time;


class SimulatedTimerEvent extends Event {

	private SimulatedTimer timer;

	public SimulatedTimerEvent(SimulatedTimer timer) {
		super();
		this.timer = timer;
	}

	@Override
	public void execute() {
		timer.expire();
	}

}
