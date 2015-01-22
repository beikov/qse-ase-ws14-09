package at.ac.tuwien.ase09.bean;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
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
import at.ac.tuwien.ase09.exception.AppException;
import at.ac.tuwien.ase09.exception.EntityNotFoundException;
import at.ac.tuwien.ase09.model.AnalystOpinion;
import at.ac.tuwien.ase09.model.Money;
import at.ac.tuwien.ase09.model.NewsItem;
import at.ac.tuwien.ase09.model.Portfolio;
import at.ac.tuwien.ase09.model.PortfolioValuePaper;
import at.ac.tuwien.ase09.model.User;
import at.ac.tuwien.ase09.model.ValuePaper;
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
	private WebUserContext userContext;
	
	private User owner;
	private boolean isOwner;
	
	private List<User> followers;
	
	private List<PortfolioValuePaper> portfolioValuePapers;
	
	private List<Order> orders;
	private List<TransactionEntry> transactions;
	
	private List<NewsItem> news;
	
	private List<AnalystOpinion> opinions;

	
	private Long portfolioId;
	
	private Portfolio portfolio;
	
	private List<Order> filteredOrders;
	
	//private Map<String,Money> totalPayedMap = new HashMap<>();
	//private Map<String,Money> profitMap = new HashMap<>();
	//private Map<String, Double> changeMap = new HashMap<>();
	
	
	private PieChartModel valuePaperTypePie;
	private PieChartModel valuePaperCountryPie;
	private LineChartModel portfolioChart;
	
	public void validateParam() throws IOException {
		FacesContext context = FacesContext.getCurrentInstance();
		if (!context.isPostback() && context.isValidationFailed()) {
			context.getExternalContext().responseSendError(500, "Fehlerhafte Portfolio-Id");
			context.responseComplete();
		}
	}
	
    public void init() throws IOException {
    	try {
    		portfolio = portfolioDataAccess.getPortfolioById(portfolioId);
		} catch(EntityNotFoundException e) {
			FacesContext context = FacesContext.getCurrentInstance();
			context.getExternalContext().responseSendError(404, "Kein Portfolio mit der Id '"+ portfolioId +"' gefunden");
			context.responseComplete();
			return;
		} catch(AppException e) {
			FacesContext context = FacesContext.getCurrentInstance();
			context.getExternalContext().responseSendError(500, "Fehler beim Laden des Portfolios");
			context.responseComplete();
			return;
		}
    	
    	owner = portfolio.getOwner();
    	isOwner = owner.getId().equals(userContext.getUserId());
        createPieModels();
        createPortfolioChart();
        followers = new LinkedList<User>(portfolio.getFollowers());
        portfolioValuePapers = new ArrayList<>(portfolio.getValuePapers());
        orders = new LinkedList<Order>(portfolio.getOrders());
        transactions = new LinkedList<TransactionEntry>(portfolio.getTransactionEntries());
        news = portfolioDataAccess.getNewsForPortfolio(portfolio);
        opinions = portfolioDataAccess.getAnalystOpinionsForPortfolio(portfolio);
        
        /*for (PortfolioValuePaper pvp : portfolio.getValuePapers()) {
        	String code = pvp.getValuePaper().getCode();
        	Money payed = new Money();
        	Money profit = new Money();
        	Double change;
        	
        	payed.setCurrency(portfolio.getCurrentCapital().getCurrency());
        	//payed.setValue(portfolioDataAccess.getTotalPayedForPortfolioValuePaper(pvp));
        	payed.setValue(pvp.getBuyPrice());
        	profit.setCurrency(payed.getCurrency());
        	profit.setValue(new BigDecimal(portfolioDataAccess.getProfit(pvp)));
        	change = portfolioDataAccess.getChange(pvp);
        	
        	//totalPayedMap.put(code, payed);
        	profitMap.put(code, profit);
        	changeMap.put(code, change);
        }*/
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
		portfolioService.updatePortfolio(portfolio);
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Sichtbarkeitseinstellungen gespeichert",  null);
        FacesContext.getCurrentInstance().addMessage(null, message);
	}
	
	/*public Money getLatesValuePaperPrice(String code) {
		Money money = portfolio.getCurrentCapital();
		money.setValue(priceDataAccess.getLastPriceEntry(code).getPrice());
		return money;
	}*/
	
	/*public Money getTotalPayed(String code) {
		return totalPayedMap.get(code);
	}*/
	
	public Money getProfit(PortfolioValuePaper pvp) {
		Money profit = new Money();
		profit.setCurrency(portfolio.getCurrentCapital().getCurrency());
    	profit.setValue(new BigDecimal(portfolioDataAccess.getProfit(pvp)));
		
		return profit;
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
	
	public Money getCostValueForPortfolio() {
		Money m = new Money();
		m.setCurrency(portfolio.getCurrentCapital().getCurrency());
		m.setValue(portfolioDataAccess.getCostValueForPortfolio(portfolioId));
		return m;
	}
	
	public Money getCurrentValueForPortfolio() {
		Money m = new Money();
		m.setCurrency(portfolio.getCurrentCapital().getCurrency());
		m.setValue(portfolioDataAccess.getCurrentValueForPortfolio(portfolioId));
		return m;
	}
	
	public double getPortfolioPerformance() {
		return portfolioDataAccess.getPortfolioPerformance(portfolioId).doubleValue();
	}
	
	
	public double getChange(PortfolioValuePaper pvp) {
    	return portfolioDataAccess.getChange(pvp);
	}
	
	public boolean isHidden() {
		if (isPortfolioOwner())
			return false;
		return !portfolio.getVisibility().getPublicVisible();
	}
	
	public boolean isChangeButtonVisible() {
		return isPortfolioOwner();
	}
	
	public boolean isValuePapersVisible() {
		return checkVisibilitySetting(portfolio.getVisibility().getValuePaperListVisible());
	}
	
	public boolean isStatisticsVisible() {
		return checkVisibilitySetting(portfolio.getVisibility().getStatisticsVisible());
	}
	
	public boolean isOrdersVisible() {
		return checkVisibilitySetting(portfolio.getVisibility().getOrderHistoryVisible());
	}
	
	public boolean isTransactionsVisible() {
		return checkVisibilitySetting(portfolio.getVisibility().getTransactionHistoryVisible());
	}
	
	public boolean isChartsVisible() {
		return checkVisibilitySetting(portfolio.getVisibility().getChartsVisible());
	}
	
	public boolean isNewsVisible() {
		return checkVisibilitySetting(portfolio.getVisibility().getNewsVisible());
	}
	
	public boolean isAnalystOpinionsVisible() {
		return checkVisibilitySetting(portfolio.getVisibility().getAnalystOpinionsVisible());
	}
	
	public boolean isPortfolioOwner() {
		return isOwner;
	}
	
	private boolean checkVisibilitySetting(boolean setting) {
		if (isPortfolioOwner()) {
			return true;
		}
		if (!portfolio.getVisibility().getPublicVisible()) {
			return false;
		}
		return setting;
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
		Map<String, BigDecimal> pointResult = portfolioDataAccess.getPortfolioChartEntries(portfolio);
		if (pointResult.size() == 1) {
			// only portfolio creation entry
			return;
		}
		
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
        
        for (String date: pointResult.keySet()) {
        	BigDecimal value = pointResult.get(date);
        	series.set(date, value);
        }
        
        portfolioChart.addSeries(series);
        
	}
}
