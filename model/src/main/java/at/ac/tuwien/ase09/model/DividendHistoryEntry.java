package at.ac.tuwien.ase09.model;

import java.math.BigDecimal;
import java.util.Calendar;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class DividendHistoryEntry extends BaseEntity<Long> {
	private static final long serialVersionUID = 1L;

	private Stock stock;
	private Calendar dividendDate;
	private BigDecimal dividend;
	private BigDecimal dividendYield;
	
	@ManyToOne(optional=false, fetch=FetchType.LAZY)
	public Stock getStock() {
		return stock;
	}
	public void setStock(Stock stock) {
		this.stock = stock;
	}
	@Temporal(TemporalType.DATE)
	public Calendar getDividendDate() {
		return dividendDate;
	}
	public void setDividendDate(Calendar dividendDate) {
		this.dividendDate = dividendDate;
	}
	public BigDecimal getDividend() {
		return dividend;
	}
	public void setDividend(BigDecimal dividend) {
		this.dividend = dividend;
	}
	public BigDecimal getDividendYield() {
		return dividendYield;
	}
	public void setDividendYield(BigDecimal dividendYield) {
		this.dividendYield = dividendYield;
	}
	
	
}
