package at.ac.tuwien.ase09.parser;

import org.junit.Test;

import static org.junit.Assert.*;
import at.ac.tuwien.ase09.parser.PWatchCompiler;
import at.ac.tuwien.ase09.parser.SyntaxErrorException;

public class PWatchCompilerTest {

	private static final String PRICE_ENTRY_ALIAS = "priceEntry";
	private static final String STOCK_ALIAS = "stock";
	
	public void testSimplePriceJpql() {
		String pwatchExpression = "COUNTRY = 'DE' AND PRICE > 100";
		String actualExpression = PWatchCompiler.compileJpql(pwatchExpression);
		String expectedExpression = "SELECT " + STOCK_ALIAS + " FROM Stock " + STOCK_ALIAS;
		expectedExpression += " JOIN " + STOCK_ALIAS + ".priceEntries " + PRICE_ENTRY_ALIAS + " ON ";
		expectedExpression += PRICE_ENTRY_ALIAS + ".created = (SELECT MAX(e.created) FROM ValuePaperPriceEntry e WHERE e.valuePaper = " + STOCK_ALIAS + ")";
		expectedExpression += " WHERE " + STOCK_ALIAS + ".country = 'DE' AND " + PRICE_ENTRY_ALIAS + ".price > 100";
		assertEquals(expectedExpression, actualExpression);
	}

	@Test(expected = SyntaxErrorException.class)
	public void testUnallowedStringAttribute() {
		String pwatchExpression = "COUNTRY = 'DE'";
		PWatchCompiler.compileEpl(pwatchExpression, 1L);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testStupidEpl() {
		String pwatchExpression = "1 = 1";
		PWatchCompiler.compileEpl(pwatchExpression, 1L);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testStupidJpql() {
		String pwatchExpression = "1 = 1";
		PWatchCompiler.compileJpql(pwatchExpression);
	}

	@Test(expected = NullPointerException.class)
	public void testNullExpressionEpl() {
		PWatchCompiler.compileEpl(null, 1L);
	}

	@Test(expected = NullPointerException.class)
	public void testNullExpressionJpql() {
		PWatchCompiler.compileJpql(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testEmptyExpressionEpl() {
		PWatchCompiler.compileEpl("", 1L);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testEmptyExpressionJpql() {
		PWatchCompiler.compileJpql("");
	}
}
