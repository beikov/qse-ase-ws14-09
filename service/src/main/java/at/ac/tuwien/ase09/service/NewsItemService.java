package at.ac.tuwien.ase09.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import at.ac.tuwien.ase09.data.NewsItemDataAccess;
import at.ac.tuwien.ase09.model.NewsItem;

@Stateless
public class NewsItemService {
	
	@Inject
	private EntityManager em;

	@Inject
	private NewsItemDataAccess newsItemDataAccess;
	
	public List<NewsItem> getNewsItemsByValuePaperCode(String code){
		return newsItemDataAccess.getNewsItemsByValuePaperCode(code);
	}
	
}
