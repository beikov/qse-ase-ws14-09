package at.ac.tuwien.ase09.data.stock.analystOpinion;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Currency;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.batch.api.BatchProperty;
import javax.batch.api.chunk.AbstractItemReader;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import at.ac.tuwien.ase09.data.JsoupUtils;
import at.ac.tuwien.ase09.data.ValuePaperDataAccess;
import at.ac.tuwien.ase09.model.AnalystOpinion;
import at.ac.tuwien.ase09.model.AnalystRecommendation;
import at.ac.tuwien.ase09.model.Money;
import at.ac.tuwien.ase09.model.Stock;

@Dependent
@Named
public class AnalystOpinionReader extends AbstractItemReader  {
	private static final Pattern TARGET_PRICE_PATTERN = Pattern.compile("[^0-9]*([0-9]+,?[0-9]*)\\s*([^\\s]*)");
	private static final DateFormat FINANZEN_NEWS_DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");
	private static final DateFormat FINANZEN_CURRENT_NEWS_DATE_FORMAT = new SimpleDateFormat("HH:mm");
	private static final Set<String> BUY_KEYWORDS = new HashSet<>(Arrays.asList(new String[]{
			"kaufen",
			"buy",
			"positive",
			"positiv",
			"kauf"
	}));
	private static final Set<String> SELL_KEYWORDS = new HashSet<>(Arrays.asList(new String[]{
			"verkaufen",
			"sell",
			"negative",
			"negativ",
			"verkauf"
	}));
	private static final Set<String> HOLD_KEYWORDS = new HashSet<>(Arrays.asList(new String[]{
			"halten",
			"hold",
			"neutral"
	}));
	
	@Inject
	private ValuePaperDataAccess valuePaperDataAccess;
	
	@Inject
	@BatchProperty(name = "finanzenNetUrl")
	private String finanzenNetUrl;
	
	private List<Stock> stocks;
	private Integer stockNumber;
	private final int maxAnalystItems = 5;
	
	@Override
	public void open(Serializable checkpoint) throws Exception {
		stocks = valuePaperDataAccess.getValuePapers(Stock.class);
		if(checkpoint != null){
			stockNumber = (Integer) checkpoint;
		}else{
			stockNumber = 0;
		}
	}
	
	@Override
	public Object readItem() throws Exception {
		if(stockNumber >= stocks.size()){
			return null;
		}
		Stock stock = stocks.get(stockNumber);
		if(stock.getFinanzenNewsPageUrl() == null){
			return null;
		}
		Document detailPage = JsoupUtils.getPage(stock.getDetailUrl());
		Elements analysisRows = detailPage.select("#analysis-table tr");
		List<AnalystOpinion> newsItems = new ArrayList<AnalystOpinion>();
		
		int loopBound = analysisRows.size() - maxAnalystItems;
		if(loopBound < 0){
			loopBound = 0;
		}
		// iterate backwards to maintain chronological order
		for(int i = analysisRows.size()-1; i >= loopBound; i--){
			Element analysisRow = analysisRows.get(i);
			Elements analysisColumns = analysisRow.getElementsByTag("td");
			if(analysisColumns.size() == 4){
				AnalystRecommendation recommendation = getRecommendation(analysisColumns.get(1).text());
				if(recommendation == null){
					continue;
				}
				AnalystOpinion analystOpinion = new AnalystOpinion();
				Calendar analysisDate = parseTimeOrDate(analysisColumns.get(0).text());
				String source = analysisColumns.get(2).text();
				
				Document analysisDetailPage = JsoupUtils.getPage(finanzenNetUrl + analysisColumns.get(1).select("a").attr("href"));
				Element textElement = analysisDetailPage.select("div.news_text").first();
				String analysisText = null;
				if(textElement != null){
					analysisText= textElement.text();
				}
				
				Element targetPriceElement = analysisDetailPage.select("div.content table.table_distance.table_border_complete tr:nth-child(2) td:nth-child(3)").first();
				BigDecimal targetPrice = null;
				Currency targetPriceCurrency = null;
				if(targetPriceElement != null){
					Matcher targetPriceMatcher = TARGET_PRICE_PATTERN.matcher(targetPriceElement.text());
					if(targetPriceMatcher.find()){
						targetPrice = new BigDecimal(targetPriceMatcher.group(1).replace(',', '.'));
						String currencySymbol = targetPriceMatcher.group(2);
						if(currencySymbol.equals("\u20ac")){
							targetPriceCurrency = Currency.getInstance("EUR");
						}else{
							try{
								targetPriceCurrency = Currency.getInstance(currencySymbol);
							}catch(IllegalArgumentException e){
								// use the stock's currency as default
								targetPriceCurrency = stock.getCurrency();
							}
						}
					}
					
				}
				
				analystOpinion.setCreated(analysisDate);
				analystOpinion.setTargetPrice(new Money(targetPrice, targetPriceCurrency));
				analystOpinion.setRecommendation(recommendation);
				analystOpinion.setSource(source);
				analystOpinion.setText(analysisText);
				analystOpinion.setStock(stock);
				newsItems.add(analystOpinion);
			}
		}
		stockNumber++;
		return newsItems;
	}
	
	private Calendar parseTimeOrDate(String s) throws ParseException {
		Calendar calendar = Calendar.getInstance();
		try{
			calendar.setTime(FINANZEN_NEWS_DATE_FORMAT.parse(s));
			return calendar;
		}catch(ParseException e){
			// ignore
		}
		calendar.setTime(FINANZEN_CURRENT_NEWS_DATE_FORMAT.parse(s));
		return calendar;
	}
	
	private static AnalystRecommendation getRecommendation(String title){
		title = title.toLowerCase();
		for(String keyword : HOLD_KEYWORDS){
			if(title.contains(keyword)){
				return AnalystRecommendation.HOLD;
			}
		}
		for(String keyword : BUY_KEYWORDS){
			if(title.contains(keyword)){
				return AnalystRecommendation.BUY;
			}
		}
		for(String keyword : SELL_KEYWORDS){
			if(title.contains(keyword)){
				return AnalystRecommendation.SELL;
			}
		}
		return null;
	}
}
