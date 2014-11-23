package at.ac.tuwien.ase09.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.enterprise.context.RequestScoped;
import javax.faces.bean.ManagedBean;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceContext;

import org.hibernate.Transaction;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.DateAxis;
import org.primefaces.model.chart.LineChartModel;
import org.primefaces.model.chart.LineChartSeries;

import at.ac.tuwien.ase09.exception.EntityNotFoundException;
import at.ac.tuwien.ase09.model.Fund;
import at.ac.tuwien.ase09.model.Stock;
import at.ac.tuwien.ase09.model.StockBond;
import at.ac.tuwien.ase09.model.ValuePaper;
import at.ac.tuwien.ase09.model.ValuePaperPriceEntry;
import at.ac.tuwien.ase09.model.ValuePaperHistoryEntry;
import at.ac.tuwien.ase09.model.ValuePaperType;
import at.ac.tuwien.ase09.service.ValuePaperPriceEntryService;
import at.ac.tuwien.ase09.service.ValuePaperService;

@ManagedBean
@Named
@RequestScoped
public class ValuePaperViewBean implements Serializable{

	private static final long serialVersionUID = 1L;

	private ValuePaper valuePaper = null;

	private String valuePaperIsin;

	private LineChartModel valuePaperHistoricPriceLineChartModel = null;

	private Map<String, String> valuePaperAttributes = null;

	@Inject
	private ValuePaperPriceEntryService valuePaperPriceEntryService;

	@Inject
	private ValuePaperService valuePaperService;

	@PersistenceContext
	private EntityManager em;

	public void init() {
		loadValuePaper(valuePaperIsin);

		if(this.valuePaper != null){
			loadValuePaperAttributes();
			createLineChartModels();
		}
	}



	public String getValuePaperIsin() {
		return valuePaperIsin;
	}

	public void setValuePaperIsin(String valuePaperIsin) {
		this.valuePaperIsin = valuePaperIsin;
	}

	public ValuePaper getValuePaper() {
		return valuePaper;
	}

	public void setValuePaper(ValuePaper valuePaper) {
		this.valuePaper = valuePaper;
	}

	public LineChartModel getValuePaperHistoricPriceLineChartModel() {
		return valuePaperHistoricPriceLineChartModel;
	}

	public void setValuePaperHistoricPriceLineChartModel(
			LineChartModel valuePaperHistoricPriceLineChartModel) {
		this.valuePaperHistoricPriceLineChartModel = valuePaperHistoricPriceLineChartModel;
	}

	public Map<String, String> getValuePaperAttributes() {
		return valuePaperAttributes;
	}

	public void setValuePaperAttributes(Map<String, String> valuePaperAttributes) {
		this.valuePaperAttributes = valuePaperAttributes;
	}

	public ValuePaperPriceEntry getLastPriceEntry() {

		try{
			return valuePaperPriceEntryService.getLastPriceEntry(valuePaper.getCode());
		}
		catch(EntityNotFoundException e){
			return null;
		}
	}

	private void loadValuePaper(String valuePaperIsin) {

		try{
			this.valuePaper = valuePaperService.getValuePaperByCode(valuePaperIsin);
		}
		catch(EntityNotFoundException e){
			this.valuePaper = null;
		}

	}

	private void loadValuePaperAttributes() {
		this.valuePaperAttributes = new LinkedHashMap<String, String>();
		
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");

		if(valuePaper.getType() == ValuePaperType.STOCK){

			Stock s = (Stock)valuePaper;

			if(s.getCurrency() != null){
				this.valuePaperAttributes.put("Währung:", s.getCurrency().getCurrencyCode());
			}

			if(s.getIndex() != null){
				this.valuePaperAttributes.put("Index:", s.getIndex());
			}
			
			if(s.getCountry() != null){
				this.valuePaperAttributes.put("Land:", s.getCountry());
			}
			
			if(s.getMarketCap() != null){
				this.valuePaperAttributes.put("Market Cap:", s.getMarketCap().toString());
			}
			
			if(s.getEnterpriseValue() != null){
				this.valuePaperAttributes.put("Enterprise Value:", s.getEnterpriseValue().toString());
			}
			
			if(s.getTrailingPE() != null){
				this.valuePaperAttributes.put("Trailing PE:", s.getTrailingPE().toString());
			}
			
			if(s.getForwardPE() != null){
				this.valuePaperAttributes.put("Forward PE:", s.getForwardPE().toString());
			}
			
			if(s.getpEGRatio() != null){
				this.valuePaperAttributes.put("pEG Ratio:", s.getpEGRatio().toString());
			}
			
			if(s.getPriceSales() != null){
				this.valuePaperAttributes.put("Price Sales:", s.getPriceSales().toString());
			}
			
			if(s.getPriceBook() != null){
				this.valuePaperAttributes.put("Price Book:", s.getPriceBook().toString());
			}
			
			if(s.getEnterpriseValueRevenue() != null){
				this.valuePaperAttributes.put("Enterprise Value Revenue:", s.getEnterpriseValueRevenue().toString());
			}
			
			if(s.getEnterpriseValueEBITDA() != null){
				this.valuePaperAttributes.put("Enterprise Value EBITDA:", s.getEnterpriseValueEBITDA().toString());
			}

			if(s.getFiscalYearEnds() != null){
				this.valuePaperAttributes.put("Fiscal Year Ends:", format.format(s.getFiscalYearEnds().getTime()));
			}
			
			if(s.getMostRecentQuarter() != null){
				this.valuePaperAttributes.put("Most Recent Quaerter:", format.format(s.getMostRecentQuarter().getTime()));
			}
			
			if(s.getProfitMargin() != null){
				this.valuePaperAttributes.put("Profit Margin:", s.getProfitMargin().toString());
			}
			
			if(s.getOperatingMargin() != null){
				this.valuePaperAttributes.put("Operating Margin:", s.getOperatingMargin().toString());
			}
			
			if(s.getReturnonAssets() != null){
				this.valuePaperAttributes.put("Return on Assets:", s.getReturnonAssets().toString());
			}
			
			if(s.getReturnonEquity() != null){
				this.valuePaperAttributes.put("Return on Equity:", s.getReturnonEquity().toString());
			}
			
			if(s.getRevenue() != null){
				this.valuePaperAttributes.put("Revenue:", s.getRevenue().toString());
			}
			
			if(s.getRevenuePerShare() != null){
				this.valuePaperAttributes.put("Revenue per Share:", s.getRevenuePerShare().toString());
			}
			
			if(s.getQtrlyRevenueGrowth() != null){
				this.valuePaperAttributes.put("Quarterly Revenue Growth:", s.getQtrlyRevenueGrowth().toString());
			}
			
			if(s.getGrossProfit() != null){
				this.valuePaperAttributes.put("Gross Profit:", s.getGrossProfit().toString());
			}
			
			if(s.getEbitda() != null){
				this.valuePaperAttributes.put("Ebitda:", s.getEbitda().toString());
			}
			
			if(s.getNetIncomeAvltoCommon() != null){
				this.valuePaperAttributes.put("Net Income Available to Common:", s.getNetIncomeAvltoCommon().toString());
			}
			
			if(s.getDilutedEPS() != null){
				this.valuePaperAttributes.put("Diluted EPS:", s.getDilutedEPS().toString());
			}
			
			if(s.getQtrlyEarningsGrowth() != null){
				this.valuePaperAttributes.put("Quarterly Earnings Growth:", s.getQtrlyEarningsGrowth().toString());
			}
			
			if(s.getTotalCash() != null){
				this.valuePaperAttributes.put("Total Cash:", s.getTotalCash().toString());
			}
			
			if(s.getTotalCashPerShare() != null){
				this.valuePaperAttributes.put("Total Cash per Share:", s.getTotalCashPerShare().toString());
			}
			
			if(s.getTotalDebt() != null){
				this.valuePaperAttributes.put("Total Debt:", s.getTotalDebt().toString());
			}
			
			if(s.getTotalDebtEquity() != null){
				this.valuePaperAttributes.put("Total Debt Equity:", s.getTotalDebtEquity().toString());
			}
			
			if(s.getCurrentRatio() != null){
				this.valuePaperAttributes.put("Current Ration:", s.getCurrentRatio().toString());
			}
			
			if(s.getBookValuePerShare() != null){
				this.valuePaperAttributes.put("Book Value per Share:", s.getBookValuePerShare().toString());
			}
			
			if(s.getOperatingCashFlow() != null){
				this.valuePaperAttributes.put("Operatin Cashflow:", s.getOperatingCashFlow().toString());
			}
			
			if(s.getLeveredFreeCashFlow() != null){
				this.valuePaperAttributes.put("Levered Free Cashflow:", s.getLeveredFreeCashFlow().toString());
			}
			
			if(s.getBeta() != null){
				this.valuePaperAttributes.put("Beta:", s.getBeta().toString());
			}
			
			if(s.getP_52_WeekChange() != null){
				this.valuePaperAttributes.put("P 52 Week Change:", s.getP_52_WeekChange().toString());
			}
			
			if(s.getsP50052_WeekChange() != null){
				this.valuePaperAttributes.put("sP50052 Week Change:", s.getsP50052_WeekChange().toString());
			}
			
			if(s.getP_52_WeekHigh() != null){
				this.valuePaperAttributes.put("P 52 Week High:", s.getP_52_WeekHigh().toString());
			}
			
			if(s.getP_52_WeekLow() != null){
				this.valuePaperAttributes.put("P 52 Week Low:", s.getP_52_WeekLow().toString());
			}
			
			if(s.getP_50_DayMovingAverage() != null){
				this.valuePaperAttributes.put("P 50 Day Moving Average:", s.getP_50_DayMovingAverage().toString());
			}
			
			if(s.getP_200_DayMovingAverage() != null){
				this.valuePaperAttributes.put("P 200 Moving Average:", s.getP_200_DayMovingAverage().toString());
			}
			
			if(s.getAvgVol_3month() != null){
				this.valuePaperAttributes.put("Average Volume 3 Month:", s.getAvgVol_3month().toString());
			}
			
			if(s.getAvgVol_10day() != null){
				this.valuePaperAttributes.put("Average Volume 10 days:", s.getAvgVol_10day().toString());
			}
			
			if(s.getSharesOutstanding() != null){
				this.valuePaperAttributes.put("Shares Outstanding:", s.getSharesOutstanding().toString());
			}
			
			if(s.getFloatVal() != null){
				this.valuePaperAttributes.put("Float Val:", s.getFloatVal().toString());
			}
			
			if(s.getPercentageHeldbyInsiders() != null){
				this.valuePaperAttributes.put("Percentage held by Insiders:", s.getPercentageHeldbyInsiders().toString());
			}
			
			if(s.getPercentageHeldbyInstitutions() != null){
				this.valuePaperAttributes.put("Percentage held by Institutions:", s.getPercentageHeldbyInstitutions().toString());
			}
			
			if(s.getSharesShortCurrentMonth() != null){
				this.valuePaperAttributes.put("Shares Short Current Month:", s.getSharesShortCurrentMonth().toString());
			}
			
			if(s.getShortRatio() != null){
				this.valuePaperAttributes.put("Short Ration:", s.getShortRatio().toString());
			}
			
			if(s.getShortPercentageofFloat() != null){
				this.valuePaperAttributes.put("Short Percentage of Float:", s.getShortPercentageofFloat().toString());
			}
			
			if(s.getSharesShortPriorMonth() != null){
				this.valuePaperAttributes.put("Share Short Prior Month:", s.getSharesShortPriorMonth().toString());
			}
			
			if(s.getForwardAnnualDividendRate() != null){
				this.valuePaperAttributes.put("Forward Annual Dividend Rate:", s.getForwardAnnualDividendRate().toString());
			}
			
			if(s.getForwardAnnualDividendYield() != null){
				this.valuePaperAttributes.put("Forward Annual Dividend Yield:", s.getForwardAnnualDividendYield().toString());
			}
			
			if(s.getTrailingAnnualDividendYieldAbsolute() != null){
				this.valuePaperAttributes.put("Trailing Dividend Yield Absolute:", s.getTrailingAnnualDividendYieldAbsolute().toString());
			}
			
			if(s.getTrailingAnnualDividendYieldRelative() != null){
				this.valuePaperAttributes.put("Trailing Dividend Yield Relative:", s.getTrailingAnnualDividendYieldRelative().toString());
			}
			
			if(s.getP_5YearAverageDividendYield() != null){
				this.valuePaperAttributes.put("P 5 Year Average Dividend Yield:", s.getP_5YearAverageDividendYield().toString());
			}
			
			if(s.getPayoutRatio() != null){
				this.valuePaperAttributes.put("Payout Ratio:", s.getPayoutRatio().toString());
			}
			
			if(s.getDividendDate() != null){
				this.valuePaperAttributes.put("Dividend Rate:", format.format(s.getDividendDate().getTime()));
			}
			
			if(s.getEx_DividendDate() != null){
				this.valuePaperAttributes.put("Ex Dividend Rate:", format.format(s.getEx_DividendDate().getTime()));
			}

//			if(s.getHistoricPricesPageUrl() != null){
//				this.valuePaperAttributes.put("Historische Preise:", s.getHistoricPricesPageUrl());
//			}
//
//			if(s.getBoerseCertificatePageUrl() != null){
//				this.valuePaperAttributes.put("Börse-Zertifikate:", s.getBoerseCertificatePageUrl());
//			}
//
//			if(s.getFinanzenCertificatePageUrl() != null){
//				this.valuePaperAttributes.put("Finanzen-Zertifikate:", s.getFinanzenCertificatePageUrl());
//			}
		}

		if(valuePaper.getType() == ValuePaperType.BOND){

			StockBond sb = (StockBond)valuePaper;
			Stock baseStock = sb.getBaseStock();

//			this.valuePaperAttributes.put("URL der historischen Preise:", sb.getHistoricPricesPageUrl());

			if(baseStock != null){

				this.valuePaperAttributes.put("Bezeichnung (Basis-Aktie):", baseStock.getName());
				this.valuePaperAttributes.put("ISIN (Basis-Aktie):", baseStock.getCode());
				this.valuePaperAttributes.put("Typ (Basis-Aktie):", baseStock.getType().toString());
				this.valuePaperAttributes.put("Aktueller Kurs (Basis-Aktie):", valuePaperPriceEntryService.getLastPriceEntry(baseStock.getCode()).getPrice().toString());
				this.valuePaperAttributes.put("Währung (Basis-Aktie):", baseStock.getCurrency().getCurrencyCode());
				this.valuePaperAttributes.put("Index (Basis-Aktie):", baseStock.getIndex());
				this.valuePaperAttributes.put("Details (Basis-Aktie):", baseStock.getDetailUrl());
				this.valuePaperAttributes.put("Historische Preise (Basis-Aktie):", baseStock.getHistoricPricesPageUrl());
				this.valuePaperAttributes.put("Börse-Zertifikate (Basis-Aktie):", baseStock.getBoerseCertificatePageUrl());
				this.valuePaperAttributes.put("Finanzen-Zertifikate (Basis-Aktie):", baseStock.getFinanzenCertificatePageUrl());	
			}

		}
		if(valuePaper.getType() == ValuePaperType.FUND){

			Fund f = (Fund)valuePaper;

		}
	}

	private void createLineChartModels(){

		valuePaperHistoricPriceLineChartModel = new LineChartModel();
		LineChartSeries series1 = new LineChartSeries();
		series1.setLabel(valuePaper.getName());

		try{

			List<ValuePaperHistoryEntry> historyPriceList = valuePaperPriceEntryService.getValuePaperPriceHistoryEntries(valuePaper.getCode());

			SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");

			for (ValuePaperHistoryEntry vphe : historyPriceList) {
				String date = format.format(vphe.getDate().getTime());
				BigDecimal value = (vphe.getDayHighPrice().add(vphe.getDayLowPrice())).divide(new BigDecimal(2));
				series1.set(date, value);
			}

			ValuePaperPriceEntry currentPriceEntry = valuePaperPriceEntryService.getLastPriceEntry(valuePaper.getCode());

			series1.set(format.format(currentPriceEntry.getCreated().getTime()), currentPriceEntry.getPrice());

			valuePaperHistoricPriceLineChartModel.addSeries(series1);
			valuePaperHistoricPriceLineChartModel.setTitle("Kursverlauf");
			valuePaperHistoricPriceLineChartModel.setZoom(true);
			valuePaperHistoricPriceLineChartModel.getAxis(AxisType.Y).setLabel("Wert");

			DateAxis axis = new DateAxis("Datum");
			axis.setTickAngle(-50);
			//axis.setTickInterval("0");

			currentPriceEntry.getCreated().add(Calendar.DATE, 1);

			axis.setMax(format.format(currentPriceEntry.getCreated().getTime()));
			axis.setTickFormat("%b %#d, %y");

			valuePaperHistoricPriceLineChartModel.getAxes().put(AxisType.X, axis);

		}
		catch(EntityNotFoundException e){
			valuePaperHistoricPriceLineChartModel = null;
		}
	}

}

