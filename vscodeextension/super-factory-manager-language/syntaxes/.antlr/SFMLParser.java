// Generated from d:/Repos/Minecraft/SFM/repos/SuperFactoryManager 1.19.2/vscodeextension/super-factory-manager-language/syntaxes/SFML.g4 by ANTLR 4.13.1
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue"})
public class SFMLParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.13.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		IF=1, THEN=2, ELSE=3, HAS=4, OVERALL=5, SOME=6, ONE=7, LONE=8, TRUE=9, 
		FALSE=10, NOT=11, AND=12, OR=13, GT=14, GT_SYMBOL=15, LT=16, LT_SYMBOL=17, 
		EQ=18, EQ_SYMBOL=19, LE=20, LE_SYMBOL=21, GE=22, GE_SYMBOL=23, MOVE=24, 
		FROM=25, TO=26, INPUT=27, OUTPUT=28, WHERE=29, SLOTS=30, RETAIN=31, EACH=32, 
		EXCEPT=33, FORGET=34, WITHOUT=35, WITH=36, TAG=37, HASHTAG=38, ROUND=39, 
		ROBIN=40, BY=41, LABEL=42, BLOCK=43, TOP=44, BOTTOM=45, NORTH=46, EAST=47, 
		SOUTH=48, WEST=49, SIDE=50, TICKS=51, TICK=52, SECONDS=53, REDSTONE=54, 
		PULSE=55, DO=56, WORLD=57, PROGRAM=58, END=59, NAME=60, EVERY=61, COMMA=62, 
		COLON=63, SLASH=64, DASH=65, LPAREN=66, RPAREN=67, IDENTIFIER=68, NUMBER=69, 
		STRING=70, LINE_COMMENT=71, WS=72, UNUSED=73;
	public static final int
		RULE_program = 0, RULE_name = 1, RULE_trigger = 2, RULE_interval = 3, 
		RULE_block = 4, RULE_statement = 5, RULE_forgetStatement = 6, RULE_inputStatement = 7, 
		RULE_outputStatement = 8, RULE_inputResourceLimits = 9, RULE_outputResourceLimits = 10, 
		RULE_resourceLimitList = 11, RULE_resourceLimit = 12, RULE_limit = 13, 
		RULE_quantity = 14, RULE_retention = 15, RULE_resourceExclusion = 16, 
		RULE_resourceId = 17, RULE_resourceIdList = 18, RULE_resourceIdDisjunction = 19, 
		RULE_with = 20, RULE_withClause = 21, RULE_tagMatcher = 22, RULE_sidequalifier = 23, 
		RULE_side = 24, RULE_slotqualifier = 25, RULE_rangeset = 26, RULE_range = 27, 
		RULE_ifStatement = 28, RULE_boolexpr = 29, RULE_comparisonOp = 30, RULE_setOp = 31, 
		RULE_labelAccess = 32, RULE_roundrobin = 33, RULE_label = 34, RULE_identifier = 35, 
		RULE_string = 36, RULE_number = 37;
	private static String[] makeRuleNames() {
		return new String[] {
			"program", "name", "trigger", "interval", "block", "statement", "forgetStatement", 
			"inputStatement", "outputStatement", "inputResourceLimits", "outputResourceLimits", 
			"resourceLimitList", "resourceLimit", "limit", "quantity", "retention", 
			"resourceExclusion", "resourceId", "resourceIdList", "resourceIdDisjunction", 
			"with", "withClause", "tagMatcher", "sidequalifier", "side", "slotqualifier", 
			"rangeset", "range", "ifStatement", "boolexpr", "comparisonOp", "setOp", 
			"labelAccess", "roundrobin", "label", "identifier", "string", "number"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, "'>'", null, "'<'", null, "'='", null, "'<='", null, 
			"'>='", null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, "'#'", null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, "','", "':'", "'/'", "'-'", "'('", "')'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "IF", "THEN", "ELSE", "HAS", "OVERALL", "SOME", "ONE", "LONE", 
			"TRUE", "FALSE", "NOT", "AND", "OR", "GT", "GT_SYMBOL", "LT", "LT_SYMBOL", 
			"EQ", "EQ_SYMBOL", "LE", "LE_SYMBOL", "GE", "GE_SYMBOL", "MOVE", "FROM", 
			"TO", "INPUT", "OUTPUT", "WHERE", "SLOTS", "RETAIN", "EACH", "EXCEPT", 
			"FORGET", "WITHOUT", "WITH", "TAG", "HASHTAG", "ROUND", "ROBIN", "BY", 
			"LABEL", "BLOCK", "TOP", "BOTTOM", "NORTH", "EAST", "SOUTH", "WEST", 
			"SIDE", "TICKS", "TICK", "SECONDS", "REDSTONE", "PULSE", "DO", "WORLD", 
			"PROGRAM", "END", "NAME", "EVERY", "COMMA", "COLON", "SLASH", "DASH", 
			"LPAREN", "RPAREN", "IDENTIFIER", "NUMBER", "STRING", "LINE_COMMENT", 
			"WS", "UNUSED"
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
	public String getGrammarFileName() { return "SFML.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public SFMLParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ProgramContext extends ParserRuleContext {
		public TerminalNode EOF() { return getToken(SFMLParser.EOF, 0); }
		public NameContext name() {
			return getRuleContext(NameContext.class,0);
		}
		public List<TriggerContext> trigger() {
			return getRuleContexts(TriggerContext.class);
		}
		public TriggerContext trigger(int i) {
			return getRuleContext(TriggerContext.class,i);
		}
		public ProgramContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_program; }
	}

	public final ProgramContext program() throws RecognitionException {
		ProgramContext _localctx = new ProgramContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_program);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(77);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==NAME) {
				{
				setState(76);
				name();
				}
			}

			setState(82);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==EVERY) {
				{
				{
				setState(79);
				trigger();
				}
				}
				setState(84);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(85);
			match(EOF);
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

	@SuppressWarnings("CheckReturnValue")
	public static class NameContext extends ParserRuleContext {
		public TerminalNode NAME() { return getToken(SFMLParser.NAME, 0); }
		public StringContext string() {
			return getRuleContext(StringContext.class,0);
		}
		public NameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_name; }
	}

	public final NameContext name() throws RecognitionException {
		NameContext _localctx = new NameContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(87);
			match(NAME);
			setState(88);
			string();
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

	@SuppressWarnings("CheckReturnValue")
	public static class TriggerContext extends ParserRuleContext {
		public TriggerContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_trigger; }
	 
		public TriggerContext() { }
		public void copyFrom(TriggerContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class PulseTriggerContext extends TriggerContext {
		public TerminalNode EVERY() { return getToken(SFMLParser.EVERY, 0); }
		public TerminalNode REDSTONE() { return getToken(SFMLParser.REDSTONE, 0); }
		public TerminalNode PULSE() { return getToken(SFMLParser.PULSE, 0); }
		public TerminalNode DO() { return getToken(SFMLParser.DO, 0); }
		public BlockContext block() {
			return getRuleContext(BlockContext.class,0);
		}
		public TerminalNode END() { return getToken(SFMLParser.END, 0); }
		public PulseTriggerContext(TriggerContext ctx) { copyFrom(ctx); }
	}
	@SuppressWarnings("CheckReturnValue")
	public static class TimerTriggerContext extends TriggerContext {
		public TerminalNode EVERY() { return getToken(SFMLParser.EVERY, 0); }
		public IntervalContext interval() {
			return getRuleContext(IntervalContext.class,0);
		}
		public TerminalNode DO() { return getToken(SFMLParser.DO, 0); }
		public BlockContext block() {
			return getRuleContext(BlockContext.class,0);
		}
		public TerminalNode END() { return getToken(SFMLParser.END, 0); }
		public TimerTriggerContext(TriggerContext ctx) { copyFrom(ctx); }
	}

	public final TriggerContext trigger() throws RecognitionException {
		TriggerContext _localctx = new TriggerContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_trigger);
		try {
			setState(103);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,2,_ctx) ) {
			case 1:
				_localctx = new TimerTriggerContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(90);
				match(EVERY);
				setState(91);
				interval();
				setState(92);
				match(DO);
				setState(93);
				block();
				setState(94);
				match(END);
				}
				break;
			case 2:
				_localctx = new PulseTriggerContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(96);
				match(EVERY);
				setState(97);
				match(REDSTONE);
				setState(98);
				match(PULSE);
				setState(99);
				match(DO);
				setState(100);
				block();
				setState(101);
				match(END);
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

	@SuppressWarnings("CheckReturnValue")
	public static class IntervalContext extends ParserRuleContext {
		public IntervalContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_interval; }
	 
		public IntervalContext() { }
		public void copyFrom(IntervalContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class TicksContext extends IntervalContext {
		public NumberContext number() {
			return getRuleContext(NumberContext.class,0);
		}
		public TerminalNode TICKS() { return getToken(SFMLParser.TICKS, 0); }
		public TicksContext(IntervalContext ctx) { copyFrom(ctx); }
	}
	@SuppressWarnings("CheckReturnValue")
	public static class TickContext extends IntervalContext {
		public TerminalNode TICK() { return getToken(SFMLParser.TICK, 0); }
		public TickContext(IntervalContext ctx) { copyFrom(ctx); }
	}
	@SuppressWarnings("CheckReturnValue")
	public static class SecondsContext extends IntervalContext {
		public NumberContext number() {
			return getRuleContext(NumberContext.class,0);
		}
		public TerminalNode SECONDS() { return getToken(SFMLParser.SECONDS, 0); }
		public SecondsContext(IntervalContext ctx) { copyFrom(ctx); }
	}

	public final IntervalContext interval() throws RecognitionException {
		IntervalContext _localctx = new IntervalContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_interval);
		try {
			setState(112);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,3,_ctx) ) {
			case 1:
				_localctx = new TickContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(105);
				match(TICK);
				}
				break;
			case 2:
				_localctx = new TicksContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(106);
				number();
				setState(107);
				match(TICKS);
				}
				break;
			case 3:
				_localctx = new SecondsContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(109);
				number();
				setState(110);
				match(SECONDS);
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

	@SuppressWarnings("CheckReturnValue")
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
	}

	public final BlockContext block() throws RecognitionException {
		BlockContext _localctx = new BlockContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_block);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(117);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 17683185666L) != 0)) {
				{
				{
				setState(114);
				statement();
				}
				}
				setState(119);
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

	@SuppressWarnings("CheckReturnValue")
	public static class StatementContext extends ParserRuleContext {
		public InputStatementContext inputStatement() {
			return getRuleContext(InputStatementContext.class,0);
		}
		public OutputStatementContext outputStatement() {
			return getRuleContext(OutputStatementContext.class,0);
		}
		public IfStatementContext ifStatement() {
			return getRuleContext(IfStatementContext.class,0);
		}
		public ForgetStatementContext forgetStatement() {
			return getRuleContext(ForgetStatementContext.class,0);
		}
		public StatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_statement; }
	}

	public final StatementContext statement() throws RecognitionException {
		StatementContext _localctx = new StatementContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_statement);
		try {
			setState(124);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case FROM:
			case INPUT:
				enterOuterAlt(_localctx, 1);
				{
				setState(120);
				inputStatement();
				}
				break;
			case TO:
			case OUTPUT:
				enterOuterAlt(_localctx, 2);
				{
				setState(121);
				outputStatement();
				}
				break;
			case IF:
				enterOuterAlt(_localctx, 3);
				{
				setState(122);
				ifStatement();
				}
				break;
			case FORGET:
				enterOuterAlt(_localctx, 4);
				{
				setState(123);
				forgetStatement();
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

	@SuppressWarnings("CheckReturnValue")
	public static class ForgetStatementContext extends ParserRuleContext {
		public TerminalNode FORGET() { return getToken(SFMLParser.FORGET, 0); }
		public List<LabelContext> label() {
			return getRuleContexts(LabelContext.class);
		}
		public LabelContext label(int i) {
			return getRuleContext(LabelContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(SFMLParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(SFMLParser.COMMA, i);
		}
		public ForgetStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_forgetStatement; }
	}

	public final ForgetStatementContext forgetStatement() throws RecognitionException {
		ForgetStatementContext _localctx = new ForgetStatementContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_forgetStatement);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(126);
			match(FORGET);
			setState(128);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (((((_la - 54)) & ~0x3f) == 0 && ((1L << (_la - 54)) & 81921L) != 0)) {
				{
				setState(127);
				label();
				}
			}

			setState(134);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,7,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(130);
					match(COMMA);
					setState(131);
					label();
					}
					} 
				}
				setState(136);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,7,_ctx);
			}
			setState(138);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==COMMA) {
				{
				setState(137);
				match(COMMA);
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

	@SuppressWarnings("CheckReturnValue")
	public static class InputStatementContext extends ParserRuleContext {
		public TerminalNode INPUT() { return getToken(SFMLParser.INPUT, 0); }
		public TerminalNode FROM() { return getToken(SFMLParser.FROM, 0); }
		public LabelAccessContext labelAccess() {
			return getRuleContext(LabelAccessContext.class,0);
		}
		public InputResourceLimitsContext inputResourceLimits() {
			return getRuleContext(InputResourceLimitsContext.class,0);
		}
		public ResourceExclusionContext resourceExclusion() {
			return getRuleContext(ResourceExclusionContext.class,0);
		}
		public TerminalNode EACH() { return getToken(SFMLParser.EACH, 0); }
		public InputStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_inputStatement; }
	}

	public final InputStatementContext inputStatement() throws RecognitionException {
		InputStatementContext _localctx = new InputStatementContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_inputStatement);
		int _la;
		try {
			setState(164);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case INPUT:
				enterOuterAlt(_localctx, 1);
				{
				setState(140);
				match(INPUT);
				setState(142);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (((((_la - 31)) & ~0x3f) == 0 && ((1L << (_la - 31)) & 962081062961L) != 0)) {
					{
					setState(141);
					inputResourceLimits();
					}
				}

				setState(145);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==EXCEPT) {
					{
					setState(144);
					resourceExclusion();
					}
				}

				setState(147);
				match(FROM);
				setState(149);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==EACH) {
					{
					setState(148);
					match(EACH);
					}
				}

				setState(151);
				labelAccess();
				}
				break;
			case FROM:
				enterOuterAlt(_localctx, 2);
				{
				setState(152);
				match(FROM);
				setState(154);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==EACH) {
					{
					setState(153);
					match(EACH);
					}
				}

				setState(156);
				labelAccess();
				setState(157);
				match(INPUT);
				setState(159);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (((((_la - 31)) & ~0x3f) == 0 && ((1L << (_la - 31)) & 962081062961L) != 0)) {
					{
					setState(158);
					inputResourceLimits();
					}
				}

				setState(162);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==EXCEPT) {
					{
					setState(161);
					resourceExclusion();
					}
				}

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

	@SuppressWarnings("CheckReturnValue")
	public static class OutputStatementContext extends ParserRuleContext {
		public TerminalNode OUTPUT() { return getToken(SFMLParser.OUTPUT, 0); }
		public TerminalNode TO() { return getToken(SFMLParser.TO, 0); }
		public LabelAccessContext labelAccess() {
			return getRuleContext(LabelAccessContext.class,0);
		}
		public OutputResourceLimitsContext outputResourceLimits() {
			return getRuleContext(OutputResourceLimitsContext.class,0);
		}
		public ResourceExclusionContext resourceExclusion() {
			return getRuleContext(ResourceExclusionContext.class,0);
		}
		public TerminalNode EACH() { return getToken(SFMLParser.EACH, 0); }
		public OutputStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_outputStatement; }
	}

	public final OutputStatementContext outputStatement() throws RecognitionException {
		OutputStatementContext _localctx = new OutputStatementContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_outputStatement);
		int _la;
		try {
			setState(190);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case OUTPUT:
				enterOuterAlt(_localctx, 1);
				{
				setState(166);
				match(OUTPUT);
				setState(168);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (((((_la - 31)) & ~0x3f) == 0 && ((1L << (_la - 31)) & 962081062961L) != 0)) {
					{
					setState(167);
					outputResourceLimits();
					}
				}

				setState(171);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==EXCEPT) {
					{
					setState(170);
					resourceExclusion();
					}
				}

				setState(173);
				match(TO);
				setState(175);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==EACH) {
					{
					setState(174);
					match(EACH);
					}
				}

				setState(177);
				labelAccess();
				}
				break;
			case TO:
				enterOuterAlt(_localctx, 2);
				{
				setState(178);
				match(TO);
				setState(180);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==EACH) {
					{
					setState(179);
					match(EACH);
					}
				}

				setState(182);
				labelAccess();
				setState(183);
				match(OUTPUT);
				setState(185);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (((((_la - 31)) & ~0x3f) == 0 && ((1L << (_la - 31)) & 962081062961L) != 0)) {
					{
					setState(184);
					outputResourceLimits();
					}
				}

				setState(188);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==EXCEPT) {
					{
					setState(187);
					resourceExclusion();
					}
				}

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

	@SuppressWarnings("CheckReturnValue")
	public static class InputResourceLimitsContext extends ParserRuleContext {
		public ResourceLimitListContext resourceLimitList() {
			return getRuleContext(ResourceLimitListContext.class,0);
		}
		public InputResourceLimitsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_inputResourceLimits; }
	}

	public final InputResourceLimitsContext inputResourceLimits() throws RecognitionException {
		InputResourceLimitsContext _localctx = new InputResourceLimitsContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_inputResourceLimits);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(192);
			resourceLimitList();
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

	@SuppressWarnings("CheckReturnValue")
	public static class OutputResourceLimitsContext extends ParserRuleContext {
		public ResourceLimitListContext resourceLimitList() {
			return getRuleContext(ResourceLimitListContext.class,0);
		}
		public OutputResourceLimitsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_outputResourceLimits; }
	}

	public final OutputResourceLimitsContext outputResourceLimits() throws RecognitionException {
		OutputResourceLimitsContext _localctx = new OutputResourceLimitsContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_outputResourceLimits);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(194);
			resourceLimitList();
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

	@SuppressWarnings("CheckReturnValue")
	public static class ResourceLimitListContext extends ParserRuleContext {
		public List<ResourceLimitContext> resourceLimit() {
			return getRuleContexts(ResourceLimitContext.class);
		}
		public ResourceLimitContext resourceLimit(int i) {
			return getRuleContext(ResourceLimitContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(SFMLParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(SFMLParser.COMMA, i);
		}
		public ResourceLimitListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_resourceLimitList; }
	}

	public final ResourceLimitListContext resourceLimitList() throws RecognitionException {
		ResourceLimitListContext _localctx = new ResourceLimitListContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_resourceLimitList);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(196);
			resourceLimit();
			setState(201);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,23,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(197);
					match(COMMA);
					setState(198);
					resourceLimit();
					}
					} 
				}
				setState(203);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,23,_ctx);
			}
			setState(205);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==COMMA) {
				{
				setState(204);
				match(COMMA);
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

	@SuppressWarnings("CheckReturnValue")
	public static class ResourceLimitContext extends ParserRuleContext {
		public ResourceIdDisjunctionContext resourceIdDisjunction() {
			return getRuleContext(ResourceIdDisjunctionContext.class,0);
		}
		public LimitContext limit() {
			return getRuleContext(LimitContext.class,0);
		}
		public WithContext with() {
			return getRuleContext(WithContext.class,0);
		}
		public ResourceLimitContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_resourceLimit; }
	}

	public final ResourceLimitContext resourceLimit() throws RecognitionException {
		ResourceLimitContext _localctx = new ResourceLimitContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_resourceLimit);
		int _la;
		try {
			setState(219);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,28,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(208);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==RETAIN || _la==NUMBER) {
					{
					setState(207);
					limit();
					}
				}

				setState(210);
				resourceIdDisjunction();
				setState(212);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==WITHOUT || _la==WITH) {
					{
					setState(211);
					with();
					}
				}

				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(214);
				limit();
				setState(216);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==WITHOUT || _la==WITH) {
					{
					setState(215);
					with();
					}
				}

				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(218);
				with();
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

	@SuppressWarnings("CheckReturnValue")
	public static class LimitContext extends ParserRuleContext {
		public LimitContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_limit; }
	 
		public LimitContext() { }
		public void copyFrom(LimitContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class RetentionLimitContext extends LimitContext {
		public RetentionContext retention() {
			return getRuleContext(RetentionContext.class,0);
		}
		public RetentionLimitContext(LimitContext ctx) { copyFrom(ctx); }
	}
	@SuppressWarnings("CheckReturnValue")
	public static class QuantityRetentionLimitContext extends LimitContext {
		public QuantityContext quantity() {
			return getRuleContext(QuantityContext.class,0);
		}
		public RetentionContext retention() {
			return getRuleContext(RetentionContext.class,0);
		}
		public QuantityRetentionLimitContext(LimitContext ctx) { copyFrom(ctx); }
	}
	@SuppressWarnings("CheckReturnValue")
	public static class QuantityLimitContext extends LimitContext {
		public QuantityContext quantity() {
			return getRuleContext(QuantityContext.class,0);
		}
		public QuantityLimitContext(LimitContext ctx) { copyFrom(ctx); }
	}

	public final LimitContext limit() throws RecognitionException {
		LimitContext _localctx = new LimitContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_limit);
		try {
			setState(226);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,29,_ctx) ) {
			case 1:
				_localctx = new QuantityRetentionLimitContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(221);
				quantity();
				setState(222);
				retention();
				}
				break;
			case 2:
				_localctx = new RetentionLimitContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(224);
				retention();
				}
				break;
			case 3:
				_localctx = new QuantityLimitContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(225);
				quantity();
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

	@SuppressWarnings("CheckReturnValue")
	public static class QuantityContext extends ParserRuleContext {
		public NumberContext number() {
			return getRuleContext(NumberContext.class,0);
		}
		public TerminalNode EACH() { return getToken(SFMLParser.EACH, 0); }
		public QuantityContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_quantity; }
	}

	public final QuantityContext quantity() throws RecognitionException {
		QuantityContext _localctx = new QuantityContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_quantity);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(228);
			number();
			setState(230);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==EACH) {
				{
				setState(229);
				match(EACH);
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

	@SuppressWarnings("CheckReturnValue")
	public static class RetentionContext extends ParserRuleContext {
		public TerminalNode RETAIN() { return getToken(SFMLParser.RETAIN, 0); }
		public NumberContext number() {
			return getRuleContext(NumberContext.class,0);
		}
		public TerminalNode EACH() { return getToken(SFMLParser.EACH, 0); }
		public RetentionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_retention; }
	}

	public final RetentionContext retention() throws RecognitionException {
		RetentionContext _localctx = new RetentionContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_retention);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(232);
			match(RETAIN);
			setState(233);
			number();
			setState(235);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==EACH) {
				{
				setState(234);
				match(EACH);
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

	@SuppressWarnings("CheckReturnValue")
	public static class ResourceExclusionContext extends ParserRuleContext {
		public TerminalNode EXCEPT() { return getToken(SFMLParser.EXCEPT, 0); }
		public ResourceIdListContext resourceIdList() {
			return getRuleContext(ResourceIdListContext.class,0);
		}
		public ResourceExclusionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_resourceExclusion; }
	}

	public final ResourceExclusionContext resourceExclusion() throws RecognitionException {
		ResourceExclusionContext _localctx = new ResourceExclusionContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_resourceExclusion);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(237);
			match(EXCEPT);
			setState(238);
			resourceIdList();
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

	@SuppressWarnings("CheckReturnValue")
	public static class ResourceIdContext extends ParserRuleContext {
		public ResourceIdContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_resourceId; }
	 
		public ResourceIdContext() { }
		public void copyFrom(ResourceIdContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class StringResourceContext extends ResourceIdContext {
		public StringContext string() {
			return getRuleContext(StringContext.class,0);
		}
		public StringResourceContext(ResourceIdContext ctx) { copyFrom(ctx); }
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ResourceContext extends ResourceIdContext {
		public List<IdentifierContext> identifier() {
			return getRuleContexts(IdentifierContext.class);
		}
		public IdentifierContext identifier(int i) {
			return getRuleContext(IdentifierContext.class,i);
		}
		public List<TerminalNode> COLON() { return getTokens(SFMLParser.COLON); }
		public TerminalNode COLON(int i) {
			return getToken(SFMLParser.COLON, i);
		}
		public ResourceContext(ResourceIdContext ctx) { copyFrom(ctx); }
	}

	public final ResourceIdContext resourceId() throws RecognitionException {
		ResourceIdContext _localctx = new ResourceIdContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_resourceId);
		try {
			setState(260);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case REDSTONE:
			case IDENTIFIER:
				_localctx = new ResourceContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				{
				setState(240);
				identifier();
				}
				setState(257);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,37,_ctx) ) {
				case 1:
					{
					setState(241);
					match(COLON);
					setState(243);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,32,_ctx) ) {
					case 1:
						{
						setState(242);
						identifier();
						}
						break;
					}
					setState(255);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,36,_ctx) ) {
					case 1:
						{
						setState(245);
						match(COLON);
						setState(247);
						_errHandler.sync(this);
						switch ( getInterpreter().adaptivePredict(_input,33,_ctx) ) {
						case 1:
							{
							setState(246);
							identifier();
							}
							break;
						}
						setState(253);
						_errHandler.sync(this);
						switch ( getInterpreter().adaptivePredict(_input,35,_ctx) ) {
						case 1:
							{
							setState(249);
							match(COLON);
							setState(251);
							_errHandler.sync(this);
							switch ( getInterpreter().adaptivePredict(_input,34,_ctx) ) {
							case 1:
								{
								setState(250);
								identifier();
								}
								break;
							}
							}
							break;
						}
						}
						break;
					}
					}
					break;
				}
				}
				break;
			case STRING:
				_localctx = new StringResourceContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(259);
				string();
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

	@SuppressWarnings("CheckReturnValue")
	public static class ResourceIdListContext extends ParserRuleContext {
		public List<ResourceIdContext> resourceId() {
			return getRuleContexts(ResourceIdContext.class);
		}
		public ResourceIdContext resourceId(int i) {
			return getRuleContext(ResourceIdContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(SFMLParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(SFMLParser.COMMA, i);
		}
		public ResourceIdListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_resourceIdList; }
	}

	public final ResourceIdListContext resourceIdList() throws RecognitionException {
		ResourceIdListContext _localctx = new ResourceIdListContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_resourceIdList);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(262);
			resourceId();
			setState(267);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,39,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(263);
					match(COMMA);
					setState(264);
					resourceId();
					}
					} 
				}
				setState(269);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,39,_ctx);
			}
			setState(271);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,40,_ctx) ) {
			case 1:
				{
				setState(270);
				match(COMMA);
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

	@SuppressWarnings("CheckReturnValue")
	public static class ResourceIdDisjunctionContext extends ParserRuleContext {
		public List<ResourceIdContext> resourceId() {
			return getRuleContexts(ResourceIdContext.class);
		}
		public ResourceIdContext resourceId(int i) {
			return getRuleContext(ResourceIdContext.class,i);
		}
		public List<TerminalNode> OR() { return getTokens(SFMLParser.OR); }
		public TerminalNode OR(int i) {
			return getToken(SFMLParser.OR, i);
		}
		public ResourceIdDisjunctionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_resourceIdDisjunction; }
	}

	public final ResourceIdDisjunctionContext resourceIdDisjunction() throws RecognitionException {
		ResourceIdDisjunctionContext _localctx = new ResourceIdDisjunctionContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_resourceIdDisjunction);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(273);
			resourceId();
			setState(278);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,41,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(274);
					match(OR);
					setState(275);
					resourceId();
					}
					} 
				}
				setState(280);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,41,_ctx);
			}
			setState(282);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,42,_ctx) ) {
			case 1:
				{
				setState(281);
				match(OR);
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

	@SuppressWarnings("CheckReturnValue")
	public static class WithContext extends ParserRuleContext {
		public TerminalNode WITH() { return getToken(SFMLParser.WITH, 0); }
		public WithClauseContext withClause() {
			return getRuleContext(WithClauseContext.class,0);
		}
		public TerminalNode WITHOUT() { return getToken(SFMLParser.WITHOUT, 0); }
		public WithContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_with; }
	}

	public final WithContext with() throws RecognitionException {
		WithContext _localctx = new WithContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_with);
		try {
			setState(288);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case WITH:
				enterOuterAlt(_localctx, 1);
				{
				setState(284);
				match(WITH);
				setState(285);
				withClause(0);
				}
				break;
			case WITHOUT:
				enterOuterAlt(_localctx, 2);
				{
				setState(286);
				match(WITHOUT);
				setState(287);
				withClause(0);
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

	@SuppressWarnings("CheckReturnValue")
	public static class WithClauseContext extends ParserRuleContext {
		public WithClauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_withClause; }
	 
		public WithClauseContext() { }
		public void copyFrom(WithClauseContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class WithConjunctionContext extends WithClauseContext {
		public List<WithClauseContext> withClause() {
			return getRuleContexts(WithClauseContext.class);
		}
		public WithClauseContext withClause(int i) {
			return getRuleContext(WithClauseContext.class,i);
		}
		public TerminalNode AND() { return getToken(SFMLParser.AND, 0); }
		public WithConjunctionContext(WithClauseContext ctx) { copyFrom(ctx); }
	}
	@SuppressWarnings("CheckReturnValue")
	public static class WithParenContext extends WithClauseContext {
		public TerminalNode LPAREN() { return getToken(SFMLParser.LPAREN, 0); }
		public WithClauseContext withClause() {
			return getRuleContext(WithClauseContext.class,0);
		}
		public TerminalNode RPAREN() { return getToken(SFMLParser.RPAREN, 0); }
		public WithParenContext(WithClauseContext ctx) { copyFrom(ctx); }
	}
	@SuppressWarnings("CheckReturnValue")
	public static class WithNegationContext extends WithClauseContext {
		public TerminalNode NOT() { return getToken(SFMLParser.NOT, 0); }
		public WithClauseContext withClause() {
			return getRuleContext(WithClauseContext.class,0);
		}
		public WithNegationContext(WithClauseContext ctx) { copyFrom(ctx); }
	}
	@SuppressWarnings("CheckReturnValue")
	public static class WithTagContext extends WithClauseContext {
		public TagMatcherContext tagMatcher() {
			return getRuleContext(TagMatcherContext.class,0);
		}
		public TerminalNode TAG() { return getToken(SFMLParser.TAG, 0); }
		public TerminalNode HASHTAG() { return getToken(SFMLParser.HASHTAG, 0); }
		public WithTagContext(WithClauseContext ctx) { copyFrom(ctx); }
	}
	@SuppressWarnings("CheckReturnValue")
	public static class WithDisjunctionContext extends WithClauseContext {
		public List<WithClauseContext> withClause() {
			return getRuleContexts(WithClauseContext.class);
		}
		public WithClauseContext withClause(int i) {
			return getRuleContext(WithClauseContext.class,i);
		}
		public TerminalNode OR() { return getToken(SFMLParser.OR, 0); }
		public WithDisjunctionContext(WithClauseContext ctx) { copyFrom(ctx); }
	}

	public final WithClauseContext withClause() throws RecognitionException {
		return withClause(0);
	}

	private WithClauseContext withClause(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		WithClauseContext _localctx = new WithClauseContext(_ctx, _parentState);
		WithClauseContext _prevctx = _localctx;
		int _startState = 42;
		enterRecursionRule(_localctx, 42, RULE_withClause, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(305);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case LPAREN:
				{
				_localctx = new WithParenContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(291);
				match(LPAREN);
				setState(292);
				withClause(0);
				setState(293);
				match(RPAREN);
				}
				break;
			case NOT:
				{
				_localctx = new WithNegationContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(295);
				match(NOT);
				setState(296);
				withClause(4);
				}
				break;
			case TAG:
			case HASHTAG:
				{
				_localctx = new WithTagContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(302);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case TAG:
					{
					setState(297);
					match(TAG);
					setState(299);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==HASHTAG) {
						{
						setState(298);
						match(HASHTAG);
						}
					}

					}
					break;
				case HASHTAG:
					{
					setState(301);
					match(HASHTAG);
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(304);
				tagMatcher();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(315);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,48,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(313);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,47,_ctx) ) {
					case 1:
						{
						_localctx = new WithConjunctionContext(new WithClauseContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_withClause);
						setState(307);
						if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
						setState(308);
						match(AND);
						setState(309);
						withClause(4);
						}
						break;
					case 2:
						{
						_localctx = new WithDisjunctionContext(new WithClauseContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_withClause);
						setState(310);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(311);
						match(OR);
						setState(312);
						withClause(3);
						}
						break;
					}
					} 
				}
				setState(317);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,48,_ctx);
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

	@SuppressWarnings("CheckReturnValue")
	public static class TagMatcherContext extends ParserRuleContext {
		public List<IdentifierContext> identifier() {
			return getRuleContexts(IdentifierContext.class);
		}
		public IdentifierContext identifier(int i) {
			return getRuleContext(IdentifierContext.class,i);
		}
		public TerminalNode COLON() { return getToken(SFMLParser.COLON, 0); }
		public List<TerminalNode> SLASH() { return getTokens(SFMLParser.SLASH); }
		public TerminalNode SLASH(int i) {
			return getToken(SFMLParser.SLASH, i);
		}
		public TagMatcherContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tagMatcher; }
	}

	public final TagMatcherContext tagMatcher() throws RecognitionException {
		TagMatcherContext _localctx = new TagMatcherContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_tagMatcher);
		try {
			int _alt;
			setState(336);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,51,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(318);
				identifier();
				setState(319);
				match(COLON);
				setState(320);
				identifier();
				setState(325);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,49,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(321);
						match(SLASH);
						setState(322);
						identifier();
						}
						} 
					}
					setState(327);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,49,_ctx);
				}
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(328);
				identifier();
				setState(333);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,50,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(329);
						match(SLASH);
						setState(330);
						identifier();
						}
						} 
					}
					setState(335);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,50,_ctx);
				}
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

	@SuppressWarnings("CheckReturnValue")
	public static class SidequalifierContext extends ParserRuleContext {
		public SidequalifierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_sidequalifier; }
	 
		public SidequalifierContext() { }
		public void copyFrom(SidequalifierContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class EachSideContext extends SidequalifierContext {
		public TerminalNode EACH() { return getToken(SFMLParser.EACH, 0); }
		public TerminalNode SIDE() { return getToken(SFMLParser.SIDE, 0); }
		public EachSideContext(SidequalifierContext ctx) { copyFrom(ctx); }
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ListedSidesContext extends SidequalifierContext {
		public List<SideContext> side() {
			return getRuleContexts(SideContext.class);
		}
		public SideContext side(int i) {
			return getRuleContext(SideContext.class,i);
		}
		public TerminalNode SIDE() { return getToken(SFMLParser.SIDE, 0); }
		public List<TerminalNode> COMMA() { return getTokens(SFMLParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(SFMLParser.COMMA, i);
		}
		public ListedSidesContext(SidequalifierContext ctx) { copyFrom(ctx); }
	}

	public final SidequalifierContext sidequalifier() throws RecognitionException {
		SidequalifierContext _localctx = new SidequalifierContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_sidequalifier);
		int _la;
		try {
			setState(350);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case EACH:
				_localctx = new EachSideContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(338);
				match(EACH);
				setState(339);
				match(SIDE);
				}
				break;
			case TOP:
			case BOTTOM:
			case NORTH:
			case EAST:
			case SOUTH:
			case WEST:
				_localctx = new ListedSidesContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(340);
				side();
				setState(345);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(341);
					match(COMMA);
					setState(342);
					side();
					}
					}
					setState(347);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(348);
				match(SIDE);
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

	@SuppressWarnings("CheckReturnValue")
	public static class SideContext extends ParserRuleContext {
		public TerminalNode TOP() { return getToken(SFMLParser.TOP, 0); }
		public TerminalNode BOTTOM() { return getToken(SFMLParser.BOTTOM, 0); }
		public TerminalNode NORTH() { return getToken(SFMLParser.NORTH, 0); }
		public TerminalNode EAST() { return getToken(SFMLParser.EAST, 0); }
		public TerminalNode SOUTH() { return getToken(SFMLParser.SOUTH, 0); }
		public TerminalNode WEST() { return getToken(SFMLParser.WEST, 0); }
		public SideContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_side; }
	}

	public final SideContext side() throws RecognitionException {
		SideContext _localctx = new SideContext(_ctx, getState());
		enterRule(_localctx, 48, RULE_side);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(352);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 1108307720798208L) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
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

	@SuppressWarnings("CheckReturnValue")
	public static class SlotqualifierContext extends ParserRuleContext {
		public TerminalNode SLOTS() { return getToken(SFMLParser.SLOTS, 0); }
		public RangesetContext rangeset() {
			return getRuleContext(RangesetContext.class,0);
		}
		public SlotqualifierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_slotqualifier; }
	}

	public final SlotqualifierContext slotqualifier() throws RecognitionException {
		SlotqualifierContext _localctx = new SlotqualifierContext(_ctx, getState());
		enterRule(_localctx, 50, RULE_slotqualifier);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(354);
			match(SLOTS);
			setState(355);
			rangeset();
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

	@SuppressWarnings("CheckReturnValue")
	public static class RangesetContext extends ParserRuleContext {
		public List<RangeContext> range() {
			return getRuleContexts(RangeContext.class);
		}
		public RangeContext range(int i) {
			return getRuleContext(RangeContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(SFMLParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(SFMLParser.COMMA, i);
		}
		public RangesetContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rangeset; }
	}

	public final RangesetContext rangeset() throws RecognitionException {
		RangesetContext _localctx = new RangesetContext(_ctx, getState());
		enterRule(_localctx, 52, RULE_rangeset);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(357);
			range();
			setState(362);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(358);
				match(COMMA);
				setState(359);
				range();
				}
				}
				setState(364);
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

	@SuppressWarnings("CheckReturnValue")
	public static class RangeContext extends ParserRuleContext {
		public List<NumberContext> number() {
			return getRuleContexts(NumberContext.class);
		}
		public NumberContext number(int i) {
			return getRuleContext(NumberContext.class,i);
		}
		public TerminalNode DASH() { return getToken(SFMLParser.DASH, 0); }
		public RangeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_range; }
	}

	public final RangeContext range() throws RecognitionException {
		RangeContext _localctx = new RangeContext(_ctx, getState());
		enterRule(_localctx, 54, RULE_range);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(365);
			number();
			setState(368);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==DASH) {
				{
				setState(366);
				match(DASH);
				setState(367);
				number();
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

	@SuppressWarnings("CheckReturnValue")
	public static class IfStatementContext extends ParserRuleContext {
		public List<TerminalNode> IF() { return getTokens(SFMLParser.IF); }
		public TerminalNode IF(int i) {
			return getToken(SFMLParser.IF, i);
		}
		public List<BoolexprContext> boolexpr() {
			return getRuleContexts(BoolexprContext.class);
		}
		public BoolexprContext boolexpr(int i) {
			return getRuleContext(BoolexprContext.class,i);
		}
		public List<TerminalNode> THEN() { return getTokens(SFMLParser.THEN); }
		public TerminalNode THEN(int i) {
			return getToken(SFMLParser.THEN, i);
		}
		public List<BlockContext> block() {
			return getRuleContexts(BlockContext.class);
		}
		public BlockContext block(int i) {
			return getRuleContext(BlockContext.class,i);
		}
		public TerminalNode END() { return getToken(SFMLParser.END, 0); }
		public List<TerminalNode> ELSE() { return getTokens(SFMLParser.ELSE); }
		public TerminalNode ELSE(int i) {
			return getToken(SFMLParser.ELSE, i);
		}
		public IfStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ifStatement; }
	}

	public final IfStatementContext ifStatement() throws RecognitionException {
		IfStatementContext _localctx = new IfStatementContext(_ctx, getState());
		enterRule(_localctx, 56, RULE_ifStatement);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(370);
			match(IF);
			setState(371);
			boolexpr(0);
			setState(372);
			match(THEN);
			setState(373);
			block();
			setState(382);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,56,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(374);
					match(ELSE);
					setState(375);
					match(IF);
					setState(376);
					boolexpr(0);
					setState(377);
					match(THEN);
					setState(378);
					block();
					}
					} 
				}
				setState(384);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,56,_ctx);
			}
			setState(387);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ELSE) {
				{
				setState(385);
				match(ELSE);
				setState(386);
				block();
				}
			}

			setState(389);
			match(END);
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

	@SuppressWarnings("CheckReturnValue")
	public static class BoolexprContext extends ParserRuleContext {
		public BoolexprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_boolexpr; }
	 
		public BoolexprContext() { }
		public void copyFrom(BoolexprContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class BooleanHasContext extends BoolexprContext {
		public LabelAccessContext labelAccess() {
			return getRuleContext(LabelAccessContext.class,0);
		}
		public TerminalNode HAS() { return getToken(SFMLParser.HAS, 0); }
		public ComparisonOpContext comparisonOp() {
			return getRuleContext(ComparisonOpContext.class,0);
		}
		public NumberContext number() {
			return getRuleContext(NumberContext.class,0);
		}
		public SetOpContext setOp() {
			return getRuleContext(SetOpContext.class,0);
		}
		public ResourceIdDisjunctionContext resourceIdDisjunction() {
			return getRuleContext(ResourceIdDisjunctionContext.class,0);
		}
		public WithContext with() {
			return getRuleContext(WithContext.class,0);
		}
		public TerminalNode EXCEPT() { return getToken(SFMLParser.EXCEPT, 0); }
		public ResourceIdListContext resourceIdList() {
			return getRuleContext(ResourceIdListContext.class,0);
		}
		public BooleanHasContext(BoolexprContext ctx) { copyFrom(ctx); }
	}
	@SuppressWarnings("CheckReturnValue")
	public static class BooleanConjunctionContext extends BoolexprContext {
		public List<BoolexprContext> boolexpr() {
			return getRuleContexts(BoolexprContext.class);
		}
		public BoolexprContext boolexpr(int i) {
			return getRuleContext(BoolexprContext.class,i);
		}
		public TerminalNode AND() { return getToken(SFMLParser.AND, 0); }
		public BooleanConjunctionContext(BoolexprContext ctx) { copyFrom(ctx); }
	}
	@SuppressWarnings("CheckReturnValue")
	public static class BooleanRedstoneContext extends BoolexprContext {
		public TerminalNode REDSTONE() { return getToken(SFMLParser.REDSTONE, 0); }
		public ComparisonOpContext comparisonOp() {
			return getRuleContext(ComparisonOpContext.class,0);
		}
		public NumberContext number() {
			return getRuleContext(NumberContext.class,0);
		}
		public BooleanRedstoneContext(BoolexprContext ctx) { copyFrom(ctx); }
	}
	@SuppressWarnings("CheckReturnValue")
	public static class BooleanDisjunctionContext extends BoolexprContext {
		public List<BoolexprContext> boolexpr() {
			return getRuleContexts(BoolexprContext.class);
		}
		public BoolexprContext boolexpr(int i) {
			return getRuleContext(BoolexprContext.class,i);
		}
		public TerminalNode OR() { return getToken(SFMLParser.OR, 0); }
		public BooleanDisjunctionContext(BoolexprContext ctx) { copyFrom(ctx); }
	}
	@SuppressWarnings("CheckReturnValue")
	public static class BooleanFalseContext extends BoolexprContext {
		public TerminalNode FALSE() { return getToken(SFMLParser.FALSE, 0); }
		public BooleanFalseContext(BoolexprContext ctx) { copyFrom(ctx); }
	}
	@SuppressWarnings("CheckReturnValue")
	public static class BooleanParenContext extends BoolexprContext {
		public TerminalNode LPAREN() { return getToken(SFMLParser.LPAREN, 0); }
		public BoolexprContext boolexpr() {
			return getRuleContext(BoolexprContext.class,0);
		}
		public TerminalNode RPAREN() { return getToken(SFMLParser.RPAREN, 0); }
		public BooleanParenContext(BoolexprContext ctx) { copyFrom(ctx); }
	}
	@SuppressWarnings("CheckReturnValue")
	public static class BooleanNegationContext extends BoolexprContext {
		public TerminalNode NOT() { return getToken(SFMLParser.NOT, 0); }
		public BoolexprContext boolexpr() {
			return getRuleContext(BoolexprContext.class,0);
		}
		public BooleanNegationContext(BoolexprContext ctx) { copyFrom(ctx); }
	}
	@SuppressWarnings("CheckReturnValue")
	public static class BooleanTrueContext extends BoolexprContext {
		public TerminalNode TRUE() { return getToken(SFMLParser.TRUE, 0); }
		public BooleanTrueContext(BoolexprContext ctx) { copyFrom(ctx); }
	}

	public final BoolexprContext boolexpr() throws RecognitionException {
		return boolexpr(0);
	}

	private BoolexprContext boolexpr(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		BoolexprContext _localctx = new BoolexprContext(_ctx, _parentState);
		BoolexprContext _prevctx = _localctx;
		int _startState = 58;
		enterRecursionRule(_localctx, 58, RULE_boolexpr, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(423);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,63,_ctx) ) {
			case 1:
				{
				_localctx = new BooleanTrueContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(392);
				match(TRUE);
				}
				break;
			case 2:
				{
				_localctx = new BooleanFalseContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(393);
				match(FALSE);
				}
				break;
			case 3:
				{
				_localctx = new BooleanParenContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(394);
				match(LPAREN);
				setState(395);
				boolexpr(0);
				setState(396);
				match(RPAREN);
				}
				break;
			case 4:
				{
				_localctx = new BooleanNegationContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(398);
				match(NOT);
				setState(399);
				boolexpr(5);
				}
				break;
			case 5:
				{
				_localctx = new BooleanHasContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(401);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 2305843013508661728L) != 0)) {
					{
					setState(400);
					setOp();
					}
				}

				setState(403);
				labelAccess();
				setState(404);
				match(HAS);
				setState(405);
				comparisonOp();
				setState(406);
				number();
				setState(408);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,59,_ctx) ) {
				case 1:
					{
					setState(407);
					resourceIdDisjunction();
					}
					break;
				}
				setState(411);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,60,_ctx) ) {
				case 1:
					{
					setState(410);
					with();
					}
					break;
				}
				setState(415);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,61,_ctx) ) {
				case 1:
					{
					setState(413);
					match(EXCEPT);
					setState(414);
					resourceIdList();
					}
					break;
				}
				}
				break;
			case 6:
				{
				_localctx = new BooleanRedstoneContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(417);
				match(REDSTONE);
				setState(421);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,62,_ctx) ) {
				case 1:
					{
					setState(418);
					comparisonOp();
					setState(419);
					number();
					}
					break;
				}
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(433);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,65,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(431);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,64,_ctx) ) {
					case 1:
						{
						_localctx = new BooleanConjunctionContext(new BoolexprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_boolexpr);
						setState(425);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						setState(426);
						match(AND);
						setState(427);
						boolexpr(5);
						}
						break;
					case 2:
						{
						_localctx = new BooleanDisjunctionContext(new BoolexprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_boolexpr);
						setState(428);
						if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
						setState(429);
						match(OR);
						setState(430);
						boolexpr(4);
						}
						break;
					}
					} 
				}
				setState(435);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,65,_ctx);
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

	@SuppressWarnings("CheckReturnValue")
	public static class ComparisonOpContext extends ParserRuleContext {
		public TerminalNode GT() { return getToken(SFMLParser.GT, 0); }
		public TerminalNode LT() { return getToken(SFMLParser.LT, 0); }
		public TerminalNode EQ() { return getToken(SFMLParser.EQ, 0); }
		public TerminalNode LE() { return getToken(SFMLParser.LE, 0); }
		public TerminalNode GE() { return getToken(SFMLParser.GE, 0); }
		public TerminalNode GT_SYMBOL() { return getToken(SFMLParser.GT_SYMBOL, 0); }
		public TerminalNode LT_SYMBOL() { return getToken(SFMLParser.LT_SYMBOL, 0); }
		public TerminalNode EQ_SYMBOL() { return getToken(SFMLParser.EQ_SYMBOL, 0); }
		public TerminalNode LE_SYMBOL() { return getToken(SFMLParser.LE_SYMBOL, 0); }
		public TerminalNode GE_SYMBOL() { return getToken(SFMLParser.GE_SYMBOL, 0); }
		public ComparisonOpContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_comparisonOp; }
	}

	public final ComparisonOpContext comparisonOp() throws RecognitionException {
		ComparisonOpContext _localctx = new ComparisonOpContext(_ctx, getState());
		enterRule(_localctx, 60, RULE_comparisonOp);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(436);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 16760832L) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
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

	@SuppressWarnings("CheckReturnValue")
	public static class SetOpContext extends ParserRuleContext {
		public TerminalNode OVERALL() { return getToken(SFMLParser.OVERALL, 0); }
		public TerminalNode SOME() { return getToken(SFMLParser.SOME, 0); }
		public TerminalNode EVERY() { return getToken(SFMLParser.EVERY, 0); }
		public TerminalNode EACH() { return getToken(SFMLParser.EACH, 0); }
		public TerminalNode ONE() { return getToken(SFMLParser.ONE, 0); }
		public TerminalNode LONE() { return getToken(SFMLParser.LONE, 0); }
		public SetOpContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_setOp; }
	}

	public final SetOpContext setOp() throws RecognitionException {
		SetOpContext _localctx = new SetOpContext(_ctx, getState());
		enterRule(_localctx, 62, RULE_setOp);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(438);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 2305843013508661728L) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
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

	@SuppressWarnings("CheckReturnValue")
	public static class LabelAccessContext extends ParserRuleContext {
		public List<LabelContext> label() {
			return getRuleContexts(LabelContext.class);
		}
		public LabelContext label(int i) {
			return getRuleContext(LabelContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(SFMLParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(SFMLParser.COMMA, i);
		}
		public RoundrobinContext roundrobin() {
			return getRuleContext(RoundrobinContext.class,0);
		}
		public SidequalifierContext sidequalifier() {
			return getRuleContext(SidequalifierContext.class,0);
		}
		public SlotqualifierContext slotqualifier() {
			return getRuleContext(SlotqualifierContext.class,0);
		}
		public LabelAccessContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_labelAccess; }
	}

	public final LabelAccessContext labelAccess() throws RecognitionException {
		LabelAccessContext _localctx = new LabelAccessContext(_ctx, getState());
		enterRule(_localctx, 64, RULE_labelAccess);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(440);
			label();
			setState(445);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(441);
				match(COMMA);
				setState(442);
				label();
				}
				}
				setState(447);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(449);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ROUND) {
				{
				setState(448);
				roundrobin();
				}
			}

			setState(452);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 1108312015765504L) != 0)) {
				{
				setState(451);
				sidequalifier();
				}
			}

			setState(455);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==SLOTS) {
				{
				setState(454);
				slotqualifier();
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

	@SuppressWarnings("CheckReturnValue")
	public static class RoundrobinContext extends ParserRuleContext {
		public TerminalNode ROUND() { return getToken(SFMLParser.ROUND, 0); }
		public TerminalNode ROBIN() { return getToken(SFMLParser.ROBIN, 0); }
		public TerminalNode BY() { return getToken(SFMLParser.BY, 0); }
		public TerminalNode LABEL() { return getToken(SFMLParser.LABEL, 0); }
		public TerminalNode BLOCK() { return getToken(SFMLParser.BLOCK, 0); }
		public RoundrobinContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_roundrobin; }
	}

	public final RoundrobinContext roundrobin() throws RecognitionException {
		RoundrobinContext _localctx = new RoundrobinContext(_ctx, getState());
		enterRule(_localctx, 66, RULE_roundrobin);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(457);
			match(ROUND);
			setState(458);
			match(ROBIN);
			setState(459);
			match(BY);
			setState(460);
			_la = _input.LA(1);
			if ( !(_la==LABEL || _la==BLOCK) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
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

	@SuppressWarnings("CheckReturnValue")
	public static class LabelContext extends ParserRuleContext {
		public LabelContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_label; }
	 
		public LabelContext() { }
		public void copyFrom(LabelContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class RawLabelContext extends LabelContext {
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
		public RawLabelContext(LabelContext ctx) { copyFrom(ctx); }
	}
	@SuppressWarnings("CheckReturnValue")
	public static class StringLabelContext extends LabelContext {
		public StringContext string() {
			return getRuleContext(StringContext.class,0);
		}
		public StringLabelContext(LabelContext ctx) { copyFrom(ctx); }
	}

	public final LabelContext label() throws RecognitionException {
		LabelContext _localctx = new LabelContext(_ctx, getState());
		enterRule(_localctx, 68, RULE_label);
		try {
			setState(464);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case REDSTONE:
			case IDENTIFIER:
				_localctx = new RawLabelContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				{
				setState(462);
				identifier();
				}
				}
				break;
			case STRING:
				_localctx = new StringLabelContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(463);
				string();
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

	@SuppressWarnings("CheckReturnValue")
	public static class IdentifierContext extends ParserRuleContext {
		public TerminalNode IDENTIFIER() { return getToken(SFMLParser.IDENTIFIER, 0); }
		public TerminalNode REDSTONE() { return getToken(SFMLParser.REDSTONE, 0); }
		public IdentifierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_identifier; }
	}

	public final IdentifierContext identifier() throws RecognitionException {
		IdentifierContext _localctx = new IdentifierContext(_ctx, getState());
		enterRule(_localctx, 70, RULE_identifier);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(466);
			_la = _input.LA(1);
			if ( !(_la==REDSTONE || _la==IDENTIFIER) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
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

	@SuppressWarnings("CheckReturnValue")
	public static class StringContext extends ParserRuleContext {
		public TerminalNode STRING() { return getToken(SFMLParser.STRING, 0); }
		public StringContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_string; }
	}

	public final StringContext string() throws RecognitionException {
		StringContext _localctx = new StringContext(_ctx, getState());
		enterRule(_localctx, 72, RULE_string);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(468);
			match(STRING);
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

	@SuppressWarnings("CheckReturnValue")
	public static class NumberContext extends ParserRuleContext {
		public TerminalNode NUMBER() { return getToken(SFMLParser.NUMBER, 0); }
		public NumberContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_number; }
	}

	public final NumberContext number() throws RecognitionException {
		NumberContext _localctx = new NumberContext(_ctx, getState());
		enterRule(_localctx, 74, RULE_number);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(470);
			match(NUMBER);
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
		case 21:
			return withClause_sempred((WithClauseContext)_localctx, predIndex);
		case 29:
			return boolexpr_sempred((BoolexprContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean withClause_sempred(WithClauseContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 3);
		case 1:
			return precpred(_ctx, 2);
		}
		return true;
	}
	private boolean boolexpr_sempred(BoolexprContext _localctx, int predIndex) {
		switch (predIndex) {
		case 2:
			return precpred(_ctx, 4);
		case 3:
			return precpred(_ctx, 3);
		}
		return true;
	}

	public static final String _serializedATN =
		"\u0004\u0001I\u01d9\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"+
		"\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002"+
		"\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0002"+
		"\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007\u000f"+
		"\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002\u0012\u0007\u0012"+
		"\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014\u0002\u0015\u0007\u0015"+
		"\u0002\u0016\u0007\u0016\u0002\u0017\u0007\u0017\u0002\u0018\u0007\u0018"+
		"\u0002\u0019\u0007\u0019\u0002\u001a\u0007\u001a\u0002\u001b\u0007\u001b"+
		"\u0002\u001c\u0007\u001c\u0002\u001d\u0007\u001d\u0002\u001e\u0007\u001e"+
		"\u0002\u001f\u0007\u001f\u0002 \u0007 \u0002!\u0007!\u0002\"\u0007\"\u0002"+
		"#\u0007#\u0002$\u0007$\u0002%\u0007%\u0001\u0000\u0003\u0000N\b\u0000"+
		"\u0001\u0000\u0005\u0000Q\b\u0000\n\u0000\f\u0000T\t\u0000\u0001\u0000"+
		"\u0001\u0000\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002"+
		"\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002"+
		"\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0003\u0002"+
		"h\b\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003"+
		"\u0001\u0003\u0001\u0003\u0003\u0003q\b\u0003\u0001\u0004\u0005\u0004"+
		"t\b\u0004\n\u0004\f\u0004w\t\u0004\u0001\u0005\u0001\u0005\u0001\u0005"+
		"\u0001\u0005\u0003\u0005}\b\u0005\u0001\u0006\u0001\u0006\u0003\u0006"+
		"\u0081\b\u0006\u0001\u0006\u0001\u0006\u0005\u0006\u0085\b\u0006\n\u0006"+
		"\f\u0006\u0088\t\u0006\u0001\u0006\u0003\u0006\u008b\b\u0006\u0001\u0007"+
		"\u0001\u0007\u0003\u0007\u008f\b\u0007\u0001\u0007\u0003\u0007\u0092\b"+
		"\u0007\u0001\u0007\u0001\u0007\u0003\u0007\u0096\b\u0007\u0001\u0007\u0001"+
		"\u0007\u0001\u0007\u0003\u0007\u009b\b\u0007\u0001\u0007\u0001\u0007\u0001"+
		"\u0007\u0003\u0007\u00a0\b\u0007\u0001\u0007\u0003\u0007\u00a3\b\u0007"+
		"\u0003\u0007\u00a5\b\u0007\u0001\b\u0001\b\u0003\b\u00a9\b\b\u0001\b\u0003"+
		"\b\u00ac\b\b\u0001\b\u0001\b\u0003\b\u00b0\b\b\u0001\b\u0001\b\u0001\b"+
		"\u0003\b\u00b5\b\b\u0001\b\u0001\b\u0001\b\u0003\b\u00ba\b\b\u0001\b\u0003"+
		"\b\u00bd\b\b\u0003\b\u00bf\b\b\u0001\t\u0001\t\u0001\n\u0001\n\u0001\u000b"+
		"\u0001\u000b\u0001\u000b\u0005\u000b\u00c8\b\u000b\n\u000b\f\u000b\u00cb"+
		"\t\u000b\u0001\u000b\u0003\u000b\u00ce\b\u000b\u0001\f\u0003\f\u00d1\b"+
		"\f\u0001\f\u0001\f\u0003\f\u00d5\b\f\u0001\f\u0001\f\u0003\f\u00d9\b\f"+
		"\u0001\f\u0003\f\u00dc\b\f\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0003"+
		"\r\u00e3\b\r\u0001\u000e\u0001\u000e\u0003\u000e\u00e7\b\u000e\u0001\u000f"+
		"\u0001\u000f\u0001\u000f\u0003\u000f\u00ec\b\u000f\u0001\u0010\u0001\u0010"+
		"\u0001\u0010\u0001\u0011\u0001\u0011\u0001\u0011\u0003\u0011\u00f4\b\u0011"+
		"\u0001\u0011\u0001\u0011\u0003\u0011\u00f8\b\u0011\u0001\u0011\u0001\u0011"+
		"\u0003\u0011\u00fc\b\u0011\u0003\u0011\u00fe\b\u0011\u0003\u0011\u0100"+
		"\b\u0011\u0003\u0011\u0102\b\u0011\u0001\u0011\u0003\u0011\u0105\b\u0011"+
		"\u0001\u0012\u0001\u0012\u0001\u0012\u0005\u0012\u010a\b\u0012\n\u0012"+
		"\f\u0012\u010d\t\u0012\u0001\u0012\u0003\u0012\u0110\b\u0012\u0001\u0013"+
		"\u0001\u0013\u0001\u0013\u0005\u0013\u0115\b\u0013\n\u0013\f\u0013\u0118"+
		"\t\u0013\u0001\u0013\u0003\u0013\u011b\b\u0013\u0001\u0014\u0001\u0014"+
		"\u0001\u0014\u0001\u0014\u0003\u0014\u0121\b\u0014\u0001\u0015\u0001\u0015"+
		"\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015"+
		"\u0001\u0015\u0003\u0015\u012c\b\u0015\u0001\u0015\u0003\u0015\u012f\b"+
		"\u0015\u0001\u0015\u0003\u0015\u0132\b\u0015\u0001\u0015\u0001\u0015\u0001"+
		"\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0005\u0015\u013a\b\u0015\n"+
		"\u0015\f\u0015\u013d\t\u0015\u0001\u0016\u0001\u0016\u0001\u0016\u0001"+
		"\u0016\u0001\u0016\u0005\u0016\u0144\b\u0016\n\u0016\f\u0016\u0147\t\u0016"+
		"\u0001\u0016\u0001\u0016\u0001\u0016\u0005\u0016\u014c\b\u0016\n\u0016"+
		"\f\u0016\u014f\t\u0016\u0003\u0016\u0151\b\u0016\u0001\u0017\u0001\u0017"+
		"\u0001\u0017\u0001\u0017\u0001\u0017\u0005\u0017\u0158\b\u0017\n\u0017"+
		"\f\u0017\u015b\t\u0017\u0001\u0017\u0001\u0017\u0003\u0017\u015f\b\u0017"+
		"\u0001\u0018\u0001\u0018\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u001a"+
		"\u0001\u001a\u0001\u001a\u0005\u001a\u0169\b\u001a\n\u001a\f\u001a\u016c"+
		"\t\u001a\u0001\u001b\u0001\u001b\u0001\u001b\u0003\u001b\u0171\b\u001b"+
		"\u0001\u001c\u0001\u001c\u0001\u001c\u0001\u001c\u0001\u001c\u0001\u001c"+
		"\u0001\u001c\u0001\u001c\u0001\u001c\u0001\u001c\u0005\u001c\u017d\b\u001c"+
		"\n\u001c\f\u001c\u0180\t\u001c\u0001\u001c\u0001\u001c\u0003\u001c\u0184"+
		"\b\u001c\u0001\u001c\u0001\u001c\u0001\u001d\u0001\u001d\u0001\u001d\u0001"+
		"\u001d\u0001\u001d\u0001\u001d\u0001\u001d\u0001\u001d\u0001\u001d\u0001"+
		"\u001d\u0003\u001d\u0192\b\u001d\u0001\u001d\u0001\u001d\u0001\u001d\u0001"+
		"\u001d\u0001\u001d\u0003\u001d\u0199\b\u001d\u0001\u001d\u0003\u001d\u019c"+
		"\b\u001d\u0001\u001d\u0001\u001d\u0003\u001d\u01a0\b\u001d\u0001\u001d"+
		"\u0001\u001d\u0001\u001d\u0001\u001d\u0003\u001d\u01a6\b\u001d\u0003\u001d"+
		"\u01a8\b\u001d\u0001\u001d\u0001\u001d\u0001\u001d\u0001\u001d\u0001\u001d"+
		"\u0001\u001d\u0005\u001d\u01b0\b\u001d\n\u001d\f\u001d\u01b3\t\u001d\u0001"+
		"\u001e\u0001\u001e\u0001\u001f\u0001\u001f\u0001 \u0001 \u0001 \u0005"+
		" \u01bc\b \n \f \u01bf\t \u0001 \u0003 \u01c2\b \u0001 \u0003 \u01c5\b"+
		" \u0001 \u0003 \u01c8\b \u0001!\u0001!\u0001!\u0001!\u0001!\u0001\"\u0001"+
		"\"\u0003\"\u01d1\b\"\u0001#\u0001#\u0001$\u0001$\u0001%\u0001%\u0001%"+
		"\u0000\u0002*:&\u0000\u0002\u0004\u0006\b\n\f\u000e\u0010\u0012\u0014"+
		"\u0016\u0018\u001a\u001c\u001e \"$&(*,.02468:<>@BDFHJ\u0000\u0005\u0001"+
		"\u0000,1\u0001\u0000\u000e\u0017\u0003\u0000\u0005\b  ==\u0001\u0000*"+
		"+\u0002\u000066DD\u0203\u0000M\u0001\u0000\u0000\u0000\u0002W\u0001\u0000"+
		"\u0000\u0000\u0004g\u0001\u0000\u0000\u0000\u0006p\u0001\u0000\u0000\u0000"+
		"\bu\u0001\u0000\u0000\u0000\n|\u0001\u0000\u0000\u0000\f~\u0001\u0000"+
		"\u0000\u0000\u000e\u00a4\u0001\u0000\u0000\u0000\u0010\u00be\u0001\u0000"+
		"\u0000\u0000\u0012\u00c0\u0001\u0000\u0000\u0000\u0014\u00c2\u0001\u0000"+
		"\u0000\u0000\u0016\u00c4\u0001\u0000\u0000\u0000\u0018\u00db\u0001\u0000"+
		"\u0000\u0000\u001a\u00e2\u0001\u0000\u0000\u0000\u001c\u00e4\u0001\u0000"+
		"\u0000\u0000\u001e\u00e8\u0001\u0000\u0000\u0000 \u00ed\u0001\u0000\u0000"+
		"\u0000\"\u0104\u0001\u0000\u0000\u0000$\u0106\u0001\u0000\u0000\u0000"+
		"&\u0111\u0001\u0000\u0000\u0000(\u0120\u0001\u0000\u0000\u0000*\u0131"+
		"\u0001\u0000\u0000\u0000,\u0150\u0001\u0000\u0000\u0000.\u015e\u0001\u0000"+
		"\u0000\u00000\u0160\u0001\u0000\u0000\u00002\u0162\u0001\u0000\u0000\u0000"+
		"4\u0165\u0001\u0000\u0000\u00006\u016d\u0001\u0000\u0000\u00008\u0172"+
		"\u0001\u0000\u0000\u0000:\u01a7\u0001\u0000\u0000\u0000<\u01b4\u0001\u0000"+
		"\u0000\u0000>\u01b6\u0001\u0000\u0000\u0000@\u01b8\u0001\u0000\u0000\u0000"+
		"B\u01c9\u0001\u0000\u0000\u0000D\u01d0\u0001\u0000\u0000\u0000F\u01d2"+
		"\u0001\u0000\u0000\u0000H\u01d4\u0001\u0000\u0000\u0000J\u01d6\u0001\u0000"+
		"\u0000\u0000LN\u0003\u0002\u0001\u0000ML\u0001\u0000\u0000\u0000MN\u0001"+
		"\u0000\u0000\u0000NR\u0001\u0000\u0000\u0000OQ\u0003\u0004\u0002\u0000"+
		"PO\u0001\u0000\u0000\u0000QT\u0001\u0000\u0000\u0000RP\u0001\u0000\u0000"+
		"\u0000RS\u0001\u0000\u0000\u0000SU\u0001\u0000\u0000\u0000TR\u0001\u0000"+
		"\u0000\u0000UV\u0005\u0000\u0000\u0001V\u0001\u0001\u0000\u0000\u0000"+
		"WX\u0005<\u0000\u0000XY\u0003H$\u0000Y\u0003\u0001\u0000\u0000\u0000Z"+
		"[\u0005=\u0000\u0000[\\\u0003\u0006\u0003\u0000\\]\u00058\u0000\u0000"+
		"]^\u0003\b\u0004\u0000^_\u0005;\u0000\u0000_h\u0001\u0000\u0000\u0000"+
		"`a\u0005=\u0000\u0000ab\u00056\u0000\u0000bc\u00057\u0000\u0000cd\u0005"+
		"8\u0000\u0000de\u0003\b\u0004\u0000ef\u0005;\u0000\u0000fh\u0001\u0000"+
		"\u0000\u0000gZ\u0001\u0000\u0000\u0000g`\u0001\u0000\u0000\u0000h\u0005"+
		"\u0001\u0000\u0000\u0000iq\u00054\u0000\u0000jk\u0003J%\u0000kl\u0005"+
		"3\u0000\u0000lq\u0001\u0000\u0000\u0000mn\u0003J%\u0000no\u00055\u0000"+
		"\u0000oq\u0001\u0000\u0000\u0000pi\u0001\u0000\u0000\u0000pj\u0001\u0000"+
		"\u0000\u0000pm\u0001\u0000\u0000\u0000q\u0007\u0001\u0000\u0000\u0000"+
		"rt\u0003\n\u0005\u0000sr\u0001\u0000\u0000\u0000tw\u0001\u0000\u0000\u0000"+
		"us\u0001\u0000\u0000\u0000uv\u0001\u0000\u0000\u0000v\t\u0001\u0000\u0000"+
		"\u0000wu\u0001\u0000\u0000\u0000x}\u0003\u000e\u0007\u0000y}\u0003\u0010"+
		"\b\u0000z}\u00038\u001c\u0000{}\u0003\f\u0006\u0000|x\u0001\u0000\u0000"+
		"\u0000|y\u0001\u0000\u0000\u0000|z\u0001\u0000\u0000\u0000|{\u0001\u0000"+
		"\u0000\u0000}\u000b\u0001\u0000\u0000\u0000~\u0080\u0005\"\u0000\u0000"+
		"\u007f\u0081\u0003D\"\u0000\u0080\u007f\u0001\u0000\u0000\u0000\u0080"+
		"\u0081\u0001\u0000\u0000\u0000\u0081\u0086\u0001\u0000\u0000\u0000\u0082"+
		"\u0083\u0005>\u0000\u0000\u0083\u0085\u0003D\"\u0000\u0084\u0082\u0001"+
		"\u0000\u0000\u0000\u0085\u0088\u0001\u0000\u0000\u0000\u0086\u0084\u0001"+
		"\u0000\u0000\u0000\u0086\u0087\u0001\u0000\u0000\u0000\u0087\u008a\u0001"+
		"\u0000\u0000\u0000\u0088\u0086\u0001\u0000\u0000\u0000\u0089\u008b\u0005"+
		">\u0000\u0000\u008a\u0089\u0001\u0000\u0000\u0000\u008a\u008b\u0001\u0000"+
		"\u0000\u0000\u008b\r\u0001\u0000\u0000\u0000\u008c\u008e\u0005\u001b\u0000"+
		"\u0000\u008d\u008f\u0003\u0012\t\u0000\u008e\u008d\u0001\u0000\u0000\u0000"+
		"\u008e\u008f\u0001\u0000\u0000\u0000\u008f\u0091\u0001\u0000\u0000\u0000"+
		"\u0090\u0092\u0003 \u0010\u0000\u0091\u0090\u0001\u0000\u0000\u0000\u0091"+
		"\u0092\u0001\u0000\u0000\u0000\u0092\u0093\u0001\u0000\u0000\u0000\u0093"+
		"\u0095\u0005\u0019\u0000\u0000\u0094\u0096\u0005 \u0000\u0000\u0095\u0094"+
		"\u0001\u0000\u0000\u0000\u0095\u0096\u0001\u0000\u0000\u0000\u0096\u0097"+
		"\u0001\u0000\u0000\u0000\u0097\u00a5\u0003@ \u0000\u0098\u009a\u0005\u0019"+
		"\u0000\u0000\u0099\u009b\u0005 \u0000\u0000\u009a\u0099\u0001\u0000\u0000"+
		"\u0000\u009a\u009b\u0001\u0000\u0000\u0000\u009b\u009c\u0001\u0000\u0000"+
		"\u0000\u009c\u009d\u0003@ \u0000\u009d\u009f\u0005\u001b\u0000\u0000\u009e"+
		"\u00a0\u0003\u0012\t\u0000\u009f\u009e\u0001\u0000\u0000\u0000\u009f\u00a0"+
		"\u0001\u0000\u0000\u0000\u00a0\u00a2\u0001\u0000\u0000\u0000\u00a1\u00a3"+
		"\u0003 \u0010\u0000\u00a2\u00a1\u0001\u0000\u0000\u0000\u00a2\u00a3\u0001"+
		"\u0000\u0000\u0000\u00a3\u00a5\u0001\u0000\u0000\u0000\u00a4\u008c\u0001"+
		"\u0000\u0000\u0000\u00a4\u0098\u0001\u0000\u0000\u0000\u00a5\u000f\u0001"+
		"\u0000\u0000\u0000\u00a6\u00a8\u0005\u001c\u0000\u0000\u00a7\u00a9\u0003"+
		"\u0014\n\u0000\u00a8\u00a7\u0001\u0000\u0000\u0000\u00a8\u00a9\u0001\u0000"+
		"\u0000\u0000\u00a9\u00ab\u0001\u0000\u0000\u0000\u00aa\u00ac\u0003 \u0010"+
		"\u0000\u00ab\u00aa\u0001\u0000\u0000\u0000\u00ab\u00ac\u0001\u0000\u0000"+
		"\u0000\u00ac\u00ad\u0001\u0000\u0000\u0000\u00ad\u00af\u0005\u001a\u0000"+
		"\u0000\u00ae\u00b0\u0005 \u0000\u0000\u00af\u00ae\u0001\u0000\u0000\u0000"+
		"\u00af\u00b0\u0001\u0000\u0000\u0000\u00b0\u00b1\u0001\u0000\u0000\u0000"+
		"\u00b1\u00bf\u0003@ \u0000\u00b2\u00b4\u0005\u001a\u0000\u0000\u00b3\u00b5"+
		"\u0005 \u0000\u0000\u00b4\u00b3\u0001\u0000\u0000\u0000\u00b4\u00b5\u0001"+
		"\u0000\u0000\u0000\u00b5\u00b6\u0001\u0000\u0000\u0000\u00b6\u00b7\u0003"+
		"@ \u0000\u00b7\u00b9\u0005\u001c\u0000\u0000\u00b8\u00ba\u0003\u0014\n"+
		"\u0000\u00b9\u00b8\u0001\u0000\u0000\u0000\u00b9\u00ba\u0001\u0000\u0000"+
		"\u0000\u00ba\u00bc\u0001\u0000\u0000\u0000\u00bb\u00bd\u0003 \u0010\u0000"+
		"\u00bc\u00bb\u0001\u0000\u0000\u0000\u00bc\u00bd\u0001\u0000\u0000\u0000"+
		"\u00bd\u00bf\u0001\u0000\u0000\u0000\u00be\u00a6\u0001\u0000\u0000\u0000"+
		"\u00be\u00b2\u0001\u0000\u0000\u0000\u00bf\u0011\u0001\u0000\u0000\u0000"+
		"\u00c0\u00c1\u0003\u0016\u000b\u0000\u00c1\u0013\u0001\u0000\u0000\u0000"+
		"\u00c2\u00c3\u0003\u0016\u000b\u0000\u00c3\u0015\u0001\u0000\u0000\u0000"+
		"\u00c4\u00c9\u0003\u0018\f\u0000\u00c5\u00c6\u0005>\u0000\u0000\u00c6"+
		"\u00c8\u0003\u0018\f\u0000\u00c7\u00c5\u0001\u0000\u0000\u0000\u00c8\u00cb"+
		"\u0001\u0000\u0000\u0000\u00c9\u00c7\u0001\u0000\u0000\u0000\u00c9\u00ca"+
		"\u0001\u0000\u0000\u0000\u00ca\u00cd\u0001\u0000\u0000\u0000\u00cb\u00c9"+
		"\u0001\u0000\u0000\u0000\u00cc\u00ce\u0005>\u0000\u0000\u00cd\u00cc\u0001"+
		"\u0000\u0000\u0000\u00cd\u00ce\u0001\u0000\u0000\u0000\u00ce\u0017\u0001"+
		"\u0000\u0000\u0000\u00cf\u00d1\u0003\u001a\r\u0000\u00d0\u00cf\u0001\u0000"+
		"\u0000\u0000\u00d0\u00d1\u0001\u0000\u0000\u0000\u00d1\u00d2\u0001\u0000"+
		"\u0000\u0000\u00d2\u00d4\u0003&\u0013\u0000\u00d3\u00d5\u0003(\u0014\u0000"+
		"\u00d4\u00d3\u0001\u0000\u0000\u0000\u00d4\u00d5\u0001\u0000\u0000\u0000"+
		"\u00d5\u00dc\u0001\u0000\u0000\u0000\u00d6\u00d8\u0003\u001a\r\u0000\u00d7"+
		"\u00d9\u0003(\u0014\u0000\u00d8\u00d7\u0001\u0000\u0000\u0000\u00d8\u00d9"+
		"\u0001\u0000\u0000\u0000\u00d9\u00dc\u0001\u0000\u0000\u0000\u00da\u00dc"+
		"\u0003(\u0014\u0000\u00db\u00d0\u0001\u0000\u0000\u0000\u00db\u00d6\u0001"+
		"\u0000\u0000\u0000\u00db\u00da\u0001\u0000\u0000\u0000\u00dc\u0019\u0001"+
		"\u0000\u0000\u0000\u00dd\u00de\u0003\u001c\u000e\u0000\u00de\u00df\u0003"+
		"\u001e\u000f\u0000\u00df\u00e3\u0001\u0000\u0000\u0000\u00e0\u00e3\u0003"+
		"\u001e\u000f\u0000\u00e1\u00e3\u0003\u001c\u000e\u0000\u00e2\u00dd\u0001"+
		"\u0000\u0000\u0000\u00e2\u00e0\u0001\u0000\u0000\u0000\u00e2\u00e1\u0001"+
		"\u0000\u0000\u0000\u00e3\u001b\u0001\u0000\u0000\u0000\u00e4\u00e6\u0003"+
		"J%\u0000\u00e5\u00e7\u0005 \u0000\u0000\u00e6\u00e5\u0001\u0000\u0000"+
		"\u0000\u00e6\u00e7\u0001\u0000\u0000\u0000\u00e7\u001d\u0001\u0000\u0000"+
		"\u0000\u00e8\u00e9\u0005\u001f\u0000\u0000\u00e9\u00eb\u0003J%\u0000\u00ea"+
		"\u00ec\u0005 \u0000\u0000\u00eb\u00ea\u0001\u0000\u0000\u0000\u00eb\u00ec"+
		"\u0001\u0000\u0000\u0000\u00ec\u001f\u0001\u0000\u0000\u0000\u00ed\u00ee"+
		"\u0005!\u0000\u0000\u00ee\u00ef\u0003$\u0012\u0000\u00ef!\u0001\u0000"+
		"\u0000\u0000\u00f0\u0101\u0003F#\u0000\u00f1\u00f3\u0005?\u0000\u0000"+
		"\u00f2\u00f4\u0003F#\u0000\u00f3\u00f2\u0001\u0000\u0000\u0000\u00f3\u00f4"+
		"\u0001\u0000\u0000\u0000\u00f4\u00ff\u0001\u0000\u0000\u0000\u00f5\u00f7"+
		"\u0005?\u0000\u0000\u00f6\u00f8\u0003F#\u0000\u00f7\u00f6\u0001\u0000"+
		"\u0000\u0000\u00f7\u00f8\u0001\u0000\u0000\u0000\u00f8\u00fd\u0001\u0000"+
		"\u0000\u0000\u00f9\u00fb\u0005?\u0000\u0000\u00fa\u00fc\u0003F#\u0000"+
		"\u00fb\u00fa\u0001\u0000\u0000\u0000\u00fb\u00fc\u0001\u0000\u0000\u0000"+
		"\u00fc\u00fe\u0001\u0000\u0000\u0000\u00fd\u00f9\u0001\u0000\u0000\u0000"+
		"\u00fd\u00fe\u0001\u0000\u0000\u0000\u00fe\u0100\u0001\u0000\u0000\u0000"+
		"\u00ff\u00f5\u0001\u0000\u0000\u0000\u00ff\u0100\u0001\u0000\u0000\u0000"+
		"\u0100\u0102\u0001\u0000\u0000\u0000\u0101\u00f1\u0001\u0000\u0000\u0000"+
		"\u0101\u0102\u0001\u0000\u0000\u0000\u0102\u0105\u0001\u0000\u0000\u0000"+
		"\u0103\u0105\u0003H$\u0000\u0104\u00f0\u0001\u0000\u0000\u0000\u0104\u0103"+
		"\u0001\u0000\u0000\u0000\u0105#\u0001\u0000\u0000\u0000\u0106\u010b\u0003"+
		"\"\u0011\u0000\u0107\u0108\u0005>\u0000\u0000\u0108\u010a\u0003\"\u0011"+
		"\u0000\u0109\u0107\u0001\u0000\u0000\u0000\u010a\u010d\u0001\u0000\u0000"+
		"\u0000\u010b\u0109\u0001\u0000\u0000\u0000\u010b\u010c\u0001\u0000\u0000"+
		"\u0000\u010c\u010f\u0001\u0000\u0000\u0000\u010d\u010b\u0001\u0000\u0000"+
		"\u0000\u010e\u0110\u0005>\u0000\u0000\u010f\u010e\u0001\u0000\u0000\u0000"+
		"\u010f\u0110\u0001\u0000\u0000\u0000\u0110%\u0001\u0000\u0000\u0000\u0111"+
		"\u0116\u0003\"\u0011\u0000\u0112\u0113\u0005\r\u0000\u0000\u0113\u0115"+
		"\u0003\"\u0011\u0000\u0114\u0112\u0001\u0000\u0000\u0000\u0115\u0118\u0001"+
		"\u0000\u0000\u0000\u0116\u0114\u0001\u0000\u0000\u0000\u0116\u0117\u0001"+
		"\u0000\u0000\u0000\u0117\u011a\u0001\u0000\u0000\u0000\u0118\u0116\u0001"+
		"\u0000\u0000\u0000\u0119\u011b\u0005\r\u0000\u0000\u011a\u0119\u0001\u0000"+
		"\u0000\u0000\u011a\u011b\u0001\u0000\u0000\u0000\u011b\'\u0001\u0000\u0000"+
		"\u0000\u011c\u011d\u0005$\u0000\u0000\u011d\u0121\u0003*\u0015\u0000\u011e"+
		"\u011f\u0005#\u0000\u0000\u011f\u0121\u0003*\u0015\u0000\u0120\u011c\u0001"+
		"\u0000\u0000\u0000\u0120\u011e\u0001\u0000\u0000\u0000\u0121)\u0001\u0000"+
		"\u0000\u0000\u0122\u0123\u0006\u0015\uffff\uffff\u0000\u0123\u0124\u0005"+
		"B\u0000\u0000\u0124\u0125\u0003*\u0015\u0000\u0125\u0126\u0005C\u0000"+
		"\u0000\u0126\u0132\u0001\u0000\u0000\u0000\u0127\u0128\u0005\u000b\u0000"+
		"\u0000\u0128\u0132\u0003*\u0015\u0004\u0129\u012b\u0005%\u0000\u0000\u012a"+
		"\u012c\u0005&\u0000\u0000\u012b\u012a\u0001\u0000\u0000\u0000\u012b\u012c"+
		"\u0001\u0000\u0000\u0000\u012c\u012f\u0001\u0000\u0000\u0000\u012d\u012f"+
		"\u0005&\u0000\u0000\u012e\u0129\u0001\u0000\u0000\u0000\u012e\u012d\u0001"+
		"\u0000\u0000\u0000\u012f\u0130\u0001\u0000\u0000\u0000\u0130\u0132\u0003"+
		",\u0016\u0000\u0131\u0122\u0001\u0000\u0000\u0000\u0131\u0127\u0001\u0000"+
		"\u0000\u0000\u0131\u012e\u0001\u0000\u0000\u0000\u0132\u013b\u0001\u0000"+
		"\u0000\u0000\u0133\u0134\n\u0003\u0000\u0000\u0134\u0135\u0005\f\u0000"+
		"\u0000\u0135\u013a\u0003*\u0015\u0004\u0136\u0137\n\u0002\u0000\u0000"+
		"\u0137\u0138\u0005\r\u0000\u0000\u0138\u013a\u0003*\u0015\u0003\u0139"+
		"\u0133\u0001\u0000\u0000\u0000\u0139\u0136\u0001\u0000\u0000\u0000\u013a"+
		"\u013d\u0001\u0000\u0000\u0000\u013b\u0139\u0001\u0000\u0000\u0000\u013b"+
		"\u013c\u0001\u0000\u0000\u0000\u013c+\u0001\u0000\u0000\u0000\u013d\u013b"+
		"\u0001\u0000\u0000\u0000\u013e\u013f\u0003F#\u0000\u013f\u0140\u0005?"+
		"\u0000\u0000\u0140\u0145\u0003F#\u0000\u0141\u0142\u0005@\u0000\u0000"+
		"\u0142\u0144\u0003F#\u0000\u0143\u0141\u0001\u0000\u0000\u0000\u0144\u0147"+
		"\u0001\u0000\u0000\u0000\u0145\u0143\u0001\u0000\u0000\u0000\u0145\u0146"+
		"\u0001\u0000\u0000\u0000\u0146\u0151\u0001\u0000\u0000\u0000\u0147\u0145"+
		"\u0001\u0000\u0000\u0000\u0148\u014d\u0003F#\u0000\u0149\u014a\u0005@"+
		"\u0000\u0000\u014a\u014c\u0003F#\u0000\u014b\u0149\u0001\u0000\u0000\u0000"+
		"\u014c\u014f\u0001\u0000\u0000\u0000\u014d\u014b\u0001\u0000\u0000\u0000"+
		"\u014d\u014e\u0001\u0000\u0000\u0000\u014e\u0151\u0001\u0000\u0000\u0000"+
		"\u014f\u014d\u0001\u0000\u0000\u0000\u0150\u013e\u0001\u0000\u0000\u0000"+
		"\u0150\u0148\u0001\u0000\u0000\u0000\u0151-\u0001\u0000\u0000\u0000\u0152"+
		"\u0153\u0005 \u0000\u0000\u0153\u015f\u00052\u0000\u0000\u0154\u0159\u0003"+
		"0\u0018\u0000\u0155\u0156\u0005>\u0000\u0000\u0156\u0158\u00030\u0018"+
		"\u0000\u0157\u0155\u0001\u0000\u0000\u0000\u0158\u015b\u0001\u0000\u0000"+
		"\u0000\u0159\u0157\u0001\u0000\u0000\u0000\u0159\u015a\u0001\u0000\u0000"+
		"\u0000\u015a\u015c\u0001\u0000\u0000\u0000\u015b\u0159\u0001\u0000\u0000"+
		"\u0000\u015c\u015d\u00052\u0000\u0000\u015d\u015f\u0001\u0000\u0000\u0000"+
		"\u015e\u0152\u0001\u0000\u0000\u0000\u015e\u0154\u0001\u0000\u0000\u0000"+
		"\u015f/\u0001\u0000\u0000\u0000\u0160\u0161\u0007\u0000\u0000\u0000\u0161"+
		"1\u0001\u0000\u0000\u0000\u0162\u0163\u0005\u001e\u0000\u0000\u0163\u0164"+
		"\u00034\u001a\u0000\u01643\u0001\u0000\u0000\u0000\u0165\u016a\u00036"+
		"\u001b\u0000\u0166\u0167\u0005>\u0000\u0000\u0167\u0169\u00036\u001b\u0000"+
		"\u0168\u0166\u0001\u0000\u0000\u0000\u0169\u016c\u0001\u0000\u0000\u0000"+
		"\u016a\u0168\u0001\u0000\u0000\u0000\u016a\u016b\u0001\u0000\u0000\u0000"+
		"\u016b5\u0001\u0000\u0000\u0000\u016c\u016a\u0001\u0000\u0000\u0000\u016d"+
		"\u0170\u0003J%\u0000\u016e\u016f\u0005A\u0000\u0000\u016f\u0171\u0003"+
		"J%\u0000\u0170\u016e\u0001\u0000\u0000\u0000\u0170\u0171\u0001\u0000\u0000"+
		"\u0000\u01717\u0001\u0000\u0000\u0000\u0172\u0173\u0005\u0001\u0000\u0000"+
		"\u0173\u0174\u0003:\u001d\u0000\u0174\u0175\u0005\u0002\u0000\u0000\u0175"+
		"\u017e\u0003\b\u0004\u0000\u0176\u0177\u0005\u0003\u0000\u0000\u0177\u0178"+
		"\u0005\u0001\u0000\u0000\u0178\u0179\u0003:\u001d\u0000\u0179\u017a\u0005"+
		"\u0002\u0000\u0000\u017a\u017b\u0003\b\u0004\u0000\u017b\u017d\u0001\u0000"+
		"\u0000\u0000\u017c\u0176\u0001\u0000\u0000\u0000\u017d\u0180\u0001\u0000"+
		"\u0000\u0000\u017e\u017c\u0001\u0000\u0000\u0000\u017e\u017f\u0001\u0000"+
		"\u0000\u0000\u017f\u0183\u0001\u0000\u0000\u0000\u0180\u017e\u0001\u0000"+
		"\u0000\u0000\u0181\u0182\u0005\u0003\u0000\u0000\u0182\u0184\u0003\b\u0004"+
		"\u0000\u0183\u0181\u0001\u0000\u0000\u0000\u0183\u0184\u0001\u0000\u0000"+
		"\u0000\u0184\u0185\u0001\u0000\u0000\u0000\u0185\u0186\u0005;\u0000\u0000"+
		"\u01869\u0001\u0000\u0000\u0000\u0187\u0188\u0006\u001d\uffff\uffff\u0000"+
		"\u0188\u01a8\u0005\t\u0000\u0000\u0189\u01a8\u0005\n\u0000\u0000\u018a"+
		"\u018b\u0005B\u0000\u0000\u018b\u018c\u0003:\u001d\u0000\u018c\u018d\u0005"+
		"C\u0000\u0000\u018d\u01a8\u0001\u0000\u0000\u0000\u018e\u018f\u0005\u000b"+
		"\u0000\u0000\u018f\u01a8\u0003:\u001d\u0005\u0190\u0192\u0003>\u001f\u0000"+
		"\u0191\u0190\u0001\u0000\u0000\u0000\u0191\u0192\u0001\u0000\u0000\u0000"+
		"\u0192\u0193\u0001\u0000\u0000\u0000\u0193\u0194\u0003@ \u0000\u0194\u0195"+
		"\u0005\u0004\u0000\u0000\u0195\u0196\u0003<\u001e\u0000\u0196\u0198\u0003"+
		"J%\u0000\u0197\u0199\u0003&\u0013\u0000\u0198\u0197\u0001\u0000\u0000"+
		"\u0000\u0198\u0199\u0001\u0000\u0000\u0000\u0199\u019b\u0001\u0000\u0000"+
		"\u0000\u019a\u019c\u0003(\u0014\u0000\u019b\u019a\u0001\u0000\u0000\u0000"+
		"\u019b\u019c\u0001\u0000\u0000\u0000\u019c\u019f\u0001\u0000\u0000\u0000"+
		"\u019d\u019e\u0005!\u0000\u0000\u019e\u01a0\u0003$\u0012\u0000\u019f\u019d"+
		"\u0001\u0000\u0000\u0000\u019f\u01a0\u0001\u0000\u0000\u0000\u01a0\u01a8"+
		"\u0001\u0000\u0000\u0000\u01a1\u01a5\u00056\u0000\u0000\u01a2\u01a3\u0003"+
		"<\u001e\u0000\u01a3\u01a4\u0003J%\u0000\u01a4\u01a6\u0001\u0000\u0000"+
		"\u0000\u01a5\u01a2\u0001\u0000\u0000\u0000\u01a5\u01a6\u0001\u0000\u0000"+
		"\u0000\u01a6\u01a8\u0001\u0000\u0000\u0000\u01a7\u0187\u0001\u0000\u0000"+
		"\u0000\u01a7\u0189\u0001\u0000\u0000\u0000\u01a7\u018a\u0001\u0000\u0000"+
		"\u0000\u01a7\u018e\u0001\u0000\u0000\u0000\u01a7\u0191\u0001\u0000\u0000"+
		"\u0000\u01a7\u01a1\u0001\u0000\u0000\u0000\u01a8\u01b1\u0001\u0000\u0000"+
		"\u0000\u01a9\u01aa\n\u0004\u0000\u0000\u01aa\u01ab\u0005\f\u0000\u0000"+
		"\u01ab\u01b0\u0003:\u001d\u0005\u01ac\u01ad\n\u0003\u0000\u0000\u01ad"+
		"\u01ae\u0005\r\u0000\u0000\u01ae\u01b0\u0003:\u001d\u0004\u01af\u01a9"+
		"\u0001\u0000\u0000\u0000\u01af\u01ac\u0001\u0000\u0000\u0000\u01b0\u01b3"+
		"\u0001\u0000\u0000\u0000\u01b1\u01af\u0001\u0000\u0000\u0000\u01b1\u01b2"+
		"\u0001\u0000\u0000\u0000\u01b2;\u0001\u0000\u0000\u0000\u01b3\u01b1\u0001"+
		"\u0000\u0000\u0000\u01b4\u01b5\u0007\u0001\u0000\u0000\u01b5=\u0001\u0000"+
		"\u0000\u0000\u01b6\u01b7\u0007\u0002\u0000\u0000\u01b7?\u0001\u0000\u0000"+
		"\u0000\u01b8\u01bd\u0003D\"\u0000\u01b9\u01ba\u0005>\u0000\u0000\u01ba"+
		"\u01bc\u0003D\"\u0000\u01bb\u01b9\u0001\u0000\u0000\u0000\u01bc\u01bf"+
		"\u0001\u0000\u0000\u0000\u01bd\u01bb\u0001\u0000\u0000\u0000\u01bd\u01be"+
		"\u0001\u0000\u0000\u0000\u01be\u01c1\u0001\u0000\u0000\u0000\u01bf\u01bd"+
		"\u0001\u0000\u0000\u0000\u01c0\u01c2\u0003B!\u0000\u01c1\u01c0\u0001\u0000"+
		"\u0000\u0000\u01c1\u01c2\u0001\u0000\u0000\u0000\u01c2\u01c4\u0001\u0000"+
		"\u0000\u0000\u01c3\u01c5\u0003.\u0017\u0000\u01c4\u01c3\u0001\u0000\u0000"+
		"\u0000\u01c4\u01c5\u0001\u0000\u0000\u0000\u01c5\u01c7\u0001\u0000\u0000"+
		"\u0000\u01c6\u01c8\u00032\u0019\u0000\u01c7\u01c6\u0001\u0000\u0000\u0000"+
		"\u01c7\u01c8\u0001\u0000\u0000\u0000\u01c8A\u0001\u0000\u0000\u0000\u01c9"+
		"\u01ca\u0005\'\u0000\u0000\u01ca\u01cb\u0005(\u0000\u0000\u01cb\u01cc"+
		"\u0005)\u0000\u0000\u01cc\u01cd\u0007\u0003\u0000\u0000\u01cdC\u0001\u0000"+
		"\u0000\u0000\u01ce\u01d1\u0003F#\u0000\u01cf\u01d1\u0003H$\u0000\u01d0"+
		"\u01ce\u0001\u0000\u0000\u0000\u01d0\u01cf\u0001\u0000\u0000\u0000\u01d1"+
		"E\u0001\u0000\u0000\u0000\u01d2\u01d3\u0007\u0004\u0000\u0000\u01d3G\u0001"+
		"\u0000\u0000\u0000\u01d4\u01d5\u0005F\u0000\u0000\u01d5I\u0001\u0000\u0000"+
		"\u0000\u01d6\u01d7\u0005E\u0000\u0000\u01d7K\u0001\u0000\u0000\u0000G"+
		"MRgpu|\u0080\u0086\u008a\u008e\u0091\u0095\u009a\u009f\u00a2\u00a4\u00a8"+
		"\u00ab\u00af\u00b4\u00b9\u00bc\u00be\u00c9\u00cd\u00d0\u00d4\u00d8\u00db"+
		"\u00e2\u00e6\u00eb\u00f3\u00f7\u00fb\u00fd\u00ff\u0101\u0104\u010b\u010f"+
		"\u0116\u011a\u0120\u012b\u012e\u0131\u0139\u013b\u0145\u014d\u0150\u0159"+
		"\u015e\u016a\u0170\u017e\u0183\u0191\u0198\u019b\u019f\u01a5\u01a7\u01af"+
		"\u01b1\u01bd\u01c1\u01c4\u01c7\u01d0";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}