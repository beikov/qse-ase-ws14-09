package at.ac.tuwien.ase09.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "portfolio_valuepaper")
public class PortfolioValuePaper extends BaseEntity<Long> {

	private static final long serialVersionUID = 1L;
	
	private Integer volume;
	
	private Portfolio portfolio;
	
	private ValuePaper valuePaper;
	
	private BigDecimal buyPrice;

	@Column(nullable=false)
	public Integer getVolume() {
		return volume;
	}

	public void setVolume(Integer volume) {
		this.volume = volume;
	}

	@ManyToOne(optional = false)
	@JoinColumn(name="portfolio")
	public Portfolio getPortfolio() {
		return portfolio;
	}

	public void setPortfolio(Portfolio portfolio) {
		this.portfolio = portfolio;
	}

	@ManyToOne(optional = false)
	@JoinColumn(name="valuepaper")
	public ValuePaper getValuePaper() {
		return valuePaper;
	}

	public void setValuePaper(ValuePaper valuePaper) {
		this.valuePaper = valuePaper;
	}

	/**
	 * Price per value paper unit for which this position was bought.
	 * This price is in the currency of the portfolio.
	 */
	@Column(nullable=false)
	public BigDecimal getBuyPrice() {
		return buyPrice;
	}

	public void setBuyPrice(BigDecimal buyPrice) {
		this.buyPrice = buyPrice;
	}

}
