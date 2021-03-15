// Generated from me\joba\factorio\lang\Language.g4 by ANTLR 4.3
package me.joba.factorio.lang;
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
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] tokenNames = {
		"'\\u0000'", "'\\u0001'", "'\\u0002'", "'\\u0003'", "'\\u0004'", "'\\u0005'", 
		"'\\u0006'", "'\\u0007'", "'\b'", "'\t'", "'\n'", "'\\u000B'", "'\f'", 
		"'\r'", "'\\u000E'", "'\\u000F'", "'\\u0010'", "'\\u0011'", "'\\u0012'", 
		"'\\u0013'", "'\\u0014'", "'\\u0015'", "'\\u0016'", "'\\u0017'", "'\\u0018'", 
		"'\\u0019'", "'\\u001A'", "'\\u001B'", "'\\u001C'", "'\\u001D'", "'\\u001E'", 
		"'\\u001F'", "' '", "'!'", "'\"'"
	};
	public static final String[] ruleNames = {
		"T__12", "T__11", "T__10", "T__9", "T__8", "T__7", "T__6", "T__5", "T__4", 
		"T__3", "T__2", "T__1", "T__0", "VarName", "Type", "IntLiteral", "StringLiteral", 
		"ADD", "SUB", "MUL", "DIV", "AND", "OR", "XOR", "NOT", "LT", "GT", "LEQ", 
		"GEQ", "EQ", "NEQ", "NETWORK_IN", "WS", "WS_OPT"
	};


	public LanguageLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "Language.g4"; }

	@Override
	public String[] getTokenNames() { return tokenNames; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2$\u00bf\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\3\2\3\2\3\2\3\2\3\2\3\3\3\3\3\4\3\4\3\5\3\5\3\5\3\5"+
		"\3\5\3\6\3\6\3\6\3\6\3\6\3\6\3\7\3\7\3\b\3\b\3\t\3\t\3\t\3\n\3\n\3\n\3"+
		"\n\3\n\3\13\3\13\3\13\3\13\3\13\3\f\3\f\3\r\3\r\3\16\3\16\3\16\3\16\3"+
		"\16\3\16\3\16\3\17\3\17\7\17z\n\17\f\17\16\17}\13\17\3\20\3\20\3\20\3"+
		"\20\3\21\5\21\u0084\n\21\3\21\6\21\u0087\n\21\r\21\16\21\u0088\3\22\3"+
		"\22\3\22\3\22\3\23\3\23\3\24\3\24\3\25\3\25\3\26\3\26\3\27\3\27\3\27\3"+
		"\30\3\30\3\30\3\31\3\31\3\32\3\32\3\33\3\33\3\34\3\34\3\35\3\35\3\35\3"+
		"\36\3\36\3\36\3\37\3\37\3\37\3 \3 \3 \3!\3!\3!\3\"\6\"\u00b5\n\"\r\"\16"+
		"\"\u00b6\3\"\3\"\3#\6#\u00bc\n#\r#\16#\u00bd\2\2$\3\3\5\4\7\5\t\6\13\7"+
		"\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20\37\21!\22#\23%\24\'\25"+
		")\26+\27-\30/\31\61\32\63\33\65\34\67\359\36;\37= ?!A\"C#E$\3\2\b\4\2"+
		"C\\c|\6\2\62;C\\aac|\3\2\62;\4\2//c|\5\2\13\f\17\17\"\"\4\2\13\13\"\""+
		"\u00c3\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2"+
		"\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3"+
		"\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2"+
		"\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2"+
		"/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2\2\65\3\2\2\2\2\67\3\2\2\2\29\3\2\2"+
		"\2\2;\3\2\2\2\2=\3\2\2\2\2?\3\2\2\2\2A\3\2\2\2\2C\3\2\2\2\2E\3\2\2\2\3"+
		"G\3\2\2\2\5L\3\2\2\2\7N\3\2\2\2\tP\3\2\2\2\13U\3\2\2\2\r[\3\2\2\2\17]"+
		"\3\2\2\2\21_\3\2\2\2\23b\3\2\2\2\25g\3\2\2\2\27l\3\2\2\2\31n\3\2\2\2\33"+
		"p\3\2\2\2\35w\3\2\2\2\37~\3\2\2\2!\u0083\3\2\2\2#\u008a\3\2\2\2%\u008e"+
		"\3\2\2\2\'\u0090\3\2\2\2)\u0092\3\2\2\2+\u0094\3\2\2\2-\u0096\3\2\2\2"+
		"/\u0099\3\2\2\2\61\u009c\3\2\2\2\63\u009e\3\2\2\2\65\u00a0\3\2\2\2\67"+
		"\u00a2\3\2\2\29\u00a4\3\2\2\2;\u00a7\3\2\2\2=\u00aa\3\2\2\2?\u00ad\3\2"+
		"\2\2A\u00b0\3\2\2\2C\u00b4\3\2\2\2E\u00bb\3\2\2\2GH\7u\2\2HI\7w\2\2IJ"+
		"\7o\2\2JK\7*\2\2K\4\3\2\2\2LM\7=\2\2M\6\3\2\2\2NO\7}\2\2O\b\3\2\2\2PQ"+
		"\7c\2\2QR\7p\2\2RS\7{\2\2ST\7*\2\2T\n\3\2\2\2UV\7y\2\2VW\7j\2\2WX\7k\2"+
		"\2XY\7n\2\2YZ\7g\2\2Z\f\3\2\2\2[\\\7\177\2\2\\\16\3\2\2\2]^\7?\2\2^\20"+
		"\3\2\2\2_`\7k\2\2`a\7h\2\2a\22\3\2\2\2bc\7c\2\2cd\7n\2\2de\7n\2\2ef\7"+
		"*\2\2f\24\3\2\2\2gh\7g\2\2hi\7n\2\2ij\7u\2\2jk\7g\2\2k\26\3\2\2\2lm\7"+
		"*\2\2m\30\3\2\2\2no\7+\2\2o\32\3\2\2\2pq\7e\2\2qr\7q\2\2rs\7w\2\2st\7"+
		"p\2\2tu\7v\2\2uv\7*\2\2v\34\3\2\2\2w{\t\2\2\2xz\t\3\2\2yx\3\2\2\2z}\3"+
		"\2\2\2{y\3\2\2\2{|\3\2\2\2|\36\3\2\2\2}{\3\2\2\2~\177\7k\2\2\177\u0080"+
		"\7p\2\2\u0080\u0081\7v\2\2\u0081 \3\2\2\2\u0082\u0084\7/\2\2\u0083\u0082"+
		"\3\2\2\2\u0083\u0084\3\2\2\2\u0084\u0086\3\2\2\2\u0085\u0087\t\4\2\2\u0086"+
		"\u0085\3\2\2\2\u0087\u0088\3\2\2\2\u0088\u0086\3\2\2\2\u0088\u0089\3\2"+
		"\2\2\u0089\"\3\2\2\2\u008a\u008b\7$\2\2\u008b\u008c\t\5\2\2\u008c\u008d"+
		"\7$\2\2\u008d$\3\2\2\2\u008e\u008f\7-\2\2\u008f&\3\2\2\2\u0090\u0091\7"+
		"/\2\2\u0091(\3\2\2\2\u0092\u0093\7,\2\2\u0093*\3\2\2\2\u0094\u0095\7\61"+
		"\2\2\u0095,\3\2\2\2\u0096\u0097\7(\2\2\u0097\u0098\7(\2\2\u0098.\3\2\2"+
		"\2\u0099\u009a\7~\2\2\u009a\u009b\7~\2\2\u009b\60\3\2\2\2\u009c\u009d"+
		"\7`\2\2\u009d\62\3\2\2\2\u009e\u009f\7#\2\2\u009f\64\3\2\2\2\u00a0\u00a1"+
		"\7>\2\2\u00a1\66\3\2\2\2\u00a2\u00a3\7@\2\2\u00a38\3\2\2\2\u00a4\u00a5"+
		"\7>\2\2\u00a5\u00a6\7?\2\2\u00a6:\3\2\2\2\u00a7\u00a8\7@\2\2\u00a8\u00a9"+
		"\7?\2\2\u00a9<\3\2\2\2\u00aa\u00ab\7?\2\2\u00ab\u00ac\7?\2\2\u00ac>\3"+
		"\2\2\2\u00ad\u00ae\7#\2\2\u00ae\u00af\7?\2\2\u00af@\3\2\2\2\u00b0\u00b1"+
		"\7K\2\2\u00b1\u00b2\7P\2\2\u00b2B\3\2\2\2\u00b3\u00b5\t\6\2\2\u00b4\u00b3"+
		"\3\2\2\2\u00b5\u00b6\3\2\2\2\u00b6\u00b4\3\2\2\2\u00b6\u00b7\3\2\2\2\u00b7"+
		"\u00b8\3\2\2\2\u00b8\u00b9\b\"\2\2\u00b9D\3\2\2\2\u00ba\u00bc\t\7\2\2"+
		"\u00bb\u00ba\3\2\2\2\u00bc\u00bd\3\2\2\2\u00bd\u00bb\3\2\2\2\u00bd\u00be"+
		"\3\2\2\2\u00beF\3\2\2\2\b\2{\u0083\u0088\u00b6\u00bd\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}