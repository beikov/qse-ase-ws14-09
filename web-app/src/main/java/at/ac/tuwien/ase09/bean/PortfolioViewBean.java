package at.ac.tuwien.ase09.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.RequestScoped;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.DateAxis;
import org.primefaces.model.chart.LineChartModel;
import org.primefaces.model.chart.LineChartSeries;
import org.primefaces.model.chart.PieChartModel;

import at.ac.tuwien.ase09.data.PortfolioDataAccess;
import at.ac.tuwien.ase09.model.Portfolio;
import at.ac.tuwien.ase09.model.ValuePaper;
import at.ac.tuwien.ase09.model.ValuePaperType;
import at.ac.tuwien.ase09.model.order.Order;
import at.ac.tuwien.ase09.model.transaction.OrderFeeTransactionEntry;
import at.ac.tuwien.ase09.model.transaction.OrderTransactionEntry;
import at.ac.tuwien.ase09.model.transaction.PayoutTransactionEntry;
import at.ac.tuwien.ase09.model.transaction.TransactionEntry;
import at.ac.tuwien.ase09.service.PortfolioService;

@Named
@RequestScoped
public class PortfolioViewBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private PortfolioDataAccess portfolioDataAccess;
	
	@Inject
	private PortfolioService portfolioService;
	
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
        LineChartSeries series1 = new LineChartSeries();
        series1.setLabel("Series 1");
        
        Set<TransactionEntry> transactionSet = portfolio.getTransactionEntries();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        for (TransactionEntry transaction : transactionSet) {
        	String date = format.format(transaction.getCreated().getTime());
        	BigDecimal value = transaction.getValue().getValue();
        	series1.set(date, value);
        }
        
        /*series1.set("2014-01-01", 51);
        series1.set("2014-01-06", 22);
        series1.set("2014-01-12", 65);
        series1.set("2014-01-18", 74);
        series1.set("2014-01-24", 24);
        series1.set("2014-01-30", 51);*/
        
        portfolioChart.addSeries(series1);
        
        portfolioChart.setTitle("Zoom");
        portfolioChart.setZoom(true);
        portfolioChart.getAxis(AxisType.Y).setLabel("Wert");
        DateAxis axis = new DateAxis("Datum");
        axis.setTickAngle(-50);
        axis.setMax("2014-12-24");
        axis.setTickFormat("%b %#d, %y");
         
        portfolioChart.getAxes().put(AxisType.X, axis);
	}

	
}
