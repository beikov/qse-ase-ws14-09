package at.ac.tuwien.ase09.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import at.ac.tuwien.ase09.data.ValuePaperPriceEntryDataAccess;
import at.ac.tuwien.ase09.model.Money;
import at.ac.tuwien.ase09.model.Portfolio;
import at.ac.tuwien.ase09.model.PortfolioValuePaper;
import at.ac.tuwien.ase09.model.User;
import at.ac.tuwien.ase09.model.ValuePaper;
import at.ac.tuwien.ase09.model.ValuePaperHistoryEntry;
import at.ac.tuwien.ase09.model.ValuePaperType;
import at.ac.tuwien.ase09.model.order.Order;
import at.ac.tuwien.ase09.model.order.OrderStatus;
import at.ac.tuwien.ase09.model.transaction.FeeTransactionEntry;
import at.ac.tuwien.ase09.model.transaction.OrderFeeTransactionEntry;
import at.ac.tuwien.ase09.model.transaction.OrderTransactionEntry;
import at.ac.tuwien.ase09.model.transaction.PayoutTransactionEntry;
import at.ac.tuwien.ase09.model.transaction.TaxTransactionEntry;
import at.ac.tuwien.ase09.model.transaction.TransactionEntry;
import at.ac.tuwien.ase09.model.transaction.TransactionType;
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
	
	private Long portfolioId;
	
	private Portfolio portfolio;
	
	private List<Order> filteredOrders;
	
	
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
	
	public Money getLatesValuePaperPrice(String code) {
		Money money = portfolio.getCurrentCapital();
		money.setValue(priceDataAccess.getLastPriceEntry(code).getPrice());
		return money;
	}
	
	public Money getTotalPayed(String code) {
		Money money = portfolio.getCurrentCapital();
		money.setValue(new BigDecimal(0));
		for (TransactionEntry t : portfolio.getTransactionEntries()) {
			if (t.getType() == TransactionType.ORDER && ((OrderTransactionEntry)t).getOrder().getValuePaper().getCode().equals(code)) {
				//OrderTransactionEntry ot = (OrderTransactionEntry)t;
				//BigDecimal volume = BigDecimal.valueOf(ot.getOrder().getVolume());
				//payed = payed.add(t.getValue().getValue().divide(volume));
				//payed = payed.add(t.getValue().getValue());
				BigDecimal oldVal = money.getValue();
				BigDecimal newVal = oldVal.add(t.getValue().getValue());
				money.setValue(newVal);
			}
		}
		return money;
	}
	
	
	public Object[] getOrderStates() {
		return OrderStatus.values();
	}
	
	public void setFilteredOrders(List<Order> filteredOrders) {
		this.filteredOrders = filteredOrders;
	}
	
	
	public List<Order> getFilteredOrders() {
		return filteredOrders;
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
		portfolioChart.setTitle("Portfoliochart");
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
        
        String creationDate = format.format(portfolio.getCreated().getTime());
        BigDecimal startCapital = portfolio.getSetting().getStartCapital().getValue();
        //BigDecimal pointValue = startCapital;
        series.set(creationDate, startCapital);
        
        // GET PORTFOLIO VALUEPAPER HISTORY PRICE ENTRIES 
        Map<String, BigDecimal> pointResult = new HashMap<>();
        
        for (TransactionEntry transaction : portfolio.getTransactionEntries()) {
        	if (transaction.getType() == TransactionType.ORDER) {
        		continue;
        	}
        	
        	String date = format.format(transaction.getCreated().getTime());
        	BigDecimal value = transaction.getValue().getValue();
        	
        	if (transaction.getType() == TransactionType.PAYOUT) {
        		value = value.negate();
        	}
        	
        	if (pointResult.containsKey(date)) {
    			BigDecimal old = pointResult.get(date);
    			pointResult.put(date, old.subtract(value));
    		} else {
    			pointResult.put(date, value.negate());
    		}
        }
        
        List<ValuePaperHistoryEntry> historyEntries = priceDataAccess.getHistoricValuePaperPricesByPortfolioId(portfolio.getId());
        for (ValuePaperHistoryEntry historyEntry : historyEntries) {
        	Calendar calendar = historyEntry.getDate();
        	calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            calendar.set(Calendar.MILLISECOND, 999);
        	BigDecimal totalBuyPrice = new BigDecimal(0);
        	BigDecimal totalValue = new BigDecimal(0);
        	
        	for (TransactionEntry transaction : portfolio.getTransactionEntries()) {
        		if (transaction.getType() == TransactionType.ORDER && transaction.getCreated().before(calendar)) {        				
        			OrderTransactionEntry ot = (OrderTransactionEntry)transaction;
        			totalBuyPrice = totalBuyPrice.add(ot.getValue().getValue());
            		BigDecimal volume = BigDecimal.valueOf(ot.getOrder().getVolume());
            		totalValue = totalValue.add(volume.multiply(historyEntry.getClosingPrice()));
            		//totalValue = totalValue.add(historyEntry.getClosingPrice());
        		}
        	}
        	
        	String date = format.format(calendar.getTime());
        	BigDecimal pointValue = startCapital.subtract(totalBuyPrice).add(totalValue);
        	if (pointResult.containsKey(date)) {
        		BigDecimal oldValue = pointResult.get(date);
        		pointResult.put(format.format(calendar.getTime()), oldValue.add(pointValue));
        	} else {
        		pointResult.put(date, pointValue);
        	}
        }
        
        
        for (String date: pointResult.keySet()) {
        	BigDecimal value = pointResult.get(date);
        	series.set(date, value);
        }
        
        portfolioChart.addSeries(series);
        
	}

	
}
