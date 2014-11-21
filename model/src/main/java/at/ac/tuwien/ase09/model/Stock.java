package at.ac.tuwien.ase09.model;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Currency;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

@Entity
@DiscriminatorValue(ValuePaperType.TYPE_STOCK)
public class Stock extends ValuePaper {
	private static final long serialVersionUID = 1L;

	private Currency currency;
	private String country;
	private String boerseCertificatePageUrl;
	private String finanzenCertificatePageUrl;
	private String index;
	
	// YAHOO Keystats
	private BigDecimal marketCap;
	private BigDecimal enterpriseValue;
	private BigDecimal trailingPE;
	private BigDecimal forwardPE;
	private BigDecimal pEGRatio;
	private BigDecimal priceSales;
	private BigDecimal priceBook;
	private BigDecimal enterpriseValueRevenue;
	private BigDecimal enterpriseValueEBITDA;
	private Calendar fiscalYearEnds;
	private Calendar mostRecentQuarter;
	private BigDecimal profitMargin;
	private BigDecimal operatingMargin;
	private BigDecimal returnonAssets;
	private BigDecimal returnonEquity;
	private BigDecimal revenue;
	private BigDecimal revenuePerShare;
	private BigDecimal qtrlyRevenueGrowth;
	private BigDecimal grossProfit;
	private BigDecimal ebitda;
	private BigDecimal netIncomeAvltoCommon;
	private BigDecimal dilutedEPS;
	private BigDecimal qtrlyEarningsGrowth;
	private BigDecimal totalCash;
	private BigDecimal totalCashPerShare;
	private BigDecimal totalDebt;
	private BigDecimal totalDebtEquity;
	private BigDecimal currentRatio;
	private BigDecimal bookValuePerShare;
	private BigDecimal operatingCashFlow;
	private BigDecimal leveredFreeCashFlow;
	private BigDecimal beta;
	private BigDecimal p_52_WeekChange;
	private BigDecimal sP50052_WeekChange;
	private BigDecimal p_52_WeekHigh;
	private BigDecimal p_52_WeekLow;
	private BigDecimal p_50_DayMovingAverage;
	private BigDecimal p_200_DayMovingAverage;
	private BigDecimal avgVol_3month;
	private BigDecimal avgVol_10day;
	private BigDecimal sharesOutstanding;
	private BigDecimal floatVal;
	private BigDecimal percentageHeldbyInsiders;
	private BigDecimal percentageHeldbyInstitutions;
	private BigDecimal sharesShortCurrentMonth;
	private BigDecimal shortRatio;
	private BigDecimal shortPercentageofFloat;
	private BigDecimal sharesShortPriorMonth;
	private BigDecimal forwardAnnualDividendRate;
	private BigDecimal forwardAnnualDividendYield;
	private BigDecimal trailingAnnualDividendYieldAbsolute;
	private BigDecimal trailingAnnualDividendYieldRelative;
	private BigDecimal p_5YearAverageDividendYield;
	private BigDecimal payoutRatio;
	private Calendar dividendDate;
	private Calendar ex_DividendDate;
	
	@Override
	@Transient
	public ValuePaperType getType() {
		return ValuePaperType.STOCK;
	}

	public String getBoerseCertificatePageUrl() {
		return boerseCertificatePageUrl;
	}

	public void setBoerseCertificatePageUrl(String boerseCertificatePageUrl) {
		this.boerseCertificatePageUrl = boerseCertificatePageUrl;
	}

	public String getFinanzenCertificatePageUrl() {
		return finanzenCertificatePageUrl;
	}

	public void setFinanzenCertificatePageUrl(String finanzenCertificatePageUrl) {
		this.finanzenCertificatePageUrl = finanzenCertificatePageUrl;
	}

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	@Column(precision=19, scale=2)
	public BigDecimal getMarketCap() {
		return marketCap;
	}

	public void setMarketCap(BigDecimal marketCap) {
		this.marketCap = marketCap;
	}

	@Column(precision=19, scale=2)
	public BigDecimal getEnterpriseValue() {
		return enterpriseValue;
	}

	public void setEnterpriseValue(BigDecimal enterpriseValue) {
		this.enterpriseValue = enterpriseValue;
	}

	@Column(precision=19, scale=2)
	public BigDecimal getTrailingPE() {
		return trailingPE;
	}

	public void setTrailingPE(BigDecimal trailingPE) {
		this.trailingPE = trailingPE;
	}

	@Column(precision=19, scale=2)
	public BigDecimal getForwardPE() {
		return forwardPE;
	}

	public void setForwardPE(BigDecimal forwardPE) {
		this.forwardPE = forwardPE;
	}

	@Column(precision=19, scale=2)
	public BigDecimal getpEGRatio() {
		return pEGRatio;
	}

	public void setpEGRatio(BigDecimal pEGRatio) {
		this.pEGRatio = pEGRatio;
	}

	@Column(precision=19, scale=2)
	public BigDecimal getPriceSales() {
		return priceSales;
	}

	public void setPriceSales(BigDecimal priceSales) {
		this.priceSales = priceSales;
	}

	@Column(precision=19, scale=2)
	public BigDecimal getPriceBook() {
		return priceBook;
	}

	public void setPriceBook(BigDecimal priceBook) {
		this.priceBook = priceBook;
	}

	@Column(precision=19, scale=2)
	public BigDecimal getEnterpriseValueRevenue() {
		return enterpriseValueRevenue;
	}

	public void setEnterpriseValueRevenue(BigDecimal enterpriseValueRevenue) {
		this.enterpriseValueRevenue = enterpriseValueRevenue;
	}

	@Column(precision=19, scale=2)
	public BigDecimal getEnterpriseValueEBITDA() {
		return enterpriseValueEBITDA;
	}

	public void setEnterpriseValueEBITDA(BigDecimal enterpriseValueEBITDA) {
		this.enterpriseValueEBITDA = enterpriseValueEBITDA;
	}

	@Temporal(TemporalType.DATE)
	public Calendar getFiscalYearEnds() {
		return fiscalYearEnds;
	}

	public void setFiscalYearEnds(Calendar fiscalYearEnds) {
		this.fiscalYearEnds = fiscalYearEnds;
	}

	@Temporal(TemporalType.DATE)
	public Calendar getMostRecentQuarter() {
		return mostRecentQuarter;
	}

	public void setMostRecentQuarter(Calendar mostRecentQuarter) {
		this.mostRecentQuarter = mostRecentQuarter;
	}

	@Column(precision=19, scale=2)
	public BigDecimal getProfitMargin() {
		return profitMargin;
	}

	public void setProfitMargin(BigDecimal profitMargin) {
		this.profitMargin = profitMargin;
	}

	@Column(precision=19, scale=2)
	public BigDecimal getOperatingMargin() {
		return operatingMargin;
	}

	public void setOperatingMargin(BigDecimal operatingMargin) {
		this.operatingMargin = operatingMargin;
	}

	@Column(precision=19, scale=2)
	public BigDecimal getReturnonAssets() {
		return returnonAssets;
	}

	public void setReturnonAssets(BigDecimal returnonAssets) {
		this.returnonAssets = returnonAssets;
	}

	@Column(precision=19, scale=2)
	public BigDecimal getReturnonEquity() {
		return returnonEquity;
	}

	public void setReturnonEquity(BigDecimal returnonEquity) {
		this.returnonEquity = returnonEquity;
	}

	@Column(precision=19, scale=2)
	public BigDecimal getRevenue() {
		return revenue;
	}

	public void setRevenue(BigDecimal revenue) {
		this.revenue = revenue;
	}

	@Column(precision=19, scale=2)
	public BigDecimal getRevenuePerShare() {
		return revenuePerShare;
	}

	public void setRevenuePerShare(BigDecimal revenuePerShare) {
		this.revenuePerShare = revenuePerShare;
	}

	@Column(precision=19, scale=2)
	public BigDecimal getQtrlyRevenueGrowth() {
		return qtrlyRevenueGrowth;
	}

	public void setQtrlyRevenueGrowth(BigDecimal qtrlyRevenueGrowth) {
		this.qtrlyRevenueGrowth = qtrlyRevenueGrowth;
	}

	@Column(precision=19, scale=2)
	public BigDecimal getGrossProfit() {
		return grossProfit;
	}

	public void setGrossProfit(BigDecimal grossProfit) {
		this.grossProfit = grossProfit;
	}

	@Column(precision=19, scale=2)
	public BigDecimal getEbitda() {
		return ebitda;
	}

	public void setEbitda(BigDecimal ebitda) {
		this.ebitda = ebitda;
	}

	@Column(precision=19, scale=2)
	public BigDecimal getNetIncomeAvltoCommon() {
		return netIncomeAvltoCommon;
	}

	public void setNetIncomeAvltoCommon(BigDecimal netIncomeAvltoCommon) {
		this.netIncomeAvltoCommon = netIncomeAvltoCommon;
	}

	@Column(precision=19, scale=2)
	public BigDecimal getDilutedEPS() {
		return dilutedEPS;
	}

	public void setDilutedEPS(BigDecimal dilutedEPS) {
		this.dilutedEPS = dilutedEPS;
	}

	@Column(precision=19, scale=2)
	public BigDecimal getQtrlyEarningsGrowth() {
		return qtrlyEarningsGrowth;
	}

	public void setQtrlyEarningsGrowth(BigDecimal qtrlyEarningsGrowth) {
		this.qtrlyEarningsGrowth = qtrlyEarningsGrowth;
	}

	@Column(precision=19, scale=2)
	public BigDecimal getTotalCash() {
		return totalCash;
	}

	public void setTotalCash(BigDecimal totalCash) {
		this.totalCash = totalCash;
	}

	@Column(precision=19, scale=2)
	public BigDecimal getTotalCashPerShare() {
		return totalCashPerShare;
	}

	public void setTotalCashPerShare(BigDecimal totalCashPerShare) {
		this.totalCashPerShare = totalCashPerShare;
	}

	@Column(precision=19, scale=2)
	public BigDecimal getTotalDebt() {
		return totalDebt;
	}

	public void setTotalDebt(BigDecimal totalDebt) {
		this.totalDebt = totalDebt;
	}

	@Column(precision=19, scale=2)
	public BigDecimal getTotalDebtEquity() {
		return totalDebtEquity;
	}

	public void setTotalDebtEquity(BigDecimal totalDebtEquity) {
		this.totalDebtEquity = totalDebtEquity;
	}

	@Column(precision=19, scale=2)
	public BigDecimal getCurrentRatio() {
		return currentRatio;
	}

	public void setCurrentRatio(BigDecimal currentRatio) {
		this.currentRatio = currentRatio;
	}

	@Column(precision=19, scale=2)
	public BigDecimal getBookValuePerShare() {
		return bookValuePerShare;
	}

	public void setBookValuePerShare(BigDecimal bookValuePerShare) {
		this.bookValuePerShare = bookValuePerShare;
	}

	@Column(precision=19, scale=2)
	public BigDecimal getOperatingCashFlow() {
		return operatingCashFlow;
	}

	public void setOperatingCashFlow(BigDecimal operatingCashFlow) {
		this.operatingCashFlow = operatingCashFlow;
	}

	@Column(precision=19, scale=2)
	public BigDecimal getLeveredFreeCashFlow() {
		return leveredFreeCashFlow;
	}

	public void setLeveredFreeCashFlow(BigDecimal leveredFreeCashFlow) {
		this.leveredFreeCashFlow = leveredFreeCashFlow;
	}

	@Column(precision=19, scale=2)
	public BigDecimal getBeta() {
		return beta;
	}

	public void setBeta(BigDecimal beta) {
		this.beta = beta;
	}

	@Column(precision=19, scale=2)
	public BigDecimal getP_52_WeekChange() {
		return p_52_WeekChange;
	}

	public void setP_52_WeekChange(BigDecimal p_52_WeekChange) {
		this.p_52_WeekChange = p_52_WeekChange;
	}

	@Column(precision=19, scale=2)
	public BigDecimal getsP50052_WeekChange() {
		return sP50052_WeekChange;
	}

	public void setsP50052_WeekChange(BigDecimal sP50052_WeekChange) {
		this.sP50052_WeekChange = sP50052_WeekChange;
	}

	@Column(precision=19, scale=2)
	public BigDecimal getP_52_WeekHigh() {
		return p_52_WeekHigh;
	}

	public void setP_52_WeekHigh(BigDecimal p_52_WeekHigh) {
		this.p_52_WeekHigh = p_52_WeekHigh;
	}

	@Column(precision=19, scale=2)
	public BigDecimal getP_52_WeekLow() {
		return p_52_WeekLow;
	}

	public void setP_52_WeekLow(BigDecimal p_52_WeekLow) {
		this.p_52_WeekLow = p_52_WeekLow;
	}

	@Column(precision=19, scale=2)
	public BigDecimal getP_50_DayMovingAverage() {
		return p_50_DayMovingAverage;
	}

	public void setP_50_DayMovingAverage(BigDecimal p_50_DayMovingAverage) {
		this.p_50_DayMovingAverage = p_50_DayMovingAverage;
	}

	@Column(precision=19, scale=2)
	public BigDecimal getP_200_DayMovingAverage() {
		return p_200_DayMovingAverage;
	}

	public void setP_200_DayMovingAverage(BigDecimal p_200_DayMovingAverage) {
		this.p_200_DayMovingAverage = p_200_DayMovingAverage;
	}

	@Column(precision=19, scale=2)
	public BigDecimal getAvgVol_3month() {
		return avgVol_3month;
	}

	public void setAvgVol_3month(BigDecimal avgVol_3month) {
		this.avgVol_3month = avgVol_3month;
	}

	@Column(precision=19, scale=2)
	public BigDecimal getAvgVol_10day() {
		return avgVol_10day;
	}

	public void setAvgVol_10day(BigDecimal avgVol_10day) {
		this.avgVol_10day = avgVol_10day;
	}

	public BigDecimal getSharesOutstanding() {
		return sharesOutstanding;
	}

	public void setSharesOutstanding(BigDecimal sharesOutstanding) {
		this.sharesOutstanding = sharesOutstanding;
	}

	public BigDecimal getFloatVal() {
		return floatVal;
	}

	public void setFloatVal(BigDecimal floatVal) {
		this.floatVal = floatVal;
	}

	@Column(precision=19, scale=2)
	public BigDecimal getPercentageHeldbyInsiders() {
		return percentageHeldbyInsiders;
	}

	public void setPercentageHeldbyInsiders(BigDecimal percentageHeldbyInsiders) {
		this.percentageHeldbyInsiders = percentageHeldbyInsiders;
	}

	@Column(precision=19, scale=2)
	public BigDecimal getPercentageHeldbyInstitutions() {
		return percentageHeldbyInstitutions;
	}

	public void setPercentageHeldbyInstitutions(
			BigDecimal percentageHeldbyInstitutions) {
		this.percentageHeldbyInstitutions = percentageHeldbyInstitutions;
	}
	
	public BigDecimal getSharesShortCurrentMonth() {
		return sharesShortCurrentMonth;
	}

	public void setSharesShortCurrentMonth(BigDecimal sharesShortCurrentMonth) {
		this.sharesShortCurrentMonth = sharesShortCurrentMonth;
	}

	@Column(precision=19, scale=2)
	public BigDecimal getShortRatio() {
		return shortRatio;
	}

	public void setShortRatio(BigDecimal shortRatio) {
		this.shortRatio = shortRatio;
	}

	@Column(precision=19, scale=2)
	public BigDecimal getShortPercentageofFloat() {
		return shortPercentageofFloat;
	}

	public void setShortPercentageofFloat(BigDecimal shortPercentageofFloat) {
		this.shortPercentageofFloat = shortPercentageofFloat;
	}

	public BigDecimal getSharesShortPriorMonth() {
		return sharesShortPriorMonth;
	}

	public void setSharesShortPriorMonth(BigDecimal sharesShortPriorMonth) {
		this.sharesShortPriorMonth = sharesShortPriorMonth;
	}

	@Column(precision=19, scale=2)
	public BigDecimal getForwardAnnualDividendRate() {
		return forwardAnnualDividendRate;
	}

	public void setForwardAnnualDividendRate(BigDecimal forwardAnnualDividendRate) {
		this.forwardAnnualDividendRate = forwardAnnualDividendRate;
	}

	@Column(precision=19, scale=2)
	public BigDecimal getForwardAnnualDividendYield() {
		return forwardAnnualDividendYield;
	}

	public void setForwardAnnualDividendYield(BigDecimal forwardAnnualDividendYield) {
		this.forwardAnnualDividendYield = forwardAnnualDividendYield;
	}

	@Column(precision=19, scale=2)
	public BigDecimal getTrailingAnnualDividendYieldAbsolute() {
		return trailingAnnualDividendYieldAbsolute;
	}

	public void setTrailingAnnualDividendYieldAbsolute(
			BigDecimal trailingAnnualDividendYieldAbsolute) {
		this.trailingAnnualDividendYieldAbsolute = trailingAnnualDividendYieldAbsolute;
	}

	@Column(precision=19, scale=2)
	public BigDecimal getTrailingAnnualDividendYieldRelative() {
		return trailingAnnualDividendYieldRelative;
	}

	public void setTrailingAnnualDividendYieldRelative(
			BigDecimal trailingAnnualDividendYieldRelative) {
		this.trailingAnnualDividendYieldRelative = trailingAnnualDividendYieldRelative;
	}

	@Column(precision=19, scale=2)
	public BigDecimal getP_5YearAverageDividendYield() {
		return p_5YearAverageDividendYield;
	}

	public void setP_5YearAverageDividendYield(
			BigDecimal p_5YearAverageDividendYield) {
		this.p_5YearAverageDividendYield = p_5YearAverageDividendYield;
	}

	@Column(precision=19, scale=2)
	public BigDecimal getPayoutRatio() {
		return payoutRatio;
	}

	public void setPayoutRatio(BigDecimal payoutRatio) {
		this.payoutRatio = payoutRatio;
	}

	@Temporal(TemporalType.DATE)
	public Calendar getDividendDate() {
		return dividendDate;
	}

	public void setDividendDate(Calendar dividendDate) {
		this.dividendDate = dividendDate;
	}

	@Temporal(TemporalType.DATE)
	public Calendar getEx_DividendDate() {
		return ex_DividendDate;
	}

	public void setEx_DividendDate(Calendar ex_DividendDate) {
		this.ex_DividendDate = ex_DividendDate;
	}
	
}
