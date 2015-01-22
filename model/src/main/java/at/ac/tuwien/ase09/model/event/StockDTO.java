package at.ac.tuwien.ase09.model.event;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

import at.ac.tuwien.ase09.model.Stock;

public class StockDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private final Long id;
	private final String currency;
	private final String country;
	
	// YAHOO Keystats
	private final BigDecimal marketCap;
	private final BigDecimal enterpriseValue;
	private final BigDecimal trailingPE;
	private final BigDecimal forwardPE;
	private final BigDecimal pEGRatio;
	private final BigDecimal priceSales;
	private final BigDecimal priceBook;
	private final BigDecimal enterpriseValueRevenue;
	private final BigDecimal enterpriseValueEBITDA;
	private final Calendar fiscalYearEnds;
	private final Calendar mostRecentQuarter;
	private final BigDecimal profitMargin;
	private final BigDecimal operatingMargin;
	private final BigDecimal returnonAssets;
	private final BigDecimal returnonEquity;
	private final BigDecimal revenue;
	private final BigDecimal revenuePerShare;
	private final BigDecimal qtrlyRevenueGrowth;
	private final BigDecimal grossProfit;
	private final BigDecimal ebitda;
	private final BigDecimal netIncomeAvltoCommon;
	private final BigDecimal dilutedEPS;
	private final BigDecimal qtrlyEarningsGrowth;
	private final BigDecimal totalCash;
	private final BigDecimal totalCashPerShare;
	private final BigDecimal totalDebt;
	private final BigDecimal totalDebtEquity;
	private final BigDecimal currentRatio;
	private final BigDecimal bookValuePerShare;
	private final BigDecimal operatingCashFlow;
	private final BigDecimal leveredFreeCashFlow;
	private final BigDecimal beta;
	private final BigDecimal p_52_WeekChange;
	private final BigDecimal p_52_WeekHigh;
	private final BigDecimal p_52_WeekLow;
	private final BigDecimal p_50_DayMovingAverage;
	private final BigDecimal p_200_DayMovingAverage;
	private final BigDecimal avgVol_3month;
	private final BigDecimal avgVol_10day;
	private final BigDecimal sharesOutstanding;
	private final BigDecimal floatVal;
	private final BigDecimal percentageHeldbyInsiders;
	private final BigDecimal percentageHeldbyInstitutions;
	private final BigDecimal sharesShortCurrentMonth;
	private final BigDecimal shortRatio;
	private final BigDecimal shortPercentageofFloat;
	private final BigDecimal sharesShortPriorMonth;
	private final BigDecimal forwardAnnualDividendRate;
	private final BigDecimal forwardAnnualDividendYield;
	private final BigDecimal trailingAnnualDividendYieldAbsolute;
	private final BigDecimal trailingAnnualDividendYieldRelative;
	private final BigDecimal p_5YearAverageDividendYield;
	private final BigDecimal payoutRatio;
	private final Calendar dividendDate;
	private final Calendar ex_DividendDate;
	
	public StockDTO(Stock stock) {
		this.id = stock.getId();
		this.currency = stock.getCurrency().getCurrencyCode();
		this.country = stock.getCountry();
		this.marketCap = stock.getMarketCap();
		this.enterpriseValue = stock.getEnterpriseValue();
		this.trailingPE = stock.getTrailingPE();
		this.forwardPE = stock.getForwardPE();
		this.pEGRatio = stock.getpEGRatio();
		this.priceSales = stock.getPriceSales();
		this.priceBook = stock.getPriceBook();
		this.enterpriseValueRevenue = stock.getEnterpriseValueRevenue();
		this.enterpriseValueEBITDA = stock.getEnterpriseValueEBITDA();
		this.fiscalYearEnds = stock.getFiscalYearEnds();
		this.mostRecentQuarter = stock.getMostRecentQuarter();
		this.profitMargin = stock.getProfitMargin();
		this.operatingMargin = stock.getOperatingMargin();
		this.returnonAssets = stock.getReturnonAssets();
		this.returnonEquity = stock.getReturnonEquity();
		this.revenue = stock.getRevenue();
		this.revenuePerShare = stock.getRevenuePerShare();
		this.qtrlyRevenueGrowth = stock.getQtrlyRevenueGrowth();
		this.grossProfit = stock.getGrossProfit();
		this.ebitda = stock.getEbitda();
		this.netIncomeAvltoCommon = stock.getNetIncomeAvltoCommon();
		this.dilutedEPS = stock.getDilutedEPS();
		this.qtrlyEarningsGrowth = stock.getQtrlyEarningsGrowth();
		this.totalCash = stock.getTotalCash();
		this.totalCashPerShare = stock.getTotalCashPerShare();
		this.totalDebt = stock.getTotalDebt();
		this.totalDebtEquity = stock.getTotalDebtEquity();
		this.currentRatio = stock.getCurrentRatio();
		this.bookValuePerShare = stock.getBookValuePerShare();
		this.operatingCashFlow = stock.getOperatingCashFlow();
		this.leveredFreeCashFlow = stock.getLeveredFreeCashFlow();
		this.beta = stock.getBeta();
		this.p_52_WeekChange = stock.getP_52_WeekChange();
		this.p_52_WeekHigh = stock.getP_52_WeekHigh();
		this.p_52_WeekLow = stock.getP_52_WeekLow();
		this.p_50_DayMovingAverage = stock.getP_50_DayMovingAverage();
		this.p_200_DayMovingAverage = stock.getP_200_DayMovingAverage();
		this.avgVol_3month = stock.getAvgVol_3month();
		this.avgVol_10day = stock.getAvgVol_10day();
		this.sharesOutstanding = stock.getSharesOutstanding();
		this.floatVal = stock.getFloatVal();
		this.percentageHeldbyInsiders = stock.getPercentageHeldbyInsiders();
		this.percentageHeldbyInstitutions = stock.getPercentageHeldbyInstitutions();
		this.sharesShortCurrentMonth = stock.getSharesShortCurrentMonth();
		this.shortRatio = stock.getShortRatio();
		this.shortPercentageofFloat = stock.getShortPercentageofFloat();
		this.sharesShortPriorMonth = stock.getSharesShortPriorMonth();
		this.forwardAnnualDividendRate = stock.getForwardAnnualDividendRate();
		this.forwardAnnualDividendYield = stock.getForwardAnnualDividendYield();
		this.trailingAnnualDividendYieldAbsolute = stock.getTrailingAnnualDividendYieldAbsolute();
		this.trailingAnnualDividendYieldRelative = stock.getTrailingAnnualDividendYieldRelative();
		this.p_5YearAverageDividendYield = stock.getP_5YearAverageDividendYield();
		this.payoutRatio = stock.getPayoutRatio();
		this.dividendDate = stock.getDividendDate();
		this.ex_DividendDate = stock.getEx_DividendDate();
	}

	public Long getId() {
		return id;
	}

	public String getCurrency() {
		return currency;
	}

	public String getCountry() {
		return country;
	}

	public BigDecimal getMarketCap() {
		return marketCap;
	}

	public BigDecimal getEnterpriseValue() {
		return enterpriseValue;
	}

	public BigDecimal getTrailingPE() {
		return trailingPE;
	}

	public BigDecimal getForwardPE() {
		return forwardPE;
	}

	public BigDecimal getpEGRatio() {
		return pEGRatio;
	}

	public BigDecimal getPriceSales() {
		return priceSales;
	}

	public BigDecimal getPriceBook() {
		return priceBook;
	}

	public BigDecimal getEnterpriseValueRevenue() {
		return enterpriseValueRevenue;
	}

	public BigDecimal getEnterpriseValueEBITDA() {
		return enterpriseValueEBITDA;
	}

	public Calendar getFiscalYearEnds() {
		return fiscalYearEnds;
	}

	public Calendar getMostRecentQuarter() {
		return mostRecentQuarter;
	}

	public BigDecimal getProfitMargin() {
		return profitMargin;
	}

	public BigDecimal getOperatingMargin() {
		return operatingMargin;
	}

	public BigDecimal getReturnonAssets() {
		return returnonAssets;
	}

	public BigDecimal getReturnonEquity() {
		return returnonEquity;
	}

	public BigDecimal getRevenue() {
		return revenue;
	}

	public BigDecimal getRevenuePerShare() {
		return revenuePerShare;
	}

	public BigDecimal getQtrlyRevenueGrowth() {
		return qtrlyRevenueGrowth;
	}

	public BigDecimal getGrossProfit() {
		return grossProfit;
	}

	public BigDecimal getEbitda() {
		return ebitda;
	}

	public BigDecimal getNetIncomeAvltoCommon() {
		return netIncomeAvltoCommon;
	}

	public BigDecimal getDilutedEPS() {
		return dilutedEPS;
	}

	public BigDecimal getQtrlyEarningsGrowth() {
		return qtrlyEarningsGrowth;
	}

	public BigDecimal getTotalCash() {
		return totalCash;
	}

	public BigDecimal getTotalCashPerShare() {
		return totalCashPerShare;
	}

	public BigDecimal getTotalDebt() {
		return totalDebt;
	}

	public BigDecimal getTotalDebtEquity() {
		return totalDebtEquity;
	}

	public BigDecimal getCurrentRatio() {
		return currentRatio;
	}

	public BigDecimal getBookValuePerShare() {
		return bookValuePerShare;
	}

	public BigDecimal getOperatingCashFlow() {
		return operatingCashFlow;
	}

	public BigDecimal getLeveredFreeCashFlow() {
		return leveredFreeCashFlow;
	}

	public BigDecimal getBeta() {
		return beta;
	}

	public BigDecimal getP_52_WeekChange() {
		return p_52_WeekChange;
	}

	public BigDecimal getP_52_WeekHigh() {
		return p_52_WeekHigh;
	}

	public BigDecimal getP_52_WeekLow() {
		return p_52_WeekLow;
	}

	public BigDecimal getP_50_DayMovingAverage() {
		return p_50_DayMovingAverage;
	}

	public BigDecimal getP_200_DayMovingAverage() {
		return p_200_DayMovingAverage;
	}

	public BigDecimal getAvgVol_3month() {
		return avgVol_3month;
	}

	public BigDecimal getAvgVol_10day() {
		return avgVol_10day;
	}

	public BigDecimal getSharesOutstanding() {
		return sharesOutstanding;
	}

	public BigDecimal getFloatVal() {
		return floatVal;
	}

	public BigDecimal getPercentageHeldbyInsiders() {
		return percentageHeldbyInsiders;
	}

	public BigDecimal getPercentageHeldbyInstitutions() {
		return percentageHeldbyInstitutions;
	}

	public BigDecimal getSharesShortCurrentMonth() {
		return sharesShortCurrentMonth;
	}

	public BigDecimal getShortRatio() {
		return shortRatio;
	}

	public BigDecimal getShortPercentageofFloat() {
		return shortPercentageofFloat;
	}

	public BigDecimal getSharesShortPriorMonth() {
		return sharesShortPriorMonth;
	}

	public BigDecimal getForwardAnnualDividendRate() {
		return forwardAnnualDividendRate;
	}

	public BigDecimal getForwardAnnualDividendYield() {
		return forwardAnnualDividendYield;
	}

	public BigDecimal getTrailingAnnualDividendYieldAbsolute() {
		return trailingAnnualDividendYieldAbsolute;
	}

	public BigDecimal getTrailingAnnualDividendYieldRelative() {
		return trailingAnnualDividendYieldRelative;
	}

	public BigDecimal getP_5YearAverageDividendYield() {
		return p_5YearAverageDividendYield;
	}

	public BigDecimal getPayoutRatio() {
		return payoutRatio;
	}

	public Calendar getDividendDate() {
		return dividendDate;
	}

	public Calendar getEx_DividendDate() {
		return ex_DividendDate;
	}
	

}
