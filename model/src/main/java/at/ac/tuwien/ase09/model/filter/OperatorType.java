package at.ac.tuwien.ase09.model.filter;


public enum OperatorType {
	
	GREATER(">"),
	GREATER_OR_EQUAL(">="),
	LOWER("<"),
	LOWER_OR_EQUAL("<="),
	EQUAL("="),
	NOT_EQUAL("!=");
	
	private final String operator;

	private OperatorType(String operator) {
		this.operator = operator;
	}

	public String getOperator() {
		return operator;
	}
	
	public static OperatorType valueOfOperator(String operator) {
		switch (operator) {
		case ">" : return GREATER;
		case ">=": return GREATER_OR_EQUAL;
		case "<" : return LOWER;
		case "<=": return LOWER_OR_EQUAL;
		case "=" : return EQUAL;
		case "!=": return NOT_EQUAL;
		}
		
		throw new IllegalArgumentException("Invalid operator: " + operator);
	}
}
