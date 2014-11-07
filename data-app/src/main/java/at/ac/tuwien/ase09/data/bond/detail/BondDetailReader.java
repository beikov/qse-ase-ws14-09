package at.ac.tuwien.ase09.data.bond.detail;

import java.io.Serializable;
import java.util.Iterator;

import javax.batch.api.BatchProperty;
import javax.batch.api.chunk.AbstractItemReader;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import org.jsoup.nodes.Document;

import at.ac.tuwien.ase09.data.JsoupUtils;
import at.ac.tuwien.ase09.data.ValuePaperDataAccess;
import at.ac.tuwien.ase09.model.Stock;

@Dependent
@Named("BondDetailReader")
public class BondDetailReader extends AbstractItemReader {
	@Inject
	@BatchProperty
	private String indexName;
	
	@Inject
	private ValuePaperDataAccess valuePaperDataAccess;
	
	private Iterator<Stock> stockIter;
	private int bondNumber;
	
	@Override
	public void open(Serializable checkpoint) throws Exception {
		stockIter = valuePaperDataAccess.getStocksByIndex(indexName).iterator();
	}
	@Override
	public Object readItem() throws Exception {
		if(!stockIter.hasNext()){
			return null;
		}
		Stock stock = stockIter.next();
		Document bondPage = JsoupUtils.tryGetPage(stock.getCertificatePageUrl());
		bondNumber++;
		// we should build the certificate url in the stocks as follows:
		// http://kurse.wienerborse.at/teledata_php/prices/dispatch_list.php?TYPE=C&CATEGORYVALUE=9&CP=&ID_NOTATION_UNDERLYING=740752&LIFETIME=-1&QUOTINGTYPE=A
		return stock;
	}
}
