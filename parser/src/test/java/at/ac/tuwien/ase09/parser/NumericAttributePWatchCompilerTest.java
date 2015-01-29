package at.ac.tuwien.ase09.parser;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import at.ac.tuwien.ase09.parser.PWatchCompiler;

@RunWith(Parameterized.class)
public class NumericAttributePWatchCompilerTest {
	
	private static final String PRICE_ENTRY_ALIAS = "priceEntry";
	private static final String STOCK_ALIAS = "stock";
	private final String attribute;
	private final String eplAttribute;
	private final String operator;
	private final String literal;
	private final boolean isStock;
	
	public NumericAttributePWatchCompilerTest(String attribute, String alias, String eplAttribute, String operator, String literal) {
		this.attribute = attribute;
		this.eplAttribute = alias + "." + eplAttribute;
		this.isStock = STOCK_ALIAS.equals(alias);
		this.operator = operator;
		this.literal = literal;
	}

	@Parameters(name = "{2} {3} {4}")
	public static Collection<Object[]> getTestData() {
		Object[][] baseParameters = new Object[][] {
				{ "PRICE", PRICE_ENTRY_ALIAS, "price"},
				{ "MARKET_CAP", STOCK_ALIAS, "marketCap"},
				{ "ENTERPRISE_VALUE", STOCK_ALIAS, "enterpriseValue"},
				{ "TRAILING_PE", STOCK_ALIAS, "trailingPE"},
				{ "FORWARD_PE", STOCK_ALIAS, "forwardPE"},
				{ "PEG_RATIO", STOCK_ALIAS, "pEGRatio"},
				{ "PRICE_SALES", STOCK_ALIAS, "priceSales"},
				{ "PRICE_BOOK", STOCK_ALIAS, "priceBook"},
				{ "ENTERPRISE_VALUE_REVENUE", STOCK_ALIAS, "enterpriseValueRevenue"},
				{ "ENTERPRISE_VALUE_EBITDA", STOCK_ALIAS, "enterpriseValueEBITDA"},
				{ "PROFIT_MARGIN", STOCK_ALIAS, "profitMargin"},
				{ "OPERATING_MARGIN", STOCK_ALIAS, "operatingMargin"},
				{ "RETURN_ON_ASSETS", STOCK_ALIAS, "returnonAssets"},
				{ "RETURN_ON_EQUITY", STOCK_ALIAS, "returnonEquity"},
				{ "REVENUE", STOCK_ALIAS, "revenue"},
				{ "REVENUE_PER_SHARE", STOCK_ALIAS, "revenuePerShare"},
				{ "QTRLY_REVENUE_GROWTH", STOCK_ALIAS, "qtrlyRevenueGrowth"},
				{ "GROSS_PROFIT", STOCK_ALIAS, "grossProfit"},
				{ "EBITDA", STOCK_ALIAS, "ebitda"},
				{ "NET_INCOME_AVL_TO_COMMON", STOCK_ALIAS, "netIncomeAvltoCommon"},
				{ "DILUTED_EPS", STOCK_ALIAS, "dilutedEPS"},
				{ "QTRLY_EARNINGS_GROWTH", STOCK_ALIAS, "qtrlyEarningsGrowth"},
				{ "TOTAL_CASH", STOCK_ALIAS, "totalCash"},
				{ "TOTAL_CASH_PER_SHARE", STOCK_ALIAS, "totalCashPerShare"},
				{ "TOTAL_DEBT", STOCK_ALIAS, "totalDebt"},
				{ "TOTAL_DEBT_EQUITY", STOCK_ALIAS, "totalDebtEquity"},
				{ "CURRENT_RATIO", STOCK_ALIAS, "currentRatio"},
				{ "BOOK_VALUE_PER_SHARE", STOCK_ALIAS, "bookValuePerShare"},
				{ "OPERATING_CASH_FLOW", STOCK_ALIAS, "operatingCashFlow"},
				{ "LEVERED_FREE_CASH_FLOW", STOCK_ALIAS, "leveredFreeCashFlow"},
				{ "BETA", STOCK_ALIAS, "beta"},
				{ "52_WEEK_CHANGE", STOCK_ALIAS, "p_52_WeekChange"},
				{ "52_WEEK_HIGH", STOCK_ALIAS, "p_52_WeekHigh"},
				{ "52_WEEK_LOW", STOCK_ALIAS, "p_52_WeekLow"},
				{ "50_DAY_MOVING_AVERAGE", STOCK_ALIAS, "p_50_DayMovingAverage"},
				{ "200_DAY_MOVING_AVERAGE", STOCK_ALIAS, "p_200_DayMovingAverage"},
				{ "AVG_VOL_3_MONTH", STOCK_ALIAS, "avgVol_3month"},
				{ "AVG_VOL_10_DAY", STOCK_ALIAS, "avgVol_10day"},
				{ "SHARES_OUTSTANDING", STOCK_ALIAS, "sharesOutstanding"},
				{ "FLOAT", STOCK_ALIAS, "floatVal"},
				{ "PERCENTAGE_HELD_BY_INSIDERS", STOCK_ALIAS, "percentageHeldbyInsiders"},
				{ "PERCENTAGE_HELD_BY_INSTITUTIONS", STOCK_ALIAS, "percentageHeldbyInstitutions"},
				{ "SHARES_SHORT_CURRENT_MONTH", STOCK_ALIAS, "sharesShortCurrentMonth"},
				{ "SHORT_RATIO", STOCK_ALIAS, "shortRatio"},
				{ "SHORT_PERCENTAGE_OF_FLOAT", STOCK_ALIAS, "shortPercentageofFloat"},
				{ "SHARES_SHORT_PRIOR_MONTH", STOCK_ALIAS, "sharesShortPriorMonth"},
				{ "FORWARD_ANNUAL_DIVIDEND_RATE", STOCK_ALIAS, "forwardAnnualDividendRate"},
				{ "FORWARD_ANNUAL_DIVIDEND_YIELD", STOCK_ALIAS, "forwardAnnualDividendYield"},
				{ "TRAILING_ANNUAL_DIVIDEND_YIELD_ABSOLUTE", STOCK_ALIAS, "trailingAnnualDividendYieldAbsolute"},
				{ "TRAILING_ANNUAL_DIVIDEND_YIELD_RELATIVE", STOCK_ALIAS, "trailingAnnualDividendYieldRelative"},
				{ "5_YEAR_AVERAGE_DIVIDEND_YIELD", STOCK_ALIAS, "p_5YearAverageDividendYield"},
				{ "PAYOUT_RATIO", STOCK_ALIAS, "payoutRatio"}
		};
		
		String[] operators = { ">", ">=", "<", "<=", "=", "!=", "<>" };
		String[] literals = {
				"10",
				"+10",
				"-10",
				"10.0",
				"10e2",
				"10e-1",
				"10.0e2",
				"10.0e-1",
				// Functions
				"SIN(1)" ,
				"COS(1)" ,
				"TAN(1)" ,
				"EXP(1)" ,
				"LOG(1)" ,
				"POW(1,1)" ,
				"SQRT(1)",
				// Expressions
				"1 + 1", 
				"1 - 1", 
				"1 * 1",
				"1 / 1",  
				"1 * 1 + 1",
				"1 * 1 - 1", 
				"1 / 1 + 1",
				"1 / 1 - 1",
				"1 * 1 * 1",
				"1 * 1 / 1",
				"1 / 1 / 1",
				"1 / 1 / 1",
				// Nested Expressions
				"1 + (1 + 1)", 
				"1 - (1 + 1)", 
				"1 * (1 + 1)",
				"1 / (1 + 1)",  
				// Complex
				"POW(1e1 * (SIN(1) + COS(0)),2)"
		};
		
		List<Object[]> parameters = new ArrayList<>(baseParameters.length * operators.length * literals.length);
		for (int opIdx = 0; opIdx < operators.length; opIdx++) {
			for (int litIdx = 0; litIdx < literals.length; litIdx++) {
				for (int i = 0; i < baseParameters.length; i++) {
					parameters.add(new Object[] {
							baseParameters[i][0],
							baseParameters[i][1],
							baseParameters[i][2],
							operators[opIdx],
							literals[litIdx]
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
		
		if (isStock) {
			expectedExpression += "Stock(id = " + valuePaperId + ").std:lastevent() AS " + STOCK_ALIAS;
		} else {
			expectedExpression += "ValuePaperPriceEntry(valuePaperId = " + valuePaperId + ").std:lastevent() AS " + PRICE_ENTRY_ALIAS;
		}
		
		expectedExpression += " WHERE ";
		expectedExpression += eplAttribute + " " + operator + " " + literal + logicalExpr;
		
		assertEquals(expectedExpression, actualExpression);
	}
}
