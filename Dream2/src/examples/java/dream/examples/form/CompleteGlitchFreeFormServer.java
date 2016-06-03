package dream.examples.form;

public class CompleteGlitchFreeFormServer extends SingleGlitchFreeFormServer {

	@Override
	protected void createDependencies() {
		logger.fine("Building Dependencies");

		logger.fine("Finished building Dependencies");
	}

	public static void main(String[] args) {
		new CompleteGlitchFreeFormServer();
	}
}
