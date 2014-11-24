package at.ac.tuwien.ase09.filter;

public enum OperatorType {
	
	GREATER(">"),
	LOWER("<"),
	EQUAL("=");
	
	private final String kuerzel;
	
	OperatorType(String kuerzel){this.kuerzel=kuerzel;}
	
	public String getKuerzel(){return kuerzel;}
}
