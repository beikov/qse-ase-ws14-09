package at.ac.tuwien.ase09.parser;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import at.ac.tuwien.ase09.model.ValuePaperType;
import at.ac.tuwien.ase09.parser.PWatchCompiler;

@RunWith(Parameterized.class)
public class StringAttributePWatchCompilerTest {
	
	private static final String STOCK_ALIAS = "stock";
	private final String attribute;
	private final String jpqlAttribute;
	private final String operator;
	private final String literal;
	
	public StringAttributePWatchCompilerTest(String attribute, String jpqlAttribute, String operator, String literal) {
		this.attribute = attribute;
		this.jpqlAttribute = STOCK_ALIAS + "." + jpqlAttribute;
		this.operator = operator;
		this.literal = literal;
	}
	
	@Parameters(name = "{1} {2} {3}")
	public static Collection<Object[]> getTestData() {
		Object[][] baseParameters = new Object[][] {
				{ "CURRENCY", "currency"},
				{ "COUNTRY", "country"}
		};
		
		String[] operators = { "=", "!=", "<>" };
		String[] literals = {
				 "'DE'",
				 "\"DE\"",
		};
		
		List<Object[]> parameters = new ArrayList<>(baseParameters.length * operators.length * literals.length);
		for (int opIdx = 0; opIdx < operators.length; opIdx++) {
			for (int litIdx = 0; litIdx < literals.length; litIdx++) {
				for (int i = 0; i < baseParameters.length; i++) {
					parameters.add(new Object[] {
							baseParameters[i][0],
							baseParameters[i][1],
							operators[opIdx],
							literals[litIdx],
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
		String actualExpression = PWatchCompiler.compileJpql(pwatchExpression, ValuePaperType.STOCK);

		String expectedExpression = "SELECT " + STOCK_ALIAS + " FROM Stock " + STOCK_ALIAS;
		expectedExpression += " WHERE ";
		expectedExpression += jpqlAttribute + " " + operator + " " + literal + logicalExpr;
		
		assertEquals(expectedExpression, actualExpression);
	}
}
