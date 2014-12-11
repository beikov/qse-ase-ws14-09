package at.ac.tuwien.ase09.data.bond.detail;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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

import at.ac.tuwien.ase09.data.JsoupUtils;
import at.ac.tuwien.ase09.data.model.StockBondModel;

@Dependent
@Named("BondDetailReader")
public class BondDetailReader extends AbstractItemReader {
	private static final DateFormat FINANZEN_DATE_FORMAT = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.GERMAN);
	@Inject
	private StepContext stepContext;
	
	@Inject
	@BatchProperty(name="finanzenNetCertificatesUrl")
	private String finanzenNetCertificatesBaseUrl;
	
	private List<String> bondDetailLinks;
	private final Currency currency = Currency.getInstance("EUR");
	
	private Integer linkNumber;
	
	@Override
	public void open(Serializable checkpoint) throws Exception {
		bondDetailLinks = ((List<String>) stepContext.getPersistentUserData());
		if(checkpoint != null){
			linkNumber = (Integer) checkpoint;
		}else{
			linkNumber = 0;
		}
	}
	
	@Override
	public Object readItem() throws Exception {
		if(linkNumber >= bondDetailLinks.size()){
			return null;
		}
		String bondDetailLink = bondDetailLinks.get(linkNumber);
		Document detailPage = JsoupUtils.getPage(bondDetailLink);
		Elements elements = detailPage.select("div.main_right div.content_box:nth-child(2) div.content tr");
		Map<String, String> infoTable = elements.stream()
				.collect(Collectors.toMap(elem -> elem.getElementsByTag("td").get(0).text(), elem -> elem.getElementsByTag("td").get(1).text()));
		
		String name = infoTable.get("WKN");
		String isin = infoTable.get("ISIN");
		String emitter = infoTable.get("Emittent");
		String endDateStr = infoTable.get("Zahltag");
		Calendar endDate = null;
		if(!isEmpty(endDateStr)){
			endDate = Calendar.getInstance();
			endDate.setTime(FINANZEN_DATE_FORMAT.parse(endDateStr));
		}
		
		String couponStr = infoTable.get("Kupon in %");
		BigDecimal coupon = null;
		if(!isEmpty(couponStr)){
			coupon = new BigDecimal(couponStr.replaceAll("\\.", "").replace(',', '.'));
		}
		
		elements = detailPage.select("div.main_right div.content_box:nth-child(4) div.content tr");
		Map<String, String> emissionTable = elements.stream()
				.collect(Collectors.toMap(elem -> elem.getElementsByTag("td").get(0).text(), elem -> elem.getElementsByTag("td").get(1).text()));
		
		String emissionDateStr = emissionTable.get("Emissionstag");
		Calendar emissionDate = null;
		if(!isEmpty(emissionDateStr)){
			emissionDate = Calendar.getInstance();
			emissionDate.setTime(FINANZEN_DATE_FORMAT.parse(emissionDateStr));
		}
		String emissionPriceStr = emissionTable.get("Emissionspreis");
		BigDecimal emissionPrice = null;
		if(!isEmpty(emissionPriceStr)){
			emissionPrice = new BigDecimal(emissionPriceStr.replaceAll("\\.", "").replace(',', '.'));
		}
		
		// get historic price link
		Element tableBody = detailPage.select("div.infobox div.content tbody").get(0);
		Element historicLinkElem = tableBody.getElementsByAttributeValueMatching("href", "/historisch/.*").get(0);
		String historicPricesPageUrl = finanzenNetCertificatesBaseUrl + historicLinkElem.attr("href");
		
		// get base value ISIN
		String baseValueIsin = detailPage.select("div.content_box.pricebox div.content table:nth-child(2) tr:nth-child(2) td:nth-child(4)").first().text();
		
		linkNumber++;
		return new StockBondModel(name, isin, historicPricesPageUrl, bondDetailLink, baseValueIsin, coupon, emissionPrice, emitter, emissionDate, endDate);
	}
	
	@Override
	public Serializable checkpointInfo() throws Exception {
		return linkNumber;
	}
	
	private boolean isEmpty(String str){
		return str == null || str.isEmpty() || "-".equals(str);
	}
}
