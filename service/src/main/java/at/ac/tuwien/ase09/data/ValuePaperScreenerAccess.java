package at.ac.tuwien.ase09.data;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.hibernate.ejb.EntityManagerImpl;

import at.ac.tuwien.ase09.filter.AttributeFilter;
import at.ac.tuwien.ase09.filter.AttributeType;
import at.ac.tuwien.ase09.model.Stock;
import at.ac.tuwien.ase09.model.ValuePaper;
import at.ac.tuwien.ase09.model.ValuePaperType;


@Stateless
public class ValuePaperScreenerAccess {

	@Inject
	private EntityManager em;
	
	/*
	 * Searchmethod used by AndroidApp
	 * 
	 * @param filters Suchfilter
	 * @param type Wertpapiertyp
	 * 
	 * @return Liste der übereinstimmenden Wertpapiere
	 */
	@SuppressWarnings("unchecked")
	public List<ValuePaper> findByFilter(List<AttributeFilter> filters, ValuePaperType type)
	{
		Criteria crit = null;
				
				try{
					crit=((Session)em.getDelegate()).createCriteria(ValuePaper.class, "valuePaper");
				}
				catch(ClassCastException e)
				{
					crit=((Session)((EntityManagerImpl)em.getDelegate()).getSession()).createCriteria(ValuePaper.class, "valuePaper");
				}
		
		if(type!=null)
		{
			crit.add(Restrictions.eq("class", type.toString()));
		}
		if(filters!=null)
		{
		for(AttributeFilter filter:filters)
		{
			if (filter.getEnabled()&&filter.getAttribute()!=null) 
			{
				if (filter.getNumeric()) 
				{
					crit.add(filter.getOperator().createRestriction("valuePaper."+ filter.getAttribute().getParmName(),
									filter.getNumericValue()));
				}
				else if (filter.getIndexValue() != null && filter.getAttribute().equals(AttributeType.INDEX))
				{
					crit.add(Restrictions.eq("valuePaper."+ filter.getAttribute().getParmName(), filter.getIndexValue()));
				} 
				else if (filter.getCurrencyValue() != null && filter.getAttribute().equals(AttributeType.CURRENCY))
				{			
					crit.add(Restrictions.eq("valuePaper."+ filter.getAttribute().getParmName(), Currency.getInstance(filter.getCurrencyValue())));
				}
				 else if (filter.getTextValue() != null) 
				{
					String textValue = filter.getTextValue().replace('*', '%').replace('?', '_');
					crit.add(Restrictions.ilike("valuePaper."+ filter.getAttribute().getParmName(), textValue));
				} 
			}
			
		}
		}
		
		return crit.list();
	}
	@SuppressWarnings("unchecked")
	public List<Currency> getUsedCurrencyCodes()
	{
		return em.createQuery("SELECT s.currency FROM Stock s Group by s.currency").getResultList();	
	}
	@SuppressWarnings("unchecked")
	public List<String> getUsedIndexes()
	{
		return em.createQuery("SELECT s.index FROM Stock s Group by s.index").getResultList();	
	}
	
	/*
	 * Searchmethod used by AndroidApp
	 * 
	 * @param valuePaper Wertpapier
	 * 
	 * @return List of matching value papers
	 */
	@SuppressWarnings("unchecked")
	public <T extends ValuePaper> List<T> findByValuePaper(T valuePaper) {
		if(valuePaper == null){
			throw new NullPointerException("valuePaper");
		}

		Metamodel m = em.getMetamodel();
		Class<T> clazz = (Class<T>) valuePaper.getClass();
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(clazz);
		Root<T> valuePaperRoot = cq.from(clazz);
		EntityType<T> valuePaperMetamodel = m.entity(clazz);
		
		List<Predicate> disjunctivePredicates = new ArrayList<>();
		if (valuePaper.getName() != null) {
			String name = valuePaper.getName().replace('*', '%')
					.replace('?', '_');
			
			disjunctivePredicates.add(cb.like(cb.lower(valuePaperRoot.get(valuePaperMetamodel.getSingularAttribute("name", String.class))), name.toLowerCase()));
		}
		if (valuePaper.getCode() != null) {
			String isin = valuePaper.getCode().replace('*', '%')
					.replace('?', '_');
			disjunctivePredicates.add(cb.like(cb.lower(valuePaperRoot.get(valuePaperMetamodel.getSingularAttribute("code", String.class))), isin.toLowerCase()));
		}
		if (valuePaper.getType() == ValuePaperType.STOCK){
			Stock stock = (Stock) valuePaper;
			if(stock.getTickerSymbol() != null){
				String tickerSymbol = stock.getTickerSymbol().replace('*', '%')
						.replace('?', '_');
				disjunctivePredicates.add(cb.like(cb.lower(valuePaperRoot.get(valuePaperMetamodel.getSingularAttribute("tickerSymbol", String.class))), tickerSymbol.toLowerCase()));
			}
		}
		
		cq.where(cb.or(disjunctivePredicates.toArray(new Predicate[0])));
	
		return em.createQuery(cq).getResultList();
	}
}
