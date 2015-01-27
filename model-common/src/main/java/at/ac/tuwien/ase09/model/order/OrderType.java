package at.ac.tuwien.ase09.model.order;

public enum OrderType {

	MARKET("Market"),
	LIMIT("Limit");
	
	public static final String TYPE_MARKET = "MARKET";
	public static final String TYPE_LIMIT = "LIMIT";
	
	private final String displayName;
	
	private OrderType(String displayName){
		this.displayName = displayName;
	}

	public String getDisplayName() {
		return displayName;
	}
	
}
