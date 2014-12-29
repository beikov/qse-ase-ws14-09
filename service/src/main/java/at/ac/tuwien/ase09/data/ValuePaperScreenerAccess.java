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
import at.ac.tuwien.ase09.filter.AttributeType;
import at.ac.tuwien.ase09.model.Fund;
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
		return em.createQuery("SELECT s.currency FROM Stock s Group by s.currency UNION SELECT f.currency FROM Fund f Group by f.currency").getResultList();	
	}
	@SuppressWarnings("unchecked")
	public List<String> getUsedIndexes()
	{
		return em.createQuery("SELECT s.index FROM Stock s Group by s.index").getResultList();	
	}
	@SuppressWarnings("unchecked")
	public List<String> getUsedCountries()
	{
		return em.createQuery("SELECT s.country FROM Stock s Group by s.country").getResultList();	
	}
	
	
	/*
	 * Searchmethod used by AndroidApp
	 * 
	 * @param valuePaper Wertpapier
	 * @param isTypeSpecificated Wertpapiertyp ausgewählt
	 * 
	 * @return Liste der übereinstimmenden Wertpapiere
	 */
	@SuppressWarnings("unchecked")
	public List<ValuePaper> findByValuePaper(ValuePaper valuePaper, Boolean isTypeSecificated) {
		
		
		Criteria crit = null;
		
		try{
			crit=((Session)em.getDelegate()).createCriteria(ValuePaper.class, "valuePaper");
		}
		catch(ClassCastException e)
		{
			crit=((Session)((EntityManagerImpl)em.getDelegate()).getSession()).createCriteria(ValuePaper.class, "valuePaper");
		}
		
		if (valuePaper != null) {
			if (valuePaper.getName() != null && !valuePaper.getName().isEmpty()) {
				String name = valuePaper.getName().replace('*', '%')
						.replace('?', '_');
				crit.add(Restrictions.ilike("valuePaper.name", name));
			}
			if (valuePaper.getType() == ValuePaperType.STOCK
					&& ((Stock) valuePaper).getCurrency() != null) {
				crit.add(Restrictions.eq("valuePaper.currency",
						((Stock) valuePaper).getCurrency()));
			}
			if (valuePaper.getType() == ValuePaperType.FUND
					&& ((Fund) valuePaper).getCurrency() != null) {
				crit.add(Restrictions.eq("valuePaper.currency",
						((Fund) valuePaper).getCurrency()));
			}
			if (valuePaper.getCode() != null && !valuePaper.getCode().isEmpty()) {
				String isin = valuePaper.getCode().replace('*', '%')
						.replace('?', '_');
				crit.add(Restrictions.ilike("valuePaper.code", isin));
			}
			if (valuePaper.getType() == ValuePaperType.STOCK
					&& ((Stock) valuePaper).getCountry() != null
					&& !((Stock) valuePaper).getCountry().isEmpty()) {
				String co = ((Stock) valuePaper).getCountry().replace('*', '%')
						.replace('?', '_');
				crit.add(Restrictions.ilike("valuePaper.country", co));
			}
			if (valuePaper.getType() == ValuePaperType.STOCK
					&& ((Stock) valuePaper).getIndex() != null
					&& !((Stock) valuePaper).getIndex().isEmpty()) {
				String co = ((Stock) valuePaper).getIndex().replace('*', '%')
						.replace('?', '_');
				crit.add(Restrictions.ilike("valuePaper.index", co));
			}
		
			
			if(isTypeSecificated)
			{	
				crit.add(Restrictions.eq("class", valuePaper.getType().toString()));
			}
		}
		return crit.list();
		
		
	}
}
