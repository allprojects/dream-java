// Generated from antlr_grammars/Doubles.g4 by ANTLR 4.1
package javareact.common.expressions.antlr_grammars;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class DoublesParser extends Parser {
	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__1=1, T__0=2, MUL=3, DIV=4, ADD=5, SUB=6, DOT=7, AND=8, OR=9, NOT=10, 
		BOOL=11, ID=12, DIGIT=13, DOUBLE=14, STRING=15, WS=16;
	public static final String[] tokenNames = {
		"<INVALID>", "')'", "'('", "'*'", "'/'", "'+'", "'-'", "'.'", "'&'", "'|'", 
		"'!'", "BOOL", "ID", "DIGIT", "DOUBLE", "STRING", "WS"
	};
	public static final int
		RULE_start = 0, RULE_doubleExpr = 1, RULE_identifier = 2, RULE_hostId = 3, 
		RULE_observableId = 4, RULE_method = 5;
	public static final String[] ruleNames = {
		"start", "doubleExpr", "identifier", "hostId", "observableId", "method"
	};

	@Override
	public String getGrammarFileName() { return "Doubles.g4"; }

	@Override
	public String[] getTokenNames() { return tokenNames; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public DoublesParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class StartContext extends ParserRuleContext {
		public DoubleExprContext doubleExpr() {
			return getRuleContext(DoubleExprContext.class,0);
		}
		public StartContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_start; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DoublesVisitor ) return ((DoublesVisitor<? extends T>)visitor).visitStart(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StartContext start() throws RecognitionException {
		StartContext _localctx = new StartContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_start);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(12); doubleExpr(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class DoubleExprContext extends ParserRuleContext {
		public int _p;
		public DoubleExprContext(ParserRuleContext parent, int invokingState) { super(parent, invokingState); }
		public DoubleExprContext(ParserRuleContext parent, int invokingState, int _p) {
			super(parent, invokingState);
			this._p = _p;
		}
		@Override public int getRuleIndex() { return RULE_doubleExpr; }
	 
		public DoubleExprContext() { }
		public void copyFrom(DoubleExprContext ctx) {
			super.copyFrom(ctx);
			this._p = ctx._p;
		}
	}
	public static class DoubleContext extends DoubleExprContext {
		public TerminalNode DOUBLE() { return getToken(DoublesParser.DOUBLE, 0); }
		public DoubleContext(DoubleExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DoublesVisitor ) return ((DoublesVisitor<? extends T>)visitor).visitDouble(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ParensContext extends DoubleExprContext {
		public DoubleExprContext doubleExpr() {
			return getRuleContext(DoubleExprContext.class,0);
		}
		public ParensContext(DoubleExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DoublesVisitor ) return ((DoublesVisitor<? extends T>)visitor).visitParens(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class AddSubContext extends DoubleExprContext {
		public Token op;
		public List<DoubleExprContext> doubleExpr() {
			return getRuleContexts(DoubleExprContext.class);
		}
		public DoubleExprContext doubleExpr(int i) {
			return getRuleContext(DoubleExprContext.class,i);
		}
		public TerminalNode SUB() { return getToken(DoublesParser.SUB, 0); }
		public TerminalNode ADD() { return getToken(DoublesParser.ADD, 0); }
		public AddSubContext(DoubleExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DoublesVisitor ) return ((DoublesVisitor<? extends T>)visitor).visitAddSub(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class IdContext extends DoubleExprContext {
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
		public IdContext(DoubleExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DoublesVisitor ) return ((DoublesVisitor<? extends T>)visitor).visitId(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class MulDivContext extends DoubleExprContext {
		public Token op;
		public List<DoubleExprContext> doubleExpr() {
			return getRuleContexts(DoubleExprContext.class);
		}
		public TerminalNode MUL() { return getToken(DoublesParser.MUL, 0); }
		public TerminalNode DIV() { return getToken(DoublesParser.DIV, 0); }
		public DoubleExprContext doubleExpr(int i) {
			return getRuleContext(DoubleExprContext.class,i);
		}
		public MulDivContext(DoubleExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DoublesVisitor ) return ((DoublesVisitor<? extends T>)visitor).visitMulDiv(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DoubleExprContext doubleExpr(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		DoubleExprContext _localctx = new DoubleExprContext(_ctx, _parentState, _p);
		DoubleExprContext _prevctx = _localctx;
		int _startState = 2;
		enterRecursionRule(_localctx, RULE_doubleExpr);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(21);
			switch (_input.LA(1)) {
			case DOUBLE:
				{
				_localctx = new DoubleContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(15); match(DOUBLE);
				}
				break;
			case MUL:
			case ID:
				{
				_localctx = new IdContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(16); identifier();
				}
				break;
			case 2:
				{
				_localctx = new ParensContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(17); match(2);
				setState(18); doubleExpr(0);
				setState(19); match(1);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(31);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,2,_ctx);
			while ( _alt!=2 && _alt!=-1 ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(29);
					switch ( getInterpreter().adaptivePredict(_input,1,_ctx) ) {
					case 1:
						{
						_localctx = new MulDivContext(new DoubleExprContext(_parentctx, _parentState, _p));
						pushNewRecursionContext(_localctx, _startState, RULE_doubleExpr);
						setState(23);
						if (!(5 >= _localctx._p)) throw new FailedPredicateException(this, "5 >= $_p");
						setState(24);
						((MulDivContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==MUL || _la==DIV) ) {
							((MulDivContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						consume();
						setState(25); doubleExpr(6);
						}
						break;

					case 2:
						{
						_localctx = new AddSubContext(new DoubleExprContext(_parentctx, _parentState, _p));
						pushNewRecursionContext(_localctx, _startState, RULE_doubleExpr);
						setState(26);
						if (!(4 >= _localctx._p)) throw new FailedPredicateException(this, "4 >= $_p");
						setState(27);
						((AddSubContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==ADD || _la==SUB) ) {
							((AddSubContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						consume();
						setState(28); doubleExpr(5);
						}
						break;
					}
					} 
				}
				setState(33);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,2,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class IdentifierContext extends ParserRuleContext {
		public List<TerminalNode> DOT() { return getTokens(DoublesParser.DOT); }
		public MethodContext method() {
			return getRuleContext(MethodContext.class,0);
		}
		public ObservableIdContext observableId() {
			return getRuleContext(ObservableIdContext.class,0);
		}
		public TerminalNode DOT(int i) {
			return getToken(DoublesParser.DOT, i);
		}
		public HostIdContext hostId() {
			return getRuleContext(HostIdContext.class,0);
		}
		public IdentifierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_identifier; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DoublesVisitor ) return ((DoublesVisitor<? extends T>)visitor).visitIdentifier(this);
			else return visitor.visitChildren(this);
		}
	}

	public final IdentifierContext identifier() throws RecognitionException {
		IdentifierContext _localctx = new IdentifierContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_identifier);
		try {
			setState(44);
			switch ( getInterpreter().adaptivePredict(_input,3,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(34); hostId();
				setState(35); match(DOT);
				setState(36); observableId();
				setState(37); match(DOT);
				setState(38); method();
				}
				break;

			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(40); observableId();
				setState(41); match(DOT);
				setState(42); method();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class HostIdContext extends ParserRuleContext {
		public TerminalNode ID() { return getToken(DoublesParser.ID, 0); }
		public HostIdContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_hostId; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DoublesVisitor ) return ((DoublesVisitor<? extends T>)visitor).visitHostId(this);
			else return visitor.visitChildren(this);
		}
	}

	public final HostIdContext hostId() throws RecognitionException {
		HostIdContext _localctx = new HostIdContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_hostId);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(46);
			_la = _input.LA(1);
			if ( !(_la==MUL || _la==ID) ) {
			_errHandler.recoverInline(this);
			}
			consume();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ObservableIdContext extends ParserRuleContext {
		public TerminalNode ID() { return getToken(DoublesParser.ID, 0); }
		public ObservableIdContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_observableId; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DoublesVisitor ) return ((DoublesVisitor<? extends T>)visitor).visitObservableId(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ObservableIdContext observableId() throws RecognitionException {
		ObservableIdContext _localctx = new ObservableIdContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_observableId);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(48); match(ID);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class MethodContext extends ParserRuleContext {
		public TerminalNode ID() { return getToken(DoublesParser.ID, 0); }
		public MethodContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_method; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DoublesVisitor ) return ((DoublesVisitor<? extends T>)visitor).visitMethod(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MethodContext method() throws RecognitionException {
		MethodContext _localctx = new MethodContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_method);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(50); match(ID);
			setState(51); match(2);
			setState(52); match(1);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 1: return doubleExpr_sempred((DoubleExprContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean doubleExpr_sempred(DoubleExprContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0: return 5 >= _localctx._p;

		case 1: return 4 >= _localctx._p;
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\uacf5\uee8c\u4f5d\u8b0d\u4a45\u78bd\u1b2f\u3378\3\229\4\2\t\2\4\3\t"+
		"\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\3\2\3\2\3\3\3\3\3\3\3\3\3\3\3\3\3\3"+
		"\5\3\30\n\3\3\3\3\3\3\3\3\3\3\3\3\3\7\3 \n\3\f\3\16\3#\13\3\3\4\3\4\3"+
		"\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\5\4/\n\4\3\5\3\5\3\6\3\6\3\7\3\7\3\7\3"+
		"\7\3\7\2\b\2\4\6\b\n\f\2\5\3\2\5\6\3\2\7\b\4\2\5\5\16\16\67\2\16\3\2\2"+
		"\2\4\27\3\2\2\2\6.\3\2\2\2\b\60\3\2\2\2\n\62\3\2\2\2\f\64\3\2\2\2\16\17"+
		"\5\4\3\2\17\3\3\2\2\2\20\21\b\3\1\2\21\30\7\20\2\2\22\30\5\6\4\2\23\24"+
		"\7\4\2\2\24\25\5\4\3\2\25\26\7\3\2\2\26\30\3\2\2\2\27\20\3\2\2\2\27\22"+
		"\3\2\2\2\27\23\3\2\2\2\30!\3\2\2\2\31\32\6\3\2\3\32\33\t\2\2\2\33 \5\4"+
		"\3\2\34\35\6\3\3\3\35\36\t\3\2\2\36 \5\4\3\2\37\31\3\2\2\2\37\34\3\2\2"+
		"\2 #\3\2\2\2!\37\3\2\2\2!\"\3\2\2\2\"\5\3\2\2\2#!\3\2\2\2$%\5\b\5\2%&"+
		"\7\t\2\2&\'\5\n\6\2\'(\7\t\2\2()\5\f\7\2)/\3\2\2\2*+\5\n\6\2+,\7\t\2\2"+
		",-\5\f\7\2-/\3\2\2\2.$\3\2\2\2.*\3\2\2\2/\7\3\2\2\2\60\61\t\4\2\2\61\t"+
		"\3\2\2\2\62\63\7\16\2\2\63\13\3\2\2\2\64\65\7\16\2\2\65\66\7\4\2\2\66"+
		"\67\7\3\2\2\67\r\3\2\2\2\6\27\37!.";
	public static final ATN _ATN =
		ATNSimulator.deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}