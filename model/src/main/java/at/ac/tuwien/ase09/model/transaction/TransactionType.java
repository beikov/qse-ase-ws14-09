package at.ac.tuwien.ase09.model.transaction;

public enum TransactionType {

	ORDER,
	TAX,
	FEE,
	ORDER_FEE,
	PAYOUT;
	
	public static final String TYPE_ORDER = "ORDER";
	public static final String TYPE_TAX = "TAX";
	public static final String TYPE_FEE = "FEE";
	public static final String TYPE_ORDER_FEE = "ORDER_FEE";
	public static final String TYPE_PAYOUT = "PAYOUT";
}
