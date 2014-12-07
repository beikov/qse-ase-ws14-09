package at.ac.tuwien.ase09.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
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
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.deltaspike.core.api.scope.ViewAccessScoped;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.DateAxis;
import org.primefaces.model.chart.LineChartModel;
import org.primefaces.model.chart.LineChartSeries;
import org.primefaces.model.chart.MeterGaugeChartModel;
import org.primefaces.model.chart.PieChartModel;

import at.ac.tuwien.ase09.data.PortfolioDataAccess;
import at.ac.tuwien.ase09.data.ValuePaperPriceEntryDataAccess;
import at.ac.tuwien.ase09.model.AnalystOpinion;
import at.ac.tuwien.ase09.model.AnalystRecommendation;
import at.ac.tuwien.ase09.model.Money;
import at.ac.tuwien.ase09.model.NewsItem;
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
	
	public List<NewsItem> getNewsForValuePapers() {
		return portfolioService.getNewsForValuePapers(portfolio.getValuePapers());
	}
	
	public List<AnalystOpinion> getAnalystOpinionsForValuePapers() {
		return portfolioService.getAnalystOpinionsForValuePapers(portfolio.getValuePapers());
	}
	
	/*public String getTransactionValuePaperName(TransactionEntry t) {
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
	}*/
	
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
		portfolio = portfolioService.updatePortfolio(portfolio);
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Sichtbarkeitseinstellungen gespeichert",  null);
        FacesContext.getCurrentInstance().addMessage(null, message);
	}
	
	public Money getLatesValuePaperPrice(String code) {
		Money money = portfolio.getCurrentCapital();
		money.setValue(priceDataAccess.getLastPriceEntry(code).getPrice());
		return money;
	}
	
	public Money getTotalPayed(String code) {
		Money money = portfolio.getCurrentCapital();
		money.setValue(portfolioService.getTotalPayedForValuePaper(code));
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
	
	public double getChange(String code) {
		PortfolioValuePaper pvp = null;
		for (PortfolioValuePaper portfolioValuePaper : portfolio.getValuePapers()) {
			if (portfolioValuePaper.getValuePaper().getCode().equals(code)) {
				pvp = portfolioValuePaper;
				break;
			}
		}
		return portfolioService.getChange(pvp);
		//return 0;
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
        portfolioChart.getAxis(AxisType.Y).setLabel("Wert in " + portfolio.getCurrentCapital().getCurrency().getCurrencyCode());
        DateAxis axis = new DateAxis("Datum");
        axis.setTickAngle(-50);
        axis.setTickFormat("%b %#d, %y");
        //axis.setTickInterval("0");
        //axis.setMax("2014-12-24");
        portfolioChart.getAxes().put(AxisType.X, axis); 
        
        LineChartSeries series = new LineChartSeries();
        series.setLabel("Series");
        
        Map<String, BigDecimal> pointResult = portfolioService.getPortfolioChartEntries(portfolio);        
        
        for (String date: pointResult.keySet()) {
        	BigDecimal value = pointResult.get(date);
        	series.set(date, value);
        }
        
        portfolioChart.addSeries(series);
        
	}
}
