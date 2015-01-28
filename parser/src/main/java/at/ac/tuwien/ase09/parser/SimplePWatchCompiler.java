package at.ac.tuwien.ase09.parser;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import at.ac.tuwien.ase09.model.filter.Attribute;
import at.ac.tuwien.ase09.model.filter.AttributeFilter;
import at.ac.tuwien.ase09.model.filter.OperatorType;
import at.ac.tuwien.ase09.parser.SimplePWatchParser.AndExpressionContext;
import at.ac.tuwien.ase09.parser.SimplePWatchParser.ArithmeticAttributeContext;
import at.ac.tuwien.ase09.parser.SimplePWatchParser.ArithmeticComparisonExpressionContext;
import at.ac.tuwien.ase09.parser.SimplePWatchParser.ArithmeticLiteralContext;
import at.ac.tuwien.ase09.parser.SimplePWatchParser.Comparison_operatorContext;
import at.ac.tuwien.ase09.parser.SimplePWatchParser.DateTimeAttributeContext;
import at.ac.tuwien.ase09.parser.SimplePWatchParser.DateTimeComparisonExpressionContext;
import at.ac.tuwien.ase09.parser.SimplePWatchParser.Equality_comparison_operatorContext;
import at.ac.tuwien.ase09.parser.SimplePWatchParser.FactorExpressionContext;
import at.ac.tuwien.ase09.parser.SimplePWatchParser.StartContext;
import at.ac.tuwien.ase09.parser.SimplePWatchParser.StringComparisonExpressionContext;
import at.ac.tuwien.ase09.parser.SimplePWatchParser.StringLiteralContext;
import at.ac.tuwien.ase09.parser.SimplePWatchParser.String_attributeContext;
import at.ac.tuwien.ase09.parser.SimplePWatchParser.TimestampLiteralContext;

public class SimplePWatchCompiler extends SimplePWatchBaseVisitor<List<AttributeFilter>> {
	
	private List<AttributeFilter> filters = new ArrayList<AttributeFilter>();
	private AttributeFilter current;

	@Override
	public List<AttributeFilter> visitStart(StartContext ctx) {
		ctx.conditional_term().accept(this);
		return filters;
	}

	@Override
	public List<AttributeFilter> visitStringComparisonExpression(StringComparisonExpressionContext ctx) {
		current = new AttributeFilter();
		filters.add(current);
		return super.visitStringComparisonExpression(ctx);
	}

	@Override
	public List<AttributeFilter> visitArithmeticComparisonExpression(ArithmeticComparisonExpressionContext ctx) {
		current = new AttributeFilter();
		filters.add(current);
		return super.visitArithmeticComparisonExpression(ctx);
	}

	@Override
	public List<AttributeFilter> visitDateTimeComparisonExpression(DateTimeComparisonExpressionContext ctx) {
		current = new AttributeFilter();
		filters.add(current);
		return super.visitDateTimeComparisonExpression(ctx);
	}

	@Override
	public List<AttributeFilter> visitFactorExpression(FactorExpressionContext ctx) {
		return ctx.factor.accept(this);
	}

	@Override
	public List<AttributeFilter> visitAndExpression(AndExpressionContext ctx) {
		return super.visitAndExpression(ctx);
	}

	@Override
	public List<AttributeFilter> visitEquality_comparison_operator(Equality_comparison_operatorContext ctx) {
		current.setOperatorType(OperatorType.valueOfOperator(ctx.getText()));
		return null;
	}

	@Override
	public List<AttributeFilter> visitComparison_operator(Comparison_operatorContext ctx) {
		current.setOperatorType(OperatorType.valueOfOperator(ctx.getText()));
		return null;
	}

	@Override
	public List<AttributeFilter> visitString_attribute(String_attributeContext ctx) {
		current.setAttribute(Attribute.valueOfPwatch(ctx.getText()));
		return null;
	}

	@Override
	public List<AttributeFilter> visitArithmeticAttribute(ArithmeticAttributeContext ctx) {
		current.setAttribute(Attribute.valueOfPwatch(ctx.getText()));
		return null;
	}

	@Override
	public List<AttributeFilter> visitDateTimeAttribute(DateTimeAttributeContext ctx) {
		current.setAttribute(Attribute.valueOfPwatch(ctx.getText()));
		return null;
	}

	@Override
	public List<AttributeFilter> visitStringLiteral(StringLiteralContext ctx) {
		String value = ctx.getText();
		value = value.substring(1, value.length() - 2);
		current.setTextValue(value);
		return null;
	}

	@Override
	public List<AttributeFilter> visitArithmeticLiteral(ArithmeticLiteralContext ctx) {
		String stringValue = ctx.getText();
		current.setNumericValue(new BigDecimal(stringValue));
		return null;
	}
	
	private static final Pattern timestampLiteralPattern = Pattern.compile("TIMESTAMP\\('(\\d\\d\\d\\d)\\-(\\d\\d)\\-(\\d\\d)'\\)");

	@Override
	public List<AttributeFilter> visitTimestampLiteral(TimestampLiteralContext ctx) {
		Matcher matcher = timestampLiteralPattern.matcher(ctx.getText());
		// Since it comes from the parser, it should always match the pattern, but we need the call to matches to access the groups
		matcher.matches();
		
		Calendar calendar = Calendar.getInstance();
		// Esper month starts a 0, so we need to adapt here
		calendar.set(trim(matcher.group(1)), Integer.valueOf(trim(matcher.group(2))) - 1, trim(matcher.group(3)));
		return null;
	}
	
	private int trim(String s) {
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
			return 0;
		}
		
		return Integer.valueOf(sb.toString());
	}


}
