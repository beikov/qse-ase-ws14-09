package at.ac.tuwien.ase09.data.stock.news;

import java.util.ArrayList;
import java.util.List;

import javax.batch.api.chunk.ItemProcessor;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import at.ac.tuwien.ase09.data.ValuePaperDataAccess;
import at.ac.tuwien.ase09.exception.EntityNotFoundException;
import at.ac.tuwien.ase09.model.NewsItem;


@Dependent
@Named
public class NewsProcessor implements ItemProcessor {

	@Inject
	private ValuePaperDataAccess valuePaperDataAccess;
	
	@Override
	public Object processItem(Object item) throws Exception {
		List<NewsItem> newsItems = (List<NewsItem>) item;
		if(newsItems.isEmpty()){
			return null;
		}
		List<NewsItem> newNewsItem = new ArrayList<>();
		for(NewsItem newsItem : newsItems){
			try{
				valuePaperDataAccess.getNewsItemForStockWithTitle(newsItem.getStock().getCode(), newsItem.getTitle());
				// the news item already exists
				// since the items are chronologically ordered we imply that all following items must exist as well
				// hence we can end the loop
				break;
			} catch(EntityNotFoundException nfe){
				// ignore
			}
			// the news item does not exist
			newNewsItem.add(newsItem);
		}
		
		if(newNewsItem.isEmpty()){
			return null;
		}else{
			return newNewsItem;
		}
	}

}
