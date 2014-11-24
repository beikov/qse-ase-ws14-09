package at.ac.tuwien.ase09.data;

import java.util.ArrayList;
import java.util.List;

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

import at.ac.tuwien.ase09.model.Stock;
import at.ac.tuwien.ase09.model.ValuePaper;
import at.ac.tuwien.ase09.model.ValuePaperType;

@Stateless
public class ValuePaperScreenerAccess {

	@PersistenceContext
	private EntityManager em;
	
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
		
		/**
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<ValuePaper> query = builder.createQuery(ValuePaper.class);
		Root<ValuePaper> rootValuePaper = query.from(ValuePaper.class);
		
		List<Predicate> wherePredicates = new ArrayList<Predicate>();
		
		if (valuePaper.getName() != null && !valuePaper.getName().isEmpty()) {
			String na = valuePaper.getName().replace('*', '%').replace('?', '_').toUpperCase();
			wherePredicates.add( builder.like( builder.upper( rootValuePaper.<String>get("name") ), na) );
		}
		
		if (valuePaper.getCurrency() != null) {
			wherePredicates.add( builder.equal(rootValuePaper.get("currency") , valuePaper.getCurrency()));
		}
		
		if (valuePaper.getIsin() != null && !valuePaper.getIsin().isEmpty()) {
			String isin = valuePaper.getIsin().replace('*', '%').replace('?', '_').toUpperCase();
			wherePredicates.add( builder.like( builder.upper(rootValuePaper.<String>get("isin")), isin));
		}

		if (valuePaper.getCountry() != null && !valuePaper.getCountry().isEmpty()) {
			String co = valuePaper.getCountry().replace('*', '%').replace('?', '_').toUpperCase();
			wherePredicates.add( builder.like( builder.upper( rootValuePaper.<String>get("country") ), co) );
		}
		if(isTypeSecificated)
		{
			
			wherePredicates.add( builder.equal(rootValuePaper.get("class"), valuePaper.getType()) );
		}
		
		Predicate whereClause = builder.and(wherePredicates.toArray(new Predicate[0]));
		
		query.where(whereClause);
		
		return em.createQuery(query).getResultList();
		*/
		
	}
}
