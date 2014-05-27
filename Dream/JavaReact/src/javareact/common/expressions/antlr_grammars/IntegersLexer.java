// Generated from antlr_grammars/Integers.g4 by ANTLR 4.1
package javareact.common.expressions.antlr_grammars;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class IntegersLexer extends Lexer {
	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__1=1, T__0=2, MUL=3, DIV=4, ADD=5, SUB=6, DOT=7, AND=8, OR=9, NOT=10, 
		BOOL=11, ID=12, DIGIT=13, DOUBLE=14, STRING=15, WS=16;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] tokenNames = {
		"<INVALID>",
		"')'", "'('", "'*'", "'/'", "'+'", "'-'", "'.'", "'&'", "'|'", "'!'", 
		"BOOL", "ID", "DIGIT", "DOUBLE", "STRING", "WS"
	};
	public static final String[] ruleNames = {
		"T__1", "T__0", "MUL", "DIV", "ADD", "SUB", "DOT", "AND", "OR", "NOT", 
		"BOOL", "ID", "DIGIT", "DOUBLE", "STRING", "WS"
	};


	public IntegersLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "Integers.g4"; }

	@Override
	public String[] getTokenNames() { return tokenNames; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	@Override
	public void action(RuleContext _localctx, int ruleIndex, int actionIndex) {
		switch (ruleIndex) {
		case 15: WS_action((RuleContext)_localctx, actionIndex); break;
		}
	}
	private void WS_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 0: skip();  break;
		}
	}

	public static final String _serializedATN =
		"\3\uacf5\uee8c\u4f5d\u8b0d\u4a45\u78bd\u1b2f\u3378\2\22f\b\1\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\3\2\3\2\3"+
		"\3\3\3\3\4\3\4\3\5\3\5\3\6\3\6\3\7\3\7\3\b\3\b\3\t\3\t\3\n\3\n\3\13\3"+
		"\13\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\5\fA\n\f\3\r\3\r\7\rE\n\r\f\r"+
		"\16\rH\13\r\3\16\6\16K\n\16\r\16\16\16L\3\17\7\17P\n\17\f\17\16\17S\13"+
		"\17\3\17\3\17\6\17W\n\17\r\17\16\17X\3\20\6\20\\\n\20\r\20\16\20]\3\21"+
		"\6\21a\n\21\r\21\16\21b\3\21\3\21\2\22\3\3\1\5\4\1\7\5\1\t\6\1\13\7\1"+
		"\r\b\1\17\t\1\21\n\1\23\13\1\25\f\1\27\r\1\31\16\1\33\17\1\35\20\1\37"+
		"\21\1!\22\2\3\2\6\4\2C\\c|\5\2\62;C\\c|\3\2\62;\4\2\13\f\"\"l\2\3\3\2"+
		"\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17"+
		"\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2"+
		"\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\3#\3\2\2\2\5%\3"+
		"\2\2\2\7\'\3\2\2\2\t)\3\2\2\2\13+\3\2\2\2\r-\3\2\2\2\17/\3\2\2\2\21\61"+
		"\3\2\2\2\23\63\3\2\2\2\25\65\3\2\2\2\27@\3\2\2\2\31B\3\2\2\2\33J\3\2\2"+
		"\2\35Q\3\2\2\2\37[\3\2\2\2!`\3\2\2\2#$\7+\2\2$\4\3\2\2\2%&\7*\2\2&\6\3"+
		"\2\2\2\'(\7,\2\2(\b\3\2\2\2)*\7\61\2\2*\n\3\2\2\2+,\7-\2\2,\f\3\2\2\2"+
		"-.\7/\2\2.\16\3\2\2\2/\60\7\60\2\2\60\20\3\2\2\2\61\62\7(\2\2\62\22\3"+
		"\2\2\2\63\64\7~\2\2\64\24\3\2\2\2\65\66\7#\2\2\66\26\3\2\2\2\678\7v\2"+
		"\289\7t\2\29:\7w\2\2:A\7g\2\2;<\7h\2\2<=\7c\2\2=>\7n\2\2>?\7u\2\2?A\7"+
		"g\2\2@\67\3\2\2\2@;\3\2\2\2A\30\3\2\2\2BF\t\2\2\2CE\t\3\2\2DC\3\2\2\2"+
		"EH\3\2\2\2FD\3\2\2\2FG\3\2\2\2G\32\3\2\2\2HF\3\2\2\2IK\t\4\2\2JI\3\2\2"+
		"\2KL\3\2\2\2LJ\3\2\2\2LM\3\2\2\2M\34\3\2\2\2NP\t\4\2\2ON\3\2\2\2PS\3\2"+
		"\2\2QO\3\2\2\2QR\3\2\2\2RT\3\2\2\2SQ\3\2\2\2TV\5\17\b\2UW\t\4\2\2VU\3"+
		"\2\2\2WX\3\2\2\2XV\3\2\2\2XY\3\2\2\2Y\36\3\2\2\2Z\\\t\3\2\2[Z\3\2\2\2"+
		"\\]\3\2\2\2][\3\2\2\2]^\3\2\2\2^ \3\2\2\2_a\t\5\2\2`_\3\2\2\2ab\3\2\2"+
		"\2b`\3\2\2\2bc\3\2\2\2cd\3\2\2\2de\b\21\2\2e\"\3\2\2\2\n\2@FLQX]b";
	public static final ATN _ATN =
		ATNSimulator.deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}