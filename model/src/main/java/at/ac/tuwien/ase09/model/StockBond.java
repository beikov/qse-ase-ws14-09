package at.ac.tuwien.ase09.model;

import java.math.BigDecimal;
import java.util.Calendar;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

@Entity
@DiscriminatorValue(ValuePaperType.TYPE_BOND)
public class StockBond extends ValuePaper {
	private static final long serialVersionUID = 1L;

	private Stock baseStock;
	private BigDecimal coupon;
	private BigDecimal emissionPrice;
	private String emitter;
	private Calendar emissionDate;
	private Calendar endDate;
	
	
	@Override
	@Transient
	public ValuePaperType getType() {
		return ValuePaperType.BOND;
	}

	@ManyToOne(fetch=FetchType.EAGER)
	public Stock getBaseStock() {
		return baseStock;
	}

	public void setBaseStock(Stock baseStock) {
		this.baseStock = baseStock;
	}

	public BigDecimal getCoupon() {
		return coupon;
	}

	public void setCoupon(BigDecimal coupon) {
		this.coupon = coupon;
	}

	public BigDecimal getEmissionPrice() {
		return emissionPrice;
	}

	public void setEmissionPrice(BigDecimal emissionPrice) {
		this.emissionPrice = emissionPrice;
	}

	public String getEmitter() {
		return emitter;
	}

	public void setEmitter(String emitter) {
		this.emitter = emitter;
	}

	@Temporal(TemporalType.DATE)
	public Calendar getEmissionDate() {
		return emissionDate;
	}

	public void setEmissionDate(Calendar emissionDate) {
		this.emissionDate = emissionDate;
	}

	@Temporal(TemporalType.DATE)
	public Calendar getEndDate() {
		return endDate;
	}

	public void setEndDate(Calendar endDate) {
		this.endDate = endDate;
	}
	
}
