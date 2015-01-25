package at.ac.tuwien.ase09.model;

public enum ValuePaperType {
	STOCK("Aktie", "Stock"),
	FUND("Fonds", "Fund"),
	BOND("Anleihe", "StockBond");
	
	public static final String TYPE_STOCK = "STOCK";
	public static final String TYPE_FUND = "FUND";
	public static final String TYPE_BOND = "BOND";
	
	private final String label;
	private final String entityName;
	
	ValuePaperType(String label, String entityName) {
		this.label = label;
		this.entityName = entityName;
	}
	
	public String getLabel() {
		return label;
	}
	
	public String getEntityName() {
		return entityName;
	}
	
}
