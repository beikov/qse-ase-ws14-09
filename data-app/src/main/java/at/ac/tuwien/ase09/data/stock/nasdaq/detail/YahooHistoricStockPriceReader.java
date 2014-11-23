package at.ac.tuwien.ase09.data.stock.nasdaq.detail;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import javax.batch.api.BatchProperty;
import javax.batch.api.chunk.AbstractItemReader;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.jboss.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import at.ac.tuwien.ase09.data.JsoupUtils;
import at.ac.tuwien.ase09.data.ValuePaperDataAccess;
import at.ac.tuwien.ase09.model.Stock;
import at.ac.tuwien.ase09.model.ValuePaperHistoryEntry;

@Dependent
@Named
public class YahooHistoricStockPriceReader extends AbstractItemReader {
	private static final Logger LOG = Logger.getLogger(YahooHistoricStockPriceReader.class.getName());
	private static final String YQL_HISTORIC_STOCK_PRICE_QUERY_TEMPLATE = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.historicaldata%20where%20symbol%20%3D%20%22#{symbolPlaceholder}%22%20and%20startDate%20%3D%20%222009-09-11%22%20and%20endDate%20%3D%20%222010-03-10%22&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys";
	private static final DateFormat YQL_HISTORIC_PRICE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
	
	@Inject
	@BatchProperty(name="indexName")
	private String indexName;
	
	@Inject
	private ValuePaperDataAccess valuePaperDataAccess;
	
	private Integer linkNumber;
	private List<Stock> stocks;
	
    private static final ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
	
    private Calendar from;
    private Calendar to;
	public void open(java.io.Serializable checkpoint) throws Exception {
		if(checkpoint != null){
			linkNumber = (Integer) checkpoint;
		}else{
			linkNumber = 0;
			stocks = valuePaperDataAccess.getStocksByIndex(indexName);
			from = Calendar.getInstance();
			from.roll(Calendar.MONTH, -1);
			to = Calendar.getInstance();
		}
	};
	
	@Override
	public Object readItem() throws Exception {
		if(linkNumber >= stocks.size()){
			return null;
		}
		
		Stock stock = stocks.get(linkNumber);
		LOG.info("Extracting historic prices for " + stock.getName());
		Document historicPrices = JsoupUtils.getPage(YQL_HISTORIC_STOCK_PRICE_QUERY_TEMPLATE.replaceAll("#\\{symbolPlaceholder\\}", stock.getCode()));
		
		Elements tableRows = historicPrices.select("results quote");
		List<ValuePaperHistoryEntry> historicPriceEntries = new ArrayList<>();
		for(Element row : tableRows){
			String dateStr = row.getElementsByTag("Date").first().text();
			String openingPriceStr = row.getElementsByTag("Open").first().text();
			String closingPriceStr = row.getElementsByTag("Close").first().text();
			String dayHighStr = row.getElementsByTag("High").first().text();
			String dayLowStr = row.getElementsByTag("Low").first().text();
			String marketVolStr = row.getElementsByTag("Volume").first().text();
			
			ValuePaperHistoryEntry historicPriceEntry = new ValuePaperHistoryEntry();
			Calendar priceDate = Calendar.getInstance();
			priceDate.setTime(YQL_HISTORIC_PRICE_DATE_FORMAT.parse(dateStr));
			historicPriceEntry.setDate(priceDate);
			historicPriceEntry.setOpeningPrice(new BigDecimal(openingPriceStr));
			historicPriceEntry.setClosingPrice(new BigDecimal(closingPriceStr));
			historicPriceEntry.setDayHighPrice(new BigDecimal(dayHighStr));
			historicPriceEntry.setDayLowPrice(new BigDecimal(dayLowStr));
			historicPriceEntry.setMarketVolume(new Integer(marketVolStr));
			historicPriceEntry.setValuePaper(stock);
			
			historicPriceEntries.add(historicPriceEntry);
		}
		linkNumber++;
		return historicPriceEntries;
	}
	
	@Override
	public Serializable checkpointInfo() throws Exception {
		return linkNumber;
	}
}
