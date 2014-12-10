package at.ac.tuwien.ase09.data;

import java.util.Currency;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.ejb.EntityManagerImpl;

import at.ac.tuwien.ase09.filter.AttributeFilter;
import at.ac.tuwien.ase09.model.Stock;
import at.ac.tuwien.ase09.model.ValuePaper;
import at.ac.tuwien.ase09.model.ValuePaperType;


@Stateless
public class ValuePaperScreenerAccess {

	@Inject
	private EntityManager em;
	
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
