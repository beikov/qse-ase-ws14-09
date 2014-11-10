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

	public void startATXIntradayPriceExtraction(){
		BatchRuntime.getJobOperator().start("intradayExtractionATX", null);
	}
	
	public void startATXBondDetailExtraction(){
		BatchRuntime.getJobOperator().start("detailBondExtractionATX", null);
	}
}
