package at.ac.tuwien.ase09.data.bond.detail;

import java.util.logging.Logger;

import javax.batch.api.chunk.ItemProcessor;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import at.ac.tuwien.ase09.data.ValuePaperDataAccess;
import at.ac.tuwien.ase09.data.model.StockBondModel;
import at.ac.tuwien.ase09.model.Stock;
import at.ac.tuwien.ase09.model.StockBond;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;

@Named
@Dependent
public class BondDetailProcessor implements ItemProcessor{
	private static final Logger LOG = Logger.getLogger(BondDetailProcessor.class.getName());
	
	@Inject
	private ValuePaperDataAccess valuePaperDataAccess;
	
	@Override
	public Object processItem(Object item) throws Exception {
		StockBondModel bondModel = (StockBondModel) item;
		StockBond stockBond = new StockBond();
		try{
			Stock baseStock = valuePaperDataAccess.getValuePaperByIsin(bondModel.getBaseValueIsin(), Stock.class);
			stockBond.setBaseStock(baseStock);
			stockBond.setHistoricPricesPageUrl(bondModel.getHistoricPricesPageUrl());
			stockBond.setDetailUrl(bondModel.getDetailUrl());
			stockBond.setIsin(bondModel.getIsin());
			stockBond.setName(bondModel.getName());
			return stockBond;
		}catch(ElementNotFoundException e){
			LOG.warning("Could not find base value (" + bondModel.getBaseValueIsin() + ") for bond (" + bondModel.getIsin() + ")");
			return null;
		}
	}
}
