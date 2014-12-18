package at.ac.tuwien.ase09.service;

import java.util.List;

import javax.ejb.Stateless;

import at.ac.tuwien.ase09.model.NewsItem;

@Stateless
public class NewsService extends AbstractService {

	public void saveNews(List<NewsItem> newsItems) {
		for (NewsItem newsItem : newsItems) {
			em.persist(newsItem);
		}

		em.flush();
	}
}
