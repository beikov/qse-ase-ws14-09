package at.ac.tuwien.ase09.data.bond.detail;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

import javax.batch.api.BatchProperty;
import javax.batch.api.chunk.AbstractItemReader;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import at.ac.tuwien.ase09.data.JsoupUtils;
import at.ac.tuwien.ase09.data.ValuePaperDataAccess;
import at.ac.tuwien.ase09.model.StockBond;
import at.ac.tuwien.ase09.model.ValuePaper;
import at.ac.tuwien.ase09.model.ValuePaperHistoryEntry;

@Dependent
@Named
public class HistoricBondPriceReader extends AbstractItemReader {
	private static final Logger LOG = Logger.getLogger(HistoricBondPriceReader.class.getName());
	@Inject
	@BatchProperty(name="indexName")
	private String indexName;
	
	@Inject
	private ValuePaperDataAccess valuePaperDataAccess;
	
	private Integer linkNumber;
	private List<StockBond> bonds;
	
	public void open(java.io.Serializable checkpoint) throws Exception {
		if(checkpoint != null){
			linkNumber = (Integer) checkpoint;
		}else{
			linkNumber = 0;
			bonds = valuePaperDataAccess.getStockBondsByBaseStockIndex(indexName);
		}
	};
	
	@Override
	public Object readItem() throws Exception {
		if(linkNumber >= bonds.size()){
			return null;
		}
		final DateFormat df = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY);
		StockBond bond= bonds.get(linkNumber);
		LOG.info("Extracting historic prices for bond " + bond.getName());
		
		
		String historicPricesPageUrl = bond.getHistoricPricesPageUrl();
		// by default fetches the prices from last month
		Document historicPricesPage = JsoupUtils.getPage(historicPricesPageUrl);
		
		Elements tableRows = historicPricesPage.select("div.content_box.table_quotes div.content tr");
		tableRows.remove(0);	// remove table header
		List<ValuePaperHistoryEntry> historicPriceEntries = new ArrayList<>();
		for(Element row : tableRows){
			if(row.children().size() >= 5){
				String dateStr = row.child(0).text();
				String openingPriceStr = row.child(1).text().replace(',', '.');
				String closingPriceStr = row.child(2).text().replace(',', '.');
				String dayHighStr = row.child(3).text().replace(',', '.');
				String dayLowStr = row.child(4).text().replace(',', '.');
				
				ValuePaperHistoryEntry historicPriceEntry = new ValuePaperHistoryEntry();
				Calendar priceDate = Calendar.getInstance();
				priceDate.setTime(df.parse(dateStr));
				historicPriceEntry.setDate(priceDate);
				historicPriceEntry.setOpeningPrice(new BigDecimal(openingPriceStr));
				historicPriceEntry.setClosingPrice(new BigDecimal(closingPriceStr));
				historicPriceEntry.setDayHighPrice(new BigDecimal(dayHighStr));
				historicPriceEntry.setDayLowPrice(new BigDecimal(dayLowStr));
				historicPriceEntry.setValuePaper(bond);
				
				historicPriceEntries.add(historicPriceEntry);
			}
		}
		linkNumber++;
		return historicPriceEntries;
	}
	
	@Override
	public Serializable checkpointInfo() throws Exception {
		return linkNumber;
	}
}
