package at.ac.tuwien.ase09.data;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import at.ac.tuwien.ase09.exception.AppException;
import at.ac.tuwien.ase09.model.NewsItem;

@Stateless
public class NewsItemDataAccess {
	
	@Inject
	private EntityManager em;

	public List<NewsItem> getNewsItemsByValuePaperCode(String code){
		try{
			return em.createQuery("SELECT n FROM NewsItem n WHERE n.stock.index = :code", NewsItem.class).setParameter("code", code).getResultList();
		}catch(Exception e){
			throw new AppException(e);
		}
	}
}
