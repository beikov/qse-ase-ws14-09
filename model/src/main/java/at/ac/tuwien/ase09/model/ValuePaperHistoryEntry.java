package at.ac.tuwien.ase09.model;

import java.math.BigDecimal;
import java.util.Calendar;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class ValuePaperHistoryEntry extends BaseEntity<Long> {
	private static final long serialVersionUID = 1L;

	private ValuePaper valuePaper;
	private Calendar date;
	private BigDecimal dayLowPrice;
	private BigDecimal dayHighPrice;
	private BigDecimal openingPrice;
	private BigDecimal closingPrice;
	private Integer marketVolume;
	
	@ManyToOne(optional=false, fetch=FetchType.LAZY)
	public ValuePaper getValuePaper() {
		return valuePaper;
	}
	public void setValuePaper(ValuePaper valuePaper) {
		this.valuePaper = valuePaper;
	}
	@Temporal(TemporalType.DATE)
	public Calendar getDate() {
		return date;
	}
	public void setDate(Calendar date) {
		this.date = date;
	}
	public BigDecimal getDayLowPrice() {
		return dayLowPrice;
	}
	public void setDayLowPrice(BigDecimal dayLowPrice) {
		this.dayLowPrice = dayLowPrice;
	}
	public BigDecimal getDayHighPrice() {
		return dayHighPrice;
	}
	public void setDayHighPrice(BigDecimal dayHighPrice) {
		this.dayHighPrice = dayHighPrice;
	}
	public BigDecimal getOpeningPrice() {
		return openingPrice;
	}
	public void setOpeningPrice(BigDecimal openingPrice) {
		this.openingPrice = openingPrice;
	}
	public BigDecimal getClosingPrice() {
		return closingPrice;
	}
	public void setClosingPrice(BigDecimal closingPrice) {
		this.closingPrice = closingPrice;
	}
	public Integer getMarketVolume() {
		return marketVolume;
	}
	public void setMarketVolume(Integer marketVolume) {
		this.marketVolume = marketVolume;
	}
}
