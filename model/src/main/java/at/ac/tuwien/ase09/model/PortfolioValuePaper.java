package at.ac.tuwien.ase09.model;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "portfolio_valuepaper")
public class PortfolioValuePaper extends BaseEntity<Long> {

	private static final long serialVersionUID = 1L;
	
	private int volume;
	
	private Portfolio portfolio;
	
	private ValuePaper valuePaper;


	public int getVolume() {
		return volume;
	}

	public void setVolume(int volume) {
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
	
	
}