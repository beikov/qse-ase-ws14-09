package at.ac.tuwien.ase09.model;

public enum ValuePaperType {
	STOCK("Aktie"),
	FUND("Fonds"),
	BOND("Anleihe");
	
	public static final String TYPE_STOCK = "STOCK";
	public static final String TYPE_FUND = "FUND";
	public static final String TYPE_BOND = "BOND";
	
	private final String label;
	
	ValuePaperType(String label){this.label=label;}
	
	public String getLabel(){return label;}
	
}
