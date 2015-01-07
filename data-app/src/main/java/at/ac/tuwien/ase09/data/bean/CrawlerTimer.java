package at.ac.tuwien.ase09.data.bean;

import javax.ejb.Schedule;
import javax.ejb.Schedules;
import javax.ejb.Singleton;
import javax.inject.Inject;

@Singleton
public class CrawlerTimer {
	
	@Inject
	private CrawlerService crawlerService;
	
	// 8:55 - 17:35
	@Schedules({
			@Schedule(dayOfWeek="Mon, Tue, Wed, Thu, Fri", hour="8", minute="55"),
			@Schedule(dayOfWeek="Mon, Tue, Wed, Thu, Fri", hour="9-16", minute="*/20"),
			@Schedule(dayOfWeek="Mon, Tue, Wed, Thu, Fri", hour="17", minute="0, 20")
	})
	public void startIntradayExtraction(){
		crawlerService.startIntradayFundPriceExtraction();
		crawlerService.startATXIntradayStockPriceExtraction();
		crawlerService.startNasdaq100IntradayStockPriceExtraction();
		crawlerService.startATXIntradayBondPriceExtraction();
	}
	
	@Schedule(dayOfWeek="Mon, Tue, Wed, Thu, Fri", hour="8", minute="45")
	public void startDetailExtraction(){
		crawlerService.startATXBondDetailExtraction();
		crawlerService.startATXStockDetailExtraction();
		crawlerService.startNasdaq100StockDetailExtraction();
		crawlerService.startFundDetailExtraction();
	}
	
	@Schedule(dayOfWeek="Mon, Tue, Wed, Thu, Fri", hour="7-17", minute="*/30")
	public void startNewsExtraction(){
		crawlerService.startNewsExtraction();
	}
	
	@Schedule(dayOfWeek="Mon, Tue, Wed, Thu, Fri", hour="7-17")
	public void startAnalystOpinionExtraction(){
		crawlerService.startNewsExtraction();
	}
}
