package at.ac.tuwien.ase09.data.web.bean;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;

import at.ac.tuwien.ase09.data.bean.CrawlerService;

@Named("ctrlBean")
@Stateless
public class CrawlerWebCtrlBean {
	@Inject
	private CrawlerService crawlerService;
	
	public void startATXStockDetailExtraction(){
		crawlerService.startATXStockDetailExtraction();
	}

	public void startATXIntradayStockPriceExtraction(){
		crawlerService.startATXIntradayStockPriceExtraction();
	}
	
	public void startNasdaq100StockDetailExtraction(){
		crawlerService.startNasdaq100StockDetailExtraction();
	}
	
	public void startNasdaq100IntradayStockPriceExtraction(){
		crawlerService.startNasdaq100IntradayStockPriceExtraction();
	}
	
	public void startATXBondDetailExtraction(){
		crawlerService.startATXBondDetailExtraction();
	}
	
	public void startATXIntradayBondPriceExtraction(){
		crawlerService.startATXIntradayBondPriceExtraction();
	}
	
	public void startFundDetailExtraction(){
		crawlerService.startFundDetailExtraction();
	}
	
	public void startIntradayFundPriceExtraction(){
		crawlerService.startIntradayFundPriceExtraction();
	}

	public void startNewsExtraction(){
		crawlerService.startNewsExtraction();
	}
	
	public void startAnalystOpinionExtraction(){
		crawlerService.startAnalystOpinionExtraction();
	}
}
