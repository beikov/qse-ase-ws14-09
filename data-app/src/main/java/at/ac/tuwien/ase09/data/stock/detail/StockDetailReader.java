package at.ac.tuwien.ase09.data.stock.detail;

import java.io.Serializable;
import java.util.Currency;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.batch.api.chunk.AbstractItemReader;
import javax.batch.runtime.context.StepContext;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import at.ac.tuwien.ase09.data.JsoupUtils;
import at.ac.tuwien.ase09.model.Stock;

@Dependent
@Named("StockDetailReader")
public class StockDetailReader extends AbstractItemReader {
	private static final Pattern urlTypePattern = Pattern.compile("TYPE=([^&]*)");
	
	@Inject
	private StepContext stepContext;
	
	private List<String> stockDetailLinks;
	private final Currency currency = Currency.getInstance("EUR");
	
	private Integer linkNumber;
	
	@Override
	public void open(Serializable checkpoint) throws Exception {
		stockDetailLinks = ((List<String>) stepContext.getPersistentUserData());
		if(checkpoint != null){
			linkNumber = (Integer) checkpoint;
		}else{
			linkNumber = 0;
		}
	}
	
	@Override
	public Object readItem() throws Exception {
		if(linkNumber + 1 >= stockDetailLinks.size()){
			return null;
		}
		String stockDetailLink = stockDetailLinks.get(linkNumber + 1);
		Document detailPage = JsoupUtils.tryGetPage(stockDetailLink);
		Elements elements = detailPage.select("div.summary div.left td:nth-child(1)");
		String name = elements.get(0).text();
		String isin = elements.get(1).text().replaceAll("ISIN: ", "");
		String certificatePageUrl = detailPage.select("div.detail_list div.links a:nth-child(2)").get(0).attr("href");
		String indexName = null;
		Matcher urlTypeMatcher = urlTypePattern.matcher(stockDetailLink);
		if(urlTypeMatcher.find()){
			indexName = urlTypeMatcher.group(1);
		}
		
		
		Stock stock = new Stock();
		stock.setCode(isin);
		stock.setName(name);
		stock.setCurrency(currency);
		stock.setCertificatePageUrl(certificatePageUrl);
		stock.setIndex(indexName);
		linkNumber++;
		return stock;
	}
	
	@Override
	public Serializable checkpointInfo() throws Exception {
		return linkNumber;
	}
	
}
