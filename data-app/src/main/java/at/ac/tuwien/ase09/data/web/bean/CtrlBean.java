package at.ac.tuwien.ase09.data.web.bean;

import javax.batch.runtime.BatchRuntime;
import javax.ejb.Stateless;
import javax.inject.Named;

@Named
@Stateless
public class CtrlBean {
	public void startATXStockDetailExtraction(){
		BatchRuntime.getJobOperator().start("detailStockExtractionATX", null);
	}

	public void startATXIntradayStockPriceExtraction(){
		BatchRuntime.getJobOperator().start("intradayStockExtractionATX", null);
	}
	
	public void startNasdaq100StockDetailExtraction(){
		BatchRuntime.getJobOperator().start("detailStockExtractionNasdaq100", null);
	}
	
	public void startATXBondDetailExtraction(){
		BatchRuntime.getJobOperator().start("detailBondExtractionATX", null);
	}
	
	public void startATXIntradayBondPriceExtraction(){
		BatchRuntime.getJobOperator().start("intradayBondExtractionATX", null);
	}
	
	public void startFundDetailExtraction(){
		BatchRuntime.getJobOperator().start("detailFundExtraction", null);
	}
	
	public void startIntradayFundPriceExtraction(){
		BatchRuntime.getJobOperator().start("intradayFundExtraction", null);
	}
}
