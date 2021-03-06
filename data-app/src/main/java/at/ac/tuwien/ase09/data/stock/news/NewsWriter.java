package at.ac.tuwien.ase09.data.stock.news;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;

import javax.batch.api.chunk.AbstractItemWriter;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import at.ac.tuwien.ase09.data.stock.atx.intraday.PriceListWriter;
import at.ac.tuwien.ase09.model.NewsItem;
import at.ac.tuwien.ase09.service.NewsService;

@Dependent
@Named
public class NewsWriter extends AbstractItemWriter {
	
	private static final Logger LOG = Logger.getLogger(PriceListWriter.class.getName());
	@Inject
	private NewsService service;
	
	@Override
	public void open(Serializable checkpoint) throws Exception {
//		if(checkpoint != null){
//			em.clear();
//		}
	}
	
	@Override
	public void writeItems(List<Object> items) throws Exception {
		for(Object item : items){
			List<NewsItem> newsItems = (List<NewsItem>) item;
			service.saveNews(newsItems);
			LOG.info("Saved " + newsItems.size() + " news items");
		}
	}
	
	@Override
	public Serializable checkpointInfo() throws Exception {
		return 0;
	}
}
