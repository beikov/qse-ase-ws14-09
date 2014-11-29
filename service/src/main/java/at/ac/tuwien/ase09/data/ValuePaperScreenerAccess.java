package at.ac.tuwien.ase09.data;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import at.ac.tuwien.ase09.filter.AttributeFilter;
import at.ac.tuwien.ase09.model.Stock;
import at.ac.tuwien.ase09.model.ValuePaper;
import at.ac.tuwien.ase09.model.ValuePaperType;


@Stateless
public class ValuePaperScreenerAccess {

	@PersistenceContext
	private EntityManager em;
	
	@SuppressWarnings("unchecked")
	public List<ValuePaper> findByFilter(List<AttributeFilter> filters, ValuePaperType type)
	{
		Criteria crit = ((Session)em.getDelegate()).createCriteria(ValuePaper.class, "valuePaper");
		
		if(type!=null)
		{
			crit.add(Restrictions.eq("class", type.toString()));
		}
		
		for(AttributeFilter filter:filters)
		{
			if (filter.getEnabled()&&filter.getAttribute()!=null) 
			{
				if (filter.getNumeric()) 
				{
					crit.add(filter.getOperator().createRestriction("valuePaper."+ filter.getAttribute().getParmName(),
									filter.getNumericValue()));
				} else if (filter.getTextValue() != null) 
				{
					String textValue = filter.getTextValue().replace('*', '%').replace('?', '_');
					crit.add(Restrictions.ilike("valuePaper."+ filter.getAttribute().getParmName(), textValue));
				} 
				else if (filter.getCurrencyValue() != null) 
				{			
					crit.add(Restrictions.eq("valuePaper."+ filter.getAttribute().getParmName(), Currency.getInstance(filter.getCurrencyValue())));
				}
			}
			
		}
		
		return crit.list();
	}
	@SuppressWarnings("unchecked")
	public List<Currency> getUsedCurrencyCodes()
	{
		//List<Currency> curList=new ArrayList<Currency>();
		return em.createQuery("SELECT s.currency FROM Stock s Group by s.currency").getResultList();
		
		
	}
	@SuppressWarnings("unchecked")
	public List<ValuePaper> findByValuePaper(ValuePaper valuePaper, Boolean isTypeSecificated) {
		
		
		Criteria crit = ((Session)em.getDelegate()).createCriteria(ValuePaper.class, "valuePaper");
		
		
		
		if (valuePaper.getName() != null && !valuePaper.getName().isEmpty()) {
			String name = valuePaper.getName().replace('*', '%').replace('?', '_');
			crit.add(Restrictions.ilike("valuePaper.name", name));
		}
		if (valuePaper.getType() == ValuePaperType.STOCK && ((Stock)valuePaper).getCurrency() != null) {
			crit.add(Restrictions.eq("valuePaper.currency", ((Stock)valuePaper).getCurrency()));
		}
		if (valuePaper.getCode() != null && !valuePaper.getCode().isEmpty()) {
			String isin = valuePaper.getCode().replace('*', '%').replace('?', '_');
			crit.add(Restrictions.ilike("valuePaper.code", isin));
		}
		if (valuePaper.getType() == ValuePaperType.STOCK && ((Stock)valuePaper).getCountry() != null && !((Stock)valuePaper).getCountry().isEmpty()) {
			String co = ((Stock)valuePaper).getCountry().replace('*', '%').replace('?', '_');
			crit.add(Restrictions.ilike("valuePaper.country", co));
		}
		
			
		if(isTypeSecificated)
		{	
			crit.add(Restrictions.eq("class", valuePaper.getType().toString()));
		}
		
		return crit.list();
		
		
	}
}
