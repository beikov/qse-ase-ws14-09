package at.ac.tuwien.ase09.bean;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.SessionScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.DateAxis;
import org.primefaces.model.chart.LineChartModel;
import org.primefaces.model.chart.LineChartSeries;

import at.ac.tuwien.ase09.data.AnalystOpinionDataAccess;
import at.ac.tuwien.ase09.data.DividendHistoryEntryDataAccess;
import at.ac.tuwien.ase09.data.NewsItemDataAccess;
import at.ac.tuwien.ase09.data.ValuePaperDataAccess;
import at.ac.tuwien.ase09.data.ValuePaperPriceEntryDataAccess;
import at.ac.tuwien.ase09.exception.EntityNotFoundException;
import at.ac.tuwien.ase09.model.AnalystOpinion;
import at.ac.tuwien.ase09.model.DividendHistoryEntry;
import at.ac.tuwien.ase09.model.Fund;
import at.ac.tuwien.ase09.model.NewsItem;
import at.ac.tuwien.ase09.model.Stock;
import at.ac.tuwien.ase09.model.StockBond;
import at.ac.tuwien.ase09.model.ValuePaper;
import at.ac.tuwien.ase09.model.ValuePaperHistoryEntry;
import at.ac.tuwien.ase09.model.ValuePaperPriceEntry;
import at.ac.tuwien.ase09.model.ValuePaperType;
import at.ac.tuwien.ase09.model.filter.Attribute;
import at.ac.tuwien.ase09.service.ValuePaperPriceEntryService;

@ManagedBean
@Named
@SessionScoped
public class ValuePaperViewBean implements Serializable{

	private static final long serialVersionUID = 1L;

	@Inject
	private ValuePaperPriceEntryDataAccess valuePaperPriceEntryDataAccess;

	@Inject
	private ValuePaperDataAccess valuePaperDataAccess;

	@Inject
	private NewsItemDataAccess newsItemDataAccess;

	@Inject
	private DividendHistoryEntryDataAccess dividendHistoryEntryDataAccess;

	@Inject
	private AnalystOpinionDataAccess analystOpinionDataAccess;
	
	private ValuePaper valuePaper = null;
	private NewsItem selectedNewsItem = null;
	private AnalystOpinion selectedAnalystOpinion = null;

	private String valuePaperCode;

	private LineChartModel valuePaperHistoricPriceLineChartModel = null;

	private List<NewsItem> newsItemsList = null;
	private List<DividendHistoryEntry> stockDividendList = null;
	private List<AnalystOpinion> stockAnalystOpinionList = null;

	private Map<String, String> mainValuePaperAttributes = null;
	private Map<String, String> additionalValuePaperAttributes = null;


	public void init() throws IOException {
		loadValuePaper(valuePaperCode);

		if(this.valuePaper != null){
			loadValuePaperAttributes();
			loadNewsItems();
			loadStockDividendHistoryEntries();
			loadStockAnalystOpinions();
			createLineChartModels();
		}
		else{
			FacesContext context = FacesContext.getCurrentInstance();
			context.getExternalContext().responseSendError(404, "Wertpapier-Code existiert nicht");
			context.responseComplete();
			return;
		}
	}

	public String getValuePaperCode() {
		return valuePaperCode;
	}

	public void setValuePaperCode(String valuePaperCode) {
		this.valuePaperCode = valuePaperCode;
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

	public Map<String, String> getAdditionalValuePaperAttributes() {
		return additionalValuePaperAttributes;
	}

	public void setAdditionalValuePaperAttributes(
			Map<String, String> additionalValuePaperAttributes) {
		this.additionalValuePaperAttributes = additionalValuePaperAttributes;
	}

	public Map<String, String> getMainValuePaperAttributes() {
		return mainValuePaperAttributes;
	}

	public void setMainValuePaperAttributes(
			Map<String, String> mainValuePaperAttributes) {
		this.mainValuePaperAttributes = mainValuePaperAttributes;
	}

	public NewsItem getSelectedNewsItem() {		
		return selectedNewsItem;
	}

	public void setSelectedNewsItem(NewsItem selectedNewsItem) {		
		this.selectedNewsItem = selectedNewsItem;
	}

	public List<NewsItem> getNewsItemsList() {
		return newsItemsList;
	}

	public void setNewsItemsList(List<NewsItem> newsItemsList) {
		this.newsItemsList = newsItemsList;
	}

	public List<DividendHistoryEntry> getStockDividendList() {
		return stockDividendList;
	}

	public void setStockDividendList(List<DividendHistoryEntry> stockDividendList) {
		this.stockDividendList = stockDividendList;
	}

	public List<AnalystOpinion> getStockAnalystOpinionList() {
		return stockAnalystOpinionList;
	}

	public void setStockAnalystOpinionList(
			List<AnalystOpinion> stockAnalystOpinionList) {
		this.stockAnalystOpinionList = stockAnalystOpinionList;
	}

	public AnalystOpinion getSelectedAnalystOpinion() {
		return selectedAnalystOpinion;
	}

	public void setSelectedAnalystOpinion(AnalystOpinion selectedAnalystOpinion) {
		this.selectedAnalystOpinion = selectedAnalystOpinion;
	}




	public ValuePaperPriceEntry getLastPriceEntry() {

		try{
			return valuePaperPriceEntryDataAccess.getLastPriceEntry(valuePaper.getCode());
		}
		catch(EntityNotFoundException e){
			return null;
		}
	}
	
	public String getLastPriceEntryString() {
		
		if(getLastPriceEntry() == null){
			return "-";
		}
			
		switch(valuePaper.getType()){
			case STOCK:
				if(((Stock)valuePaper).getCurrency() != null){
					return getLastPriceEntry().getPrice().toString() + "" + ((Stock)valuePaper).getCurrency().getSymbol();
				}
				else{
					return getLastPriceEntry().getPrice().toString();
				}
				
			case FUND:
				if(((Fund)valuePaper).getCurrency() != null){
					return getLastPriceEntry().getPrice().toString() + " " + ((Fund)valuePaper).getCurrency().getSymbol();
				}
				else{
					return getLastPriceEntry().getPrice().toString();
				}
				
			default:			
				return getLastPriceEntry().getPrice().toString()+"%";
		}
		
	}

	public List<NewsItem> getNewsItems(){		
		return newsItemDataAccess.getNewsItemsByValuePaperCode(valuePaper.getCode());
	}

	private void loadValuePaper(String valuePaperIsin) {

		try{
			this.valuePaper = valuePaperDataAccess.getValuePaperByCode(valuePaperIsin, ValuePaper.class);
		}
		catch(EntityNotFoundException e){
			this.valuePaper = null;
		}

	}

	private void loadNewsItems() {
		newsItemsList = newsItemDataAccess.getNewsItemsByValuePaperCode(valuePaper.getCode());

		Collections.sort(newsItemsList, new Comparator<NewsItem>() {

			@Override
			public int compare(NewsItem arg0, NewsItem arg1) {
				if(arg0.getCreated().getTime().getTime() < arg1.getCreated().getTime().getTime())
					return 1;
				if(arg0.getCreated().getTime().getTime() > arg1.getCreated().getTime().getTime())
					return -1;
				return 0;
			}
		}); 
	}

	private void loadStockDividendHistoryEntries() {
		stockDividendList = dividendHistoryEntryDataAccess.getDividendHistoryEntryByValuePaperCode(valuePaper.getCode());

		Collections.sort(stockDividendList, new Comparator<DividendHistoryEntry>() {

			@Override
			public int compare(DividendHistoryEntry arg0, DividendHistoryEntry arg1) {
				if(arg0.getDividendDate().getTime().getTime() < arg1.getDividendDate().getTime().getTime())
					return 1;
				if(arg0.getDividendDate().getTime().getTime() > arg1.getDividendDate().getTime().getTime())
					return -1;
				return 0;
			}
		});
	}

	private void loadStockAnalystOpinions() {
		stockAnalystOpinionList = analystOpinionDataAccess.getAnalystOpinionsByValuePaperCode(valuePaper.getCode());

		Collections.sort(stockAnalystOpinionList, new Comparator<AnalystOpinion>() {

			@Override
			public int compare(AnalystOpinion o1, AnalystOpinion o2) {
				if(o1.getCreated().getTime().getTime() < o2.getCreated().getTime().getTime())
					return 1;
				if(o1.getCreated().getTime().getTime() > o2.getCreated().getTime().getTime())
					return -1;
				return 0;
			}

		});
	}

	private void loadValuePaperAttributes() {

		this.mainValuePaperAttributes = new LinkedHashMap<String, String>();
		this.additionalValuePaperAttributes = new LinkedHashMap<String, String>();

		SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");	

		if(valuePaper.getName() != null){
			this.mainValuePaperAttributes.put("Bezeichnung:", valuePaper.getName());
		}

		if(valuePaper.getCode() != null){
			this.mainValuePaperAttributes.put("Code:", valuePaper.getCode());
		}

		if(valuePaper.getType() != null){
			switch(valuePaper.getType()){
			case STOCK:
				this.mainValuePaperAttributes.put("Typ:", "Aktie");
				break;
			case FUND:
				this.mainValuePaperAttributes.put("Typ:", "Fond");
				break;
			case BOND:
				this.mainValuePaperAttributes.put("Typ:", "Anleihe");
			}
		}

		if(valuePaper.getType() == ValuePaperType.STOCK){

			Stock s = (Stock)valuePaper;

			if(s.getIndex() != null){
				this.mainValuePaperAttributes.put("Index:", s.getIndex());
			}

			if(s.getCountry() != null){
				this.mainValuePaperAttributes.put("Land:", s.getCountry());
			}

			if(s.getCurrency() != null){
				this.mainValuePaperAttributes.put("Währung:", s.getCurrency().getCurrencyCode());
			}

			if(s.getMarketCap() != null){
				this.additionalValuePaperAttributes.put("Market Cap:", s.getMarketCap().toString());
			}

			if(s.getEnterpriseValue() != null){
				this.additionalValuePaperAttributes.put("Enterprise Value:", s.getEnterpriseValue().toString());
			}

			if(s.getTrailingPE() != null){
				this.additionalValuePaperAttributes.put("Trailing PE:", s.getTrailingPE().toString());
			}

			if(s.getForwardPE() != null){
				this.additionalValuePaperAttributes.put("Forward PE:", s.getForwardPE().toString());
			}

			if(s.getpEGRatio() != null){
				this.additionalValuePaperAttributes.put("pEG Ratio:", s.getpEGRatio().toString());
			}

			if(s.getPriceSales() != null){
				this.additionalValuePaperAttributes.put("Price Sales:", s.getPriceSales().toString());
			}

			if(s.getPriceBook() != null){
				this.additionalValuePaperAttributes.put("Price Book:", s.getPriceBook().toString());
			}

			if(s.getEnterpriseValueRevenue() != null){
				this.additionalValuePaperAttributes.put("Enterprise Value Revenue:", s.getEnterpriseValueRevenue().toString());
			}

			if(s.getEnterpriseValueEBITDA() != null){
				this.additionalValuePaperAttributes.put("Enterprise Value EBITDA:", s.getEnterpriseValueEBITDA().toString());
			}

			if(s.getFiscalYearEnds() != null){
				this.additionalValuePaperAttributes.put("Fiscal Year Ends:", format.format(s.getFiscalYearEnds().getTime()));
			}

			if(s.getMostRecentQuarter() != null){
				this.additionalValuePaperAttributes.put("Most Recent Quaerter:", format.format(s.getMostRecentQuarter().getTime()));
			}

			if(s.getProfitMargin() != null){
				this.additionalValuePaperAttributes.put("Profit Margin:", s.getProfitMargin().toString());
			}

			if(s.getOperatingMargin() != null){
				this.additionalValuePaperAttributes.put("Operating Margin:", s.getOperatingMargin().toString());
			}

			if(s.getReturnonAssets() != null){
				this.additionalValuePaperAttributes.put("Return on Assets:", s.getReturnonAssets().toString());
			}

			if(s.getReturnonEquity() != null){
				this.additionalValuePaperAttributes.put("Return on Equity:", s.getReturnonEquity().toString());
			}

			if(s.getRevenue() != null){
				this.additionalValuePaperAttributes.put("Revenue:", s.getRevenue().toString());
			}

			if(s.getRevenuePerShare() != null){
				this.additionalValuePaperAttributes.put("Revenue per Share:", s.getRevenuePerShare().toString());
			}

			if(s.getQtrlyRevenueGrowth() != null){
				this.additionalValuePaperAttributes.put("Quarterly Revenue Growth:", s.getQtrlyRevenueGrowth().toString());
			}

			if(s.getGrossProfit() != null){
				this.additionalValuePaperAttributes.put("Gross Profit:", s.getGrossProfit().toString());
			}

			if(s.getEbitda() != null){
				this.additionalValuePaperAttributes.put("Ebitda:", s.getEbitda().toString());
			}

			if(s.getNetIncomeAvltoCommon() != null){
				this.additionalValuePaperAttributes.put("Net Income Available to Common:", s.getNetIncomeAvltoCommon().toString());
			}

			if(s.getDilutedEPS() != null){
				this.additionalValuePaperAttributes.put("Diluted EPS:", s.getDilutedEPS().toString());
			}

			if(s.getQtrlyEarningsGrowth() != null){
				this.additionalValuePaperAttributes.put("Quarterly Earnings Growth:", s.getQtrlyEarningsGrowth().toString());
			}

			if(s.getTotalCash() != null){
				this.additionalValuePaperAttributes.put("Total Cash:", s.getTotalCash().toString());
			}

			if(s.getTotalCashPerShare() != null){
				this.additionalValuePaperAttributes.put("Total Cash per Share:", s.getTotalCashPerShare().toString());
			}

			if(s.getTotalDebt() != null){
				this.additionalValuePaperAttributes.put("Total Debt:", s.getTotalDebt().toString());
			}

			if(s.getTotalDebtEquity() != null){
				this.additionalValuePaperAttributes.put("Total Debt Equity:", s.getTotalDebtEquity().toString());
			}

			if(s.getCurrentRatio() != null){
				this.additionalValuePaperAttributes.put("Current Ration:", s.getCurrentRatio().toString());
			}

			if(s.getBookValuePerShare() != null){
				this.additionalValuePaperAttributes.put("Book Value per Share:", s.getBookValuePerShare().toString());
			}

			if(s.getOperatingCashFlow() != null){
				this.additionalValuePaperAttributes.put("Operatin Cashflow:", s.getOperatingCashFlow().toString());
			}

			if(s.getLeveredFreeCashFlow() != null){
				this.additionalValuePaperAttributes.put("Levered Free Cashflow:", s.getLeveredFreeCashFlow().toString());
			}

			if(s.getBeta() != null){
				this.additionalValuePaperAttributes.put("Beta:", s.getBeta().toString());
			}

			if(s.getP_52_WeekChange() != null){
				this.additionalValuePaperAttributes.put("P 52 Week Change:", s.getP_52_WeekChange().toString());
			}

			if(s.getsP50052_WeekChange() != null){
				this.additionalValuePaperAttributes.put("sP50052 Week Change:", s.getsP50052_WeekChange().toString());
			}

			if(s.getP_52_WeekHigh() != null){
				this.additionalValuePaperAttributes.put("P 52 Week High:", s.getP_52_WeekHigh().toString());
			}

			if(s.getP_52_WeekLow() != null){
				this.additionalValuePaperAttributes.put("P 52 Week Low:", s.getP_52_WeekLow().toString());
			}

			if(s.getP_50_DayMovingAverage() != null){
				this.additionalValuePaperAttributes.put("P 50 Day Moving Average:", s.getP_50_DayMovingAverage().toString());
			}

			if(s.getP_200_DayMovingAverage() != null){
				this.additionalValuePaperAttributes.put("P 200 Moving Average:", s.getP_200_DayMovingAverage().toString());
			}

			if(s.getAvgVol_3month() != null){
				this.additionalValuePaperAttributes.put("Average Volume 3 Month:", s.getAvgVol_3month().toString());
			}

			if(s.getAvgVol_10day() != null){
				this.additionalValuePaperAttributes.put("Average Volume 10 days:", s.getAvgVol_10day().toString());
			}

			if(s.getSharesOutstanding() != null){
				this.additionalValuePaperAttributes.put("Shares Outstanding:", s.getSharesOutstanding().toString());
			}

			if(s.getFloatVal() != null){
				this.additionalValuePaperAttributes.put("Float Val:", s.getFloatVal().toString());
			}

			if(s.getPercentageHeldbyInsiders() != null){
				this.additionalValuePaperAttributes.put("Percentage held by Insiders:", s.getPercentageHeldbyInsiders().toString());
			}

			if(s.getPercentageHeldbyInstitutions() != null){
				this.additionalValuePaperAttributes.put("Percentage held by Institutions:", s.getPercentageHeldbyInstitutions().toString());
			}

			if(s.getSharesShortCurrentMonth() != null){
				this.additionalValuePaperAttributes.put("Shares Short Current Month:", s.getSharesShortCurrentMonth().toString());
			}

			if(s.getShortRatio() != null){
				this.additionalValuePaperAttributes.put("Short Ration:", s.getShortRatio().toString());
			}

			if(s.getShortPercentageofFloat() != null){
				this.additionalValuePaperAttributes.put("Short Percentage of Float:", s.getShortPercentageofFloat().toString());
			}

			if(s.getSharesShortPriorMonth() != null){
				this.additionalValuePaperAttributes.put("Share Short Prior Month:", s.getSharesShortPriorMonth().toString());
			}

			if(s.getForwardAnnualDividendRate() != null){
				this.additionalValuePaperAttributes.put("Forward Annual Dividend Rate:", s.getForwardAnnualDividendRate().toString());
			}

			if(s.getForwardAnnualDividendYield() != null){
				this.additionalValuePaperAttributes.put("Forward Annual Dividend Yield:", s.getForwardAnnualDividendYield().toString());
			}

			if(s.getTrailingAnnualDividendYieldAbsolute() != null){
				this.additionalValuePaperAttributes.put("Trailing Dividend Yield Absolute:", s.getTrailingAnnualDividendYieldAbsolute().toString());
			}

			if(s.getTrailingAnnualDividendYieldRelative() != null){
				this.additionalValuePaperAttributes.put("Trailing Dividend Yield Relative:", s.getTrailingAnnualDividendYieldRelative().toString());
			}

			if(s.getP_5YearAverageDividendYield() != null){
				this.additionalValuePaperAttributes.put("P 5 Year Average Dividend Yield:", s.getP_5YearAverageDividendYield().toString());
			}

			if(s.getPayoutRatio() != null){
				this.additionalValuePaperAttributes.put("Payout Ratio:", s.getPayoutRatio().toString());
			}

			if(s.getDividendDate() != null){
				this.additionalValuePaperAttributes.put("Dividend Date:", format.format(s.getDividendDate().getTime()));
			}

			if(s.getEx_DividendDate() != null){
				this.additionalValuePaperAttributes.put("Ex Dividend Date:", format.format(s.getEx_DividendDate().getTime()));
			}

		}

		if(valuePaper.getType() == ValuePaperType.BOND){

			StockBond sb = (StockBond)valuePaper;
			Stock baseStock = sb.getBaseStock();

			if(sb.getCoupon() != null){
				this.mainValuePaperAttributes.put("Nominalzins", sb.getCoupon().toString() + "%");
			}

			if(sb.getEndDate() != null){
				this.mainValuePaperAttributes.put("Ende der Laufzeit:", format.format(sb.getEndDate().getTime()));
			}

			if(sb.getEmitter() != null){
				this.additionalValuePaperAttributes.put("Emittent", sb.getEmitter());
			}
			
			if(sb.getEmissionDate() != null){
				this.additionalValuePaperAttributes.put("Emissionsdatum:", format.format(sb.getEmissionDate().getTime()));
			}

			if(sb.getEmissionPrice() != null){
				this.additionalValuePaperAttributes.put("Emissionskurs:", sb.getEmissionPrice().toString()+"%");
			}

		}
		if(valuePaper.getType() == ValuePaperType.FUND){

			Fund f = (Fund)valuePaper;

			if(f.getDepotBank() != null){
				this.mainValuePaperAttributes.put("Depot-Bank", f.getDepotBank());
			}

			if(f.getYieldType() != null){
				switch (f.getYieldType()) {
				case CUMULATIVE:
					this.mainValuePaperAttributes.put("Ausschüttungsart:", "ausschüttend");
					break;
				case DISTRIBUTING:
					this.mainValuePaperAttributes.put("Ausschüttungsart:", "thesaurierend");
				}
			}
			
			if(f.getCurrency() != null){
				this.mainValuePaperAttributes.put("Währung:", f.getCurrency().getCurrencyCode());
			}
			
			if(f.getCategory() != null){
				this.additionalValuePaperAttributes.put("Kategorie:", f.getCategory());
			}
			
			if(f.getDenomination() != null){
				this.additionalValuePaperAttributes.put("Stückelung:", f.getDenomination().toString());
			}
			
			if(f.getBusinessYearStartDay() != null && f.getBusinessYearStartMonth() != null){
				this.additionalValuePaperAttributes.put("Start des Geschäftsjahres:", f.getBusinessYearStartDay().toString() + "." + f.getBusinessYearStartMonth().toString() + ".");
			}

		}

		if(getLastPriceEntry() != null){
			switch(valuePaper.getType()){
			case STOCK:
				if(((Stock)valuePaper).getCurrency() != null){
					this.mainValuePaperAttributes.put("Aktueller Kurs:", getLastPriceEntry().getPrice().toString() + " " + ((Stock)valuePaper).getCurrency().getCurrencyCode());
				}
				else{
					this.mainValuePaperAttributes.put("Aktueller Kurs:", getLastPriceEntry().getPrice().toString());
				}
				break;
			case FUND:
				if(((Fund)valuePaper).getCurrency() != null){
					this.mainValuePaperAttributes.put("Aktueller Kurs:", getLastPriceEntry().getPrice().toString() + " " + ((Fund)valuePaper).getCurrency().getCurrencyCode());
				}
				else{
					this.mainValuePaperAttributes.put("Aktueller Kurs:", getLastPriceEntry().getPrice().toString());
				}
				break;
			case BOND:			
				this.mainValuePaperAttributes.put("Aktueller Kurs:", getLastPriceEntry().getPrice().toString()+"%");
			}
		}
		else{
			this.mainValuePaperAttributes.put("Aktueller Kurs:", "kein aktueller Kurs vorhanden");
		}

	}

	private void createLineChartModels(){
		createValuePaperHistoricPriceLineChart();
	}

	private void createValuePaperHistoricPriceLineChart(){

		valuePaperHistoricPriceLineChartModel = new LineChartModel();
		LineChartSeries series1 = new LineChartSeries();
		series1.setLabel(valuePaper.getName());
		series1.setShowMarker(false);

		try{

			List<ValuePaperHistoryEntry> historyPriceList = valuePaperPriceEntryDataAccess.getValuePaperPriceHistoryEntries(valuePaper.getCode());

			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

			for (ValuePaperHistoryEntry vphe : historyPriceList) {
				String date = format.format(vphe.getDate().getTime());
				BigDecimal value = (vphe.getDayHighPrice().add(vphe.getDayLowPrice())).divide(new BigDecimal(2));
				series1.set(date, value);
			}

			ValuePaperPriceEntry currentPriceEntry = valuePaperPriceEntryDataAccess.getLastPriceEntry(valuePaper.getCode());

			series1.set(format.format(currentPriceEntry.getCreated().getTime()), currentPriceEntry.getPrice());

			if(series1.getData().size() == 1){
				series1.setShowMarker(true);
			}

			valuePaperHistoricPriceLineChartModel.addSeries(series1);
			valuePaperHistoricPriceLineChartModel.setTitle("Kursverlauf");
			valuePaperHistoricPriceLineChartModel.setZoom(true);
			valuePaperHistoricPriceLineChartModel.getAxis(AxisType.Y).setLabel("Wert");

			DateAxis axis = new DateAxis("Datum");
			axis.setTickAngle(-50);
			//axis.setTickInterval("0");

			currentPriceEntry.getCreated().add(Calendar.DATE, 5);

			axis.setMax(format.format(currentPriceEntry.getCreated().getTime()));
			axis.setTickFormat("%b %#d, %y");

			valuePaperHistoricPriceLineChartModel.getAxes().put(AxisType.X, axis);

		}
		catch(EntityNotFoundException e){
			valuePaperHistoricPriceLineChartModel = null;
		}

	}

}

