package at.ac.tuwien.ase09.model;

import java.io.Serializable;

import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "portfolio_valuepaper")
/*@AssociationOverrides({
	@AssociationOverride(name = "pk.portfolio", joinColumns = @JoinColumn(name = "PORTFOLIO_ID")),
	@AssociationOverride(name = "pk.valuePaper", joinColumns = @JoinColumn(name = "VALUEPAPER_ID"))
})*/
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
