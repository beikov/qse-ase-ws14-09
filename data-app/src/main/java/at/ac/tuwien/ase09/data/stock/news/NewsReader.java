package at.ac.tuwien.ase09.data.stock.news;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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
import at.ac.tuwien.ase09.model.NewsItem;
import at.ac.tuwien.ase09.model.Stock;

@Dependent
@Named
public class NewsReader extends AbstractItemReader  {
	private static final DateFormat FINANZEN_NEWS_DATE_FORMAT = new SimpleDateFormat("dd.MM.yy");
	private static final DateFormat FINANZEN_CURRENT_NEWS_DATE_FORMAT = new SimpleDateFormat("HH:mm");
	
	static {
		Calendar twoDigitYearStart = Calendar.getInstance();
		twoDigitYearStart.set(2000, 1, 1); // 1.1.2000
		((SimpleDateFormat) FINANZEN_NEWS_DATE_FORMAT).set2DigitYearStart(twoDigitYearStart.getTime());
	}
	
	@Inject
	private ValuePaperDataAccess valuePaperDataAccess;
	
	@Inject
	@BatchProperty(name = "finanzenNetUrl")
	private String finanzenNetUrl;
	
	private List<Stock> stocks;
	private Integer stockNumber;
	private final int maxNewsItems = 5;
	
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
		Document newsPage = JsoupUtils.getPage(stock.getFinanzenNewsPageUrl());
		Elements newsRows = newsPage.select("#news-list-table tr");
		List<NewsItem> newsItems = new ArrayList<NewsItem>();
		
		int loopBound = newsRows.size() - maxNewsItems;
		if(loopBound < 0){
			loopBound = 0;
		}
		// iterate backwards to maintain chronological order
		for(int i = newsRows.size()-1-loopBound; i >= 0; i--){
			Element newsRow = newsRows.get(i);
			Elements newsColumns = newsRow.getElementsByTag("td");
			if(newsColumns.size() == 3){
				NewsItem newsItem = new NewsItem();
				Calendar newsDate = parseTimeOrDate(newsColumns.get(0).text());
				
				String source = null;
				Element sourceElement = newsColumns.get(2).select("span.news_source").first();
				if(sourceElement != null){
					source = unwrap(sourceElement.text());
				}
				
				Document newsDetailPage = JsoupUtils.getPage(finanzenNetUrl + newsColumns.get(2).select("a").attr("href"));
				String newsTitle = newsDetailPage.select("h1.news_title").first().text();
				Element textElement = newsDetailPage.select("div.news_text").first();
				String newsText = null;
				if(textElement != null){
					newsText= textElement.text().trim();
				}
				
				newsItem.setCreated(newsDate);
				newsItem.setSource(source);
				newsItem.setTitle(newsTitle);
				newsItem.setText(newsText);
				newsItem.setStock(stock);
				newsItems.add(newsItem);
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
		Calendar currentDate = Calendar.getInstance();
		calendar.setTime(FINANZEN_CURRENT_NEWS_DATE_FORMAT.parse(s));
		calendar.set(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH));
		return calendar;
	}
	
	private String unwrap(String s){
		if(s.startsWith("(") && s.endsWith(")")){
			return s.substring(1, s.length()-1);
		}
		return s;
	}
}
