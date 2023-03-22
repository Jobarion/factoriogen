// Generated from C:/Users/jonas/Documents/Java/FactorioSimulator/src/main/antlr4/me/joba/factorio/lang\Language.g4 by ANTLR 4.9.2
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
	static { RuntimeMetaData.checkVersion("4.9.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, T__11=12, T__12=13, T__13=14, T__14=15, T__15=16, T__16=17, 
		T__17=18, T__18=19, T__19=20, T__20=21, NameCharacterFirst=22, NameCharacterRest=23, 
		NumberCharacter=24, HexCharacter=25, TypeName=26, BoolLiteral=27, ADD=28, 
		SUB=29, MUL=30, DIV=31, MOD=32, LSH=33, RSH=34, BAND=35, BOR=36, BXOR=37, 
		ACCESS=38, AND=39, OR=40, XOR=41, NOT=42, LT=43, GT=44, LEQ=45, GEQ=46, 
		EQ=47, NEQ=48, WS=49;
	public static final int
		RULE_file = 0, RULE_function = 1, RULE_functionHeader = 2, RULE_functionModifiers = 3, 
		RULE_functionModifier = 4, RULE_functionParams = 5, RULE_functionParam = 6, 
		RULE_signalList = 7, RULE_block = 8, RULE_statement = 9, RULE_assignment = 10, 
		RULE_arrayAssignment = 11, RULE_ifExpr = 12, RULE_elseStatement = 13, 
		RULE_ifStatement = 14, RULE_whileExpr = 15, RULE_loopBody = 16, RULE_returnStatement = 17, 
		RULE_functionCall = 18, RULE_argumentList = 19, RULE_expr = 20, RULE_simpleExpr = 21, 
		RULE_exprList = 22, RULE_boolExpr = 23, RULE_type = 24, RULE_typeList = 25, 
		RULE_arrayDeclaration = 26, RULE_fixedpLiteral = 27, RULE_intLiteral = 28, 
		RULE_decimalLiteral = 29, RULE_hexLiteral = 30, RULE_boolLiteral = 31, 
		RULE_varName = 32, RULE_functionName = 33, RULE_signalName = 34;
	private static String[] makeRuleNames() {
		return new String[] {
			"file", "function", "functionHeader", "functionModifiers", "functionModifier", 
			"functionParams", "functionParam", "signalList", "block", "statement", 
			"assignment", "arrayAssignment", "ifExpr", "elseStatement", "ifStatement", 
			"whileExpr", "loopBody", "returnStatement", "functionCall", "argumentList", 
			"expr", "simpleExpr", "exprList", "boolExpr", "type", "typeList", "arrayDeclaration", 
			"fixedpLiteral", "intLiteral", "decimalLiteral", "hexLiteral", "boolLiteral", 
			"varName", "functionName", "signalName"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "';'", "'function '", "'('", "')'", "'->'", "'['", "','", "']'", 
			"'pipelined'", "'native'", "'delay'", "'='", "':'", "'{'", "'}'", "'if'", 
			"'else'", "'while'", "'return '", "'fixedp'", "'[]'", null, null, null, 
			null, null, null, "'+'", "'-'", "'*'", "'/'", "'%'", "'>>'", "'<<'", 
			"'&'", "'|'", null, "'.'", "'&&'", "'||'", null, "'!'", "'<'", "'>'", 
			"'<='", "'>='", "'=='", "'!='"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, "NameCharacterFirst", 
			"NameCharacterRest", "NumberCharacter", "HexCharacter", "TypeName", "BoolLiteral", 
			"ADD", "SUB", "MUL", "DIV", "MOD", "LSH", "RSH", "BAND", "BOR", "BXOR", 
			"ACCESS", "AND", "OR", "XOR", "NOT", "LT", "GT", "LEQ", "GEQ", "EQ", 
			"NEQ", "WS"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "Language.g4"; }

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

	public static class FileContext extends ParserRuleContext {
		public List<ArrayDeclarationContext> arrayDeclaration() {
			return getRuleContexts(ArrayDeclarationContext.class);
		}
		public ArrayDeclarationContext arrayDeclaration(int i) {
			return getRuleContext(ArrayDeclarationContext.class,i);
		}
		public List<FunctionContext> function() {
			return getRuleContexts(FunctionContext.class);
		}
		public FunctionContext function(int i) {
			return getRuleContext(FunctionContext.class,i);
		}
		public FileContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_file; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LanguageListener ) ((LanguageListener)listener).enterFile(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LanguageListener ) ((LanguageListener)listener).exitFile(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LanguageVisitor ) return ((LanguageVisitor<? extends T>)visitor).visitFile(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FileContext file() throws RecognitionException {
		FileContext _localctx = new FileContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_file);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(73);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__2) | (1L << T__19) | (1L << TypeName))) != 0)) {
				{
				{
				setState(70);
				arrayDeclaration();
				}
				}
				setState(75);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(77); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(76);
				function();
				}
				}
				setState(79); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==T__1 );
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

	public static class FunctionContext extends ParserRuleContext {
		public FunctionHeaderContext functionHeader() {
			return getRuleContext(FunctionHeaderContext.class,0);
		}
		public BlockContext block() {
			return getRuleContext(BlockContext.class,0);
		}
		public FunctionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_function; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LanguageListener ) ((LanguageListener)listener).enterFunction(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LanguageListener ) ((LanguageListener)listener).exitFunction(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LanguageVisitor ) return ((LanguageVisitor<? extends T>)visitor).visitFunction(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FunctionContext function() throws RecognitionException {
		FunctionContext _localctx = new FunctionContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_function);
		try {
			setState(87);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,2,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(81);
				functionHeader();
				setState(82);
				block();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(84);
				functionHeader();
				setState(85);
				match(T__0);
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

	public static class FunctionHeaderContext extends ParserRuleContext {
		public FunctionNameContext name;
		public TypeContext returnType;
		public SignalListContext returnSignal;
		public FunctionParamsContext functionParams() {
			return getRuleContext(FunctionParamsContext.class,0);
		}
		public FunctionNameContext functionName() {
			return getRuleContext(FunctionNameContext.class,0);
		}
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public FunctionModifiersContext functionModifiers() {
			return getRuleContext(FunctionModifiersContext.class,0);
		}
		public TerminalNode LT() { return getToken(LanguageParser.LT, 0); }
		public TerminalNode GT() { return getToken(LanguageParser.GT, 0); }
		public SignalListContext signalList() {
			return getRuleContext(SignalListContext.class,0);
		}
		public FunctionHeaderContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_functionHeader; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LanguageListener ) ((LanguageListener)listener).enterFunctionHeader(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LanguageListener ) ((LanguageListener)listener).exitFunctionHeader(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LanguageVisitor ) return ((LanguageVisitor<? extends T>)visitor).visitFunctionHeader(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FunctionHeaderContext functionHeader() throws RecognitionException {
		FunctionHeaderContext _localctx = new FunctionHeaderContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_functionHeader);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(89);
			match(T__1);
			setState(91);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,3,_ctx) ) {
			case 1:
				{
				setState(90);
				functionModifiers();
				}
				break;
			}
			setState(93);
			((FunctionHeaderContext)_localctx).name = functionName();
			setState(94);
			match(T__2);
			setState(95);
			functionParams();
			setState(96);
			match(T__3);
			setState(97);
			match(T__4);
			setState(98);
			((FunctionHeaderContext)_localctx).returnType = type(0);
			setState(103);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LT) {
				{
				setState(99);
				match(LT);
				setState(100);
				((FunctionHeaderContext)_localctx).returnSignal = signalList();
				setState(101);
				match(GT);
				}
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

	public static class FunctionModifiersContext extends ParserRuleContext {
		public List<FunctionModifierContext> functionModifier() {
			return getRuleContexts(FunctionModifierContext.class);
		}
		public FunctionModifierContext functionModifier(int i) {
			return getRuleContext(FunctionModifierContext.class,i);
		}
		public FunctionModifiersContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_functionModifiers; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LanguageListener ) ((LanguageListener)listener).enterFunctionModifiers(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LanguageListener ) ((LanguageListener)listener).exitFunctionModifiers(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LanguageVisitor ) return ((LanguageVisitor<? extends T>)visitor).visitFunctionModifiers(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FunctionModifiersContext functionModifiers() throws RecognitionException {
		FunctionModifiersContext _localctx = new FunctionModifiersContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_functionModifiers);
		int _la;
		try {
			setState(117);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case NameCharacterFirst:
				enterOuterAlt(_localctx, 1);
				{
				}
				break;
			case T__5:
				enterOuterAlt(_localctx, 2);
				{
				setState(106);
				match(T__5);
				setState(107);
				functionModifier();
				setState(112);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__6) {
					{
					{
					setState(108);
					match(T__6);
					setState(109);
					functionModifier();
					}
					}
					setState(114);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(115);
				match(T__7);
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

	public static class FunctionModifierContext extends ParserRuleContext {
		public Token key;
		public IntLiteralContext intLiteral() {
			return getRuleContext(IntLiteralContext.class,0);
		}
		public FunctionModifierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_functionModifier; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LanguageListener ) ((LanguageListener)listener).enterFunctionModifier(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LanguageListener ) ((LanguageListener)listener).exitFunctionModifier(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LanguageVisitor ) return ((LanguageVisitor<? extends T>)visitor).visitFunctionModifier(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FunctionModifierContext functionModifier() throws RecognitionException {
		FunctionModifierContext _localctx = new FunctionModifierContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_functionModifier);
		try {
			setState(124);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__8:
				enterOuterAlt(_localctx, 1);
				{
				setState(119);
				((FunctionModifierContext)_localctx).key = match(T__8);
				}
				break;
			case T__9:
				enterOuterAlt(_localctx, 2);
				{
				setState(120);
				((FunctionModifierContext)_localctx).key = match(T__9);
				}
				break;
			case T__10:
				enterOuterAlt(_localctx, 3);
				{
				setState(121);
				((FunctionModifierContext)_localctx).key = match(T__10);
				setState(122);
				match(T__11);
				setState(123);
				intLiteral();
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

	public static class FunctionParamsContext extends ParserRuleContext {
		public List<FunctionParamContext> functionParam() {
			return getRuleContexts(FunctionParamContext.class);
		}
		public FunctionParamContext functionParam(int i) {
			return getRuleContext(FunctionParamContext.class,i);
		}
		public FunctionParamsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_functionParams; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LanguageListener ) ((LanguageListener)listener).enterFunctionParams(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LanguageListener ) ((LanguageListener)listener).exitFunctionParams(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LanguageVisitor ) return ((LanguageVisitor<? extends T>)visitor).visitFunctionParams(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FunctionParamsContext functionParams() throws RecognitionException {
		FunctionParamsContext _localctx = new FunctionParamsContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_functionParams);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(134);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==NameCharacterFirst) {
				{
				setState(126);
				functionParam();
				setState(131);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__6) {
					{
					{
					setState(127);
					match(T__6);
					setState(128);
					functionParam();
					}
					}
					setState(133);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
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

	public static class FunctionParamContext extends ParserRuleContext {
		public VarNameContext varName() {
			return getRuleContext(VarNameContext.class,0);
		}
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public TerminalNode LT() { return getToken(LanguageParser.LT, 0); }
		public SignalListContext signalList() {
			return getRuleContext(SignalListContext.class,0);
		}
		public TerminalNode GT() { return getToken(LanguageParser.GT, 0); }
		public FunctionParamContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_functionParam; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LanguageListener ) ((LanguageListener)listener).enterFunctionParam(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LanguageListener ) ((LanguageListener)listener).exitFunctionParam(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LanguageVisitor ) return ((LanguageVisitor<? extends T>)visitor).visitFunctionParam(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FunctionParamContext functionParam() throws RecognitionException {
		FunctionParamContext _localctx = new FunctionParamContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_functionParam);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(136);
			varName();
			setState(137);
			match(T__12);
			setState(138);
			type(0);
			setState(143);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LT) {
				{
				setState(139);
				match(LT);
				setState(140);
				signalList();
				setState(141);
				match(GT);
				}
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

	public static class SignalListContext extends ParserRuleContext {
		public TerminalNode LT() { return getToken(LanguageParser.LT, 0); }
		public List<SignalNameContext> signalName() {
			return getRuleContexts(SignalNameContext.class);
		}
		public SignalNameContext signalName(int i) {
			return getRuleContext(SignalNameContext.class,i);
		}
		public TerminalNode GT() { return getToken(LanguageParser.GT, 0); }
		public SignalListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_signalList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LanguageListener ) ((LanguageListener)listener).enterSignalList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LanguageListener ) ((LanguageListener)listener).exitSignalList(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LanguageVisitor ) return ((LanguageVisitor<? extends T>)visitor).visitSignalList(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SignalListContext signalList() throws RecognitionException {
		SignalListContext _localctx = new SignalListContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_signalList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(145);
			match(LT);
			setState(146);
			signalName();
			setState(151);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__6) {
				{
				{
				setState(147);
				match(T__6);
				setState(148);
				signalName();
				}
				}
				setState(153);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(154);
			match(GT);
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

	public static class BlockContext extends ParserRuleContext {
		public List<StatementContext> statement() {
			return getRuleContexts(StatementContext.class);
		}
		public StatementContext statement(int i) {
			return getRuleContext(StatementContext.class,i);
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
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LanguageVisitor ) return ((LanguageVisitor<? extends T>)visitor).visitBlock(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BlockContext block() throws RecognitionException {
		BlockContext _localctx = new BlockContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_block);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(156);
			match(T__13);
			setState(158); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(157);
				statement();
				}
				}
				setState(160); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__2) | (1L << T__13) | (1L << T__15) | (1L << T__17) | (1L << T__18) | (1L << NameCharacterFirst) | (1L << NumberCharacter) | (1L << HexCharacter) | (1L << BoolLiteral) | (1L << SUB))) != 0) );
			setState(162);
			match(T__14);
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
		public BlockContext block() {
			return getRuleContext(BlockContext.class,0);
		}
		public AssignmentContext assignment() {
			return getRuleContext(AssignmentContext.class,0);
		}
		public ArrayAssignmentContext arrayAssignment() {
			return getRuleContext(ArrayAssignmentContext.class,0);
		}
		public IfExprContext ifExpr() {
			return getRuleContext(IfExprContext.class,0);
		}
		public WhileExprContext whileExpr() {
			return getRuleContext(WhileExprContext.class,0);
		}
		public ReturnStatementContext returnStatement() {
			return getRuleContext(ReturnStatementContext.class,0);
		}
		public FunctionCallContext functionCall() {
			return getRuleContext(FunctionCallContext.class,0);
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
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LanguageVisitor ) return ((LanguageVisitor<? extends T>)visitor).visitStatement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StatementContext statement() throws RecognitionException {
		StatementContext _localctx = new StatementContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_statement);
		try {
			setState(179);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,13,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(164);
				block();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(165);
				assignment();
				setState(166);
				match(T__0);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(168);
				arrayAssignment();
				setState(169);
				match(T__0);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(171);
				ifExpr();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(172);
				whileExpr();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(173);
				returnStatement();
				setState(174);
				match(T__0);
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(176);
				functionCall();
				setState(177);
				match(T__0);
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

	public static class AssignmentContext extends ParserRuleContext {
		public VarNameContext var;
		public IntLiteralContext fractBits;
		public ExprContext x;
		public VarNameContext varName() {
			return getRuleContext(VarNameContext.class,0);
		}
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public TerminalNode LT() { return getToken(LanguageParser.LT, 0); }
		public TerminalNode GT() { return getToken(LanguageParser.GT, 0); }
		public IntLiteralContext intLiteral() {
			return getRuleContext(IntLiteralContext.class,0);
		}
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
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LanguageVisitor ) return ((LanguageVisitor<? extends T>)visitor).visitAssignment(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AssignmentContext assignment() throws RecognitionException {
		AssignmentContext _localctx = new AssignmentContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_assignment);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(181);
			((AssignmentContext)_localctx).var = varName();
			setState(186);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LT) {
				{
				setState(182);
				match(LT);
				setState(183);
				((AssignmentContext)_localctx).fractBits = intLiteral();
				setState(184);
				match(GT);
				}
			}

			setState(188);
			match(T__11);
			setState(189);
			((AssignmentContext)_localctx).x = expr(0);
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

	public static class ArrayAssignmentContext extends ParserRuleContext {
		public ExprContext array;
		public ExprContext index;
		public ExprContext x;
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public ArrayAssignmentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_arrayAssignment; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LanguageListener ) ((LanguageListener)listener).enterArrayAssignment(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LanguageListener ) ((LanguageListener)listener).exitArrayAssignment(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LanguageVisitor ) return ((LanguageVisitor<? extends T>)visitor).visitArrayAssignment(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ArrayAssignmentContext arrayAssignment() throws RecognitionException {
		ArrayAssignmentContext _localctx = new ArrayAssignmentContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_arrayAssignment);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(191);
			((ArrayAssignmentContext)_localctx).array = expr(0);
			setState(192);
			match(T__5);
			setState(193);
			((ArrayAssignmentContext)_localctx).index = expr(0);
			setState(194);
			match(T__7);
			setState(195);
			match(T__11);
			setState(196);
			((ArrayAssignmentContext)_localctx).x = expr(0);
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

	public static class IfExprContext extends ParserRuleContext {
		public BoolExprContext ifCond;
		public IfStatementContext ifPart;
		public ElseStatementContext elsePart;
		public BoolExprContext boolExpr() {
			return getRuleContext(BoolExprContext.class,0);
		}
		public IfStatementContext ifStatement() {
			return getRuleContext(IfStatementContext.class,0);
		}
		public ElseStatementContext elseStatement() {
			return getRuleContext(ElseStatementContext.class,0);
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
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LanguageVisitor ) return ((LanguageVisitor<? extends T>)visitor).visitIfExpr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final IfExprContext ifExpr() throws RecognitionException {
		IfExprContext _localctx = new IfExprContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_ifExpr);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(198);
			match(T__15);
			setState(199);
			match(T__2);
			setState(200);
			((IfExprContext)_localctx).ifCond = boolExpr(0);
			setState(201);
			match(T__3);
			setState(202);
			((IfExprContext)_localctx).ifPart = ifStatement();
			setState(205);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,15,_ctx) ) {
			case 1:
				{
				setState(203);
				match(T__16);
				setState(204);
				((IfExprContext)_localctx).elsePart = elseStatement();
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

	public static class ElseStatementContext extends ParserRuleContext {
		public StatementContext statement() {
			return getRuleContext(StatementContext.class,0);
		}
		public ElseStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_elseStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LanguageListener ) ((LanguageListener)listener).enterElseStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LanguageListener ) ((LanguageListener)listener).exitElseStatement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LanguageVisitor ) return ((LanguageVisitor<? extends T>)visitor).visitElseStatement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ElseStatementContext elseStatement() throws RecognitionException {
		ElseStatementContext _localctx = new ElseStatementContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_elseStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(207);
			statement();
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

	public static class IfStatementContext extends ParserRuleContext {
		public StatementContext statement() {
			return getRuleContext(StatementContext.class,0);
		}
		public IfStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ifStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LanguageListener ) ((LanguageListener)listener).enterIfStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LanguageListener ) ((LanguageListener)listener).exitIfStatement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LanguageVisitor ) return ((LanguageVisitor<? extends T>)visitor).visitIfStatement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final IfStatementContext ifStatement() throws RecognitionException {
		IfStatementContext _localctx = new IfStatementContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_ifStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(209);
			statement();
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
		public LoopBodyContext loopStatement;
		public BoolExprContext boolExpr() {
			return getRuleContext(BoolExprContext.class,0);
		}
		public LoopBodyContext loopBody() {
			return getRuleContext(LoopBodyContext.class,0);
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
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LanguageVisitor ) return ((LanguageVisitor<? extends T>)visitor).visitWhileExpr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final WhileExprContext whileExpr() throws RecognitionException {
		WhileExprContext _localctx = new WhileExprContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_whileExpr);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(211);
			match(T__17);
			setState(212);
			match(T__2);
			setState(213);
			((WhileExprContext)_localctx).loopCond = boolExpr(0);
			setState(214);
			match(T__3);
			setState(215);
			((WhileExprContext)_localctx).loopStatement = loopBody();
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

	public static class LoopBodyContext extends ParserRuleContext {
		public StatementContext statement() {
			return getRuleContext(StatementContext.class,0);
		}
		public LoopBodyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_loopBody; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LanguageListener ) ((LanguageListener)listener).enterLoopBody(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LanguageListener ) ((LanguageListener)listener).exitLoopBody(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LanguageVisitor ) return ((LanguageVisitor<? extends T>)visitor).visitLoopBody(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LoopBodyContext loopBody() throws RecognitionException {
		LoopBodyContext _localctx = new LoopBodyContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_loopBody);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(217);
			statement();
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

	public static class ReturnStatementContext extends ParserRuleContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public ReturnStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_returnStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LanguageListener ) ((LanguageListener)listener).enterReturnStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LanguageListener ) ((LanguageListener)listener).exitReturnStatement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LanguageVisitor ) return ((LanguageVisitor<? extends T>)visitor).visitReturnStatement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ReturnStatementContext returnStatement() throws RecognitionException {
		ReturnStatementContext _localctx = new ReturnStatementContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_returnStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(219);
			match(T__18);
			setState(220);
			expr(0);
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

	public static class FunctionCallContext extends ParserRuleContext {
		public FunctionNameContext functionName() {
			return getRuleContext(FunctionNameContext.class,0);
		}
		public ArgumentListContext argumentList() {
			return getRuleContext(ArgumentListContext.class,0);
		}
		public FunctionCallContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_functionCall; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LanguageListener ) ((LanguageListener)listener).enterFunctionCall(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LanguageListener ) ((LanguageListener)listener).exitFunctionCall(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LanguageVisitor ) return ((LanguageVisitor<? extends T>)visitor).visitFunctionCall(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FunctionCallContext functionCall() throws RecognitionException {
		FunctionCallContext _localctx = new FunctionCallContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_functionCall);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(222);
			functionName();
			setState(223);
			match(T__2);
			setState(224);
			argumentList();
			setState(225);
			match(T__3);
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

	public static class ArgumentListContext extends ParserRuleContext {
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public ArgumentListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_argumentList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LanguageListener ) ((LanguageListener)listener).enterArgumentList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LanguageListener ) ((LanguageListener)listener).exitArgumentList(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LanguageVisitor ) return ((LanguageVisitor<? extends T>)visitor).visitArgumentList(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ArgumentListContext argumentList() throws RecognitionException {
		ArgumentListContext _localctx = new ArgumentListContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_argumentList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(235);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__2) | (1L << NameCharacterFirst) | (1L << NumberCharacter) | (1L << HexCharacter) | (1L << BoolLiteral) | (1L << SUB))) != 0)) {
				{
				setState(227);
				expr(0);
				setState(232);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__6) {
					{
					{
					setState(228);
					match(T__6);
					setState(229);
					expr(0);
					}
					}
					setState(234);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
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

	public static class ExprContext extends ParserRuleContext {
		public ExprContext array;
		public ExprContext tuple;
		public ExprContext left;
		public ExprContext wrapped;
		public ExprListContext tupleValues;
		public VarNameContext var;
		public FunctionCallContext call;
		public Token op;
		public ExprContext right;
		public ExprContext index;
		public IntLiteralContext propertyId;
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public ExprListContext exprList() {
			return getRuleContext(ExprListContext.class,0);
		}
		public VarNameContext varName() {
			return getRuleContext(VarNameContext.class,0);
		}
		public FunctionCallContext functionCall() {
			return getRuleContext(FunctionCallContext.class,0);
		}
		public SimpleExprContext simpleExpr() {
			return getRuleContext(SimpleExprContext.class,0);
		}
		public TerminalNode MUL() { return getToken(LanguageParser.MUL, 0); }
		public TerminalNode DIV() { return getToken(LanguageParser.DIV, 0); }
		public TerminalNode MOD() { return getToken(LanguageParser.MOD, 0); }
		public TerminalNode ADD() { return getToken(LanguageParser.ADD, 0); }
		public TerminalNode SUB() { return getToken(LanguageParser.SUB, 0); }
		public TerminalNode LSH() { return getToken(LanguageParser.LSH, 0); }
		public TerminalNode RSH() { return getToken(LanguageParser.RSH, 0); }
		public TerminalNode BAND() { return getToken(LanguageParser.BAND, 0); }
		public TerminalNode BOR() { return getToken(LanguageParser.BOR, 0); }
		public TerminalNode BXOR() { return getToken(LanguageParser.BXOR, 0); }
		public IntLiteralContext intLiteral() {
			return getRuleContext(IntLiteralContext.class,0);
		}
		public TerminalNode ACCESS() { return getToken(LanguageParser.ACCESS, 0); }
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
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LanguageVisitor ) return ((LanguageVisitor<? extends T>)visitor).visitExpr(this);
			else return visitor.visitChildren(this);
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
		int _startState = 40;
		enterRecursionRule(_localctx, 40, RULE_expr, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(249);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,18,_ctx) ) {
			case 1:
				{
				setState(238);
				match(T__2);
				setState(239);
				((ExprContext)_localctx).wrapped = expr(0);
				setState(240);
				match(T__3);
				}
				break;
			case 2:
				{
				setState(242);
				match(T__2);
				setState(243);
				((ExprContext)_localctx).tupleValues = exprList();
				setState(244);
				match(T__3);
				}
				break;
			case 3:
				{
				setState(246);
				((ExprContext)_localctx).var = varName();
				}
				break;
			case 4:
				{
				setState(247);
				((ExprContext)_localctx).call = functionCall();
				}
				break;
			case 5:
				{
				setState(248);
				simpleExpr();
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(289);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,23,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(287);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,22,_ctx) ) {
					case 1:
						{
						_localctx = new ExprContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(251);
						if (!(precpred(_ctx, 7))) throw new FailedPredicateException(this, "precpred(_ctx, 7)");
						setState(255);
						_errHandler.sync(this);
						switch (_input.LA(1)) {
						case MUL:
							{
							setState(252);
							((ExprContext)_localctx).op = match(MUL);
							}
							break;
						case DIV:
							{
							setState(253);
							((ExprContext)_localctx).op = match(DIV);
							}
							break;
						case MOD:
							{
							setState(254);
							((ExprContext)_localctx).op = match(MOD);
							}
							break;
						default:
							throw new NoViableAltException(this);
						}
						setState(257);
						((ExprContext)_localctx).right = expr(8);
						}
						break;
					case 2:
						{
						_localctx = new ExprContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(258);
						if (!(precpred(_ctx, 6))) throw new FailedPredicateException(this, "precpred(_ctx, 6)");
						setState(261);
						_errHandler.sync(this);
						switch (_input.LA(1)) {
						case ADD:
							{
							setState(259);
							((ExprContext)_localctx).op = match(ADD);
							}
							break;
						case SUB:
							{
							setState(260);
							((ExprContext)_localctx).op = match(SUB);
							}
							break;
						default:
							throw new NoViableAltException(this);
						}
						setState(263);
						((ExprContext)_localctx).right = expr(7);
						}
						break;
					case 3:
						{
						_localctx = new ExprContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(264);
						if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
						setState(267);
						_errHandler.sync(this);
						switch (_input.LA(1)) {
						case LSH:
							{
							setState(265);
							((ExprContext)_localctx).op = match(LSH);
							}
							break;
						case RSH:
							{
							setState(266);
							((ExprContext)_localctx).op = match(RSH);
							}
							break;
						default:
							throw new NoViableAltException(this);
						}
						setState(269);
						((ExprContext)_localctx).right = expr(6);
						}
						break;
					case 4:
						{
						_localctx = new ExprContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(270);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						setState(271);
						((ExprContext)_localctx).op = match(BAND);
						setState(272);
						((ExprContext)_localctx).right = expr(5);
						}
						break;
					case 5:
						{
						_localctx = new ExprContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(273);
						if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
						setState(274);
						((ExprContext)_localctx).op = match(BOR);
						setState(275);
						((ExprContext)_localctx).right = expr(4);
						}
						break;
					case 6:
						{
						_localctx = new ExprContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(276);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(277);
						((ExprContext)_localctx).op = match(BXOR);
						setState(278);
						((ExprContext)_localctx).right = expr(3);
						}
						break;
					case 7:
						{
						_localctx = new ExprContext(_parentctx, _parentState);
						_localctx.array = _prevctx;
						_localctx.array = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(279);
						if (!(precpred(_ctx, 10))) throw new FailedPredicateException(this, "precpred(_ctx, 10)");
						setState(280);
						match(T__5);
						setState(281);
						((ExprContext)_localctx).index = expr(0);
						setState(282);
						match(T__7);
						}
						break;
					case 8:
						{
						_localctx = new ExprContext(_parentctx, _parentState);
						_localctx.tuple = _prevctx;
						_localctx.tuple = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(284);
						if (!(precpred(_ctx, 8))) throw new FailedPredicateException(this, "precpred(_ctx, 8)");
						{
						setState(285);
						((ExprContext)_localctx).op = match(ACCESS);
						}
						setState(286);
						((ExprContext)_localctx).propertyId = intLiteral();
						}
						break;
					}
					} 
				}
				setState(291);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,23,_ctx);
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

	public static class SimpleExprContext extends ParserRuleContext {
		public FixedpLiteralContext fixedpLit;
		public IntLiteralContext numberLit;
		public BoolLiteralContext boolLit;
		public FixedpLiteralContext fixedpLiteral() {
			return getRuleContext(FixedpLiteralContext.class,0);
		}
		public IntLiteralContext intLiteral() {
			return getRuleContext(IntLiteralContext.class,0);
		}
		public BoolLiteralContext boolLiteral() {
			return getRuleContext(BoolLiteralContext.class,0);
		}
		public SimpleExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_simpleExpr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LanguageListener ) ((LanguageListener)listener).enterSimpleExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LanguageListener ) ((LanguageListener)listener).exitSimpleExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LanguageVisitor ) return ((LanguageVisitor<? extends T>)visitor).visitSimpleExpr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SimpleExprContext simpleExpr() throws RecognitionException {
		SimpleExprContext _localctx = new SimpleExprContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_simpleExpr);
		try {
			setState(295);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,24,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(292);
				((SimpleExprContext)_localctx).fixedpLit = fixedpLiteral();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(293);
				((SimpleExprContext)_localctx).numberLit = intLiteral();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(294);
				((SimpleExprContext)_localctx).boolLit = boolLiteral();
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

	public static class ExprListContext extends ParserRuleContext {
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public ExprListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_exprList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LanguageListener ) ((LanguageListener)listener).enterExprList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LanguageListener ) ((LanguageListener)listener).exitExprList(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LanguageVisitor ) return ((LanguageVisitor<? extends T>)visitor).visitExprList(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExprListContext exprList() throws RecognitionException {
		ExprListContext _localctx = new ExprListContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_exprList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(297);
			expr(0);
			setState(300); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(298);
				match(T__6);
				setState(299);
				expr(0);
				}
				}
				setState(302); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==T__6 );
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
		public ExprContext leftComponent;
		public Token op;
		public ExprContext rightComponent;
		public BoolExprContext right;
		public List<BoolExprContext> boolExpr() {
			return getRuleContexts(BoolExprContext.class);
		}
		public BoolExprContext boolExpr(int i) {
			return getRuleContext(BoolExprContext.class,i);
		}
		public TerminalNode NOT() { return getToken(LanguageParser.NOT, 0); }
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public TerminalNode LT() { return getToken(LanguageParser.LT, 0); }
		public TerminalNode GT() { return getToken(LanguageParser.GT, 0); }
		public TerminalNode LEQ() { return getToken(LanguageParser.LEQ, 0); }
		public TerminalNode GEQ() { return getToken(LanguageParser.GEQ, 0); }
		public TerminalNode EQ() { return getToken(LanguageParser.EQ, 0); }
		public TerminalNode NEQ() { return getToken(LanguageParser.NEQ, 0); }
		public TerminalNode AND() { return getToken(LanguageParser.AND, 0); }
		public TerminalNode OR() { return getToken(LanguageParser.OR, 0); }
		public TerminalNode XOR() { return getToken(LanguageParser.XOR, 0); }
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
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LanguageVisitor ) return ((LanguageVisitor<? extends T>)visitor).visitBoolExpr(this);
			else return visitor.visitChildren(this);
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
		int _startState = 46;
		enterRecursionRule(_localctx, 46, RULE_boolExpr, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(315);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,26,_ctx) ) {
			case 1:
				{
				setState(305);
				match(T__2);
				setState(306);
				boolExpr(0);
				setState(307);
				match(T__3);
				}
				break;
			case 2:
				{
				setState(309);
				match(NOT);
				setState(310);
				((BoolExprContext)_localctx).negated = boolExpr(3);
				}
				break;
			case 3:
				{
				setState(311);
				((BoolExprContext)_localctx).leftComponent = expr(0);
				setState(312);
				((BoolExprContext)_localctx).op = _input.LT(1);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LT) | (1L << GT) | (1L << LEQ) | (1L << GEQ) | (1L << EQ) | (1L << NEQ))) != 0)) ) {
					((BoolExprContext)_localctx).op = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(313);
				((BoolExprContext)_localctx).rightComponent = expr(0);
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(322);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,27,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new BoolExprContext(_parentctx, _parentState);
					_localctx.left = _prevctx;
					_localctx.left = _prevctx;
					pushNewRecursionContext(_localctx, _startState, RULE_boolExpr);
					setState(317);
					if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
					setState(318);
					((BoolExprContext)_localctx).op = _input.LT(1);
					_la = _input.LA(1);
					if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << AND) | (1L << OR) | (1L << XOR))) != 0)) ) {
						((BoolExprContext)_localctx).op = (Token)_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					setState(319);
					((BoolExprContext)_localctx).right = boolExpr(3);
					}
					} 
				}
				setState(324);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,27,_ctx);
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

	public static class TypeContext extends ParserRuleContext {
		public TypeContext arrayType;
		public Token singleType;
		public IntLiteralContext fracbits;
		public TerminalNode TypeName() { return getToken(LanguageParser.TypeName, 0); }
		public TerminalNode LT() { return getToken(LanguageParser.LT, 0); }
		public TerminalNode GT() { return getToken(LanguageParser.GT, 0); }
		public IntLiteralContext intLiteral() {
			return getRuleContext(IntLiteralContext.class,0);
		}
		public TypeListContext typeList() {
			return getRuleContext(TypeListContext.class,0);
		}
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public TypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_type; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LanguageListener ) ((LanguageListener)listener).enterType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LanguageListener ) ((LanguageListener)listener).exitType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LanguageVisitor ) return ((LanguageVisitor<? extends T>)visitor).visitType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TypeContext type() throws RecognitionException {
		return type(0);
	}

	private TypeContext type(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		TypeContext _localctx = new TypeContext(_ctx, _parentState);
		TypeContext _prevctx = _localctx;
		int _startState = 48;
		enterRecursionRule(_localctx, 48, RULE_type, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(336);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case TypeName:
				{
				setState(326);
				((TypeContext)_localctx).singleType = match(TypeName);
				}
				break;
			case T__19:
				{
				setState(327);
				match(T__19);
				setState(328);
				match(LT);
				setState(329);
				((TypeContext)_localctx).fracbits = intLiteral();
				setState(330);
				match(GT);
				}
				break;
			case T__2:
				{
				setState(332);
				match(T__2);
				setState(333);
				typeList();
				setState(334);
				match(T__3);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(342);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,29,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new TypeContext(_parentctx, _parentState);
					_localctx.arrayType = _prevctx;
					_localctx.arrayType = _prevctx;
					pushNewRecursionContext(_localctx, _startState, RULE_type);
					setState(338);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(339);
					match(T__20);
					}
					} 
				}
				setState(344);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,29,_ctx);
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

	public static class TypeListContext extends ParserRuleContext {
		public List<TypeContext> type() {
			return getRuleContexts(TypeContext.class);
		}
		public TypeContext type(int i) {
			return getRuleContext(TypeContext.class,i);
		}
		public TypeListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_typeList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LanguageListener ) ((LanguageListener)listener).enterTypeList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LanguageListener ) ((LanguageListener)listener).exitTypeList(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LanguageVisitor ) return ((LanguageVisitor<? extends T>)visitor).visitTypeList(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TypeListContext typeList() throws RecognitionException {
		TypeListContext _localctx = new TypeListContext(_ctx, getState());
		enterRule(_localctx, 50, RULE_typeList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(345);
			type(0);
			setState(348); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(346);
				match(T__6);
				setState(347);
				type(0);
				}
				}
				setState(350); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==T__6 );
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

	public static class ArrayDeclarationContext extends ParserRuleContext {
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public IntLiteralContext intLiteral() {
			return getRuleContext(IntLiteralContext.class,0);
		}
		public VarNameContext varName() {
			return getRuleContext(VarNameContext.class,0);
		}
		public ArrayDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_arrayDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LanguageListener ) ((LanguageListener)listener).enterArrayDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LanguageListener ) ((LanguageListener)listener).exitArrayDeclaration(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LanguageVisitor ) return ((LanguageVisitor<? extends T>)visitor).visitArrayDeclaration(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ArrayDeclarationContext arrayDeclaration() throws RecognitionException {
		ArrayDeclarationContext _localctx = new ArrayDeclarationContext(_ctx, getState());
		enterRule(_localctx, 52, RULE_arrayDeclaration);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(352);
			type(0);
			setState(353);
			match(T__5);
			setState(354);
			intLiteral();
			setState(355);
			match(T__7);
			setState(356);
			varName();
			setState(357);
			match(T__0);
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

	public static class FixedpLiteralContext extends ParserRuleContext {
		public DecimalLiteralContext decimalLiteral() {
			return getRuleContext(DecimalLiteralContext.class,0);
		}
		public TerminalNode ACCESS() { return getToken(LanguageParser.ACCESS, 0); }
		public List<TerminalNode> NumberCharacter() { return getTokens(LanguageParser.NumberCharacter); }
		public TerminalNode NumberCharacter(int i) {
			return getToken(LanguageParser.NumberCharacter, i);
		}
		public FixedpLiteralContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fixedpLiteral; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LanguageListener ) ((LanguageListener)listener).enterFixedpLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LanguageListener ) ((LanguageListener)listener).exitFixedpLiteral(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LanguageVisitor ) return ((LanguageVisitor<? extends T>)visitor).visitFixedpLiteral(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FixedpLiteralContext fixedpLiteral() throws RecognitionException {
		FixedpLiteralContext _localctx = new FixedpLiteralContext(_ctx, getState());
		enterRule(_localctx, 54, RULE_fixedpLiteral);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(359);
			decimalLiteral();
			setState(360);
			match(ACCESS);
			setState(362); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(361);
					match(NumberCharacter);
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(364); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,31,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
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

	public static class IntLiteralContext extends ParserRuleContext {
		public DecimalLiteralContext decimal;
		public HexLiteralContext hex;
		public DecimalLiteralContext decimalLiteral() {
			return getRuleContext(DecimalLiteralContext.class,0);
		}
		public HexLiteralContext hexLiteral() {
			return getRuleContext(HexLiteralContext.class,0);
		}
		public IntLiteralContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_intLiteral; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LanguageListener ) ((LanguageListener)listener).enterIntLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LanguageListener ) ((LanguageListener)listener).exitIntLiteral(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LanguageVisitor ) return ((LanguageVisitor<? extends T>)visitor).visitIntLiteral(this);
			else return visitor.visitChildren(this);
		}
	}

	public final IntLiteralContext intLiteral() throws RecognitionException {
		IntLiteralContext _localctx = new IntLiteralContext(_ctx, getState());
		enterRule(_localctx, 56, RULE_intLiteral);
		try {
			setState(368);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,32,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(366);
				((IntLiteralContext)_localctx).decimal = decimalLiteral();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(367);
				((IntLiteralContext)_localctx).hex = hexLiteral();
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

	public static class DecimalLiteralContext extends ParserRuleContext {
		public TerminalNode SUB() { return getToken(LanguageParser.SUB, 0); }
		public List<TerminalNode> NumberCharacter() { return getTokens(LanguageParser.NumberCharacter); }
		public TerminalNode NumberCharacter(int i) {
			return getToken(LanguageParser.NumberCharacter, i);
		}
		public DecimalLiteralContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_decimalLiteral; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LanguageListener ) ((LanguageListener)listener).enterDecimalLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LanguageListener ) ((LanguageListener)listener).exitDecimalLiteral(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LanguageVisitor ) return ((LanguageVisitor<? extends T>)visitor).visitDecimalLiteral(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DecimalLiteralContext decimalLiteral() throws RecognitionException {
		DecimalLiteralContext _localctx = new DecimalLiteralContext(_ctx, getState());
		enterRule(_localctx, 58, RULE_decimalLiteral);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(371);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==SUB) {
				{
				setState(370);
				match(SUB);
				}
			}

			setState(374); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(373);
					match(NumberCharacter);
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(376); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,34,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
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

	public static class HexLiteralContext extends ParserRuleContext {
		public TerminalNode SUB() { return getToken(LanguageParser.SUB, 0); }
		public List<TerminalNode> HexCharacter() { return getTokens(LanguageParser.HexCharacter); }
		public TerminalNode HexCharacter(int i) {
			return getToken(LanguageParser.HexCharacter, i);
		}
		public HexLiteralContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_hexLiteral; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LanguageListener ) ((LanguageListener)listener).enterHexLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LanguageListener ) ((LanguageListener)listener).exitHexLiteral(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LanguageVisitor ) return ((LanguageVisitor<? extends T>)visitor).visitHexLiteral(this);
			else return visitor.visitChildren(this);
		}
	}

	public final HexLiteralContext hexLiteral() throws RecognitionException {
		HexLiteralContext _localctx = new HexLiteralContext(_ctx, getState());
		enterRule(_localctx, 60, RULE_hexLiteral);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(379);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==SUB) {
				{
				setState(378);
				match(SUB);
				}
			}

			setState(382); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(381);
					match(HexCharacter);
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(384); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,36,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
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

	public static class BoolLiteralContext extends ParserRuleContext {
		public TerminalNode BoolLiteral() { return getToken(LanguageParser.BoolLiteral, 0); }
		public BoolLiteralContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_boolLiteral; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LanguageListener ) ((LanguageListener)listener).enterBoolLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LanguageListener ) ((LanguageListener)listener).exitBoolLiteral(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LanguageVisitor ) return ((LanguageVisitor<? extends T>)visitor).visitBoolLiteral(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BoolLiteralContext boolLiteral() throws RecognitionException {
		BoolLiteralContext _localctx = new BoolLiteralContext(_ctx, getState());
		enterRule(_localctx, 62, RULE_boolLiteral);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(386);
			match(BoolLiteral);
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

	public static class VarNameContext extends ParserRuleContext {
		public List<TerminalNode> NameCharacterFirst() { return getTokens(LanguageParser.NameCharacterFirst); }
		public TerminalNode NameCharacterFirst(int i) {
			return getToken(LanguageParser.NameCharacterFirst, i);
		}
		public List<TerminalNode> NameCharacterRest() { return getTokens(LanguageParser.NameCharacterRest); }
		public TerminalNode NameCharacterRest(int i) {
			return getToken(LanguageParser.NameCharacterRest, i);
		}
		public List<TerminalNode> NumberCharacter() { return getTokens(LanguageParser.NumberCharacter); }
		public TerminalNode NumberCharacter(int i) {
			return getToken(LanguageParser.NumberCharacter, i);
		}
		public VarNameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_varName; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LanguageListener ) ((LanguageListener)listener).enterVarName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LanguageListener ) ((LanguageListener)listener).exitVarName(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LanguageVisitor ) return ((LanguageVisitor<? extends T>)visitor).visitVarName(this);
			else return visitor.visitChildren(this);
		}
	}

	public final VarNameContext varName() throws RecognitionException {
		VarNameContext _localctx = new VarNameContext(_ctx, getState());
		enterRule(_localctx, 64, RULE_varName);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(388);
			match(NameCharacterFirst);
			setState(392);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,37,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(389);
					_la = _input.LA(1);
					if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << NameCharacterFirst) | (1L << NameCharacterRest) | (1L << NumberCharacter))) != 0)) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					}
					} 
				}
				setState(394);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,37,_ctx);
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

	public static class FunctionNameContext extends ParserRuleContext {
		public List<TerminalNode> NameCharacterFirst() { return getTokens(LanguageParser.NameCharacterFirst); }
		public TerminalNode NameCharacterFirst(int i) {
			return getToken(LanguageParser.NameCharacterFirst, i);
		}
		public List<TerminalNode> NameCharacterRest() { return getTokens(LanguageParser.NameCharacterRest); }
		public TerminalNode NameCharacterRest(int i) {
			return getToken(LanguageParser.NameCharacterRest, i);
		}
		public List<TerminalNode> NumberCharacter() { return getTokens(LanguageParser.NumberCharacter); }
		public TerminalNode NumberCharacter(int i) {
			return getToken(LanguageParser.NumberCharacter, i);
		}
		public FunctionNameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_functionName; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LanguageListener ) ((LanguageListener)listener).enterFunctionName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LanguageListener ) ((LanguageListener)listener).exitFunctionName(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LanguageVisitor ) return ((LanguageVisitor<? extends T>)visitor).visitFunctionName(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FunctionNameContext functionName() throws RecognitionException {
		FunctionNameContext _localctx = new FunctionNameContext(_ctx, getState());
		enterRule(_localctx, 66, RULE_functionName);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(395);
			match(NameCharacterFirst);
			setState(399);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << NameCharacterFirst) | (1L << NameCharacterRest) | (1L << NumberCharacter))) != 0)) {
				{
				{
				setState(396);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << NameCharacterFirst) | (1L << NameCharacterRest) | (1L << NumberCharacter))) != 0)) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				}
				}
				setState(401);
				_errHandler.sync(this);
				_la = _input.LA(1);
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

	public static class SignalNameContext extends ParserRuleContext {
		public List<TerminalNode> NameCharacterFirst() { return getTokens(LanguageParser.NameCharacterFirst); }
		public TerminalNode NameCharacterFirst(int i) {
			return getToken(LanguageParser.NameCharacterFirst, i);
		}
		public List<TerminalNode> NameCharacterRest() { return getTokens(LanguageParser.NameCharacterRest); }
		public TerminalNode NameCharacterRest(int i) {
			return getToken(LanguageParser.NameCharacterRest, i);
		}
		public List<TerminalNode> NumberCharacter() { return getTokens(LanguageParser.NumberCharacter); }
		public TerminalNode NumberCharacter(int i) {
			return getToken(LanguageParser.NumberCharacter, i);
		}
		public SignalNameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_signalName; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LanguageListener ) ((LanguageListener)listener).enterSignalName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LanguageListener ) ((LanguageListener)listener).exitSignalName(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LanguageVisitor ) return ((LanguageVisitor<? extends T>)visitor).visitSignalName(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SignalNameContext signalName() throws RecognitionException {
		SignalNameContext _localctx = new SignalNameContext(_ctx, getState());
		enterRule(_localctx, 68, RULE_signalName);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(403); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(402);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << NameCharacterFirst) | (1L << NameCharacterRest) | (1L << NumberCharacter))) != 0)) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				}
				}
				setState(405); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << NameCharacterFirst) | (1L << NameCharacterRest) | (1L << NumberCharacter))) != 0) );
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
		case 20:
			return expr_sempred((ExprContext)_localctx, predIndex);
		case 23:
			return boolExpr_sempred((BoolExprContext)_localctx, predIndex);
		case 24:
			return type_sempred((TypeContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean expr_sempred(ExprContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 7);
		case 1:
			return precpred(_ctx, 6);
		case 2:
			return precpred(_ctx, 5);
		case 3:
			return precpred(_ctx, 4);
		case 4:
			return precpred(_ctx, 3);
		case 5:
			return precpred(_ctx, 2);
		case 6:
			return precpred(_ctx, 10);
		case 7:
			return precpred(_ctx, 8);
		}
		return true;
	}
	private boolean boolExpr_sempred(BoolExprContext _localctx, int predIndex) {
		switch (predIndex) {
		case 8:
			return precpred(_ctx, 2);
		}
		return true;
	}
	private boolean type_sempred(TypeContext _localctx, int predIndex) {
		switch (predIndex) {
		case 9:
			return precpred(_ctx, 1);
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\63\u019a\4\2\t\2"+
		"\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\3\2\7\2J\n\2\f\2\16\2M\13\2\3\2\6\2P\n\2\r\2\16"+
		"\2Q\3\3\3\3\3\3\3\3\3\3\3\3\5\3Z\n\3\3\4\3\4\5\4^\n\4\3\4\3\4\3\4\3\4"+
		"\3\4\3\4\3\4\3\4\3\4\3\4\5\4j\n\4\3\5\3\5\3\5\3\5\3\5\7\5q\n\5\f\5\16"+
		"\5t\13\5\3\5\3\5\5\5x\n\5\3\6\3\6\3\6\3\6\3\6\5\6\177\n\6\3\7\3\7\3\7"+
		"\7\7\u0084\n\7\f\7\16\7\u0087\13\7\5\7\u0089\n\7\3\b\3\b\3\b\3\b\3\b\3"+
		"\b\3\b\5\b\u0092\n\b\3\t\3\t\3\t\3\t\7\t\u0098\n\t\f\t\16\t\u009b\13\t"+
		"\3\t\3\t\3\n\3\n\6\n\u00a1\n\n\r\n\16\n\u00a2\3\n\3\n\3\13\3\13\3\13\3"+
		"\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\5\13\u00b6"+
		"\n\13\3\f\3\f\3\f\3\f\3\f\5\f\u00bd\n\f\3\f\3\f\3\f\3\r\3\r\3\r\3\r\3"+
		"\r\3\r\3\r\3\16\3\16\3\16\3\16\3\16\3\16\3\16\5\16\u00d0\n\16\3\17\3\17"+
		"\3\20\3\20\3\21\3\21\3\21\3\21\3\21\3\21\3\22\3\22\3\23\3\23\3\23\3\24"+
		"\3\24\3\24\3\24\3\24\3\25\3\25\3\25\7\25\u00e9\n\25\f\25\16\25\u00ec\13"+
		"\25\5\25\u00ee\n\25\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26"+
		"\3\26\3\26\5\26\u00fc\n\26\3\26\3\26\3\26\3\26\5\26\u0102\n\26\3\26\3"+
		"\26\3\26\3\26\5\26\u0108\n\26\3\26\3\26\3\26\3\26\5\26\u010e\n\26\3\26"+
		"\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26"+
		"\3\26\3\26\3\26\7\26\u0122\n\26\f\26\16\26\u0125\13\26\3\27\3\27\3\27"+
		"\5\27\u012a\n\27\3\30\3\30\3\30\6\30\u012f\n\30\r\30\16\30\u0130\3\31"+
		"\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\5\31\u013e\n\31\3\31"+
		"\3\31\3\31\7\31\u0143\n\31\f\31\16\31\u0146\13\31\3\32\3\32\3\32\3\32"+
		"\3\32\3\32\3\32\3\32\3\32\3\32\3\32\5\32\u0153\n\32\3\32\3\32\7\32\u0157"+
		"\n\32\f\32\16\32\u015a\13\32\3\33\3\33\3\33\6\33\u015f\n\33\r\33\16\33"+
		"\u0160\3\34\3\34\3\34\3\34\3\34\3\34\3\34\3\35\3\35\3\35\6\35\u016d\n"+
		"\35\r\35\16\35\u016e\3\36\3\36\5\36\u0173\n\36\3\37\5\37\u0176\n\37\3"+
		"\37\6\37\u0179\n\37\r\37\16\37\u017a\3 \5 \u017e\n \3 \6 \u0181\n \r "+
		"\16 \u0182\3!\3!\3\"\3\"\7\"\u0189\n\"\f\"\16\"\u018c\13\"\3#\3#\7#\u0190"+
		"\n#\f#\16#\u0193\13#\3$\6$\u0196\n$\r$\16$\u0197\3$\2\5*\60\62%\2\4\6"+
		"\b\n\f\16\20\22\24\26\30\32\34\36 \"$&(*,.\60\62\64\668:<>@BDF\2\5\3\2"+
		"-\62\3\2)+\3\2\30\32\2\u01b1\2K\3\2\2\2\4Y\3\2\2\2\6[\3\2\2\2\bw\3\2\2"+
		"\2\n~\3\2\2\2\f\u0088\3\2\2\2\16\u008a\3\2\2\2\20\u0093\3\2\2\2\22\u009e"+
		"\3\2\2\2\24\u00b5\3\2\2\2\26\u00b7\3\2\2\2\30\u00c1\3\2\2\2\32\u00c8\3"+
		"\2\2\2\34\u00d1\3\2\2\2\36\u00d3\3\2\2\2 \u00d5\3\2\2\2\"\u00db\3\2\2"+
		"\2$\u00dd\3\2\2\2&\u00e0\3\2\2\2(\u00ed\3\2\2\2*\u00fb\3\2\2\2,\u0129"+
		"\3\2\2\2.\u012b\3\2\2\2\60\u013d\3\2\2\2\62\u0152\3\2\2\2\64\u015b\3\2"+
		"\2\2\66\u0162\3\2\2\28\u0169\3\2\2\2:\u0172\3\2\2\2<\u0175\3\2\2\2>\u017d"+
		"\3\2\2\2@\u0184\3\2\2\2B\u0186\3\2\2\2D\u018d\3\2\2\2F\u0195\3\2\2\2H"+
		"J\5\66\34\2IH\3\2\2\2JM\3\2\2\2KI\3\2\2\2KL\3\2\2\2LO\3\2\2\2MK\3\2\2"+
		"\2NP\5\4\3\2ON\3\2\2\2PQ\3\2\2\2QO\3\2\2\2QR\3\2\2\2R\3\3\2\2\2ST\5\6"+
		"\4\2TU\5\22\n\2UZ\3\2\2\2VW\5\6\4\2WX\7\3\2\2XZ\3\2\2\2YS\3\2\2\2YV\3"+
		"\2\2\2Z\5\3\2\2\2[]\7\4\2\2\\^\5\b\5\2]\\\3\2\2\2]^\3\2\2\2^_\3\2\2\2"+
		"_`\5D#\2`a\7\5\2\2ab\5\f\7\2bc\7\6\2\2cd\7\7\2\2di\5\62\32\2ef\7-\2\2"+
		"fg\5\20\t\2gh\7.\2\2hj\3\2\2\2ie\3\2\2\2ij\3\2\2\2j\7\3\2\2\2kx\3\2\2"+
		"\2lm\7\b\2\2mr\5\n\6\2no\7\t\2\2oq\5\n\6\2pn\3\2\2\2qt\3\2\2\2rp\3\2\2"+
		"\2rs\3\2\2\2su\3\2\2\2tr\3\2\2\2uv\7\n\2\2vx\3\2\2\2wk\3\2\2\2wl\3\2\2"+
		"\2x\t\3\2\2\2y\177\7\13\2\2z\177\7\f\2\2{|\7\r\2\2|}\7\16\2\2}\177\5:"+
		"\36\2~y\3\2\2\2~z\3\2\2\2~{\3\2\2\2\177\13\3\2\2\2\u0080\u0085\5\16\b"+
		"\2\u0081\u0082\7\t\2\2\u0082\u0084\5\16\b\2\u0083\u0081\3\2\2\2\u0084"+
		"\u0087\3\2\2\2\u0085\u0083\3\2\2\2\u0085\u0086\3\2\2\2\u0086\u0089\3\2"+
		"\2\2\u0087\u0085\3\2\2\2\u0088\u0080\3\2\2\2\u0088\u0089\3\2\2\2\u0089"+
		"\r\3\2\2\2\u008a\u008b\5B\"\2\u008b\u008c\7\17\2\2\u008c\u0091\5\62\32"+
		"\2\u008d\u008e\7-\2\2\u008e\u008f\5\20\t\2\u008f\u0090\7.\2\2\u0090\u0092"+
		"\3\2\2\2\u0091\u008d\3\2\2\2\u0091\u0092\3\2\2\2\u0092\17\3\2\2\2\u0093"+
		"\u0094\7-\2\2\u0094\u0099\5F$\2\u0095\u0096\7\t\2\2\u0096\u0098\5F$\2"+
		"\u0097\u0095\3\2\2\2\u0098\u009b\3\2\2\2\u0099\u0097\3\2\2\2\u0099\u009a"+
		"\3\2\2\2\u009a\u009c\3\2\2\2\u009b\u0099\3\2\2\2\u009c\u009d\7.\2\2\u009d"+
		"\21\3\2\2\2\u009e\u00a0\7\20\2\2\u009f\u00a1\5\24\13\2\u00a0\u009f\3\2"+
		"\2\2\u00a1\u00a2\3\2\2\2\u00a2\u00a0\3\2\2\2\u00a2\u00a3\3\2\2\2\u00a3"+
		"\u00a4\3\2\2\2\u00a4\u00a5\7\21\2\2\u00a5\23\3\2\2\2\u00a6\u00b6\5\22"+
		"\n\2\u00a7\u00a8\5\26\f\2\u00a8\u00a9\7\3\2\2\u00a9\u00b6\3\2\2\2\u00aa"+
		"\u00ab\5\30\r\2\u00ab\u00ac\7\3\2\2\u00ac\u00b6\3\2\2\2\u00ad\u00b6\5"+
		"\32\16\2\u00ae\u00b6\5 \21\2\u00af\u00b0\5$\23\2\u00b0\u00b1\7\3\2\2\u00b1"+
		"\u00b6\3\2\2\2\u00b2\u00b3\5&\24\2\u00b3\u00b4\7\3\2\2\u00b4\u00b6\3\2"+
		"\2\2\u00b5\u00a6\3\2\2\2\u00b5\u00a7\3\2\2\2\u00b5\u00aa\3\2\2\2\u00b5"+
		"\u00ad\3\2\2\2\u00b5\u00ae\3\2\2\2\u00b5\u00af\3\2\2\2\u00b5\u00b2\3\2"+
		"\2\2\u00b6\25\3\2\2\2\u00b7\u00bc\5B\"\2\u00b8\u00b9\7-\2\2\u00b9\u00ba"+
		"\5:\36\2\u00ba\u00bb\7.\2\2\u00bb\u00bd\3\2\2\2\u00bc\u00b8\3\2\2\2\u00bc"+
		"\u00bd\3\2\2\2\u00bd\u00be\3\2\2\2\u00be\u00bf\7\16\2\2\u00bf\u00c0\5"+
		"*\26\2\u00c0\27\3\2\2\2\u00c1\u00c2\5*\26\2\u00c2\u00c3\7\b\2\2\u00c3"+
		"\u00c4\5*\26\2\u00c4\u00c5\7\n\2\2\u00c5\u00c6\7\16\2\2\u00c6\u00c7\5"+
		"*\26\2\u00c7\31\3\2\2\2\u00c8\u00c9\7\22\2\2\u00c9\u00ca\7\5\2\2\u00ca"+
		"\u00cb\5\60\31\2\u00cb\u00cc\7\6\2\2\u00cc\u00cf\5\36\20\2\u00cd\u00ce"+
		"\7\23\2\2\u00ce\u00d0\5\34\17\2\u00cf\u00cd\3\2\2\2\u00cf\u00d0\3\2\2"+
		"\2\u00d0\33\3\2\2\2\u00d1\u00d2\5\24\13\2\u00d2\35\3\2\2\2\u00d3\u00d4"+
		"\5\24\13\2\u00d4\37\3\2\2\2\u00d5\u00d6\7\24\2\2\u00d6\u00d7\7\5\2\2\u00d7"+
		"\u00d8\5\60\31\2\u00d8\u00d9\7\6\2\2\u00d9\u00da\5\"\22\2\u00da!\3\2\2"+
		"\2\u00db\u00dc\5\24\13\2\u00dc#\3\2\2\2\u00dd\u00de\7\25\2\2\u00de\u00df"+
		"\5*\26\2\u00df%\3\2\2\2\u00e0\u00e1\5D#\2\u00e1\u00e2\7\5\2\2\u00e2\u00e3"+
		"\5(\25\2\u00e3\u00e4\7\6\2\2\u00e4\'\3\2\2\2\u00e5\u00ea\5*\26\2\u00e6"+
		"\u00e7\7\t\2\2\u00e7\u00e9\5*\26\2\u00e8\u00e6\3\2\2\2\u00e9\u00ec\3\2"+
		"\2\2\u00ea\u00e8\3\2\2\2\u00ea\u00eb\3\2\2\2\u00eb\u00ee\3\2\2\2\u00ec"+
		"\u00ea\3\2\2\2\u00ed\u00e5\3\2\2\2\u00ed\u00ee\3\2\2\2\u00ee)\3\2\2\2"+
		"\u00ef\u00f0\b\26\1\2\u00f0\u00f1\7\5\2\2\u00f1\u00f2\5*\26\2\u00f2\u00f3"+
		"\7\6\2\2\u00f3\u00fc\3\2\2\2\u00f4\u00f5\7\5\2\2\u00f5\u00f6\5.\30\2\u00f6"+
		"\u00f7\7\6\2\2\u00f7\u00fc\3\2\2\2\u00f8\u00fc\5B\"\2\u00f9\u00fc\5&\24"+
		"\2\u00fa\u00fc\5,\27\2\u00fb\u00ef\3\2\2\2\u00fb\u00f4\3\2\2\2\u00fb\u00f8"+
		"\3\2\2\2\u00fb\u00f9\3\2\2\2\u00fb\u00fa\3\2\2\2\u00fc\u0123\3\2\2\2\u00fd"+
		"\u0101\f\t\2\2\u00fe\u0102\7 \2\2\u00ff\u0102\7!\2\2\u0100\u0102\7\"\2"+
		"\2\u0101\u00fe\3\2\2\2\u0101\u00ff\3\2\2\2\u0101\u0100\3\2\2\2\u0102\u0103"+
		"\3\2\2\2\u0103\u0122\5*\26\n\u0104\u0107\f\b\2\2\u0105\u0108\7\36\2\2"+
		"\u0106\u0108\7\37\2\2\u0107\u0105\3\2\2\2\u0107\u0106\3\2\2\2\u0108\u0109"+
		"\3\2\2\2\u0109\u0122\5*\26\t\u010a\u010d\f\7\2\2\u010b\u010e\7#\2\2\u010c"+
		"\u010e\7$\2\2\u010d\u010b\3\2\2\2\u010d\u010c\3\2\2\2\u010e\u010f\3\2"+
		"\2\2\u010f\u0122\5*\26\b\u0110\u0111\f\6\2\2\u0111\u0112\7%\2\2\u0112"+
		"\u0122\5*\26\7\u0113\u0114\f\5\2\2\u0114\u0115\7&\2\2\u0115\u0122\5*\26"+
		"\6\u0116\u0117\f\4\2\2\u0117\u0118\7\'\2\2\u0118\u0122\5*\26\5\u0119\u011a"+
		"\f\f\2\2\u011a\u011b\7\b\2\2\u011b\u011c\5*\26\2\u011c\u011d\7\n\2\2\u011d"+
		"\u0122\3\2\2\2\u011e\u011f\f\n\2\2\u011f\u0120\7(\2\2\u0120\u0122\5:\36"+
		"\2\u0121\u00fd\3\2\2\2\u0121\u0104\3\2\2\2\u0121\u010a\3\2\2\2\u0121\u0110"+
		"\3\2\2\2\u0121\u0113\3\2\2\2\u0121\u0116\3\2\2\2\u0121\u0119\3\2\2\2\u0121"+
		"\u011e\3\2\2\2\u0122\u0125\3\2\2\2\u0123\u0121\3\2\2\2\u0123\u0124\3\2"+
		"\2\2\u0124+\3\2\2\2\u0125\u0123\3\2\2\2\u0126\u012a\58\35\2\u0127\u012a"+
		"\5:\36\2\u0128\u012a\5@!\2\u0129\u0126\3\2\2\2\u0129\u0127\3\2\2\2\u0129"+
		"\u0128\3\2\2\2\u012a-\3\2\2\2\u012b\u012e\5*\26\2\u012c\u012d\7\t\2\2"+
		"\u012d\u012f\5*\26\2\u012e\u012c\3\2\2\2\u012f\u0130\3\2\2\2\u0130\u012e"+
		"\3\2\2\2\u0130\u0131\3\2\2\2\u0131/\3\2\2\2\u0132\u0133\b\31\1\2\u0133"+
		"\u0134\7\5\2\2\u0134\u0135\5\60\31\2\u0135\u0136\7\6\2\2\u0136\u013e\3"+
		"\2\2\2\u0137\u0138\7,\2\2\u0138\u013e\5\60\31\5\u0139\u013a\5*\26\2\u013a"+
		"\u013b\t\2\2\2\u013b\u013c\5*\26\2\u013c\u013e\3\2\2\2\u013d\u0132\3\2"+
		"\2\2\u013d\u0137\3\2\2\2\u013d\u0139\3\2\2\2\u013e\u0144\3\2\2\2\u013f"+
		"\u0140\f\4\2\2\u0140\u0141\t\3\2\2\u0141\u0143\5\60\31\5\u0142\u013f\3"+
		"\2\2\2\u0143\u0146\3\2\2\2\u0144\u0142\3\2\2\2\u0144\u0145\3\2\2\2\u0145"+
		"\61\3\2\2\2\u0146\u0144\3\2\2\2\u0147\u0148\b\32\1\2\u0148\u0153\7\34"+
		"\2\2\u0149\u014a\7\26\2\2\u014a\u014b\7-\2\2\u014b\u014c\5:\36\2\u014c"+
		"\u014d\7.\2\2\u014d\u0153\3\2\2\2\u014e\u014f\7\5\2\2\u014f\u0150\5\64"+
		"\33\2\u0150\u0151\7\6\2\2\u0151\u0153\3\2\2\2\u0152\u0147\3\2\2\2\u0152"+
		"\u0149\3\2\2\2\u0152\u014e\3\2\2\2\u0153\u0158\3\2\2\2\u0154\u0155\f\3"+
		"\2\2\u0155\u0157\7\27\2\2\u0156\u0154\3\2\2\2\u0157\u015a\3\2\2\2\u0158"+
		"\u0156\3\2\2\2\u0158\u0159\3\2\2\2\u0159\63\3\2\2\2\u015a\u0158\3\2\2"+
		"\2\u015b\u015e\5\62\32\2\u015c\u015d\7\t\2\2\u015d\u015f\5\62\32\2\u015e"+
		"\u015c\3\2\2\2\u015f\u0160\3\2\2\2\u0160\u015e\3\2\2\2\u0160\u0161\3\2"+
		"\2\2\u0161\65\3\2\2\2\u0162\u0163\5\62\32\2\u0163\u0164\7\b\2\2\u0164"+
		"\u0165\5:\36\2\u0165\u0166\7\n\2\2\u0166\u0167\5B\"\2\u0167\u0168\7\3"+
		"\2\2\u0168\67\3\2\2\2\u0169\u016a\5<\37\2\u016a\u016c\7(\2\2\u016b\u016d"+
		"\7\32\2\2\u016c\u016b\3\2\2\2\u016d\u016e\3\2\2\2\u016e\u016c\3\2\2\2"+
		"\u016e\u016f\3\2\2\2\u016f9\3\2\2\2\u0170\u0173\5<\37\2\u0171\u0173\5"+
		"> \2\u0172\u0170\3\2\2\2\u0172\u0171\3\2\2\2\u0173;\3\2\2\2\u0174\u0176"+
		"\7\37\2\2\u0175\u0174\3\2\2\2\u0175\u0176\3\2\2\2\u0176\u0178\3\2\2\2"+
		"\u0177\u0179\7\32\2\2\u0178\u0177\3\2\2\2\u0179\u017a\3\2\2\2\u017a\u0178"+
		"\3\2\2\2\u017a\u017b\3\2\2\2\u017b=\3\2\2\2\u017c\u017e\7\37\2\2\u017d"+
		"\u017c\3\2\2\2\u017d\u017e\3\2\2\2\u017e\u0180\3\2\2\2\u017f\u0181\7\33"+
		"\2\2\u0180\u017f\3\2\2\2\u0181\u0182\3\2\2\2\u0182\u0180\3\2\2\2\u0182"+
		"\u0183\3\2\2\2\u0183?\3\2\2\2\u0184\u0185\7\35\2\2\u0185A\3\2\2\2\u0186"+
		"\u018a\7\30\2\2\u0187\u0189\t\4\2\2\u0188\u0187\3\2\2\2\u0189\u018c\3"+
		"\2\2\2\u018a\u0188\3\2\2\2\u018a\u018b\3\2\2\2\u018bC\3\2\2\2\u018c\u018a"+
		"\3\2\2\2\u018d\u0191\7\30\2\2\u018e\u0190\t\4\2\2\u018f\u018e\3\2\2\2"+
		"\u0190\u0193\3\2\2\2\u0191\u018f\3\2\2\2\u0191\u0192\3\2\2\2\u0192E\3"+
		"\2\2\2\u0193\u0191\3\2\2\2\u0194\u0196\t\4\2\2\u0195\u0194\3\2\2\2\u0196"+
		"\u0197\3\2\2\2\u0197\u0195\3\2\2\2\u0197\u0198\3\2\2\2\u0198G\3\2\2\2"+
		"*KQY]irw~\u0085\u0088\u0091\u0099\u00a2\u00b5\u00bc\u00cf\u00ea\u00ed"+
		"\u00fb\u0101\u0107\u010d\u0121\u0123\u0129\u0130\u013d\u0144\u0152\u0158"+
		"\u0160\u016e\u0172\u0175\u017a\u017d\u0182\u018a\u0191\u0197";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}