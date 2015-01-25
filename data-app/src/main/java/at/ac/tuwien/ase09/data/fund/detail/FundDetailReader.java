package at.ac.tuwien.ase09.data.fund.detail;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.HashMap;
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

import org.jsoup.Connection.Method;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import at.ac.tuwien.ase09.data.JsoupUtils;
import at.ac.tuwien.ase09.data.model.FundDetailLinkModel;
import at.ac.tuwien.ase09.model.Fund;
import at.ac.tuwien.ase09.model.YieldType;

@Dependent
@Named
public class FundDetailReader extends AbstractItemReader {
	private static final Pattern URL_TYPE_PATTERN = Pattern.compile("TYPE=([^&]*)");
	private static final Pattern BUSINESS_YEAR_PATTERN = Pattern.compile("([\\d]+)\\.([\\d]+)\\.\\s*-\\s*[\\d]+\\.[\\d]+\\.");
	private static final String DETAIL_LINK_PARAMETER_TEMPLATE = "todo=hist&rxpath=&xsl=/present/fund_hist_kz.xsl&sxpath=*[@key=%27#{keyPlaceholder}|%27]&context=/pool_data/fonds_stammdaten/fonds&dxpath=*[head[*]%20and%20tab[fps[preis[preis_e1[preis[wert%20and%20whrg[iso]%20and%20whrg/iso=%27EUR%27%20and%20@hist_date%3E=getHistDate(%27MONTH%27,-1)]]]]]]";
	
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
		Map<String, String> tableRows = new HashMap<>();
		for(Element row : tableRowElems){
			if(row.children().size() >= 2 && !row.children().get(0).text().trim().isEmpty()){
				String key = row.children().get(0).text();
				String value = row.children().get(1).text();
				if(!tableRows.containsKey(key)){
					tableRows.put(key, value);
				}
			}
		}
		
		String isin = tableRows.get("ISIN");
		Fund fund = null;
		if(isin != null){
			fund = new Fund();
			fund.setCode(isin);
			fund.setName(tableRows.get("Bezeichnung"));
			fund.setDetailUrl(fundDetailLink.getDetailUrl());
			fund.setHistoricPricesPageUrl(historyBaseUrl + "?" + DETAIL_LINK_PARAMETER_TEMPLATE.replaceAll("#\\{keyPlaceholder\\}", fundDetailLink.getKey()));
			fund.setDepotBank(tableRows.get("Depotbank"));
			fund.setCategory(tableRows.get("Fondskategorie"));
			
			String currencyStr = tableRows.get("Währung");
			if(currencyStr != null && !currencyStr.isEmpty()){
				fund.setCurrency(Currency.getInstance(currencyStr));
			}	
			
			String businessYearStr = tableRows.get("Geschäftsjahr");
			if(businessYearStr != null && !businessYearStr.isEmpty()){
				Matcher matcher = BUSINESS_YEAR_PATTERN.matcher(businessYearStr);
				if(matcher.find()){
					fund.setBusinessYearStartDay(Short.valueOf(matcher.group(1)));
					fund.setBusinessYearStartMonth(Short.valueOf(matcher.group(2)));
				}
			}
			
			String yieldTypeStr = tableRows.get("Ertragstyp");
			if(yieldTypeStr != null){
				if("Ausschütter".equals(yieldTypeStr)){
					fund.setYieldType(YieldType.DISTRIBUTING);
				}else if(yieldTypeStr.toLowerCase().contains("thesaurier")){
					fund.setYieldType(YieldType.CUMULATIVE);
				}
			}
			
			String denominationStr = tableRows.get("Stückelung");
			if(denominationStr != null && !denominationStr.isEmpty()){
				fund.setDenomination(new BigDecimal(denominationStr.replaceAll("\\.", "").replace(',', '.')));
			}
			
		}
		linkNumber++;
		return fund;
	}
	
	@Override
	public Serializable checkpointInfo() throws Exception {
		return linkNumber;
	}
	
}
