package at.ac.tuwien.ase09.data.stock.atx.detail;

import javax.enterprise.context.Dependent;
import javax.inject.Named;

import at.ac.tuwien.ase09.data.AbstractStockDetailReader;
import at.ac.tuwien.ase09.data.model.StockDetailModel;

@Dependent
@Named
public class StockDetailReader extends AbstractStockDetailReader {
	@Override
	protected void readDividendHistoryEntries(
			StockDetailModel stockDetailLinkModel) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void readStats(StockDetailModel stockDetailLinkModel) {
		// TODO Auto-generated method stub

	}
}
