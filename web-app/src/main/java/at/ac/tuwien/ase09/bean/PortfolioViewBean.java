package at.ac.tuwien.ase09.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.RequestScoped;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.deltaspike.core.api.scope.ViewAccessScoped;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.DateAxis;
import org.primefaces.model.chart.LineChartModel;
import org.primefaces.model.chart.LineChartSeries;
import org.primefaces.model.chart.PieChartModel;

import at.ac.tuwien.ase09.data.PortfolioDataAccess;
import at.ac.tuwien.ase09.data.TransactionDataAccess;
import at.ac.tuwien.ase09.data.ValuePaperPriceEntryDataAccess;
import at.ac.tuwien.ase09.model.Portfolio;
import at.ac.tuwien.ase09.model.PortfolioValuePaper;
import at.ac.tuwien.ase09.model.User;
import at.ac.tuwien.ase09.model.ValuePaper;
import at.ac.tuwien.ase09.model.ValuePaperHistoryEntry;
import at.ac.tuwien.ase09.model.ValuePaperType;
import at.ac.tuwien.ase09.model.order.Order;
import at.ac.tuwien.ase09.model.transaction.OrderFeeTransactionEntry;
import at.ac.tuwien.ase09.model.transaction.OrderTransactionEntry;
import at.ac.tuwien.ase09.model.transaction.PayoutTransactionEntry;
import at.ac.tuwien.ase09.model.transaction.TransactionEntry;
import at.ac.tuwien.ase09.service.PortfolioService;
import at.ac.tuwien.ase09.service.ValuePaperPriceEntryService;

@Named
@ViewScoped
public class PortfolioViewBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private PortfolioDataAccess portfolioDataAccess;
	
	@Inject
	private PortfolioService portfolioService;
	
	@Inject
	private ValuePaperPriceEntryDataAccess priceDataAccess;
	
	@Inject
	private TransactionDataAccess transactionDataAccess;
	
	private Long portfolioId;
	
	private Portfolio portfolio;
	
	
	private PieChartModel valuePaperTypePie;
	private PieChartModel valuePaperCountryPie;
	private LineChartModel portfolioChart;
	
    public void init() {
		loadPortfolio(portfolioId);
        createPieModels();
        createPortfolioChart();
    }
    
    public Portfolio getPortfolio() {
    	return portfolio;
    }
	
	public Long getPortfolioId() {
		return portfolioId;
	}

	public void setPortfolioId(Long portfolioId) {
		this.portfolioId = portfolioId;
		//loadPortfolio(portfolioId);
	}
	
	public PieChartModel getValuePaperCountryPie() {
		return valuePaperCountryPie;
	}
	
	public PieChartModel getValuePaperTypePie() {
		return valuePaperTypePie;
	}
	
	public LineChartModel getPortfolioChart() {
		return portfolioChart;
	}
	
	public String getTransactionValuePaperName(TransactionEntry t) {
		if (t instanceof OrderTransactionEntry) {
			OrderTransactionEntry ot = (OrderTransactionEntry)t;
			return ot.getOrder().getValuePaper().getName();
		} else if (t instanceof OrderFeeTransactionEntry) {
			OrderFeeTransactionEntry oft = (OrderFeeTransactionEntry)t;
			return oft.getOrder().getValuePaper().getName();
		} else if (t instanceof PayoutTransactionEntry) {
			PayoutTransactionEntry pt = (PayoutTransactionEntry)t;
			return pt.getValuePaper().getName();
		}
		return "";
	}
	
	public List<PortfolioValuePaper> getValuePaperList() {
		return new LinkedList<PortfolioValuePaper>(portfolio.getValuePapers());
	}
	
	public List<Order> getOrderList() {
		return new LinkedList<Order>(portfolio.getOrders());
	}
	
	public List<TransactionEntry> getTransactionList() {
		return new LinkedList<TransactionEntry>(portfolio.getTransactionEntries());
	}
	
	public List<User> getFollowerList() {
		return new LinkedList<User>(portfolio.getFollowers());
	}
	
	public void changeVisibility() {
		portfolioService.updatePortfolio(portfolio);
	}
	
	
	private void loadPortfolio(Long portfolioId) {
		this.portfolio = portfolioDataAccess.getPortfolioById(portfolioId);
	}
	
	private void createPieModels() {
		createValuePaperCountryPie();
		createValuePaperTypePie();
		
	}
	
	private void createValuePaperTypePie() {
		Map<ValuePaperType, Integer> valuePaperTypeCounterMap = portfolioService.getValuePaperTypeCountMap(portfolio);
		
		valuePaperTypePie = new PieChartModel();
		for (ValuePaperType type : valuePaperTypeCounterMap.keySet()) {
			valuePaperTypePie.set(type.name() + ": " + valuePaperTypeCounterMap.get(type), valuePaperTypeCounterMap.get(type));
		}
		valuePaperTypePie.setTitle("Wertpapiere nach Typen");
		valuePaperTypePie.setLegendPosition("w");
		valuePaperTypePie.setShowDataLabels(true);
		/*valuePaperTypePie.set("Fonds", 240);
		valuePaperTypePie.set("Anleihen", 525);
		valuePaperTypePie.set("Aktien", 702);*/
         
		
		
	}

	private void createValuePaperCountryPie() {
		Map<String, Integer> valuePaperCountryCountMap = portfolioService.getValuePaperCountryCountMap(portfolio);
		
		valuePaperCountryPie = new PieChartModel();
		for (String country : valuePaperCountryCountMap.keySet()) {
			valuePaperCountryPie.set(country + ": " + valuePaperCountryCountMap.get(country), valuePaperCountryCountMap.get(country));
		}
		valuePaperCountryPie.setTitle("Wertpapiere nach Länder");
		valuePaperCountryPie.setLegendPosition("w");
		
		
    }
	
	private void createPortfolioChart() {
		portfolioChart = new LineChartModel();
		portfolioChart.setTitle("Zoom");
        portfolioChart.setZoom(true);
        portfolioChart.getAxis(AxisType.Y).setLabel("Wert");
        DateAxis axis = new DateAxis("Datum");
        axis.setTickAngle(-50);
        axis.setTickFormat("%b %#d, %y");
        //axis.setTickInterval("0");
        //axis.setMax("2014-12-24");
         
        portfolioChart.getAxes().put(AxisType.X, axis);
        LineChartSeries series = new LineChartSeries();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        series.setLabel("Series");
        
        
        // get valuepaper list
        /*Set<PortfolioValuePaper> valuePaperSet = portfolio.getValuePapers();
        List<ValuePaperHistoryEntry> result = new LinkedList<>();
        for (PortfolioValuePaper portfolio_valuepaper : valuePaperSet) {
        	ValuePaper paper = portfolio_valuepaper.getValuePaper();
        	// get history price entries by valuepaper code
        	List<ValuePaperHistoryEntry> priceEntries = priceDataAccess.getValuePaperPriceHistoryEntries(paper.getCode());
        	result.addAll(priceEntries);
        }*/
        String creationDate = format.format(portfolio.getCreated().getTime());
        BigDecimal startCapital = portfolio.getSetting().getStartCapital().getValue();
        //BigDecimal pointValue = startCapital;
        series.set(creationDate, startCapital);
        
        // GET PORTFOLIO VALUEPAPER HISTORY PRICE ENTRIES
        List<ValuePaperHistoryEntry> historyEntries = priceDataAccess.getHistoricValuePaperPricesByPortfolioId(portfolio.getId()); 
        Map<Calendar, BigDecimal> pointResults = new HashMap<>();
        
        for (ValuePaperHistoryEntry historyEntry : historyEntries) {
        	Calendar date = historyEntry.getDate();
        	BigDecimal closingPrice = historyEntry.getClosingPrice();
        	BigDecimal pointValue = startCapital;
        	System.out.println("---------------------------------------------------");
        	System.out.println("zeitpunkt: " + date.getTime());
        	System.out.println("old pointValue: " + pointValue);
        	System.out.println("closingPrice: " + closingPrice);
        	
        	List<OrderTransactionEntry> buyTransactions = transactionDataAccess.getBuyTransactionsUntil(portfolio, historyEntry.getValuePaper(), date);
        	BigDecimal totalBuyPrice = new BigDecimal(0);
        	BigDecimal totalValue = new BigDecimal(0);
        	System.out.println("buyTransaction: " + buyTransactions);
        	for (OrderTransactionEntry ot : buyTransactions) {
        		totalBuyPrice = totalBuyPrice.add(ot.getValue().getValue());
        		BigDecimal volume = BigDecimal.valueOf(ot.getOrder().getVolume());
        		totalValue = totalValue.add(volume.multiply(closingPrice));
        	}
        	System.out.println("totalBuyPrice: " + totalBuyPrice);
        	System.out.println("totalValue: " + totalValue);
        	
        	pointValue = pointValue.subtract(totalBuyPrice).add(totalValue);
        	System.out.println("new pointValue: " + pointValue);
        	
        	pointResults.put(date, pointValue);
        	/*if (pointResults.containsKey(date)) {
        		BigDecimal oldValue = pointResults.get(date);
        		pointResults.put(date, new BigDecimal(oldValue.longValue()+closingPrice.longValue()));
        	} else {
        		pointResults.put(date, closingPrice);
        	}*/
        }
        for (Calendar date: pointResults.keySet()) {
        	BigDecimal value = pointResults.get(date);
        	series.set(format.format(date.getTime()), value);
        }
        
        // GET PORTFOLIO TRANSACTIONS
        
        //series.set("2014-01-03", BigDecimal.valueOf(30+portfolio.getSetting().getStartCapital().getValue().longValue()));
        
        /*Set<TransactionEntry> transactionSet = portfolio.getTransactionEntries();
        for (TransactionEntry transaction : transactionSet) {
        	String date = format.format(transaction.getCreated().getTime());
        	BigDecimal value = transaction.getValue().getValue();
        	series.set(date, value);
        }*/
        
        /*series1.set("2014-01-01", 51);
        series1.set("2014-01-06", 22);
        series1.set("2014-01-12", 65);
        series1.set("2014-01-18", 74);
        series1.set("2014-01-24", 24);
        series1.set("2014-01-30", 51);*/
        
        portfolioChart.addSeries(series);
        
        
	}

	
}
