package at.ac.tuwien.ase09.data.stock.atx.detail;

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
import at.ac.tuwien.ase09.data.model.StockDetailModel;
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
		
		// get wienerboerse certificate link
		String boerseCertificatePageUrl = detailPage.select("div.detail_list div.links a:nth-child(2)").get(0).attr("href");
		boerseCertificatePageUrl = boerseCertificatePageUrl.replaceAll("TYPE=[^&]*", "TYPE=C").replace("..", "");
		boerseCertificatePageUrl = "http://kurse.wienerborse.at/teledata_php" + boerseCertificatePageUrl + "&CATEGORYVALUE=9&LIFETIME=-1&QUOTINGTYPE=A";

		// get historic prices page link
		Document finanzenNetDetailPage = JsoupUtils.getPage(stockDetailLinks.get(linkNumber)[1]);
		Element tableBody = finanzenNetDetailPage.select("div.infobox div.content tbody").get(0);
		Element historicLinkElem = tableBody.getElementsByAttributeValueMatching("href", "/kurse/historisch/.*").get(0);
		String historicPricesPageUrl = finanzenNetUrl + historicLinkElem.attr("href");
				
		// get finanzen.net certificate link
		Element finanzenCertificateLinkElem = tableBody.getElementsByAttributeValueMatching("href", "http://zertifikate.finanzen.at/zertifikate/.*").get(0);
		String finanzenCertificateLink = finanzenCertificateLinkElem.attr("href");
		
		String indexName = null;
		Matcher urlTypeMatcher = urlTypePattern.matcher(stockDetailLink);
		if(urlTypeMatcher.find()){
			indexName = urlTypeMatcher.group(1);
		}
		
		Stock stock = new Stock();
		stock.setCode(isin);
		stock.setCountry("AT");
		stock.setName(name);
		stock.setCurrency(currency);
		stock.setBoerseCertificatePageUrl(boerseCertificatePageUrl);
		stock.setFinanzenCertificatePageUrl(finanzenCertificateLink);
		stock.setHistoricPricesPageUrl(historicPricesPageUrl);
		stock.setIndex(indexName);
		
		linkNumber++;
		return new StockDetailModel(stock);
	}
	
	@Override
	public Serializable checkpointInfo() throws Exception {
		return linkNumber;
	}
	
}
