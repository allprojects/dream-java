package protopeer.util;

import org.apache.log4j.*;
import org.apache.log4j.spi.*;

import protopeer.*;
import protopeer.time.*;

public class VerbosePatternLayout extends PatternLayout {

	@Override
	public String format(LoggingEvent event) {
		Experiment experiment = Experiment.getSingleton();
		StringBuffer prefix = new StringBuffer("");

		if (experiment != null) {
			// prepend with simulation time if available
			Clock clock = experiment.getClock();
			if (clock != null) {
				prefix.append(clock.getCurrentTime());
				prefix.append(" - ");
			}

			// add the info about the peer if available
			ExecutionContext executionContext = experiment.getExecutionContext();
			if (executionContext != null) {
				prefix.append(executionContext.toString());								
			}
			prefix.append(" - ");
		}
		prefix.append(super.format(event));
		return prefix.toString();
	}

}
