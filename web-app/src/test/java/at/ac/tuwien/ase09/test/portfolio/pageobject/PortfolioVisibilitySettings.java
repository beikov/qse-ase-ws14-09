package at.ac.tuwien.ase09.test.portfolio.pageobject;

import at.ac.tuwien.ase09.test.pageabstraction.FindBy;
import at.ac.tuwien.ase09.test.pageobject.InputSwitch;

public class PortfolioVisibilitySettings {
	@FindBy(relativeId="publicSwitch")
	private InputSwitch publicSwitch;
	@FindBy(relativeId="statisticsSwitch")
	private InputSwitch statisticsSwitch;
	@FindBy(relativeId="valuePaperSwitch")
	private InputSwitch valuePaperSwitch;
	@FindBy(relativeId="chartsSwitch")
	private InputSwitch chartsSwitch;
	@FindBy(relativeId="orderHistorySwitch")
	private InputSwitch orderHistorySwitch;
	@FindBy(relativeId="transactionHistorySwitch")
	private InputSwitch transactionHistorySwitch;
	@FindBy(relativeId="newsSwitch")
	private InputSwitch newsSwitch;
	@FindBy(relativeId="opinionSwitch")
	private InputSwitch opinionSwitch;
	
	public InputSwitch getPublicSwitch() {
		return publicSwitch;
	}
	public InputSwitch getStatisticsSwitch() {
		return statisticsSwitch;
	}
	public InputSwitch getValuePaperSwitch() {
		return valuePaperSwitch;
	}
	public InputSwitch getChartsSwitch() {
		return chartsSwitch;
	}
	public InputSwitch getOrderHistorySwitch() {
		return orderHistorySwitch;
	}
	public InputSwitch getTransactionHistorySwitch() {
		return transactionHistorySwitch;
	}
	public InputSwitch getNewsSwitch() {
		return newsSwitch;
	}
	public InputSwitch getOpinionSwitch() {
		return opinionSwitch;
	}

}
