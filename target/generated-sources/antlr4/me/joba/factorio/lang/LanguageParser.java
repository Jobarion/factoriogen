// Generated from me\joba\factorio\lang\Language.g4 by ANTLR 4.3
package me.joba.factorio.lang;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class LanguageParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.3", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__12=1, T__11=2, T__10=3, T__9=4, T__8=5, T__7=6, T__6=7, T__5=8, T__4=9, 
		T__3=10, T__2=11, T__1=12, T__0=13, VarName=14, Type=15, IntLiteral=16, 
		StringLiteral=17, ADD=18, SUB=19, MUL=20, DIV=21, AND=22, OR=23, XOR=24, 
		NOT=25, LT=26, GT=27, LEQ=28, GEQ=29, EQ=30, NEQ=31, NETWORK_IN=32, WS=33, 
		WS_OPT=34;
	public static final String[] tokenNames = {
		"<INVALID>", "'sum('", "';'", "'{'", "'any('", "'while'", "'}'", "'='", 
		"'if'", "'all('", "'else'", "'('", "')'", "'count('", "VarName", "'int'", 
		"IntLiteral", "StringLiteral", "'+'", "'-'", "'*'", "'/'", "'&&'", "'||'", 
		"'^'", "'!'", "'<'", "'>'", "'<='", "'>='", "'=='", "'!='", "'IN'", "WS", 
		"WS_OPT"
	};
	public static final int
		RULE_assignment = 0, RULE_completeExpression = 1, RULE_expr = 2, RULE_ifExpr = 3, 
		RULE_elseExpr = 4, RULE_whileExpr = 5, RULE_boolExprComponent = 6, RULE_boolExpr = 7, 
		RULE_block = 8, RULE_blockStatement = 9, RULE_statement = 10;
	public static final String[] ruleNames = {
		"assignment", "completeExpression", "expr", "ifExpr", "elseExpr", "whileExpr", 
		"boolExprComponent", "boolExpr", "block", "blockStatement", "statement"
	};

	@Override
	public String getGrammarFileName() { return "Language.g4"; }

	@Override
	public String[] getTokenNames() { return tokenNames; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public LanguageParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class AssignmentContext extends ParserRuleContext {
		public Token var;
		public CompleteExpressionContext x;
		public CompleteExpressionContext completeExpression() {
			return getRuleContext(CompleteExpressionContext.class,0);
		}
		public TerminalNode VarName() { return getToken(LanguageParser.VarName, 0); }
		public AssignmentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_assignment; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LanguageListener ) ((LanguageListener)listener).enterAssignment(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LanguageListener ) ((LanguageListener)listener).exitAssignment(this);
		}
	}

	public final AssignmentContext assignment() throws RecognitionException {
		AssignmentContext _localctx = new AssignmentContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_assignment);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(22); ((AssignmentContext)_localctx).var = match(VarName);
			setState(23); match(T__6);
			setState(24); ((AssignmentContext)_localctx).x = completeExpression();
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

	public static class CompleteExpressionContext extends ParserRuleContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public CompleteExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_completeExpression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LanguageListener ) ((LanguageListener)listener).enterCompleteExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LanguageListener ) ((LanguageListener)listener).exitCompleteExpression(this);
		}
	}

	public final CompleteExpressionContext completeExpression() throws RecognitionException {
		CompleteExpressionContext _localctx = new CompleteExpressionContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_completeExpression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(26); expr(0);
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

	public static class ExprContext extends ParserRuleContext {
		public ExprContext left;
		public ExprContext wrapped;
		public Token numberLit;
		public Token var;
		public Token vecAccessor;
		public ExprContext sumExpr;
		public ExprContext countExpr;
		public Token op;
		public ExprContext right;
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public TerminalNode StringLiteral() { return getToken(LanguageParser.StringLiteral, 0); }
		public TerminalNode SUB() { return getToken(LanguageParser.SUB, 0); }
		public TerminalNode NETWORK_IN() { return getToken(LanguageParser.NETWORK_IN, 0); }
		public TerminalNode ADD() { return getToken(LanguageParser.ADD, 0); }
		public TerminalNode VarName() { return getToken(LanguageParser.VarName, 0); }
		public TerminalNode IntLiteral() { return getToken(LanguageParser.IntLiteral, 0); }
		public TerminalNode DIV() { return getToken(LanguageParser.DIV, 0); }
		public TerminalNode MUL() { return getToken(LanguageParser.MUL, 0); }
		public ExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LanguageListener ) ((LanguageListener)listener).enterExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LanguageListener ) ((LanguageListener)listener).exitExpr(this);
		}
	}

	public final ExprContext expr() throws RecognitionException {
		return expr(0);
	}

	private ExprContext expr(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		ExprContext _localctx = new ExprContext(_ctx, _parentState);
		ExprContext _prevctx = _localctx;
		int _startState = 4;
		enterRecursionRule(_localctx, 4, RULE_expr, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(49);
			switch (_input.LA(1)) {
			case T__2:
				{
				setState(29); match(T__2);
				setState(30); ((ExprContext)_localctx).wrapped = expr(0);
				setState(31); match(T__1);
				}
				break;
			case IntLiteral:
				{
				setState(33); ((ExprContext)_localctx).numberLit = match(IntLiteral);
				}
				break;
			case VarName:
				{
				setState(34); ((ExprContext)_localctx).var = match(VarName);
				setState(38);
				switch ( getInterpreter().adaptivePredict(_input,0,_ctx) ) {
				case 1:
					{
					setState(35); match(T__2);
					setState(36); ((ExprContext)_localctx).vecAccessor = match(StringLiteral);
					setState(37); match(T__1);
					}
					break;
				}
				}
				break;
			case T__12:
				{
				setState(40); match(T__12);
				setState(41); ((ExprContext)_localctx).sumExpr = expr(0);
				setState(42); match(T__1);
				}
				break;
			case T__0:
				{
				setState(44); match(T__0);
				setState(45); ((ExprContext)_localctx).countExpr = expr(0);
				setState(46); match(T__1);
				}
				break;
			case NETWORK_IN:
				{
				setState(48); match(NETWORK_IN);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(65);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,5,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(63);
					switch ( getInterpreter().adaptivePredict(_input,4,_ctx) ) {
					case 1:
						{
						_localctx = new ExprContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(51);
						if (!(precpred(_ctx, 7))) throw new FailedPredicateException(this, "precpred(_ctx, 7)");
						setState(54);
						switch (_input.LA(1)) {
						case MUL:
							{
							setState(52); ((ExprContext)_localctx).op = match(MUL);
							}
							break;
						case DIV:
							{
							setState(53); ((ExprContext)_localctx).op = match(DIV);
							}
							break;
						default:
							throw new NoViableAltException(this);
						}
						setState(56); ((ExprContext)_localctx).right = expr(8);
						}
						break;

					case 2:
						{
						_localctx = new ExprContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(57);
						if (!(precpred(_ctx, 6))) throw new FailedPredicateException(this, "precpred(_ctx, 6)");
						setState(60);
						switch (_input.LA(1)) {
						case ADD:
							{
							setState(58); ((ExprContext)_localctx).op = match(ADD);
							}
							break;
						case SUB:
							{
							setState(59); ((ExprContext)_localctx).op = match(SUB);
							}
							break;
						default:
							throw new NoViableAltException(this);
						}
						setState(62); ((ExprContext)_localctx).right = expr(7);
						}
						break;
					}
					} 
				}
				setState(67);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,5,_ctx);
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

	public static class IfExprContext extends ParserRuleContext {
		public BoolExprContext ifCond;
		public StatementContext ifStatement;
		public ElseExprContext elseStatement;
		public BoolExprContext boolExpr() {
			return getRuleContext(BoolExprContext.class,0);
		}
		public ElseExprContext elseExpr() {
			return getRuleContext(ElseExprContext.class,0);
		}
		public StatementContext statement() {
			return getRuleContext(StatementContext.class,0);
		}
		public IfExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ifExpr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LanguageListener ) ((LanguageListener)listener).enterIfExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LanguageListener ) ((LanguageListener)listener).exitIfExpr(this);
		}
	}

	public final IfExprContext ifExpr() throws RecognitionException {
		IfExprContext _localctx = new IfExprContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_ifExpr);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(68); match(T__5);
			setState(69); match(T__2);
			setState(70); ((IfExprContext)_localctx).ifCond = boolExpr(0);
			setState(71); match(T__1);
			setState(72); ((IfExprContext)_localctx).ifStatement = statement();
			setState(75);
			switch ( getInterpreter().adaptivePredict(_input,6,_ctx) ) {
			case 1:
				{
				setState(73); match(T__3);
				setState(74); ((IfExprContext)_localctx).elseStatement = elseExpr();
				}
				break;
			}
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

	public static class ElseExprContext extends ParserRuleContext {
		public StatementContext statement() {
			return getRuleContext(StatementContext.class,0);
		}
		public ElseExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_elseExpr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LanguageListener ) ((LanguageListener)listener).enterElseExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LanguageListener ) ((LanguageListener)listener).exitElseExpr(this);
		}
	}

	public final ElseExprContext elseExpr() throws RecognitionException {
		ElseExprContext _localctx = new ElseExprContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_elseExpr);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(77); statement();
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

	public static class WhileExprContext extends ParserRuleContext {
		public BoolExprContext loopCond;
		public StatementContext loopStatement;
		public BoolExprContext boolExpr() {
			return getRuleContext(BoolExprContext.class,0);
		}
		public StatementContext statement() {
			return getRuleContext(StatementContext.class,0);
		}
		public WhileExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_whileExpr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LanguageListener ) ((LanguageListener)listener).enterWhileExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LanguageListener ) ((LanguageListener)listener).exitWhileExpr(this);
		}
	}

	public final WhileExprContext whileExpr() throws RecognitionException {
		WhileExprContext _localctx = new WhileExprContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_whileExpr);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(79); match(T__8);
			setState(80); match(T__2);
			setState(81); ((WhileExprContext)_localctx).loopCond = boolExpr(0);
			setState(82); match(T__1);
			setState(83); ((WhileExprContext)_localctx).loopStatement = statement();
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

	public static class BoolExprComponentContext extends ParserRuleContext {
		public CompleteExpressionContext anyExpr;
		public CompleteExpressionContext allExpr;
		public CompleteExpressionContext completeExpression() {
			return getRuleContext(CompleteExpressionContext.class,0);
		}
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public BoolExprComponentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_boolExprComponent; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LanguageListener ) ((LanguageListener)listener).enterBoolExprComponent(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LanguageListener ) ((LanguageListener)listener).exitBoolExprComponent(this);
		}
	}

	public final BoolExprComponentContext boolExprComponent() throws RecognitionException {
		BoolExprComponentContext _localctx = new BoolExprComponentContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_boolExprComponent);
		try {
			setState(94);
			switch (_input.LA(1)) {
			case T__12:
			case T__2:
			case T__0:
			case VarName:
			case IntLiteral:
			case NETWORK_IN:
				enterOuterAlt(_localctx, 1);
				{
				setState(85); expr(0);
				}
				break;
			case T__9:
				enterOuterAlt(_localctx, 2);
				{
				setState(86); match(T__9);
				setState(87); ((BoolExprComponentContext)_localctx).anyExpr = completeExpression();
				setState(88); match(T__1);
				}
				break;
			case T__4:
				enterOuterAlt(_localctx, 3);
				{
				setState(90); match(T__4);
				setState(91); ((BoolExprComponentContext)_localctx).allExpr = completeExpression();
				setState(92); match(T__1);
				}
				break;
			default:
				throw new NoViableAltException(this);
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

	public static class BoolExprContext extends ParserRuleContext {
		public BoolExprContext left;
		public BoolExprContext negated;
		public BoolExprComponentContext leftComponent;
		public Token op;
		public BoolExprComponentContext rightComponent;
		public BoolExprContext right;
		public BoolExprComponentContext boolExprComponent(int i) {
			return getRuleContext(BoolExprComponentContext.class,i);
		}
		public List<BoolExprContext> boolExpr() {
			return getRuleContexts(BoolExprContext.class);
		}
		public TerminalNode NEQ() { return getToken(LanguageParser.NEQ, 0); }
		public TerminalNode XOR() { return getToken(LanguageParser.XOR, 0); }
		public BoolExprContext boolExpr(int i) {
			return getRuleContext(BoolExprContext.class,i);
		}
		public TerminalNode LT() { return getToken(LanguageParser.LT, 0); }
		public TerminalNode GT() { return getToken(LanguageParser.GT, 0); }
		public TerminalNode OR() { return getToken(LanguageParser.OR, 0); }
		public TerminalNode NOT() { return getToken(LanguageParser.NOT, 0); }
		public List<BoolExprComponentContext> boolExprComponent() {
			return getRuleContexts(BoolExprComponentContext.class);
		}
		public TerminalNode GEQ() { return getToken(LanguageParser.GEQ, 0); }
		public TerminalNode LEQ() { return getToken(LanguageParser.LEQ, 0); }
		public TerminalNode AND() { return getToken(LanguageParser.AND, 0); }
		public TerminalNode EQ() { return getToken(LanguageParser.EQ, 0); }
		public BoolExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_boolExpr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LanguageListener ) ((LanguageListener)listener).enterBoolExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LanguageListener ) ((LanguageListener)listener).exitBoolExpr(this);
		}
	}

	public final BoolExprContext boolExpr() throws RecognitionException {
		return boolExpr(0);
	}

	private BoolExprContext boolExpr(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		BoolExprContext _localctx = new BoolExprContext(_ctx, _parentState);
		BoolExprContext _prevctx = _localctx;
		int _startState = 14;
		enterRecursionRule(_localctx, 14, RULE_boolExpr, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(107);
			switch ( getInterpreter().adaptivePredict(_input,8,_ctx) ) {
			case 1:
				{
				setState(97); match(NOT);
				setState(98); ((BoolExprContext)_localctx).negated = boolExpr(3);
				}
				break;

			case 2:
				{
				setState(99); match(T__2);
				setState(100); boolExpr(0);
				setState(101); match(T__1);
				}
				break;

			case 3:
				{
				setState(103); ((BoolExprContext)_localctx).leftComponent = boolExprComponent();
				setState(104);
				((BoolExprContext)_localctx).op = _input.LT(1);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LT) | (1L << GT) | (1L << LEQ) | (1L << GEQ) | (1L << EQ) | (1L << NEQ))) != 0)) ) {
					((BoolExprContext)_localctx).op = (Token)_errHandler.recoverInline(this);
				}
				consume();
				setState(105); ((BoolExprContext)_localctx).rightComponent = boolExprComponent();
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(114);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,9,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new BoolExprContext(_parentctx, _parentState);
					_localctx.left = _prevctx;
					pushNewRecursionContext(_localctx, _startState, RULE_boolExpr);
					setState(109);
					if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
					setState(110);
					((BoolExprContext)_localctx).op = _input.LT(1);
					_la = _input.LA(1);
					if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << AND) | (1L << OR) | (1L << XOR))) != 0)) ) {
						((BoolExprContext)_localctx).op = (Token)_errHandler.recoverInline(this);
					}
					consume();
					setState(111); ((BoolExprContext)_localctx).right = boolExpr(3);
					}
					} 
				}
				setState(116);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,9,_ctx);
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

	public static class BlockContext extends ParserRuleContext {
		public List<BlockStatementContext> blockStatement() {
			return getRuleContexts(BlockStatementContext.class);
		}
		public BlockStatementContext blockStatement(int i) {
			return getRuleContext(BlockStatementContext.class,i);
		}
		public BlockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_block; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LanguageListener ) ((LanguageListener)listener).enterBlock(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LanguageListener ) ((LanguageListener)listener).exitBlock(this);
		}
	}

	public final BlockContext block() throws RecognitionException {
		BlockContext _localctx = new BlockContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_block);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(117); match(T__10);
			setState(119); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(118); blockStatement();
				}
				}
				setState(121); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__10) | (1L << T__8) | (1L << T__5) | (1L << VarName))) != 0) );
			setState(123); match(T__7);
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

	public static class BlockStatementContext extends ParserRuleContext {
		public StatementContext statement() {
			return getRuleContext(StatementContext.class,0);
		}
		public BlockStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_blockStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LanguageListener ) ((LanguageListener)listener).enterBlockStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LanguageListener ) ((LanguageListener)listener).exitBlockStatement(this);
		}
	}

	public final BlockStatementContext blockStatement() throws RecognitionException {
		BlockStatementContext _localctx = new BlockStatementContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_blockStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(125); statement();
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

	public static class StatementContext extends ParserRuleContext {
		public AssignmentContext assignment() {
			return getRuleContext(AssignmentContext.class,0);
		}
		public WhileExprContext whileExpr() {
			return getRuleContext(WhileExprContext.class,0);
		}
		public IfExprContext ifExpr() {
			return getRuleContext(IfExprContext.class,0);
		}
		public BlockContext block() {
			return getRuleContext(BlockContext.class,0);
		}
		public StatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_statement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LanguageListener ) ((LanguageListener)listener).enterStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LanguageListener ) ((LanguageListener)listener).exitStatement(this);
		}
	}

	public final StatementContext statement() throws RecognitionException {
		StatementContext _localctx = new StatementContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_statement);
		try {
			setState(135);
			switch (_input.LA(1)) {
			case T__10:
				enterOuterAlt(_localctx, 1);
				{
				setState(127); block();
				setState(128); match(T__11);
				}
				break;
			case VarName:
				enterOuterAlt(_localctx, 2);
				{
				setState(130); assignment();
				setState(131); match(T__11);
				}
				break;
			case T__5:
				enterOuterAlt(_localctx, 3);
				{
				setState(133); ifExpr();
				}
				break;
			case T__8:
				enterOuterAlt(_localctx, 4);
				{
				setState(134); whileExpr();
				}
				break;
			default:
				throw new NoViableAltException(this);
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
		case 2: return expr_sempred((ExprContext)_localctx, predIndex);

		case 7: return boolExpr_sempred((BoolExprContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean expr_sempred(ExprContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0: return precpred(_ctx, 7);

		case 1: return precpred(_ctx, 6);
		}
		return true;
	}
	private boolean boolExpr_sempred(BoolExprContext _localctx, int predIndex) {
		switch (predIndex) {
		case 2: return precpred(_ctx, 2);
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3$\u008c\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\3\2\3\2\3\2\3\2\3\3\3\3\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3"+
		"\4\3\4\5\4)\n\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\5\4\64\n\4\3\4\3\4"+
		"\3\4\5\49\n\4\3\4\3\4\3\4\3\4\5\4?\n\4\3\4\7\4B\n\4\f\4\16\4E\13\4\3\5"+
		"\3\5\3\5\3\5\3\5\3\5\3\5\5\5N\n\5\3\6\3\6\3\7\3\7\3\7\3\7\3\7\3\7\3\b"+
		"\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\5\ba\n\b\3\t\3\t\3\t\3\t\3\t\3\t\3\t"+
		"\3\t\3\t\3\t\3\t\5\tn\n\t\3\t\3\t\3\t\7\ts\n\t\f\t\16\tv\13\t\3\n\3\n"+
		"\6\nz\n\n\r\n\16\n{\3\n\3\n\3\13\3\13\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f"+
		"\5\f\u008a\n\f\3\f\2\4\6\20\r\2\4\6\b\n\f\16\20\22\24\26\2\4\3\2\34!\3"+
		"\2\30\32\u0094\2\30\3\2\2\2\4\34\3\2\2\2\6\63\3\2\2\2\bF\3\2\2\2\nO\3"+
		"\2\2\2\fQ\3\2\2\2\16`\3\2\2\2\20m\3\2\2\2\22w\3\2\2\2\24\177\3\2\2\2\26"+
		"\u0089\3\2\2\2\30\31\7\20\2\2\31\32\7\t\2\2\32\33\5\4\3\2\33\3\3\2\2\2"+
		"\34\35\5\6\4\2\35\5\3\2\2\2\36\37\b\4\1\2\37 \7\r\2\2 !\5\6\4\2!\"\7\16"+
		"\2\2\"\64\3\2\2\2#\64\7\22\2\2$(\7\20\2\2%&\7\r\2\2&\'\7\23\2\2\')\7\16"+
		"\2\2(%\3\2\2\2()\3\2\2\2)\64\3\2\2\2*+\7\3\2\2+,\5\6\4\2,-\7\16\2\2-\64"+
		"\3\2\2\2./\7\17\2\2/\60\5\6\4\2\60\61\7\16\2\2\61\64\3\2\2\2\62\64\7\""+
		"\2\2\63\36\3\2\2\2\63#\3\2\2\2\63$\3\2\2\2\63*\3\2\2\2\63.\3\2\2\2\63"+
		"\62\3\2\2\2\64C\3\2\2\2\658\f\t\2\2\669\7\26\2\2\679\7\27\2\28\66\3\2"+
		"\2\28\67\3\2\2\29:\3\2\2\2:B\5\6\4\n;>\f\b\2\2<?\7\24\2\2=?\7\25\2\2>"+
		"<\3\2\2\2>=\3\2\2\2?@\3\2\2\2@B\5\6\4\tA\65\3\2\2\2A;\3\2\2\2BE\3\2\2"+
		"\2CA\3\2\2\2CD\3\2\2\2D\7\3\2\2\2EC\3\2\2\2FG\7\n\2\2GH\7\r\2\2HI\5\20"+
		"\t\2IJ\7\16\2\2JM\5\26\f\2KL\7\f\2\2LN\5\n\6\2MK\3\2\2\2MN\3\2\2\2N\t"+
		"\3\2\2\2OP\5\26\f\2P\13\3\2\2\2QR\7\7\2\2RS\7\r\2\2ST\5\20\t\2TU\7\16"+
		"\2\2UV\5\26\f\2V\r\3\2\2\2Wa\5\6\4\2XY\7\6\2\2YZ\5\4\3\2Z[\7\16\2\2[a"+
		"\3\2\2\2\\]\7\13\2\2]^\5\4\3\2^_\7\16\2\2_a\3\2\2\2`W\3\2\2\2`X\3\2\2"+
		"\2`\\\3\2\2\2a\17\3\2\2\2bc\b\t\1\2cd\7\33\2\2dn\5\20\t\5ef\7\r\2\2fg"+
		"\5\20\t\2gh\7\16\2\2hn\3\2\2\2ij\5\16\b\2jk\t\2\2\2kl\5\16\b\2ln\3\2\2"+
		"\2mb\3\2\2\2me\3\2\2\2mi\3\2\2\2nt\3\2\2\2op\f\4\2\2pq\t\3\2\2qs\5\20"+
		"\t\5ro\3\2\2\2sv\3\2\2\2tr\3\2\2\2tu\3\2\2\2u\21\3\2\2\2vt\3\2\2\2wy\7"+
		"\5\2\2xz\5\24\13\2yx\3\2\2\2z{\3\2\2\2{y\3\2\2\2{|\3\2\2\2|}\3\2\2\2}"+
		"~\7\b\2\2~\23\3\2\2\2\177\u0080\5\26\f\2\u0080\25\3\2\2\2\u0081\u0082"+
		"\5\22\n\2\u0082\u0083\7\4\2\2\u0083\u008a\3\2\2\2\u0084\u0085\5\2\2\2"+
		"\u0085\u0086\7\4\2\2\u0086\u008a\3\2\2\2\u0087\u008a\5\b\5\2\u0088\u008a"+
		"\5\f\7\2\u0089\u0081\3\2\2\2\u0089\u0084\3\2\2\2\u0089\u0087\3\2\2\2\u0089"+
		"\u0088\3\2\2\2\u008a\27\3\2\2\2\16(\638>ACM`mt{\u0089";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}