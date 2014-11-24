package at.ac.tuwien.ase09.data.stock.atx.detail;

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
public class HistoricStockPriceReader extends AbstractItemReader {
	private static final Logger LOG = Logger.getLogger(HistoricStockPriceReader.class.getName());
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
		final DateFormat df = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY);
		Stock stock = stocks.get(linkNumber);
		LOG.info("Extracting historic prices for " + stock.getName());
		String historicPricesPageUrl = stock.getHistoricPricesPageUrl() + "/" + df.format(from.getTime()) + "_" + df.format(to.getTime());
		Document p = JsoupUtils.getPage(historicPricesPageUrl);
		
		String __atts = p.getElementsByAttributeValue("name", "__atts").get(0).attr("value");
		String __ath = p.getElementsByAttributeValue("name", "__ath").get(0).attr("value");
		String __atcrv = engine.eval(p.getElementsByAttributeValue("name", "__atcrv").get(0).attr("value")).toString();
		String historicPricesXHRUrl = historicPricesPageUrl = historicPricesPageUrl.replace("kurse/historisch", "Ajax/SharesController_HistoricPriceList");
		
		Document historicPricesPage = Jsoup.connect(historicPricesXHRUrl)
				 .header("X-Requested-With", "XMLHttpRequest")
				 .header("__atts", __atts)
				 .header("__ath", __ath)
				 .header("__atcrv", __atcrv)
				 .post();
		Elements tableRows = historicPricesPage.select("tr");
		tableRows.remove(0);	// remove table header
		List<ValuePaperHistoryEntry> historicPriceEntries = new ArrayList<>();
		for(Element row : tableRows){
			String dateStr = row.child(0).text();
			String openingPriceStr = row.child(1).text().replace(',', '.');
			String closingPriceStr = row.child(2).text().replace(',', '.');
			String dayHighStr = row.child(3).text().replace(',', '.');
			String dayLowStr = row.child(4).text().replace(',', '.');
			String marketVolStr = row.child(5).text().replace(".", "");
			
			ValuePaperHistoryEntry historicPriceEntry = new ValuePaperHistoryEntry();
			Calendar priceDate = Calendar.getInstance();
			priceDate.setTime(df.parse(dateStr));
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
