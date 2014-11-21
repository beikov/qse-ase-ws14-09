package at.ac.tuwien.ase09.data.stock.nasdaq.detail;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.batch.api.chunk.AbstractItemReader;
import javax.batch.runtime.context.StepContext;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import at.ac.tuwien.ase09.data.JsoupUtils;
import at.ac.tuwien.ase09.model.Stock;

@Dependent
@Named
public class YahooStockDetailReader extends AbstractItemReader {
	private static final Pattern urlTypePattern = Pattern
			.compile("TYPE=([^&]*)");
	private static final String yqlKeyStatsQueryTemplate = "https://query.yahooapis.com/v1/public/yql?q=SELECT%20*%20FROM%20yahoo.finance.keystats%20WHERE%20symbol%3D'#{symbolPlaceholder}'&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys";
	private static final DateFormat yahooDateFormat = new SimpleDateFormat(
			"MMM dd, yyyy");
	@Inject
	private StepContext stepContext;

	private final Currency currency = Currency.getInstance("USD");
	private Integer symbolNumber;
	private List<String> stockSymbols;

	@Override
	public void open(Serializable checkpoint) throws Exception {
		stockSymbols = (List<String>) stepContext.getPersistentUserData();
		if (checkpoint != null) {
			symbolNumber = (Integer) checkpoint;
		} else {
			symbolNumber = 0;
		}
	}

	@Override
	public Object readItem() throws Exception {
		if(symbolNumber >= stockSymbols.size()){
			return null;
		}
		String stockSymbol = stockSymbols.get(symbolNumber);
		
		Document d = JsoupUtils.getPage(yqlKeyStatsQueryTemplate.replaceAll("#\\{symbolPlaceholder\\}", stockSymbol));

		Elements keyStats = d.select("results stats");
		Map<String, Element> keyStatsMap = keyStats.stream().collect(Collectors.toMap(elem -> getKeyForStatsElement(elem), Function.<Element>identity()));
		
		Stock stock = new Stock();
		stock.setMarketCap(new BigDecimal(keyStatsMap.get("MarketCap_intraday").text()));
		stock.setEnterpriseValue(new BigDecimal(keyStatsMap.get("EnterpriseValue_date").text()));
		stock.setTrailingPE(new BigDecimal(keyStatsMap.get("TrailingPE_ttm_intraday").text()));
		stock.setForwardPE(new BigDecimal(keyStatsMap.get("ForwardPE_fye").text()));
		stock.setpEGRatio(new BigDecimal(keyStatsMap.get("PEGRatio_5_yr_expected").text()));
		stock.setPriceSales(new BigDecimal(keyStatsMap.get("PriceSales_ttm").text()));
		stock.setPriceBook(new BigDecimal(keyStatsMap.get("PriceBook_mrq").text()));
		stock.setEnterpriseValueRevenue(new BigDecimal(keyStatsMap.get("EnterpriseValueRevenue_ttm").text()));
		stock.setEnterpriseValueEBITDA(new BigDecimal(keyStatsMap.get("EnterpriseValueEBITDA_ttm").text()));
		stock.setFiscalYearEnds(getYahooDate(keyStatsMap.get("FiscalYearEnds").text()));
		stock.setMostRecentQuarter(getYahooDate(keyStatsMap.get("MostRecentQuarter_mrq").text()));
		stock.setProfitMargin(new BigDecimal(keyStatsMap.get("ProfitMargin_ttm").text()));
		stock.setOperatingMargin(new BigDecimal(keyStatsMap.get("OperatingMargin_ttm").text()));
		stock.setReturnonAssets(new BigDecimal(keyStatsMap.get("ReturnonAssets_ttm").text()));
		stock.setReturnonEquity(new BigDecimal(keyStatsMap.get("ReturnonEquity_ttm").text()));
		stock.setRevenue(new BigDecimal(keyStatsMap.get("Revenue_ttm").text()));
		stock.setRevenuePerShare(new BigDecimal(keyStatsMap.get("RevenuePerShare_ttm").text()));
		stock.setQtrlyRevenueGrowth(new BigDecimal(keyStatsMap.get("QtrlyRevenueGrowth_yoy").text()));
		stock.setGrossProfit(new BigDecimal(keyStatsMap.get("GrossProfit_ttm").text()));
		stock.setEbitda(new BigDecimal(keyStatsMap.get("EBITDA_ttm").text()));
		stock.setNetIncomeAvltoCommon(new BigDecimal(keyStatsMap.get("NetIncomeAvltoCommon_ttm").text()));
		stock.setDilutedEPS(new BigDecimal(keyStatsMap.get("DilutedEPS_ttm").text()));
		stock.setQtrlyEarningsGrowth(new BigDecimal(keyStatsMap.get("QtrlyEarningsGrowth_yoy").text()));
		stock.setTotalCash(new BigDecimal(keyStatsMap.get("TotalCash_mrq").text()));
		stock.setTotalCashPerShare(new BigDecimal(keyStatsMap.get("TotalCashPerShare_mrq").text()));
		keyStatsMap.get("TotalDebt_mrq");
		keyStatsMap.get("TotalDebtEquity_mrq");
		keyStatsMap.get("CurrentRatio_mrq");
		keyStatsMap.get("BookValuePerShare_mrq");
		keyStatsMap.get("OperatingCashFlow_ttm");
		keyStatsMap.get("LeveredFreeCashFlow_ttm");
		keyStatsMap.get("Beta");
		keyStatsMap.get("p_52_WeekChange_relative");
		keyStatsMap.get("SP50052_WeekChange_relative");
		keyStatsMap.get("p_52_WeekHigh_date");
		keyStatsMap.get("p_52_WeekLow_date");
		keyStatsMap.get("p_50_DayMovingAverage");
		keyStatsMap.get("p_200_DayMovingAverage");
		keyStatsMap.get("AvgVol_3_month");
		keyStatsMap.get("AvgVol_10_day");
		keyStatsMap.get("SharesOutstanding");
		keyStatsMap.get("Float");
		keyStatsMap.get("PercentageHeldbyInsiders_relative");
		keyStatsMap.get("PercentageHeldbyInstitutions_relative");
		keyStatsMap.get("SharesShort_date");
		keyStatsMap.get("ShortRatio_date");
		keyStatsMap.get("ShortPercentageofFloat_date");
		keyStatsMap.get("SharesShort_prior_month");
		keyStatsMap.get("ForwardAnnualDividendRate");
		keyStatsMap.get("ForwardAnnualDividendYield");
		keyStatsMap.get("TrailingAnnualDividendYield");
		keyStatsMap.get("TrailingAnnualDividendYield_relative");
		keyStatsMap.get("p_5YearAverageDividendYield_relative");
		keyStatsMap.get("PayoutRatio_relative");
		keyStatsMap.get("DividendDate");
		keyStatsMap.get("Ex_DividendDate");
		keyStatsMap.get("LastSplitFactor_new_per_old");
		keyStatsMap.get("LastSplitDate");
//		Stock stock = new Stock();
//		stock.setIsin(isin);
//		stock.setCountry(Country.AUSTRIA.toString());
//		stock.setName(name);
//		stock.setCurrency(currency);
//		stock.setBoerseCertificatePageUrl(boerseCertificatePageUrl);
//		stock.setFinanzenCertificatePageUrl(finanzenCertificateLink);
//		stock.setHistoricPricesPageUrl(historicPricesPageUrl);
//		stock.setIndex(indexName);
		
		symbolNumber++;
		return null;
	}

	@Override
	public Serializable checkpointInfo() throws Exception {
		return symbolNumber;
	}

	private String getKeyForStatsElement(Element elem) {

		String tagName = elem.tagName();
		String termAttr;
		if ((termAttr = elem.attr("term")) != null) {
			if (isYahooDate(termAttr) || termAttr.startsWith("as of")) {
				return tagName;
			} else {
				return tagName + "_"
						+ termAttr.replace(' ', '_').replaceAll(",", "");
			}
		} else {
			String elemVal = elem.text();
			if (elemVal.endsWith("%")) {
				return tagName + "_relative";
			} else {
				return tagName;
			}
		}
	}

	private static boolean isYahooDate(String dateStr) {
		try {
			yahooDateFormat.parse(dateStr);
		} catch (ParseException e) {
			return false;
		}
		return true;
	}

	private static Calendar getYahooDate(String dateStr) {
		try {
			Date date = yahooDateFormat.parse(dateStr);
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			return cal;
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

}
