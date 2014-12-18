package at.ac.tuwien.ase09.data.bond.intraday;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.batch.api.BatchProperty;
import javax.batch.api.chunk.AbstractItemReader;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import at.ac.tuwien.ase09.data.JsoupUtils;
import at.ac.tuwien.ase09.data.ValuePaperDataAccess;
import at.ac.tuwien.ase09.data.model.IntradayPrice;
import at.ac.tuwien.ase09.model.StockBond;

@Dependent
@Named
public class BondPriceReader extends AbstractItemReader{
	
	@Inject
	@BatchProperty(name="indexName")
	private String indexName;

	private List<StockBond> bonds;
	
	private int itemNumber;
	
	@Inject
	private ValuePaperDataAccess valuePaperDataAccess;
	
	@Override
	public void open(Serializable checkpoint) throws Exception {
		if(checkpoint == null){
			bonds = valuePaperDataAccess.getStockBondsByBaseStockIndex(indexName);
			itemNumber = 0;
		}else{
			itemNumber = (Integer) checkpoint;
		}
	}
	
	@Override
	public Object readItem() throws Exception {
		if(itemNumber >= bonds.size()){
			return null;
		}
		StockBond bond = bonds.get(itemNumber);
		Document bondDetailPage = JsoupUtils.getPage(bond.getDetailUrl());
		String priceStr = bondDetailPage.select("th.pricebox").text().replace(',', '.');

		itemNumber++;
		return new IntradayPrice(bond.getCode(), new BigDecimal(priceStr));
	}

}
