package at.ac.tuwien.ase09.model.filter;

import java.util.Arrays;
import java.util.List;


public enum AttributeType {
	NUMERIC(OperatorType.values()),
	DATE_TIME(OperatorType.values()),
	STRING(OperatorType.EQUAL, OperatorType.NOT_EQUAL);
	
	private final List<OperatorType> allowedOperatorTypes;
	
	private AttributeType(OperatorType... operatorTypes) {
		this.allowedOperatorTypes = Arrays.asList(operatorTypes);
	}

	public List<OperatorType> getAllowedOperatorTypes() {
		return allowedOperatorTypes;
	}
}
