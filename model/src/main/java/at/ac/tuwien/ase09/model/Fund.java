package at.ac.tuwien.ase09.model;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Currency;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

@Entity
@DiscriminatorValue(ValuePaperType.TYPE_FUND)
public class Fund extends ValuePaper {
	private static final long serialVersionUID = 1L;
	
	private YieldType yieldType;
	private String depotBank;
	private Short businessYearStartDay;
	private Short businessYearStartMonth;
	private BigDecimal denomination; //Stückelung
	private String category;
	private Currency currency;
	
	@Override
	@Transient
	public ValuePaperType getType() {
		return ValuePaperType.FUND;
	}

	public YieldType getYieldType() {
		return yieldType;
	}

	public void setYieldType(YieldType yieldType) {
		this.yieldType = yieldType;
	}

	public String getDepotBank() {
		return depotBank;
	}

	public void setDepotBank(String depotBank) {
		this.depotBank = depotBank;
	}

	public Short getBusinessYearStartDay() {
		return businessYearStartDay;
	}

	public void setBusinessYearStartDay(Short businessYearStartDay) {
		this.businessYearStartDay = businessYearStartDay;
	}

	public Short getBusinessYearStartMonth() {
		return businessYearStartMonth;
	}

	public void setBusinessYearStartMonth(Short businessYearStartMonth) {
		this.businessYearStartMonth = businessYearStartMonth;
	}

	@Column(precision=10,scale=4)
	public BigDecimal getDenomination() {
		return denomination;
	}

	public void setDenomination(BigDecimal denomination) {
		this.denomination = denomination;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
	
	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}
	
}
