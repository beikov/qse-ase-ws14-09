package at.ac.tuwien.ase09.parser;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.antlr.v4.runtime.ParserRuleContext;
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
import at.ac.tuwien.ase09.parser.PWatchParser.String_attributeContext;
import at.ac.tuwien.ase09.parser.PWatchParser.TermExpressionContext;
import at.ac.tuwien.ase09.parser.PWatchParser.TimestampLiteralContext;

public class AdvancedPWatchCompiler extends PWatchBaseVisitor<CharSequence> {

	public static final String PRICE_ENTRY_ALIAS = "priceEntry";
	public static final String VALUE_PAPER_ALIAS = "stock";
	private static final Map<String, String> ATTRIBUTE_MAPPING = new HashMap<>();
	
    private boolean usesPrice = false;
    private boolean usesOtherAttrs = false;
	
	static {
		ATTRIBUTE_MAPPING.put("TRAILING_PE", "trailingPE");
		ATTRIBUTE_MAPPING.put("FORWARD_PE", "forwardPE");
		ATTRIBUTE_MAPPING.put("PEG_RATIO", "pEGRatio");
		ATTRIBUTE_MAPPING.put("ENTERPRISE_VALUE_EBITDA", "enterpriseValueEBITDA");
		ATTRIBUTE_MAPPING.put("RETURN_ON_ASSETS", "returnonAssets");
		ATTRIBUTE_MAPPING.put("RETURN_ON_EQUITY", "returnonEquity");
		ATTRIBUTE_MAPPING.put("NET_INCOME_AVL_TO_COMMON", "netIncomeAvltoCommon");
		ATTRIBUTE_MAPPING.put("DILUTED_EPS", "dilutedEPS");
		ATTRIBUTE_MAPPING.put("52_WEEK_CHANGE", "p_52_WeekChange");
		ATTRIBUTE_MAPPING.put("52_WEEK_HIGH", "p_52_WeekHigh");
		ATTRIBUTE_MAPPING.put("52_WEEK_LOW", "p_52_WeekLow");
		ATTRIBUTE_MAPPING.put("50_DAY_MOVING_AVERAGE", "p_50_DayMovingAverage");
		ATTRIBUTE_MAPPING.put("200_DAY_MOVING_AVERAGE", "p_200_DayMovingAverage");
		ATTRIBUTE_MAPPING.put("AVG_VOL_3_MONTH", "avgVol_3month");
		ATTRIBUTE_MAPPING.put("AVG_VOL_10_DAY", "avgVol_10day");
		ATTRIBUTE_MAPPING.put("FLOAT", "floatVal");
		ATTRIBUTE_MAPPING.put("PERCENTAGE_HELD_BY_INSIDERS", "percentageHeldbyInsiders");
		ATTRIBUTE_MAPPING.put("PERCENTAGE_HELD_BY_INSTITUTIONS", "percentageHeldbyInstitutions");
		ATTRIBUTE_MAPPING.put("SHORT_PERCENTAGE_OF_FLOAT", "shortPercentageofFloat");
		ATTRIBUTE_MAPPING.put("5_YEAR_AVERAGE_DIVIDEND_YIELD", "p_5YearAverageDividendYield");
		ATTRIBUTE_MAPPING.put("EX_DIVIDEND_DATE", "ex_DividendDate");
	}

	public boolean usesPrice() {
		return usesPrice;
	}

	public boolean usesOtherAttrs() {
		return usesOtherAttrs;
	}

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
	
	private String resolveEplAttributePath(String attributeName) {
		String eplAttribute = ATTRIBUTE_MAPPING.get(attributeName);
		
		if (eplAttribute == null) {
			eplAttribute = toCamelCase(attributeName);
		}
		
		if ("price".equals(eplAttribute)) {
			usesPrice = true;
			return PRICE_ENTRY_ALIAS + ".price";
		} else {
			usesOtherAttrs = true;
			return VALUE_PAPER_ALIAS + "." + eplAttribute;
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
		return ctx.string_attribute().accept(this);
	}

	@Override
	public CharSequence visitString_attribute(String_attributeContext ctx) {
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
		matcher.matches();
		
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
