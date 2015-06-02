package protopeer.time;

import org.apache.log4j.*;

class TimerTask extends java.util.TimerTask {

	private static Logger logger = Logger.getLogger(TimerTask.class);

	private RealTimer timer;

	public TimerTask(RealTimer timer) {
		this.timer = timer;
	}

	public synchronized void run() {
		try {
			//FIXME: do we really need this cancellation?
			timer.cancel();
			timer.fireTimerExpired();			
		} catch (Exception e) {
			logger.error("", e);
		}
	}
}