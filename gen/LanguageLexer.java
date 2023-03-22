// Generated from C:/Users/jonas/Documents/Java/FactorioSimulator/src/main/antlr4/me/joba/factorio/lang\Language.g4 by ANTLR 4.9.2
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class LanguageLexer extends Lexer {
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
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "T__6", "T__7", "T__8", 
			"T__9", "T__10", "T__11", "T__12", "T__13", "T__14", "T__15", "T__16", 
			"T__17", "T__18", "T__19", "T__20", "NameCharacterFirst", "NameCharacterRest", 
			"NumberCharacter", "HexCharacter", "TypeName", "BoolLiteral", "ADD", 
			"SUB", "MUL", "DIV", "MOD", "LSH", "RSH", "BAND", "BOR", "BXOR", "ACCESS", 
			"AND", "OR", "XOR", "NOT", "LT", "GT", "LEQ", "GEQ", "EQ", "NEQ", "WS"
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


	public LanguageLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "Language.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\63\u0119\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31"+
		"\t\31\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t"+
		" \4!\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t"+
		"+\4,\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\3\2\3\2\3\3\3"+
		"\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\4\3\4\3\5\3\5\3\6\3\6\3\6\3\7\3\7"+
		"\3\b\3\b\3\t\3\t\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\13\3\13\3\13"+
		"\3\13\3\13\3\13\3\13\3\f\3\f\3\f\3\f\3\f\3\f\3\r\3\r\3\16\3\16\3\17\3"+
		"\17\3\20\3\20\3\21\3\21\3\21\3\22\3\22\3\22\3\22\3\22\3\23\3\23\3\23\3"+
		"\23\3\23\3\23\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\25\3\25\3\25\3"+
		"\25\3\25\3\25\3\25\3\26\3\26\3\26\3\27\3\27\3\30\3\30\3\31\3\31\3\32\3"+
		"\32\3\33\3\33\3\33\3\33\3\33\3\33\3\33\3\33\3\33\3\33\3\33\3\33\3\33\3"+
		"\33\5\33\u00d4\n\33\3\34\3\34\3\34\3\34\3\34\3\34\3\34\3\34\3\34\5\34"+
		"\u00df\n\34\3\35\3\35\3\36\3\36\3\37\3\37\3 \3 \3!\3!\3\"\3\"\3\"\3#\3"+
		"#\3#\3$\3$\3%\3%\3&\3&\3\'\3\'\3(\3(\3(\3)\3)\3)\3*\3*\3+\3+\3,\3,\3-"+
		"\3-\3.\3.\3.\3/\3/\3/\3\60\3\60\3\60\3\61\3\61\3\61\3\62\6\62\u0114\n"+
		"\62\r\62\16\62\u0115\3\62\3\62\2\2\63\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21"+
		"\n\23\13\25\f\27\r\31\16\33\17\35\20\37\21!\22#\23%\24\'\25)\26+\27-\30"+
		"/\31\61\32\63\33\65\34\67\359\36;\37= ?!A\"C#E$G%I&K\'M(O)Q*S+U,W-Y.["+
		"/]\60_\61a\62c\63\3\2\7\4\2C\\c|\4\2C\\aa\3\2\62;\5\2\62;CHch\5\2\13\f"+
		"\17\17\"\"\2\u011c\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13"+
		"\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2"+
		"\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2"+
		"!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3"+
		"\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2\2\65\3\2\2\2\2\67\3\2\2\2"+
		"\29\3\2\2\2\2;\3\2\2\2\2=\3\2\2\2\2?\3\2\2\2\2A\3\2\2\2\2C\3\2\2\2\2E"+
		"\3\2\2\2\2G\3\2\2\2\2I\3\2\2\2\2K\3\2\2\2\2M\3\2\2\2\2O\3\2\2\2\2Q\3\2"+
		"\2\2\2S\3\2\2\2\2U\3\2\2\2\2W\3\2\2\2\2Y\3\2\2\2\2[\3\2\2\2\2]\3\2\2\2"+
		"\2_\3\2\2\2\2a\3\2\2\2\2c\3\2\2\2\3e\3\2\2\2\5g\3\2\2\2\7q\3\2\2\2\ts"+
		"\3\2\2\2\13u\3\2\2\2\rx\3\2\2\2\17z\3\2\2\2\21|\3\2\2\2\23~\3\2\2\2\25"+
		"\u0088\3\2\2\2\27\u008f\3\2\2\2\31\u0095\3\2\2\2\33\u0097\3\2\2\2\35\u0099"+
		"\3\2\2\2\37\u009b\3\2\2\2!\u009d\3\2\2\2#\u00a0\3\2\2\2%\u00a5\3\2\2\2"+
		"\'\u00ab\3\2\2\2)\u00b3\3\2\2\2+\u00ba\3\2\2\2-\u00bd\3\2\2\2/\u00bf\3"+
		"\2\2\2\61\u00c1\3\2\2\2\63\u00c3\3\2\2\2\65\u00d3\3\2\2\2\67\u00de\3\2"+
		"\2\29\u00e0\3\2\2\2;\u00e2\3\2\2\2=\u00e4\3\2\2\2?\u00e6\3\2\2\2A\u00e8"+
		"\3\2\2\2C\u00ea\3\2\2\2E\u00ed\3\2\2\2G\u00f0\3\2\2\2I\u00f2\3\2\2\2K"+
		"\u00f4\3\2\2\2M\u00f6\3\2\2\2O\u00f8\3\2\2\2Q\u00fb\3\2\2\2S\u00fe\3\2"+
		"\2\2U\u0100\3\2\2\2W\u0102\3\2\2\2Y\u0104\3\2\2\2[\u0106\3\2\2\2]\u0109"+
		"\3\2\2\2_\u010c\3\2\2\2a\u010f\3\2\2\2c\u0113\3\2\2\2ef\7=\2\2f\4\3\2"+
		"\2\2gh\7h\2\2hi\7w\2\2ij\7p\2\2jk\7e\2\2kl\7v\2\2lm\7k\2\2mn\7q\2\2no"+
		"\7p\2\2op\7\"\2\2p\6\3\2\2\2qr\7*\2\2r\b\3\2\2\2st\7+\2\2t\n\3\2\2\2u"+
		"v\7/\2\2vw\7@\2\2w\f\3\2\2\2xy\7]\2\2y\16\3\2\2\2z{\7.\2\2{\20\3\2\2\2"+
		"|}\7_\2\2}\22\3\2\2\2~\177\7r\2\2\177\u0080\7k\2\2\u0080\u0081\7r\2\2"+
		"\u0081\u0082\7g\2\2\u0082\u0083\7n\2\2\u0083\u0084\7k\2\2\u0084\u0085"+
		"\7p\2\2\u0085\u0086\7g\2\2\u0086\u0087\7f\2\2\u0087\24\3\2\2\2\u0088\u0089"+
		"\7p\2\2\u0089\u008a\7c\2\2\u008a\u008b\7v\2\2\u008b\u008c\7k\2\2\u008c"+
		"\u008d\7x\2\2\u008d\u008e\7g\2\2\u008e\26\3\2\2\2\u008f\u0090\7f\2\2\u0090"+
		"\u0091\7g\2\2\u0091\u0092\7n\2\2\u0092\u0093\7c\2\2\u0093\u0094\7{\2\2"+
		"\u0094\30\3\2\2\2\u0095\u0096\7?\2\2\u0096\32\3\2\2\2\u0097\u0098\7<\2"+
		"\2\u0098\34\3\2\2\2\u0099\u009a\7}\2\2\u009a\36\3\2\2\2\u009b\u009c\7"+
		"\177\2\2\u009c \3\2\2\2\u009d\u009e\7k\2\2\u009e\u009f\7h\2\2\u009f\""+
		"\3\2\2\2\u00a0\u00a1\7g\2\2\u00a1\u00a2\7n\2\2\u00a2\u00a3\7u\2\2\u00a3"+
		"\u00a4\7g\2\2\u00a4$\3\2\2\2\u00a5\u00a6\7y\2\2\u00a6\u00a7\7j\2\2\u00a7"+
		"\u00a8\7k\2\2\u00a8\u00a9\7n\2\2\u00a9\u00aa\7g\2\2\u00aa&\3\2\2\2\u00ab"+
		"\u00ac\7t\2\2\u00ac\u00ad\7g\2\2\u00ad\u00ae\7v\2\2\u00ae\u00af\7w\2\2"+
		"\u00af\u00b0\7t\2\2\u00b0\u00b1\7p\2\2\u00b1\u00b2\7\"\2\2\u00b2(\3\2"+
		"\2\2\u00b3\u00b4\7h\2\2\u00b4\u00b5\7k\2\2\u00b5\u00b6\7z\2\2\u00b6\u00b7"+
		"\7g\2\2\u00b7\u00b8\7f\2\2\u00b8\u00b9\7r\2\2\u00b9*\3\2\2\2\u00ba\u00bb"+
		"\7]\2\2\u00bb\u00bc\7_\2\2\u00bc,\3\2\2\2\u00bd\u00be\t\2\2\2\u00be.\3"+
		"\2\2\2\u00bf\u00c0\t\3\2\2\u00c0\60\3\2\2\2\u00c1\u00c2\t\4\2\2\u00c2"+
		"\62\3\2\2\2\u00c3\u00c4\t\5\2\2\u00c4\64\3\2\2\2\u00c5\u00c6\7k\2\2\u00c6"+
		"\u00c7\7p\2\2\u00c7\u00d4\7v\2\2\u00c8\u00c9\7d\2\2\u00c9\u00ca\7q\2\2"+
		"\u00ca\u00cb\7q\2\2\u00cb\u00cc\7n\2\2\u00cc\u00cd\7g\2\2\u00cd\u00ce"+
		"\7c\2\2\u00ce\u00d4\7p\2\2\u00cf\u00d0\7x\2\2\u00d0\u00d1\7q\2\2\u00d1"+
		"\u00d2\7k\2\2\u00d2\u00d4\7f\2\2\u00d3\u00c5\3\2\2\2\u00d3\u00c8\3\2\2"+
		"\2\u00d3\u00cf\3\2\2\2\u00d4\66\3\2\2\2\u00d5\u00d6\7v\2\2\u00d6\u00d7"+
		"\7t\2\2\u00d7\u00d8\7w\2\2\u00d8\u00df\7g\2\2\u00d9\u00da\7h\2\2\u00da"+
		"\u00db\7c\2\2\u00db\u00dc\7n\2\2\u00dc\u00dd\7u\2\2\u00dd\u00df\7g\2\2"+
		"\u00de\u00d5\3\2\2\2\u00de\u00d9\3\2\2\2\u00df8\3\2\2\2\u00e0\u00e1\7"+
		"-\2\2\u00e1:\3\2\2\2\u00e2\u00e3\7/\2\2\u00e3<\3\2\2\2\u00e4\u00e5\7,"+
		"\2\2\u00e5>\3\2\2\2\u00e6\u00e7\7\61\2\2\u00e7@\3\2\2\2\u00e8\u00e9\7"+
		"\'\2\2\u00e9B\3\2\2\2\u00ea\u00eb\7@\2\2\u00eb\u00ec\7@\2\2\u00ecD\3\2"+
		"\2\2\u00ed\u00ee\7>\2\2\u00ee\u00ef\7>\2\2\u00efF\3\2\2\2\u00f0\u00f1"+
		"\7(\2\2\u00f1H\3\2\2\2\u00f2\u00f3\7~\2\2\u00f3J\3\2\2\2\u00f4\u00f5\7"+
		"`\2\2\u00f5L\3\2\2\2\u00f6\u00f7\7\60\2\2\u00f7N\3\2\2\2\u00f8\u00f9\7"+
		"(\2\2\u00f9\u00fa\7(\2\2\u00faP\3\2\2\2\u00fb\u00fc\7~\2\2\u00fc\u00fd"+
		"\7~\2\2\u00fdR\3\2\2\2\u00fe\u00ff\7`\2\2\u00ffT\3\2\2\2\u0100\u0101\7"+
		"#\2\2\u0101V\3\2\2\2\u0102\u0103\7>\2\2\u0103X\3\2\2\2\u0104\u0105\7@"+
		"\2\2\u0105Z\3\2\2\2\u0106\u0107\7>\2\2\u0107\u0108\7?\2\2\u0108\\\3\2"+
		"\2\2\u0109\u010a\7@\2\2\u010a\u010b\7?\2\2\u010b^\3\2\2\2\u010c\u010d"+
		"\7?\2\2\u010d\u010e\7?\2\2\u010e`\3\2\2\2\u010f\u0110\7#\2\2\u0110\u0111"+
		"\7?\2\2\u0111b\3\2\2\2\u0112\u0114\t\6\2\2\u0113\u0112\3\2\2\2\u0114\u0115"+
		"\3\2\2\2\u0115\u0113\3\2\2\2\u0115\u0116\3\2\2\2\u0116\u0117\3\2\2\2\u0117"+
		"\u0118\b\62\2\2\u0118d\3\2\2\2\6\2\u00d3\u00de\u0115\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}