package protopeer.scenarios;

/**
 * Listens to the {@link ScenarioParser} parsing errors and warnings.
 * 
 *
 */
public interface ScenarioParserListener {
	
	public abstract void warning(String string);
	
	public abstract void error(String string);
		
}
