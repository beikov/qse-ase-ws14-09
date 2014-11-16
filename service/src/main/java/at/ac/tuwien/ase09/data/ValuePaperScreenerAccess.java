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

import at.ac.tuwien.ase09.model.ValuePaper;

@Stateless
public class ValuePaperScreenerAccess {

	@PersistenceContext
	private EntityManager em;
	
	public List<ValuePaper> findByValuePaper(ValuePaper valuePaper, Boolean isTypeSecificated) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<ValuePaper> query = builder.createQuery(ValuePaper.class);
		Root<ValuePaper> rootValuePaper = query.from(ValuePaper.class);
		
		List<Predicate> wherePredicates = new ArrayList<Predicate>();
		
		if (valuePaper.getName() != null) {
			String na = valuePaper.getName().replace('*', '%').replace('?', '_').toUpperCase();
			wherePredicates.add( builder.like( builder.upper( rootValuePaper.<String>get("c_name") ), na) );
		}
		
		if (valuePaper.getCurrency() != null) {
			wherePredicates.add( builder.equal(rootValuePaper.get("c_currency") , valuePaper.getCurrency()));
		}
		
		if (valuePaper.getIsin() != null) {
			String isin = valuePaper.getIsin().replace('*', '%').replace('?', '_').toUpperCase();
			wherePredicates.add( builder.like( builder.upper(rootValuePaper.<String>get("c_isin")), isin));
		}

		if (valuePaper.getCountry() != null) {
			String co = valuePaper.getCountry().replace('*', '%').replace('?', '_').toUpperCase();
			wherePredicates.add( builder.like( builder.upper( rootValuePaper.<String>get("c_country") ), co) );
		}
		if(isTypeSecificated)
		{
			wherePredicates.add( builder.equal(rootValuePaper.get("c_valuepaper_type"), valuePaper.getType()) );
		}
		
		Predicate whereClause = builder.and(wherePredicates.toArray(new Predicate[0]));
		
		query.where(whereClause);
		
		return em.createQuery(query).getResultList();
		
	}
}
