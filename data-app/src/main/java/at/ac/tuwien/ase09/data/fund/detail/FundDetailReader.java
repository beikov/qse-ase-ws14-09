package at.ac.tuwien.ase09.data.fund.detail;

import java.io.Serializable;
import java.util.Currency;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.batch.api.BatchProperty;
import javax.batch.api.chunk.AbstractItemReader;
import javax.batch.runtime.context.StepContext;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import org.jsoup.Connection.Method;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import at.ac.tuwien.ase09.data.JsoupUtils;
import at.ac.tuwien.ase09.data.model.FundDetailLinkModel;
import at.ac.tuwien.ase09.model.Fund;

@Dependent
@Named
public class FundDetailReader extends AbstractItemReader {
	private static final Pattern urlTypePattern = Pattern.compile("TYPE=([^&]*)");
	private static final String detailLinkParameterTemplate = "todo=hist&rxpath=&xsl=/present/fund_hist_kz.xsl&sxpath=*[@key=%27#{keyPlaceholder}|%27]&context=/pool_data/fonds_stammdaten/fonds&dxpath=*[head[*]%20and%20tab[fps[preis[preis_e1[preis[wert%20and%20whrg[iso]%20and%20whrg/iso=%27EUR%27%20and%20@hist_date%3E=getHistDate(%27MONTH%27,-1)]]]]]]";
	
	@Inject
	@BatchProperty(name="profitwebHistoryUrl")
	private String historyBaseUrl;
	
	@Inject
	private StepContext stepContext;
	
	private List<FundDetailLinkModel> fundDetailLinks;
	private final Currency currency = Currency.getInstance("EUR");
	
	private Integer linkNumber;
	
	@Override
	public void open(Serializable checkpoint) throws Exception {
		fundDetailLinks = (List<FundDetailLinkModel>) stepContext.getPersistentUserData();
		if(checkpoint != null){
			linkNumber = (Integer) checkpoint;
		}else{
			linkNumber = 0;
		}
	}
	
	@Override
	public Object readItem() throws Exception {
		if(linkNumber >= fundDetailLinks.size()){
			return null;
		}
		FundDetailLinkModel fundDetailLink = fundDetailLinks.get(linkNumber);
		Document detailPage = JsoupUtils.getPage(fundDetailLink.getDetailUrl(), Method.POST, 3000);
		
		Elements tableRowElems = detailPage.select("tr");
		Map<String, String> tableRows =tableRowElems.stream()
			.filter(elem -> elem.children().stream().filter(child -> "colorth".equals(child.className()) || "colortd".equals(child.className())).count() == 2)
			.collect(Collectors.toMap(elem -> elem.getElementsByClass("labelB").get(0).text(), elem -> elem.getElementsByClass("label").get(0).text()));
		Fund fund = new Fund();
		fund.setIsin(tableRows.get("ISIN"));
		fund.setName(tableRows.get("Bezeichnung"));
		fund.setDetailUrl(fundDetailLink.getDetailUrl());
		fund.setHistoricPricesPageUrl(historyBaseUrl + "?" + detailLinkParameterTemplate.replaceAll("#\\{keyPlaceholder\\}", fundDetailLink.getKey()));
		
		linkNumber++;
		return fund;
	}
	
	@Override
	public Serializable checkpointInfo() throws Exception {
		return linkNumber;
	}
	
}
