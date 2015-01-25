package at.ac.tuwien.ase09.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.batch.api.BatchProperty;
import javax.batch.api.chunk.AbstractItemReader;
import javax.batch.runtime.context.StepContext;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import at.ac.tuwien.ase09.data.model.StockDetailLinkModel;
import at.ac.tuwien.ase09.data.model.StockDetailModel;
import at.ac.tuwien.ase09.model.DividendHistoryEntry;
import at.ac.tuwien.ase09.model.Stock;

public abstract class AbstractStockDetailReader extends AbstractItemReader {
	
	@Inject
	@BatchProperty(name = "finanzenNetUrl")
	private String finanzenNetUrl;
	
	@Inject
	@BatchProperty(name = "indexName")
	private String indexName;
	
	@Inject
	private StepContext stepContext;
	
	private List<StockDetailLinkModel> stockDetailLinkModels;
	private Integer linkNumber;
	
	@Override
	public void open(Serializable checkpoint) throws Exception {
		stockDetailLinkModels = new ArrayList<>((List<StockDetailLinkModel>) stepContext.getPersistentUserData());
		if(checkpoint != null){
			linkNumber = (Integer) checkpoint;
		}else{
			linkNumber = 0;
		}
	}
	
	@Override
	public Object readItem() throws Exception {
		if(linkNumber >= stockDetailLinkModels.size()){
			return null;
		}
		StockDetailLinkModel detailLinkModel = stockDetailLinkModels.get(linkNumber);

		// get historic prices page link
		Document finanzenNetDetailPage = JsoupUtils.getPage(detailLinkModel.getDetailLink());
		Element tableBody = finanzenNetDetailPage.select("div.infobox div.content tbody").get(0);
		Element historicLinkElem = tableBody.getElementsByAttributeValueMatching("href", "/kurse/historisch/.*").first();
		String historicPricesPageUrl = finanzenNetUrl + historicLinkElem.attr("href");
				
		// get finanzen.net certificate link
		Element finanzenCertificateLinkElem = tableBody.getElementsByAttributeValueMatching("href", "http://zertifikate.finanzen.at/zertifikate/.*").first();
		String finanzenCertificateLink = finanzenCertificateLinkElem.attr("href");
		
		// get dividend history page link
		Element finanzenDividendHistoryLinkElem = tableBody.getElementsByAttributeValueMatching("href", "/dividende/.*").first();
		String finanzenDividendHistoryLink = finanzenNetUrl + finanzenDividendHistoryLinkElem.attr("href");
		
		// get news page link
		Elements moreLinks = finanzenNetDetailPage.select("div.content_box.content_navi a.more");
		String finanzenNewsPageUrl = null;
		for(Element moreLink : moreLinks){
			if(moreLink.attr("href").matches("/nachrichten/.*")){
				finanzenNewsPageUrl = finanzenNetUrl + moreLink.attr("href") + "/Alle";
			}
		}
		
		Elements contentTableRows = finanzenNetDetailPage.select("div.main_right div.content_box div.content tr");
		String country = null;
		for(Element contentTableRow : contentTableRows){
			Elements columns = contentTableRow.getElementsByTag("td");
			if(columns.size() >= 2 && "Land".equals(columns.first().text().trim())){
				country = columns.get(1).text();
				if("k.A.".equals(country)){
					country = null;
				}
			}
		}

		Elements currencyElements =  finanzenNetDetailPage.select("div.pricebox.content_box div.content table:nth-child(1) th:nth-child(1)");
		String currency = null;
		if(!currencyElements.isEmpty()){
			currency = currencyElements.last().getElementsByTag("span").text();
		}
		
		Stock stock = new Stock();
		stock.setCode(detailLinkModel.getIsin());
		stock.setDetailUrl(detailLinkModel.getDetailLink());
		stock.setCountry(country);
		stock.setName(detailLinkModel.getCompanyName());
		stock.setCurrency(Currency.getInstance(currency));
		stock.setFinanzenCertificatePageUrl(finanzenCertificateLink);
		stock.setHistoricPricesPageUrl(historicPricesPageUrl);
		stock.setFinanzenDividendHistoryPageUrl(finanzenDividendHistoryLink);
		stock.setFinanzenNewsPageUrl(finanzenNewsPageUrl);
		stock.setIndex(indexName);
		stock.setTickerSymbol(detailLinkModel.getTickerSymbol());
		
		StockDetailModel stockDetailModel = new StockDetailModel(stock);
		
		readDividendHistoryEntries(stockDetailModel);
		readStats(stockDetailModel);

		linkNumber++;
		return stockDetailModel;
	}
	
	protected abstract void readDividendHistoryEntries(StockDetailModel stockDetailModel) throws Exception;

	protected abstract void readStats(StockDetailModel stockDetailModel) throws Exception;
	
	@Override
	public Serializable checkpointInfo() throws Exception {
		return linkNumber;
	}
	
}
