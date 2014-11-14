package at.ac.tuwien.ase09.data.stock.detail;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.batch.api.BatchProperty;
import javax.batch.api.chunk.AbstractItemReader;
import javax.batch.runtime.context.StepContext;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import at.ac.tuwien.ase09.data.JsoupUtils;
import at.ac.tuwien.ase09.model.Stock;

@Dependent
@Named("StockDetailReader")
public class StockDetailReader extends AbstractItemReader {
	private static final Pattern urlTypePattern = Pattern.compile("TYPE=([^&]*)");
	
	@Inject
	@BatchProperty(name = "finanzenNetUrl")
	private String finanzenNetUrl;
	
	@Inject
	private StepContext stepContext;
	
	private List<String[]> stockDetailLinks;
	private final Currency currency = Currency.getInstance("EUR");
	
	private Integer linkNumber;
	
	@Override
	public void open(Serializable checkpoint) throws Exception {
		stockDetailLinks = new ArrayList<>(((Map<String, String[]>) stepContext.getPersistentUserData()).values());
		if(checkpoint != null){
			linkNumber = (Integer) checkpoint;
		}else{
			linkNumber = 0;
		}
	}
	
	@Override
	public Object readItem() throws Exception {
		if(linkNumber >= stockDetailLinks.size()){
			return null;
		}
		String stockDetailLink = stockDetailLinks.get(linkNumber)[0];
		Document detailPage = JsoupUtils.getPage(stockDetailLink);
		Elements elements = detailPage.select("div.summary div.left td:nth-child(1)");
		String name = elements.get(0).text();
		String isin = elements.get(1).text().replaceAll("ISIN: ", "");
		// we should build the certificate url for the stocks as follows:
		// http://kurse.wienerborse.at/teledata_php/prices/dispatch_list.php?TYPE=C&CATEGORYVALUE=9&CP=&ID_NOTATION_UNDERLYING=740752&LIFETIME=-1&QUOTINGTYPE=A
		String certificatePageUrl = detailPage.select("div.detail_list div.links a:nth-child(2)").get(0).attr("href");
		certificatePageUrl = certificatePageUrl.replaceAll("TYPE=[^&]*", "TYPE=C").replace("..", "");
		certificatePageUrl = "http://kurse.wienerborse.at/teledata_php" + certificatePageUrl + "&CATEGORYVALUE=9&LIFETIME=-1&QUOTINGTYPE=A";
				
		String indexName = null;
		Matcher urlTypeMatcher = urlTypePattern.matcher(stockDetailLink);
		if(urlTypeMatcher.find()){
			indexName = urlTypeMatcher.group(1);
		}
		
		// get historic prices page link
		Document finanzenNetDetailPage = JsoupUtils.getPage(stockDetailLinks.get(linkNumber)[1]);
		Element tableBody = finanzenNetDetailPage.select("div.infobox div.content tbody").get(0);
		Element historicLinkElem = tableBody.getElementsByAttributeValueMatching("href", "/kurse/historisch/.*").get(0);
		String historicPricesPageUrl = finanzenNetUrl + historicLinkElem.attr("href");
		
		Stock stock = new Stock();
		stock.setIsin(isin);
		stock.setName(name);
		stock.setCurrency(currency);
		stock.setBoerseCertificatePageUrl(certificatePageUrl);
		stock.setHistoricPricesPageUrl(historicPricesPageUrl);
		stock.setIndex(indexName);
		linkNumber++;
		return stock;
	}
	
	@Override
	public Serializable checkpointInfo() throws Exception {
		return linkNumber;
	}
	
}
