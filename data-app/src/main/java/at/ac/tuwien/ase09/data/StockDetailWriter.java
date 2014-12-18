package at.ac.tuwien.ase09.data;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import at.ac.tuwien.ase09.data.model.StockDetailModel;
import at.ac.tuwien.ase09.service.StockService;

@Dependent
@Named
public class StockDetailWriter extends AbstractEntityWriter<StockDetailModel> {
	@Inject
	private StockService stockService;
	
	@Override
	protected void persistEntity(StockDetailModel stockDetailModel) {
		stockService.saveStock(stockDetailModel.getStock());
		stockService.saveStockDividends(stockDetailModel.getStock(), stockDetailModel.getDividendHistoryEntries());
	}

}
