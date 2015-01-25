package at.ac.tuwien.ase09.data.stock.nasdaq.detail;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.enterprise.context.Dependent;
import javax.inject.Named;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import at.ac.tuwien.ase09.data.AbstractStockDetailReader;
import at.ac.tuwien.ase09.data.JsoupUtils;
import at.ac.tuwien.ase09.data.model.StockDetailModel;
import at.ac.tuwien.ase09.model.DividendHistoryEntry;
import at.ac.tuwien.ase09.model.Stock;

@Dependent
@Named
public class YahooStockDetailReader extends AbstractStockDetailReader {
	private static Logger LOG = Logger.getLogger(YahooStockDetailReader.class.getName());
	private static final String YQL_KEYSTATS_QUERY_TEMPLATE = "https://query.yahooapis.com/v1/public/yql?q=SELECT%20*%20FROM%20yahoo.finance.keystats%20WHERE%20symbol%3D'#{symbol}'&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys";
	private static final DateFormat YQL_KEYSTATS_DATE_FORMAT = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
	private static final DateFormat YQL_KEYSTATS_REP_DATE_FORMAT = new SimpleDateFormat("MMM dd", Locale.US);
	private static final String YQL_DIVIDEND_HISTORY_QUERY_TEMPLATE = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.dividendhistory%20where%20symbol%20%3D%20%22#{symbol}%22%20and%20startDate%20%3D%20%22#{startDate}%22%20and%20endDate%20%3D%20%22#{endDate}%22&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys";
	private static final DateFormat YQL_DIVIDEND_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	
	private Calendar startDate;
	private Calendar endDate;

	@Override
	public void open(Serializable checkpoint) throws Exception {
		super.open(checkpoint);
		startDate = Calendar.getInstance();
		startDate.roll(Calendar.YEAR, -5);
		endDate = Calendar.getInstance();
	}

	@Override
	protected void readStats(StockDetailModel stockDetailModel) throws Exception {
		Stock stock = stockDetailModel.getStock();
		Document d = JsoupUtils.getPage(YQL_KEYSTATS_QUERY_TEMPLATE.replaceAll("#\\{symbol\\}", stock.getTickerSymbol()));
		Elements keyStats = d.select("results stats").first().children();
		Map<String, Element> keyStatsMap = keyStats.stream().filter(elem -> !elem.text().isEmpty() && !"N/A".equals(elem.text())).collect(Collectors.toMap(elem -> getKeyForStatsElement(elem), Function.<Element>identity()));
		
		// To make this more readable:
//		Map<String, BiFunction<Stock, String, Void>> operations = new HashMap<String, BiFunction<Stock,String,Void>>();
//		operations.put("marketcap_intraday", (stock, text) -> { stock.setMarketCap(new BigDecimal(text.replaceAll(",", ""))); return null; });
//		
//		Stock stock = new Stock();
//		operations.forEach((key, function) -> {
//			Element elem;
//			if ((elem = keyStatsMap.get(key)) != null) {
//				function.apply(stock, elem.text());
//			}
//		});
		
		Element elem;
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
	}
	
	@Override
	protected void readDividendHistoryEntries(StockDetailModel stockDetailModel)
			throws Exception {
		Stock stock = stockDetailModel.getStock();
		String startDateStr = YQL_DIVIDEND_DATE_FORMAT.format(startDate.getTime());
		String endDateStr = YQL_DIVIDEND_DATE_FORMAT.format(endDate.getTime());
		Document dividendHistory = JsoupUtils.getPage(YQL_DIVIDEND_HISTORY_QUERY_TEMPLATE.replaceAll("#\\{symbol\\}", stock.getTickerSymbol()).replaceAll("#\\{startDate\\}", startDateStr).replaceAll("#\\{endDate\\}", endDateStr));
		Elements quotes = dividendHistory.select("quote");
		for(Element quote : quotes){
			String dateStr = quote.getElementsByTag("Date").first().text();
			String dividendStr = quote.getElementsByTag("Dividends").first().text();
			Calendar dividendDate = Calendar.getInstance();
			dividendDate.setTime(YQL_DIVIDEND_DATE_FORMAT.parse(dateStr));
			DividendHistoryEntry dividendHistoryEntry = new DividendHistoryEntry();
			dividendHistoryEntry.setStock(stock);
			dividendHistoryEntry.setDividend(new BigDecimal(dividendStr));
			dividendHistoryEntry.setDividendDate(dividendDate);
			stockDetailModel.getDividendHistoryEntries().add(dividendHistoryEntry);
		}
		
		LOG.info("Extracted " + stockDetailModel.getDividendHistoryEntries().size() + " dividend entries for " + stock.getTickerSymbol());
		
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
