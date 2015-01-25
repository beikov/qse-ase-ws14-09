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
import javax.persistence.metamodel.SingularAttribute;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.jpa.internal.EntityManagerImpl;

import at.ac.tuwien.ase09.model.Fund;
import at.ac.tuwien.ase09.model.Stock;
import at.ac.tuwien.ase09.model.StockBond;
import at.ac.tuwien.ase09.model.ValuePaper;
import at.ac.tuwien.ase09.model.ValuePaperType;
import at.ac.tuwien.ase09.model.filter.AttributeFilter;
import at.ac.tuwien.ase09.model.filter.Attribute;
import at.ac.tuwien.ase09.parser.PWatchCompiler;


@Stateless
public class ValuePaperScreenerAccess {

	@Inject
	private EntityManager em;
	
	public List<ValuePaper> findByFilter(List<AttributeFilter> filters, ValuePaperType type) {
		String jpql = PWatchCompiler.compileJpql(PWatchCompiler.attributeFiltersAsPWatch(filters), type);
		return em.createQuery(jpql, ValuePaper.class).getResultList();
	}
	
	public List<Currency> getUsedCurrencyCodes() {
		return em.createQuery("SELECT s.currency FROM Stock s Group by s.currency UNION SELECT f.currency FROM Fund f Group by f.currency", Currency.class).getResultList();	
	}
	
	public List<String> getUsedIndexes() {
		return em.createQuery("SELECT s.index FROM Stock s Group by s.index", String.class).getResultList();	
	}	
	
	public List<String> getUsedCountries() {
		return em.createQuery("SELECT s.country FROM Stock s Group by s.country", String.class).getResultList();	
	}
	
	/*
	 * Search method used by the Android app
	 * 
	 * @param valuePaperType
	 * @param template
	 * 
	 * @return List of matching value papers
	 */
	@SuppressWarnings("unchecked")
	public List<ValuePaper> findByValuePaper(ValuePaperType valuePaperType, ValuePaper template) {
		if(template == null){
			throw new NullPointerException("template");
		}
		if(valuePaperType != null && template.getType() != valuePaperType){
			throw new IllegalArgumentException("If a value paper type is specified, the template must be of this type");
		}

		Metamodel m = em.getMetamodel();
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ValuePaper> cq = cb.createQuery(ValuePaper.class);
		
		Root<? extends ValuePaper> valuePaperRoot;
		
		Class<? extends ValuePaper> concreteClass = null;
		if(valuePaperType != null){
			concreteClass = getClassForValuePaperType(valuePaperType);
		}else{
			concreteClass = ValuePaper.class;
		}
		valuePaperRoot = cq.from(concreteClass);
		
		EntityType<ValuePaper> valuePaperMetamodel = m.entity(ValuePaper.class);
		
		List<Predicate> disjunctivePredicates = new ArrayList<>();
		if (template.getName() != null) {
			String name = template.getName().replace('*', '%')
					.replace('?', '_');
			
			disjunctivePredicates.add(cb.like(cb.lower(valuePaperRoot.get(valuePaperMetamodel.getSingularAttribute("name", String.class))), name.toLowerCase()));
		}
		if (template.getCode() != null) {
			String isin = template.getCode().replace('*', '%')
					.replace('?', '_');
			disjunctivePredicates.add(cb.like(cb.lower(valuePaperRoot.get(valuePaperMetamodel.getSingularAttribute("code", String.class))), isin.toLowerCase()));
		}
		if (valuePaperType == ValuePaperType.STOCK){
			Stock stock = (Stock) template;
			EntityType<Stock> stockMetamodel = m.entity(Stock.class);
 			if(stock.getTickerSymbol() != null){
				String tickerSymbol = stock.getTickerSymbol().replace('*', '%')
						.replace('?', '_');
				disjunctivePredicates.add(cb.like(cb.lower(valuePaperRoot.get((SingularAttribute<ValuePaper, String>) stockMetamodel.getSingularAttribute("tickerSymbol", String.class))), tickerSymbol.toLowerCase()));
			}
		}
		
		cq.where(cb.or(disjunctivePredicates.toArray(new Predicate[0])));
		cq.select(valuePaperRoot);
		return em.createQuery(cq).getResultList();
	}
	
	private Class<? extends ValuePaper> getClassForValuePaperType(ValuePaperType type){
		switch(type){
			case FUND: return Fund.class;
			case STOCK: return Stock.class;
			case BOND: return StockBond.class;
			default: throw new IllegalStateException("Unknown value paper type [" + type + "]");
		}
	}
	
	
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

	public List<ValuePaper> findByExpression(String pwatchExpression, ValuePaperType valuePaperType){
		String jpql = PWatchCompiler.compileJpql(pwatchExpression, valuePaperType);
		return em.createQuery(jpql, ValuePaper.class).getResultList();
	}
}
