package at.ac.tuwien.ase09.bean;

import java.io.Serializable;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.model.chart.PieChartModel;

import at.ac.tuwien.ase09.model.Portfolio;
import at.ac.tuwien.ase09.model.ValuePaperType;
import at.ac.tuwien.ase09.service.PortfolioService;

@Named
@RequestScoped
public class PortfolioDetailBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Inject
	private PortfolioService portfolioService;
	
	private Long portfolioId;
	
	private Portfolio portfolio;
	
	
	private PieChartModel valuePaperTypePie;
	private PieChartModel valuePaperCountryPie;
	
    public void init() {
		loadPortfolio(portfolioId);
        createPieModels();
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
	
	
	
	private void loadPortfolio(Long portfolioId) {
		this.portfolio = portfolioService.getPortfolioById(portfolioId);
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
	
}
