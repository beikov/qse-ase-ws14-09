package at.ac.tuwien.ase09.model;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;

@Embeddable
public class PortfolioSetting implements Serializable {
	private static final long serialVersionUID = 1L;

	private Money startCapital;
	private Money orderFee;
	private Money portfolioFee;
	private BigDecimal capitalReturnTax;
	
	@Embedded
	public Money getStartCapital() {
		return startCapital;
	}
	
	public void setStartCapital(Money startCapital) {
		this.startCapital = startCapital;
	}

	@Embedded
	public Money getOrderFee() {
		return orderFee;
	}
	
	public void setOrderFee(Money orderFee) {
		this.orderFee = orderFee;
	}
	
	@Embedded
	public Money getPortfolioFee() {
		return portfolioFee;
	}
	
	public void setPortfolioFee(Money portfolioFee) {
		this.portfolioFee = portfolioFee;
	}
	
	public BigDecimal getCapitalReturnTax() {
		return capitalReturnTax;
	}
	
	public void setCapitalReturnTax(BigDecimal capitalReturnTax) {
		this.capitalReturnTax = capitalReturnTax;
	}
	
}
