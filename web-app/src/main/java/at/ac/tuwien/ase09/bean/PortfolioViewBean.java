package at.ac.tuwien.ase09.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
import org.primefaces.model.chart.PieChartModel;

import at.ac.tuwien.ase09.context.UserContext;
import at.ac.tuwien.ase09.context.WebUserContext;
import at.ac.tuwien.ase09.data.PortfolioDataAccess;
import at.ac.tuwien.ase09.data.ValuePaperPriceEntryDataAccess;
import at.ac.tuwien.ase09.model.AnalystOpinion;
import at.ac.tuwien.ase09.model.Money;
import at.ac.tuwien.ase09.model.NewsItem;
import at.ac.tuwien.ase09.model.Portfolio;
import at.ac.tuwien.ase09.model.PortfolioValuePaper;
import at.ac.tuwien.ase09.model.User;
import at.ac.tuwien.ase09.model.ValuePaperType;
import at.ac.tuwien.ase09.model.order.Order;
import at.ac.tuwien.ase09.model.order.OrderStatus;
import at.ac.tuwien.ase09.model.transaction.TransactionEntry;
import at.ac.tuwien.ase09.service.PortfolioService;

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
	WebUserContext userContext;

	private List<User> followers;
	
	private List<PortfolioValuePaper> portfolioValuePapers;
	
	private List<Order> orders;
	private List<TransactionEntry> transactions;
	
	private List<NewsItem> news;
	
	private List<AnalystOpinion> opinions;

	
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
        followers = new LinkedList<User>(portfolio.getFollowers());
        portfolioValuePapers = new ArrayList<>(portfolio.getValuePapers());
        orders = new LinkedList<Order>(portfolio.getOrders());
        transactions = new LinkedList<TransactionEntry>(portfolio.getTransactionEntries());
        news = portfolioDataAccess.getNewsForPortfolio(portfolio);
        opinions = portfolioDataAccess.getAnalystOpinionsForPortfolio(portfolio);
        
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
		return news;
	}
	
	public List<AnalystOpinion> getAnalystOpinionsForValuePapers() {
		return opinions;
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
		return portfolioValuePapers;
	}
	
	public List<Order> getOrderList() {
		return orders;
	}
	
	public List<TransactionEntry> getTransactionList() {
		return transactions;
	}
	
	public List<User> getFollowerList() {
		return followers;
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
		Money money = portfolio.getSetting().getStartCapital();
		money.setValue(portfolioDataAccess.getTotalPayedForValuePaper(code));
		return money;
	}
	
	public Money getProfit(PortfolioValuePaper pvp) {
		Money money = portfolio.getSetting().getStartCapital();
		money.setValue(new BigDecimal(portfolioDataAccess.getProfit(pvp)));
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
		return portfolioDataAccess.getChange(pvp);
		//return 0;
	}
	
	public boolean isVisible(String context) {
		boolean setting = false;
		if (context.equals("valuePapers")) {
			setting = portfolio.getVisibility().getValuePaperListVisible();
		}
		if (setting) { // dont need to check user if current "tab" should be visible
			return true;
		} else if (userContext != null) {
			// setting = false -> only owner should see current "tab"
			User u = userContext.getUser();
			if (u == null) {
				return false;
			}
			return portfolio.getOwner().getUsername().equals(u.getUsername());
		}
		return false;
	}
	
	
	private void loadPortfolio(Long portfolioId) {
		this.portfolio = portfolioDataAccess.getPortfolioById(portfolioId);
	}
	
	private void createPieModels() {
		createValuePaperCountryPie();
		createValuePaperTypePie();
		
	}
	
	private void createValuePaperTypePie() {
		Map<ValuePaperType, Integer> valuePaperTypeCounterMap = portfolioDataAccess.getValuePaperTypeCountMap(portfolio);
		
		if (valuePaperTypeCounterMap.keySet().size() <= 0) {
			return;
		}
		
		valuePaperTypePie = new PieChartModel();
		for (ValuePaperType type : valuePaperTypeCounterMap.keySet()) {
			valuePaperTypePie.set(type.name() + ": " + valuePaperTypeCounterMap.get(type), valuePaperTypeCounterMap.get(type));
		}
		valuePaperTypePie.setTitle("Wertpapiere nach Typen");
		valuePaperTypePie.setLegendPosition("w");
		valuePaperTypePie.setShowDataLabels(true);
	}

	private void createValuePaperCountryPie() {
		Map<String, Integer> valuePaperCountryCountMap = portfolioDataAccess.getValuePaperCountryCountMap(portfolio);
		
		if (valuePaperCountryCountMap.keySet().size() <= 0) {
			return;
		}
		
		valuePaperCountryPie = new PieChartModel();
		for (String country : valuePaperCountryCountMap.keySet()) {
			valuePaperCountryPie.set(country + ": " + valuePaperCountryCountMap.get(country), valuePaperCountryCountMap.get(country));
		}
		valuePaperCountryPie.setTitle("Wertpapiere nach Länder");
		valuePaperCountryPie.setLegendPosition("w");
    }
	
	private void createPortfolioChart() {
		portfolioChart = new LineChartModel();
		portfolioChart.setTitle("Portfoliochart mit Gebühren, etc.");
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
        
        Map<String, BigDecimal> pointResult = portfolioDataAccess.getPortfolioChartEntries(portfolio);        
        
        for (String date: pointResult.keySet()) {
        	BigDecimal value = pointResult.get(date);
        	series.set(date, value);
        }
        
        portfolioChart.addSeries(series);
        
	}
}
