package at.ac.tuwien.ase09.filter;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

public enum OperatorType {
	
	GREATER(">"){ public Criterion createRestriction(String param,Object value){
		return Restrictions.gt(param, value);
	}},
	LOWER("<"){ public Criterion createRestriction(String param,Object value){
		return Restrictions.lt(param, value);
	}},
	EQUAL("="){ public Criterion createRestriction(String param,Object value){
		return Restrictions.eq(param, value);
	}};
	
	private final String label;
	
	OperatorType(String label){this.label=label;}
	
	public String getLabel(){return label;}
	
	public abstract Criterion createRestriction(String param,Object value);
}
