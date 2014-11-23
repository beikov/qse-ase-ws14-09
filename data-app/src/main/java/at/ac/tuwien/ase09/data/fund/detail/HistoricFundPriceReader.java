package at.ac.tuwien.ase09.data.fund.detail;

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
import org.jsoup.Connection.Method;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import at.ac.tuwien.ase09.data.JsoupUtils;
import at.ac.tuwien.ase09.data.ValuePaperDataAccess;
import at.ac.tuwien.ase09.model.Fund;
import at.ac.tuwien.ase09.model.StockBond;
import at.ac.tuwien.ase09.model.ValuePaper;
import at.ac.tuwien.ase09.model.ValuePaperHistoryEntry;

@Dependent
@Named
public class HistoricFundPriceReader extends AbstractItemReader {
	private static final Logger LOG = Logger.getLogger(HistoricFundPriceReader.class.getName());
	
	@Inject
	private ValuePaperDataAccess valuePaperDataAccess;
	
	private Integer linkNumber;
	private List<Fund> funds;
	
	public void open(java.io.Serializable checkpoint) throws Exception {
		if(checkpoint != null){
			linkNumber = (Integer) checkpoint;
		}else{
			linkNumber = 0;
			funds = valuePaperDataAccess.getValuePapers(Fund.class);
		}
	};
	
	@Override
	public Object readItem() throws Exception {
		if(linkNumber >= funds.size()){
			return null;
		}
		final DateFormat df = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY);
		Fund fund= funds.get(linkNumber);
		LOG.info("Extracting historic prices for bond " + fund.getName());
		
		String historicPricesPageUrl = fund.getHistoricPricesPageUrl();
		// by default fetches the prices from last month
		Document historicPricesPage = JsoupUtils.getPage(historicPricesPageUrl, Method.GET, 6000);
		
		Elements tableRows = historicPricesPage.select("tr.colorth");
		tableRows.remove(0);	// remove table header
		List<ValuePaperHistoryEntry> historicPriceEntries = new ArrayList<>();
		for(Element row : tableRows){
			String dateStr = row.child(0).text();
			BigDecimal price = new BigDecimal(row.child(1).text().replace(',', '.'));
						
			ValuePaperHistoryEntry historicPriceEntry = new ValuePaperHistoryEntry();
			Calendar priceDate = Calendar.getInstance();
			priceDate.setTime(df.parse(dateStr));
			historicPriceEntry.setDate(priceDate);
			historicPriceEntry.setOpeningPrice(price);
			historicPriceEntry.setClosingPrice(price);
			historicPriceEntry.setDayHighPrice(price);
			historicPriceEntry.setDayLowPrice(price);
			historicPriceEntry.setValuePaper(fund);
			
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
