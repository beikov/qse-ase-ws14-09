package at.ac.tuwien.ase09.bean;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
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
import at.ac.tuwien.ase09.currency.CurrencyConversionService;
import at.ac.tuwien.ase09.data.PortfolioDataAccess;
import at.ac.tuwien.ase09.data.UserDataAccess;
import at.ac.tuwien.ase09.data.ValuePaperPriceEntryDataAccess;
import at.ac.tuwien.ase09.exception.AppException;
import at.ac.tuwien.ase09.exception.EntityNotFoundException;
import at.ac.tuwien.ase09.model.AnalystOpinion;
import at.ac.tuwien.ase09.model.Fund;
import at.ac.tuwien.ase09.model.Money;
import at.ac.tuwien.ase09.model.NewsItem;
import at.ac.tuwien.ase09.model.Portfolio;
import at.ac.tuwien.ase09.model.PortfolioValuePaper;
import at.ac.tuwien.ase09.model.Stock;
import at.ac.tuwien.ase09.model.User;
import at.ac.tuwien.ase09.model.ValuePaper;
import at.ac.tuwien.ase09.model.ValuePaperType;
import at.ac.tuwien.ase09.model.order.Order;
import at.ac.tuwien.ase09.model.order.OrderStatus;
import at.ac.tuwien.ase09.model.order.OrderType;
import at.ac.tuwien.ase09.model.transaction.TransactionEntry;
import at.ac.tuwien.ase09.service.OrderService;
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
	private UserDataAccess userDataAccess;
	
	@Inject
	private UserContext userContext;
	
	@Inject
	private OrderService orderService;
	
	private User owner;
	private User user;
	private boolean isOwner;
	
	private List<User> followers = new ArrayList<>();
	
	private List<PortfolioValuePaper> portfolioValuePapers = new ArrayList<>();
	
	private List<Order> orders = new ArrayList<>();
	private List<TransactionEntry> transactions = new ArrayList<>();
	
	private List<NewsItem> news = new ArrayList<>();
	
	private List<AnalystOpinion> opinions = new ArrayList<>();

	
	private Long portfolioId;
	
	private Portfolio portfolio;
	
	private List<Order> filteredOrders;
	
	private Money costValueForPortfolio;
	private Money currentValueForPortfolio;
	private Double portfolioPerformance = null;
	//private Map<String,Money> totalPayedMap = new HashMap<>();
	
	private Map<Currency, BigDecimal> conversionRateMap = new HashMap<>();
	
	private Map<PortfolioValuePaper,BigDecimal> profitMap = new HashMap<>();
	private Map<PortfolioValuePaper, Double> performanceMap = new HashMap<>();
	
	
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
    		if (portfolio.isDeleted()) {
    			throw new EntityNotFoundException();
    		}
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
    	conversionRateMap = portfolioDataAccess.getConversionRateMapForPortfolio(portfolio);
    	
    	owner = portfolio.getOwner();
    	user = userContext.getUserId() == null ? null : userDataAccess.getUserById(userContext.getUserId());
    	isOwner = owner.getId().equals(userContext.getUserId());
        createPieModels();
        createPortfolioChart();
        followers = new ArrayList<User>(portfolio.getFollowers());
        portfolioValuePapers = new ArrayList<>();
        for (PortfolioValuePaper pvp : portfolio.getValuePapers()) {
        	if (pvp.getVolume() > 0) {
        		portfolioValuePapers.add(pvp);
        	}
        }
        
        orders = new ArrayList<Order>(portfolio.getOrders());
        transactions = new ArrayList<TransactionEntry>(portfolio.getTransactionEntries());
        news = portfolioDataAccess.getNewsForPortfolio(portfolio);
        opinions = portfolioDataAccess.getAnalystOpinionsForPortfolio(portfolio);
        
        initProfitMap();
        initCostValueForPortfolio(); //
        initCurrentValueForPortfolio();
        initPortfolioPerformance();
        initPerformanceMap();
        
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
    
    public boolean isOrderCancelable(Order order) {
		if (order.getStatus() == OrderStatus.OPEN && isOwner) {
			if (order.getValidTo() == null) {
				return true;
			}
			return order.getValidTo().after(Calendar.getInstance());
		}
		return false;
	}
    
    public void cancelOrder(Order order) {
    	orderService.cancelOrder(order.getId());
    	FacesMessage message = new FacesMessage("Order wird abgebrochen");
        FacesContext.getCurrentInstance().addMessage(null, message);
    }
    
    public boolean isFollowable(){
		return (userContext.getUserId() != null && !followers.contains(user) && !isPortfolioOwner());
	}
	
	public boolean isUnfollowable(){
		return (followers.contains(user));
	}
	
	public String getFollowUnfollowButtonText() {
		if (isFollowable()) {
			return "Folgen";
		} else if (isUnfollowable()) {
			return "Nicht mehr folgen";
		}
		return "";
	}
	
	public void followUnfollow() {
		if (isFollowable()) {
			portfolio = portfolioService.followPortfolio(portfolio, user);
		} else if (isUnfollowable()) {
			portfolio = portfolioService.unfollowPortfolio(portfolio, user);
		}
		portfolio = portfolioDataAccess.getPortfolioById(portfolio.getId());
		followers = new ArrayList<User>(portfolio.getFollowers());
	}
	
	/*public String getBuyPrice(PortfolioValuePaper pvp) {
		ValuePaper vp = pvp.getValuePaper();
		if (vp.getType() == ValuePaperType.TYPE_BOND)
			return pvp.getBuyPrice().multiply(new BigDecimal(pvp.getVolume()));
		if (vp.getType() == ValuePaperType.TYPE_BOND)
			return ""
	}*/
	
	public String getCurrency(ValuePaper vp) {
		if (vp.getType() == ValuePaperType.BOND) {
			return "";
		} 
		if (vp.getType() == ValuePaperType.FUND) {
			return ((Fund) vp).getCurrency().getCurrencyCode();
		}
		else {
			return ((Stock) vp).getCurrency().getCurrencyCode();
		}
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
	
	private void initProfitMap() {
		for (PortfolioValuePaper pvp : portfolio.getValuePapers()) {
			if (profitMap.containsKey(pvp))
				continue;
			BigDecimal profit = new BigDecimal(portfolioDataAccess.getProfit(pvp));
			profitMap.put(pvp, profit);
		}
	}
	
	public BigDecimal getProfit(PortfolioValuePaper pvp) {
		return profitMap.get(pvp);
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
	
	private void initCostValueForPortfolio() {
		BigDecimal cost = portfolioDataAccess.getCostValueForPortfolio(portfolioId, conversionRateMap);
		costValueForPortfolio = createMoney(cost, portfolio.getCurrentCapital().getCurrency());
	}
	
	public Money getCostValueForPortfolio() {
		return costValueForPortfolio;
	}
	
	private void initCurrentValueForPortfolio() {
		BigDecimal value = portfolioDataAccess.getCurrentValueForPortfolio(portfolioId, conversionRateMap);
		//value = value.add(portfolio.getCurrentCapital().getValue());
		currentValueForPortfolio = createMoney(value, portfolio.getCurrentCapital().getCurrency());
	}
	
	public Money getCurrentValueForPortfolio() {
		return currentValueForPortfolio;
	}
	
	
	private void initPortfolioPerformance() {
		BigDecimal performance;
		try {
			//performance = portfolioDataAccess.getPortfolioPerformance(portfolioId);
			BigDecimal old = getCostValueForPortfolio().getValue();
			BigDecimal cur = getCurrentValueForPortfolio().getValue();
			if (old.compareTo( BigDecimal.ZERO) == 0 || cur.compareTo( BigDecimal.ZERO) == 0) {
				throw new AppException();
			}
			//cur = cur.subtract(portfolio.getCurrentCapital().getValue());
			performance = cur.subtract(old).multiply(new BigDecimal("100")).divide(old,4, RoundingMode.HALF_UP);
		} catch(EntityNotFoundException e) {
			throw new AppException();
		} catch(AppException e) {
			performance = new BigDecimal(0);
		}
		portfolioPerformance = performance.doubleValue();
	}
	public Double getPortfolioPerformance() {
		return portfolioPerformance;
	}
	
	private void initPerformanceMap() {
		for (PortfolioValuePaper pvp : portfolio.getValuePapers()) {
			double performance = portfolioDataAccess.getChange(pvp);
			performanceMap.put(pvp, performance);
		}
	}
	
	public double getChange(PortfolioValuePaper pvp) {
		return performanceMap.get(pvp);
	}
	
	public boolean isHidden() {
		if (isPortfolioOwner())
			return false;
		if (portfolio == null)
			return true;
		return !portfolio.getVisibility().getPublicVisible();
	}
	
	public boolean isChangeButtonVisible() {
		return isPortfolioOwner();
	}
	
	public boolean isTabVisible() {
		return isValuePapersVisible() || isOrdersVisible() 
				|| isTransactionsVisible() || isNewsVisible()
				|| isAnalystOpinionsVisible() || isChartsVisible();
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
		long startTime = System.currentTimeMillis();
		Map<String, BigDecimal> pointResult = portfolioDataAccess.getPortfolioChartEntries(portfolio, conversionRateMap);
		if (pointResult.size() == 1) {
			// only portfolio creation entry
			return;
		}
		long estimatedTime = System.currentTimeMillis() - startTime;
		System.out.println("################### getPortfolioChartEntries " + estimatedTime);
		
		startTime = System.currentTimeMillis();
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
        series.setShowMarker(false);
        
        for (String date: pointResult.keySet()) {
        	BigDecimal value = pointResult.get(date);
        	series.set(date, value);
        }
        
        if(pointResult.size() <= 15){
			series.setShowMarker(true);
		}
        
        portfolioChart.addSeries(series);
        estimatedTime = System.currentTimeMillis() - startTime;
		System.out.println("################### addSeries " + estimatedTime);
	}
	
	private Money createMoney(BigDecimal value, Currency currency) {
		return createMoney(value, currency.getCurrencyCode());
	}
	
	private Money createMoney(BigDecimal value, String currencyCode) {
		Money m = new Money();
		m.setCurrency(Currency.getInstance(currencyCode));
		if (value == null)
			value = new BigDecimal(0);
		m.setValue(value);
		return m;
		
	}
}
