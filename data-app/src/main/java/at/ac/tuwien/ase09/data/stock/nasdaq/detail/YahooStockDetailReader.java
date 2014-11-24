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
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.batch.api.BatchProperty;
import javax.batch.api.chunk.AbstractItemReader;
import javax.batch.runtime.context.StepContext;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import org.jsoup.Connection.Method;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import at.ac.tuwien.ase09.data.JsoupUtils;
import at.ac.tuwien.ase09.model.Stock;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@Dependent
@Named
public class YahooStockDetailReader extends AbstractItemReader {
	private static final String YQL_KEYSTATS_QUERY_TEMPLATE = "https://query.yahooapis.com/v1/public/yql?q=SELECT%20*%20FROM%20yahoo.finance.keystats%20WHERE%20symbol%3D'#{symbolPlaceholder}'&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys";
	private static final DateFormat YQL_KEYSTATS_DATE_FORMAT = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
	private static final DateFormat YQL_KEYSTATS_REP_DATE_FORMAT = new SimpleDateFormat("MMM dd", Locale.US);
	private static final String CNBC_STOCK_DETAIL_QUERY = "http://quote.cnbc.com/quote-html-webservice/quote.htm?partnerId=2&requestMethod=quick&exthrs=1&noform=1&fund=1&output=jsonp&symbols=#{symbolsPlaceholder}";
	
	@Inject
	private StepContext stepContext;

	@Inject
	@BatchProperty(name="indexName")
	private String indexName;
	
	private Integer symbolNumber;
	private List<String> stockSymbols;
	private final JsonParser jsonParser = new JsonParser();

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
//		if(symbolNumber == 1){
//			return null;
//		}
		String stockSymbol = stockSymbols.get(symbolNumber);
		Document d = JsoupUtils.getPage(YQL_KEYSTATS_QUERY_TEMPLATE.replaceAll("#\\{symbolPlaceholder\\}", stockSymbol));
		String cnbcNasdaqDetails = JsoupUtils.getPageAndIgnoreContentType(CNBC_STOCK_DETAIL_QUERY.replaceAll("#\\{symbolsPlaceholder\\}", stockSymbol), Method.GET, 3000);
		cnbcNasdaqDetails = cnbcNasdaqDetails.substring(cnbcNasdaqDetails.indexOf('(') + 1, cnbcNasdaqDetails.lastIndexOf(')'));
	    JsonObject detailsObject = jsonParser.parse(cnbcNasdaqDetails).getAsJsonObject();
	    JsonObject quickQuote = detailsObject.getAsJsonObject("QuickQuoteResult").getAsJsonObject("QuickQuote");

		Elements keyStats = d.select("results stats").first().children();
		Map<String, Element> keyStatsMap = keyStats.stream().filter(elem -> !elem.text().isEmpty() && !"N/A".equals(elem.text())).collect(Collectors.toMap(elem -> getKeyForStatsElement(elem), Function.<Element>identity()));
		
		Element elem;
		Stock stock = new Stock();
		
		stock.setCode(stockSymbol);
		stock.setName(quickQuote.get("name").getAsString());
		stock.setIndex(indexName);
		stock.setCountry(quickQuote.get("countryCode").getAsString());
		stock.setCurrency(Currency.getInstance(quickQuote.get("currencyCode").getAsString()));

		if((elem = keyStatsMap.get("marketcap_intraday")) != null){ stock.setMarketCap(new BigDecimal(elem.text().replaceAll(",", ""))); }
		if((elem = keyStatsMap.get("enterprisevalue")) != null){ stock.setEnterpriseValue(new BigDecimal(elem.text().replaceAll(",", ""))); }
		if((elem = keyStatsMap.get("trailingpe_ttm_intraday")) != null){ stock.setTrailingPE(new BigDecimal(elem.text().replaceAll(",", ""))); }
		if((elem = keyStatsMap.get("forwardpe")) != null){ stock.setForwardPE(new BigDecimal(elem.text().replaceAll(",", ""))); }
		if((elem = keyStatsMap.get("pegratio_5_yr_expected")) != null){ stock.setpEGRatio(new BigDecimal(elem.text().replaceAll(",", ""))); }
		if((elem = keyStatsMap.get("pricesales_ttm")) != null){ stock.setPriceSales(new BigDecimal(elem.text().replaceAll(",", ""))); }
		if((elem = keyStatsMap.get("pricebook_mrq")) != null){ stock.setPriceBook(new BigDecimal(elem.text().replaceAll(",", ""))); }
		if((elem = keyStatsMap.get("enterprisevaluerevenue_ttm")) != null){ stock.setEnterpriseValueRevenue(new BigDecimal(elem.text().replaceAll(",", ""))); }
		if((elem = keyStatsMap.get("enterprisevalueebitda_ttm")) != null){ stock.setEnterpriseValueEBITDA(new BigDecimal(elem.text().replaceAll(",", ""))); }
		if((elem = keyStatsMap.get("fiscalyearends")) != null){ stock.setFiscalYearEnds(getYahooRepeatingDate(elem.text())); }
		if((elem = keyStatsMap.get("mostrecentquarter_mrq")) != null){ stock.setMostRecentQuarter(getYahooDate(elem.text())); }
		if((elem = keyStatsMap.get("profitmargin_ttm")) != null){ stock.setProfitMargin(new BigDecimal(trimPercent(elem.text().replaceAll(",", "")))); }
		if((elem = keyStatsMap.get("operatingmargin_ttm")) != null){ stock.setOperatingMargin(new BigDecimal(trimPercent(elem.text().replaceAll(",", "")))); }
		if((elem = keyStatsMap.get("returnonassets_ttm")) != null){ stock.setReturnonAssets(new BigDecimal(trimPercent(elem.text()))); }
		if((elem = keyStatsMap.get("returnonequity_ttm")) != null){ stock.setReturnonEquity(new BigDecimal(trimPercent(elem.text().replaceAll(",", "")))); }
		if((elem = keyStatsMap.get("revenue_ttm")) != null){ stock.setRevenue(new BigDecimal(elem.text().replaceAll(",", ""))); }
		if((elem = keyStatsMap.get("revenuepershare_ttm")) != null){ stock.setRevenuePerShare(new BigDecimal(elem.text().replaceAll(",", ""))); }
		if((elem = keyStatsMap.get("qtrlyrevenuegrowth_yoy")) != null){ stock.setQtrlyRevenueGrowth(new BigDecimal(trimPercent(elem.text().replaceAll(",", "")))); }
		if((elem = keyStatsMap.get("grossprofit_ttm")) != null){ stock.setGrossProfit(new BigDecimal(elem.text().replaceAll(",", ""))); }
		if((elem = keyStatsMap.get("ebitda_ttm")) != null){ stock.setEbitda(new BigDecimal(elem.text().replaceAll(",", ""))); }
		if((elem = keyStatsMap.get("netincomeavltocommon_ttm")) != null){ stock.setNetIncomeAvltoCommon(new BigDecimal(elem.text().replaceAll(",", ""))); }
		if((elem = keyStatsMap.get("dilutedeps_ttm")) != null){ stock.setDilutedEPS(new BigDecimal(elem.text().replaceAll(",", ""))); }
		if((elem = keyStatsMap.get("qtrlyearningsgrowth_yoy")) != null){ stock.setQtrlyEarningsGrowth(new BigDecimal(trimPercent(elem.text().replaceAll(",", "")))); }
		if((elem = keyStatsMap.get("totalcash_mrq")) != null){ stock.setTotalCash(new BigDecimal(elem.text().replaceAll(",", ""))); }
		if((elem = keyStatsMap.get("totalcashpershare_mrq")) != null){ stock.setTotalCashPerShare(new BigDecimal(elem.text().replaceAll(",", ""))); }
		if((elem = keyStatsMap.get("totaldebt_mrq")) != null){ stock.setTotalDebt(new BigDecimal(elem.text().replaceAll(",", ""))); }
		if((elem = keyStatsMap.get("totaldebtequity_mrq")) != null){ stock.setTotalDebtEquity(new BigDecimal(elem.text().replaceAll(",", ""))); }
		if((elem = keyStatsMap.get("currentratio_mrq")) != null){ stock.setCurrentRatio(new BigDecimal(trimPercent(elem.text()))); }
		if((elem = keyStatsMap.get("bookvaluepershare_mrq")) != null){ stock.setBookValuePerShare(new BigDecimal(elem.text().replaceAll(",", ""))); }
		if((elem = keyStatsMap.get("operatingcashflow_ttm")) != null){ stock.setOperatingCashFlow(new BigDecimal(elem.text().replaceAll(",", ""))); }
		if((elem = keyStatsMap.get("leveredfreecashflow_ttm")) != null){ stock.setLeveredFreeCashFlow(new BigDecimal(elem.text().replaceAll(",", ""))); }
		if((elem = keyStatsMap.get("beta")) != null){ stock.setBeta(new BigDecimal(elem.text().replaceAll(",", ""))); }
		if((elem = keyStatsMap.get("p_52_weekchange_relative")) != null){ stock.setP_52_WeekChange(new BigDecimal(trimPercent(elem.text().replaceAll(",", "")))); }
		if((elem = keyStatsMap.get("sp50052_weekchange_relative")) != null){ stock.setsP50052_WeekChange(new BigDecimal(trimPercent(elem.text().replaceAll(",", "")))); }
		if((elem = keyStatsMap.get("p_52_weekhigh")) != null){ stock.setP_52_WeekHigh(new BigDecimal(elem.text().replaceAll(",", ""))); }
		if((elem = keyStatsMap.get("p_52_weeklow")) != null){ stock.setP_52_WeekLow(new BigDecimal(elem.text().replaceAll(",", ""))); }
		if((elem = keyStatsMap.get("p_50_daymovingaverage")) != null){ stock.setP_50_DayMovingAverage(new BigDecimal(elem.text().replaceAll(",", ""))); }
		if((elem = keyStatsMap.get("p_200_daymovingaverage")) != null){ stock.setP_200_DayMovingAverage(new BigDecimal(elem.text().replaceAll(",", ""))); }
		if((elem = keyStatsMap.get("avgvol_3_month")) != null){ stock.setAvgVol_3month(new BigDecimal(elem.text().replaceAll(",", ""))); }
		if((elem = keyStatsMap.get("avgvol_10_day")) != null){ stock.setAvgVol_10day(new BigDecimal(elem.text().replaceAll(",", ""))); }
		if((elem = keyStatsMap.get("sharesoutstanding")) != null){ stock.setSharesOutstanding(new BigDecimal(elem.text().replaceAll(",", ""))); }
		if((elem = keyStatsMap.get("float")) != null){ stock.setFloatVal(new BigDecimal(elem.text().replaceAll(",", ""))); }
		if((elem = keyStatsMap.get("percentageheldbyinsiders_relative")) != null){ stock.setPercentageHeldbyInsiders(new BigDecimal(trimPercent(elem.text().replaceAll(",", "")))); }
		if((elem = keyStatsMap.get("percentageheldbyinstitutions_relative")) != null){ stock.setPercentageHeldbyInstitutions(new BigDecimal(trimPercent(elem.text().replaceAll(",", "")))); }
		if((elem = keyStatsMap.get("sharesshort")) != null){ stock.setSharesShortCurrentMonth(new BigDecimal(elem.text().replaceAll(",", ""))); }
		if((elem = keyStatsMap.get("shortratio")) != null){ stock.setShortRatio(new BigDecimal(trimPercent(elem.text().replaceAll(",", "")))); }
		if((elem = keyStatsMap.get("shortpercentageoffloat")) != null){ stock.setShortPercentageofFloat(new BigDecimal(trimPercent(elem.text().replaceAll(",", "")))); }
		if((elem = keyStatsMap.get("sharesshort_prior_month")) != null){ stock.setSharesShortPriorMonth(new BigDecimal(elem.text().replaceAll(",", ""))); }
		if((elem = keyStatsMap.get("forwardannualdividendrate")) != null){ stock.setForwardAnnualDividendRate(new BigDecimal(elem.text().replaceAll(",", ""))); }
		if((elem = keyStatsMap.get("forwardannualdividendyield_relative")) != null){ stock.setForwardAnnualDividendYield(new BigDecimal(trimPercent(elem.text().replaceAll(",", "")))); }
		if((elem = keyStatsMap.get("trailingannualdividendyield")) != null){ stock.setTrailingAnnualDividendYieldAbsolute(new BigDecimal(elem.text().replaceAll(",", ""))); }
		if((elem = keyStatsMap.get("trailingannualdividendyield_relative")) != null){ stock.setTrailingAnnualDividendYieldRelative(new BigDecimal(trimPercent(elem.text().replaceAll(",", "")))); }
		if((elem = keyStatsMap.get("p_5yearaveragedividendyield_relative")) != null){ stock.setP_5YearAverageDividendYield(new BigDecimal(trimPercent(elem.text().replaceAll(",", "")))); }
		if((elem = keyStatsMap.get("payoutyatio_relative")) != null){ stock.setPayoutRatio(new BigDecimal(trimPercent(elem.text().replaceAll(",", "")))); }
		if((elem = keyStatsMap.get("DividendDate")) != null){ stock.setDividendDate(getYahooDate(elem.text())); }
		if((elem = keyStatsMap.get("ex_dividenddate")) != null){ stock.setEx_DividendDate(getYahooDate(elem.text())); }
		
		symbolNumber++;
		return stock;
	}

	@Override
	public Serializable checkpointInfo() throws Exception {
		return symbolNumber;
	}

	private String getKeyForStatsElement(Element elem) {

		String tagName = elem.tagName();
		String termAttr;
		if ((termAttr = elem.attr("term")).isEmpty()) {
			String elemVal = elem.text();
			if (elemVal.endsWith("%")) {
				return tagName + "_relative";
			} else {
				return tagName;
			}
		} else {
			if (isYahooDate(termAttr) || termAttr.startsWith("as of") || termAttr.startsWith("fye")) {
				return tagName;
			} else {
				return tagName + "_"
						+ termAttr.replace(' ', '_').replaceAll(",", "");
			}
		}
	}

	private static boolean isYahooDate(String dateStr) {
		try {
			YQL_KEYSTATS_DATE_FORMAT.parse(dateStr);
		} catch (ParseException e) {
			return false;
		}
		return true;
	}

	private static Calendar getYahooDate(String dateStr) {
		try {
			Date date = YQL_KEYSTATS_DATE_FORMAT.parse(dateStr);
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			return cal;
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	private static Calendar getYahooRepeatingDate(String dateStr) {
		try {
			Date date = YQL_KEYSTATS_REP_DATE_FORMAT.parse(dateStr);
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			return cal;
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}
	
	private static String trimPercent(String str){
		if(str.endsWith("%")){
			return str.substring(0, str.length()-1);
		}else{
			return str;
		}
	}

}
