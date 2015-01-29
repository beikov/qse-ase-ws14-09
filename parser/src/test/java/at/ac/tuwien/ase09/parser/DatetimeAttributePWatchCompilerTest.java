package at.ac.tuwien.ase09.parser;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import at.ac.tuwien.ase09.parser.PWatchCompiler;

@RunWith(Parameterized.class)
public class DatetimeAttributePWatchCompilerTest {
	
	private static final String STOCK_ALIAS = "stock";
	private final String attribute;
	private final String eplAttribute;
	private final String operator;
	private final String literal;
	private final String eplLiteral;
	
	public DatetimeAttributePWatchCompilerTest(String attribute, String eplAttribute, String operator, String literal, String eplLiteral) {
		this.attribute = attribute;
		this.eplAttribute = STOCK_ALIAS + "." + eplAttribute;
		this.operator = operator;
		this.literal = literal;
		this.eplLiteral = eplLiteral;
	}
	
	@Parameters(name = "{1} {2} {3}")
	public static Collection<Object[]> getTestData() {
		Object[][] baseParameters = new Object[][] {
				{ "FISCAL_YEAR_ENDS", "fiscalYearEnds"},
				{ "MOST_RECENT_QUARTER", "mostRecentQuarter"},
				{ "DIVIDEND_DATE", "dividendDate"},
				{ "EX_DIVIDEND_DATE", "ex_DividendDate"}
		};
		
		String[] operators = { ">", ">=", "<", "<=", "=", "!=", "<>" };
		String[][] literals = {
				{ "CURRENT_TIMESTAMP", "CURRENT_TIMESTAMP" },
				{ "TIMESTAMP('2014-01-01')", "CURRENT_TIMESTAMP.withDate(2014,0,1)" },
				{ "TIMESTAMP('2014-01-01 00:00:00')", "CURRENT_TIMESTAMP.withDate(2014,0,1).withTime(0,0,0,0)" },
				{ "TIMESTAMP('2014-01-01 00:00:00.000')", "CURRENT_TIMESTAMP.withDate(2014,0,1).withTime(0,0,0,0)" },
				{ "TIMESTAMP('2014-01-01 01:01:01.100')", "CURRENT_TIMESTAMP.withDate(2014,0,1).withTime(1,1,1,100)" }
		};
		
		List<Object[]> parameters = new ArrayList<>(baseParameters.length * operators.length * literals.length);
		for (int opIdx = 0; opIdx < operators.length; opIdx++) {
			for (int litIdx = 0; litIdx < literals.length; litIdx++) {
				for (int i = 0; i < baseParameters.length; i++) {
					parameters.add(new Object[] {
							baseParameters[i][0],
							baseParameters[i][1],
							operators[opIdx],
							literals[litIdx][0],
							literals[litIdx][1]
					});
				}
			}
		}
		
		return parameters;
	}
	
	@Test
	public void comparisonWithLiteralTest() {
		String logicalExpr = " AND (1 < 1 OR (NOT (1 > 1) AND 1 = 1))";
		String pwatchExpression = attribute + " " + operator + " " + literal + logicalExpr;
		Long valuePaperId = 1L;
		String actualExpression = PWatchCompiler.compileEpl(pwatchExpression, valuePaperId);
		
		String expectedExpression = "SELECT 1 FROM ";
		
		expectedExpression += "Stock(id = " + valuePaperId + ").std:lastevent() AS " + STOCK_ALIAS;
		
		expectedExpression += " WHERE ";
		expectedExpression += eplAttribute + " " + operator + " " + eplLiteral + logicalExpr;
		
		assertEquals(expectedExpression, actualExpression);
	}
}
