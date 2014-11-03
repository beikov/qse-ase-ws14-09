package at.ac.tuwien.ase09.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;

import javax.persistence.Embeddable;

@Embeddable
public class PortfolioSetting implements Serializable {

	private Money startCapital;
	private Money orderFee;
	private Money portfolioFee;
	private BigDecimal capitalReturnTax;
	
}
