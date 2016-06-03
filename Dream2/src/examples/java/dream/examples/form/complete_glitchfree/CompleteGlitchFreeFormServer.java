package dream.examples.form.complete_glitchfree;

import dream.examples.form.core.GlitchFreeFormServer;

public class CompleteGlitchFreeFormServer extends GlitchFreeFormServer {

	@Override
	protected void createDependencies() {
		logger.fine("Building Dependencies");

		logger.fine("Finished building Dependencies");
	}

	public static void main(String[] args) {
		new CompleteGlitchFreeFormServer();
	}
}
