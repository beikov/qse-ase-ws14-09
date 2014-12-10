package at.ac.tuwien.ase09.filter;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

public enum OperatorType {
	
	GREATER(">"){ public Criterion createRestriction(String param,BigDecimal value){
		return Restrictions.gt(param, value.setScale(2, RoundingMode.HALF_UP));
	}},
	LOWER("<"){ public Criterion createRestriction(String param,BigDecimal value){
		return Restrictions.lt(param, value.setScale(2, RoundingMode.HALF_UP));
	}},
	EQUAL("="){ public Criterion createRestriction(String param,BigDecimal value){
		return Restrictions.eq(param, value.setScale(2, RoundingMode.HALF_UP));
	}};
	
	private final String label;
	
	OperatorType(String label){this.label=label;}
	
	public String getLabel(){return label;}
	
	public abstract Criterion createRestriction(String param,BigDecimal value);
}
