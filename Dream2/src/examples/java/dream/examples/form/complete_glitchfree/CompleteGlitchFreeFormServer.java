package dream.examples.form.complete_glitchfree;

public class CompleteGlitchFreeFormServer extends FormServer {

	@Override
	protected void createDependencies() {
		logger.fine("Building Dependencies");

		logger.fine("Finished building Dependencies");
	}

	public static void main(String[] args) {
		new CompleteGlitchFreeFormServer();
	}
}
