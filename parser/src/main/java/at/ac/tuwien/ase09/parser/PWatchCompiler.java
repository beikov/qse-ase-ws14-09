package at.ac.tuwien.ase09.parser;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.TerminalNode;

import at.ac.tuwien.ase09.parser.PWatchParser.AdditiveExpressionContext;
import at.ac.tuwien.ase09.parser.PWatchParser.AndExpressionContext;
import at.ac.tuwien.ase09.parser.PWatchParser.ArithmeticAttributeContext;
import at.ac.tuwien.ase09.parser.PWatchParser.ArithmeticComparisonExpressionContext;
import at.ac.tuwien.ase09.parser.PWatchParser.ArithmeticFunctionContext;
import at.ac.tuwien.ase09.parser.PWatchParser.ArithmeticLiteralContext;
import at.ac.tuwien.ase09.parser.PWatchParser.ArithmeticNestedContext;
import at.ac.tuwien.ase09.parser.PWatchParser.Arithmetic_factorContext;
import at.ac.tuwien.ase09.parser.PWatchParser.Comparison_operatorContext;
import at.ac.tuwien.ase09.parser.PWatchParser.Conditional_factorContext;
import at.ac.tuwien.ase09.parser.PWatchParser.CurrentTimestampContext;
import at.ac.tuwien.ase09.parser.PWatchParser.DateTimeAttributeContext;
import at.ac.tuwien.ase09.parser.PWatchParser.DateTimeComparisonExpressionContext;
import at.ac.tuwien.ase09.parser.PWatchParser.Equality_comparison_operatorContext;
import at.ac.tuwien.ase09.parser.PWatchParser.FactorExpressionContext;
import at.ac.tuwien.ase09.parser.PWatchParser.MultiplicativeExpressionContext;
import at.ac.tuwien.ase09.parser.PWatchParser.NestedExpressionContext;
import at.ac.tuwien.ase09.parser.PWatchParser.OrExpressionContext;
import at.ac.tuwien.ase09.parser.PWatchParser.SimpleArithmeticFactorContext;
import at.ac.tuwien.ase09.parser.PWatchParser.SimpleArithmeticTermContext;
import at.ac.tuwien.ase09.parser.PWatchParser.SimpleExpressionContext;
import at.ac.tuwien.ase09.parser.PWatchParser.StartContext;
import at.ac.tuwien.ase09.parser.PWatchParser.StringAttributeContext;
import at.ac.tuwien.ase09.parser.PWatchParser.StringComparisonExpressionContext;
import at.ac.tuwien.ase09.parser.PWatchParser.StringLiteralContext;
import at.ac.tuwien.ase09.parser.PWatchParser.TermExpressionContext;
import at.ac.tuwien.ase09.parser.PWatchParser.TimestampLiteralContext;

public class PWatchCompiler extends PWatchBaseVisitor<CharSequence> {

	private static final String PRICE_ENTRY_ALIAS = "priceEntry";
	private static final String STOCK_ALIAS = "stock";
    private boolean usesPrice = false;
    private boolean usesOtherAttrs = false;
    
    public static String compileJpql(String pwatchExpression) {
		if (pwatchExpression == null) {
            throw new NullPointerException("pwatchExpression");
        }
        if (pwatchExpression.isEmpty()) {
            throw new IllegalArgumentException("pwatchExpression");
        }
        
        PWatchLexer lexer = new PWatchLexer(new ANTLRInputStream(pwatchExpression));
        lexer.removeErrorListeners();
        lexer.addErrorListener(ERR_LISTENER);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        boolean stringExpressionAllowed = true;
        PWatchParser parser = new PWatchParser(tokens, stringExpressionAllowed);
        parser.removeErrorListeners();
        parser.addErrorListener(ERR_LISTENER);
        
        ParserRuleContext ctx = parser.start();
        
        PWatchCompiler visitor = new PWatchCompiler();
        CharSequence result = visitor.visit(ctx);
        StringBuilder sb = new StringBuilder(result.length() + 100);
        sb.append("SELECT ");
    	sb.append(STOCK_ALIAS);
    	sb.append(" FROM Stock ");
    	sb.append(STOCK_ALIAS);
    	sb.append(' ');
        
        if (!visitor.usesPrice && ! visitor.usesOtherAttrs) {
        	throw new IllegalArgumentException("The given expression does not reference any attributes!");
        }
        if (visitor.usesPrice) {
        	sb.append("JOIN ");
        	sb.append(STOCK_ALIAS);
        	sb.append(".priceEntries ");
        	sb.append(PRICE_ENTRY_ALIAS);
        	sb.append(" WHERE ");
        	sb.append(PRICE_ENTRY_ALIAS);
        	sb.append(".created = (SELECT MAX(e.created) FROM ValuePaperPriceEntry e WHERE e.valuePaper = ");
        	sb.append(STOCK_ALIAS);
        	sb.append(") "); 
        }
        
        sb.append("AND ");
        sb.append(result);
        
        return sb.toString();
    }

	public static String compileEpl(String pwatchExpression, Long valuePaperId) {
		if (pwatchExpression == null) {
            throw new NullPointerException("pwatchExpression");
        }
		if (valuePaperId == null) {
            throw new NullPointerException("valuePaperId");
		}
        if (pwatchExpression.isEmpty()) {
            throw new IllegalArgumentException("pwatchExpression");
        }
        
        PWatchLexer lexer = new PWatchLexer(new ANTLRInputStream(pwatchExpression));
        lexer.removeErrorListeners();
        lexer.addErrorListener(ERR_LISTENER);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        boolean stringExpressionAllowed = false;
        PWatchParser parser = new PWatchParser(tokens, stringExpressionAllowed);
        parser.removeErrorListeners();
        parser.addErrorListener(ERR_LISTENER);
        
        ParserRuleContext ctx = parser.start();
        
        PWatchCompiler visitor = new PWatchCompiler();
        CharSequence result = visitor.visit(ctx);
        StringBuilder sb = new StringBuilder(result.length() + 100);
        sb.append("SELECT 1 FROM ");
        
        if (!visitor.usesPrice && ! visitor.usesOtherAttrs) {
        	throw new IllegalArgumentException("The given expression does not reference any attributes!");
        }
        if (visitor.usesPrice) {
        	sb.append("ValuePaperPriceEntry(id = ");
        	sb.append(valuePaperId);
        	sb.append(").std:lastevent() AS ");
        	sb.append(PRICE_ENTRY_ALIAS);
        	sb.append(' ');
        }
        if (visitor.usesOtherAttrs) {
        	sb.append("Stock(id = ");
        	sb.append(valuePaperId);
        	sb.append(").std:lastevent() AS ");
        	sb.append(STOCK_ALIAS);
        	sb.append(' ');
        }
        
        sb.append("WHERE ");
        sb.append(result);
        return sb.toString();
	}

    protected static final ANTLRErrorListener ERR_LISTENER = new ANTLRErrorListener() {

        @Override
        public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
            throw new SyntaxErrorException("line " + line + ":" + charPositionInLine + " " + msg);
        }

        @Override
        public void reportAmbiguity(Parser recognizer, DFA dfa, int startIndex, int stopIndex, boolean exact, BitSet ambigAlts, ATNConfigSet configs) {
        }

        @Override
        public void reportAttemptingFullContext(Parser recognizer, DFA dfa, int startIndex, int stopIndex, BitSet conflictingAlts, ATNConfigSet configs) {
        }

        @Override
        public void reportContextSensitivity(Parser recognizer, DFA dfa, int startIndex, int stopIndex, int prediction, ATNConfigSet configs) {
        }
    };

	@Override
	public CharSequence visitArithmeticNested(ArithmeticNestedContext ctx) {
		CharSequence nested = ctx.arithmetic_expression().accept(this);
		StringBuilder sb = new StringBuilder(nested.length() + 2);
		sb.append('(');
		sb.append(nested);
		sb.append(')');
		return sb;
	}

	@Override
	public CharSequence visitArithmeticFunction(ArithmeticFunctionContext ctx) {
		StringBuilder sb = new StringBuilder();
		String functionName = ctx.getStart().getText();
		
		handleFunction(functionName, sb, ctx);
		
		return sb;
	}
	
	private void handleFunction(String functionName, StringBuilder sb, ParserRuleContext ctx) {
		sb.append(functionName);
		sb.append('(');
		
		boolean first = true;
		for (int i = 0; i < ctx.getChildCount(); i++) {
            if (!(ctx.getChild(i) instanceof TerminalNode)) {
            	if (!first) {
            		sb.append(',');
            	} else {
            		first = false;
            	}
            	
                sb.append(ctx.getChild(i).accept(this));
            }
        }
		
		sb.append(')');
	}

	@Override
	public CharSequence visitArithmeticLiteral(ArithmeticLiteralContext ctx) {
		return ctx.getText();
	}

	@Override
	public CharSequence visitArithmeticAttribute(ArithmeticAttributeContext ctx) {
		return resolveEplAttributePath(ctx.getText());
	}
	
	private static final Map<String, String> attributeMapping = new HashMap<>();
	
	static {
		attributeMapping.put("TRAILING_PE", "trailingPE");
		attributeMapping.put("FORWARD_PE", "forwardPE");
		attributeMapping.put("PEG_RATIO", "pEGRatio");
		attributeMapping.put("ENTERPRISE_VALUE_EBITDA", "enterpriseValueEBITDA");
		attributeMapping.put("RETURN_ON_ASSETS", "returnonAssets");
		attributeMapping.put("RETURN_ON_EQUITY", "returnonEquity");
		attributeMapping.put("NET_INCOME_AVL_TO_COMMON", "netIncomeAvltoCommon");
		attributeMapping.put("DILUTED_EPS", "dilutedEPS");
		attributeMapping.put("52_WEEK_CHANGE", "p_52_WeekChange");
		attributeMapping.put("52_WEEK_HIGH", "p_52_WeekHigh");
		attributeMapping.put("52_WEEK_LOW", "p_52_WeekLow");
		attributeMapping.put("50_DAY_MOVING_AVERAGE", "p_50_DayMovingAverage");
		attributeMapping.put("200_DAY_MOVING_AVERAGE", "p_200_DayMovingAverage");
		attributeMapping.put("AVG_VOL_3_MONTH", "avgVol_3month");
		attributeMapping.put("AVG_VOL_10_DAY", "avgVol_10day");
		attributeMapping.put("FLOAT", "floatVal");
		attributeMapping.put("PERCENTAGE_HELD_BY_INSIDERS", "percentageHeldbyInsiders");
		attributeMapping.put("PERCENTAGE_HELD_BY_INSTITUTIONS", "percentageHeldbyInstitutions");
		attributeMapping.put("SHORT_PERCENTAGE_OF_FLOAT", "shortPercentageofFloat");
		attributeMapping.put("5_YEAR_AVERAGE_DIVIDEND_YIELD", "p_5YearAverageDividendYield");
		attributeMapping.put("EX_DIVIDEND_DATE", "ex_DividendDate");
	}
	
	private String resolveEplAttributePath(String attributeName) {
		String eplAttribute = attributeMapping.get(attributeName);
		
		if (eplAttribute == null) {
			eplAttribute = toCamelCase(attributeName);
		}
		
		if ("price".equals(eplAttribute)) {
			usesPrice = true;
			return PRICE_ENTRY_ALIAS + ".price";
		} else {
			usesOtherAttrs = true;
			return STOCK_ALIAS + "." + eplAttribute;
		}
	}
	
	private static String toCamelCase(String original) {
		StringBuilder sb = new StringBuilder(original.length());
		char[] chars = original.toCharArray();
		
		for (int i = 0; i < chars.length; i++) {
			if (chars[i] == '_') {
				i++;
				sb.append(Character.toUpperCase(chars[i]));
			} else {
				sb.append(Character.toLowerCase(chars[i]));
			}
		}
		
		return sb.toString();
	}
	
	@Override
	public CharSequence visitStringAttribute(StringAttributeContext ctx) {
		return resolveEplAttributePath(ctx.getText());
	}

	@Override
	public CharSequence visitCurrentTimestamp(CurrentTimestampContext ctx) {
		return ctx.getText();
	}

	@Override
	public CharSequence visitDateTimeAttribute(DateTimeAttributeContext ctx) {
		return resolveEplAttributePath(ctx.getText());
	}
	
	private static final Pattern timestampLiteralPattern = Pattern.compile("TIMESTAMP\\('(\\d\\d\\d\\d)\\-(\\d\\d)\\-(\\d\\d)(\\s(\\d\\d)\\:(\\d\\d)\\:(\\d\\d)(\\.(\\d\\d\\d))?)?'\\)");

	@Override
	public CharSequence visitTimestampLiteral(TimestampLiteralContext ctx) {
		Matcher matcher = timestampLiteralPattern.matcher(ctx.getText());
		// Since it comes from the parser, it should always match the pattern, but we need the call to matches to access the groups
		if (!matcher.matches()) {
			ERR_LISTENER.syntaxError(null, null, ctx.start.getLine(), ctx.start.getCharPositionInLine(), "Invalid timestamp literal!", null);
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append("CURRENT_TIMESTAMP.withDate(");
		sb.append(trim(matcher.group(1))).append(',');
		sb.append(trim(matcher.group(2))).append(',');
		sb.append(trim(matcher.group(3))).append(')');
		
		if (matcher.group(4) != null) {
			sb.append(".withTime(");
			sb.append(trim(matcher.group(5))).append(',');
			sb.append(trim(matcher.group(6))).append(',');
			sb.append(trim(matcher.group(7))).append(',');
			
			if (matcher.group(8) != null) {
				sb.append(trim(matcher.group(9))).append(')');
			} else {
				sb.append("0)");
			}
		}
		
		return sb;
	}
	
	private String trim(String s) {
		StringBuilder sb = new StringBuilder(s.length());
		int i = 0;
		for (; i < s.length(); i++) {
			if (s.charAt(i) != '0') {
				break;
			}
		}
		for (; i < s.length(); i++) {
			sb.append(s.charAt(i));
		}
		
		if (sb.length() == 0) {
			return "0";
		}
		
		return sb.toString();
	}

	@Override
	public CharSequence visitStringLiteral(StringLiteralContext ctx) {
		return ctx.getText();
	}

	@Override
	public CharSequence visitStringComparisonExpression(StringComparisonExpressionContext ctx) {
		return handleBinaryExpression(ctx.left, ctx.op, ctx.right);
	}

	@Override
	public CharSequence visitArithmeticComparisonExpression(ArithmeticComparisonExpressionContext ctx) {
		return handleBinaryExpression(ctx.left, ctx.op, ctx.right);
	}

	@Override
	public CharSequence visitDateTimeComparisonExpression(DateTimeComparisonExpressionContext ctx) {
		return handleBinaryExpression(ctx.left, ctx.op, ctx.right);
	}

	@Override
	public CharSequence visitComparison_operator(Comparison_operatorContext ctx) {
		return ctx.getText();
	}

	@Override
	public CharSequence visitEquality_comparison_operator(Equality_comparison_operatorContext ctx) {
		return ctx.getText();
	}

	@Override
	public CharSequence visitSimpleExpression(SimpleExpressionContext ctx) {
		return ctx.expr.accept(this);
	}

	@Override
	public CharSequence visitNestedExpression(NestedExpressionContext ctx) {
		CharSequence expr = ctx.expr.accept(this);
		
		StringBuilder sb = new StringBuilder(expr.length() + 2);
		sb.append('(');
		sb.append(expr);
		sb.append(')');
		
		return sb;
	}

	@Override
	public CharSequence visitConditional_factor(Conditional_factorContext ctx) {
		CharSequence expr = ctx.expr.accept(this);
		StringBuilder sb;
		
		if (ctx.not != null) {
			sb = new StringBuilder(expr.length() + 4);
			
			sb.append("NOT ");
			sb.append(expr);
			return sb;
		} else {
			return expr;
		}
	}

	@Override
	public CharSequence visitFactorExpression(FactorExpressionContext ctx) {
		return ctx.factor.accept(this);
	}

	@Override
	public CharSequence visitAndExpression(AndExpressionContext ctx) {
		return handleBinaryExpression(ctx.left, "AND", ctx.factor);
	}

	@Override
	public CharSequence visitTermExpression(TermExpressionContext ctx) {
		return ctx.term.accept(this);
	}

	@Override
	public CharSequence visitOrExpression(OrExpressionContext ctx) {
		return handleBinaryExpression(ctx.left, "OR", ctx.term);
	}

	@Override
	public CharSequence visitStart(StartContext ctx) {
		return ctx.conditional_expression().accept(this);
	}

	@Override
	public CharSequence visitArithmetic_factor(Arithmetic_factorContext ctx) {
		CharSequence value = ctx.value.accept(this);
		StringBuilder sb;
		
		if (ctx.sign != null) {
			sb = new StringBuilder(value.length() + 1);
			sb.append(ctx.sign.getText());
		} else {
			sb = new StringBuilder(value.length());
		}
		
		sb.append(value);
		return sb;
	}

	@Override
	public CharSequence visitSimpleArithmeticTerm(SimpleArithmeticTermContext ctx) {
		return ctx.term.accept(this);
	}

	@Override
	public CharSequence visitSimpleArithmeticFactor(SimpleArithmeticFactorContext ctx) {
		return ctx.factor.accept(this);
	}

	@Override
	public CharSequence visitMultiplicativeExpression(MultiplicativeExpressionContext ctx) {
		return handleBinaryExpression(ctx.left, ctx.op.getText(), ctx.factor);
	}

	@Override
	public CharSequence visitAdditiveExpression(AdditiveExpressionContext ctx) {
		return handleBinaryExpression(ctx.left, ctx.op.getText(), ctx.term);
	}

	private CharSequence handleBinaryExpression(ParserRuleContext leftCtx, ParserRuleContext opCtx, ParserRuleContext rightCtx) {
		return handleBinaryExpression(leftCtx, opCtx.accept(this), rightCtx);
	}

	private CharSequence handleBinaryExpression(ParserRuleContext leftCtx, CharSequence op, ParserRuleContext rightCtx) {
		CharSequence left = leftCtx.accept(this);
		CharSequence right = rightCtx.accept(this);
		
		StringBuilder sb = new StringBuilder(left.length() + right.length() + op.length() + 2);
		sb.append(left).append(' ');
		sb.append(op).append(' ');
		sb.append(right);
		
		return sb;
	}
}
