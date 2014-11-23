package at.ac.tuwien.ase09.data.stock.nasdaq.intraday;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import javax.batch.api.BatchProperty;
import javax.batch.api.chunk.AbstractItemReader;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import at.ac.tuwien.ase09.data.JsoupUtils;
import at.ac.tuwien.ase09.data.ValuePaperDataAccess;
import at.ac.tuwien.ase09.data.model.IntradayPrice;

@Dependent
@Named
public class YahooPriceReader extends AbstractItemReader{
	private static final String YQL_QUOTE_QUERY_TEMPLATE = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.quote%20where%20symbol%20%3D%20%22#{symbolPlaceholder}%22&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys";
	
	@Inject
	@BatchProperty(name="indexName")
	private String indexName;

	@Inject
	private ValuePaperDataAccess valuePaperDataAccess;
	
	private List<String> stockSymbols;
	private int itemNumber;
	
	@Override
	public void open(Serializable checkpoint) throws Exception {
		stockSymbols = valuePaperDataAccess.getStockCodesByIndex(indexName);
		if(checkpoint == null){
			itemNumber = 0;
		}else{
			itemNumber = (Integer) 0;
		}
	}
	
	@Override
	public Object readItem() throws Exception {
		if(itemNumber >= stockSymbols.size()){
			return null;
		}
		String stockSymbol = stockSymbols.get(itemNumber);
		Document quote = JsoupUtils.getPage(YQL_QUOTE_QUERY_TEMPLATE.replaceAll("#\\{symbolPlaceholder\\}", stockSymbol));
		Element first = quote.select("results quote LastTradePriceOnly").first();
		IntradayPrice priceModel = null;
		if(first != null){
			BigDecimal price = new BigDecimal(quote.select("results quote LastTradePriceOnly").first().text());
			priceModel = new IntradayPrice(stockSymbol, price);
		}
		itemNumber++;
		return priceModel;
	}

}
