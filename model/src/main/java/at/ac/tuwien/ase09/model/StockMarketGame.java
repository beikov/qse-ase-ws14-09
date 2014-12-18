package at.ac.tuwien.ase09.model;

import java.sql.Blob;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class StockMarketGame extends BaseEntity<Long> {
	private static final long serialVersionUID = 1L;

	private String name;
	private Institution owner;
	private Calendar validFrom;
	private Calendar validTo;
	private Calendar registrationFrom;
	private Calendar registrationTo;
	private String text;
	private Blob logo;
	private PortfolioSetting setting;
	
	private Set<ValuePaper> allowedValuePapers = new HashSet<>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@ManyToOne(optional=false, fetch=FetchType.LAZY)
	public Institution getOwner() {
		return owner;
	}

	public void setOwner(Institution owner) {
		this.owner = owner;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Calendar getValidFrom() {
		return validFrom;
	}

	public void setValidFrom(Calendar validFrom) {
		this.validFrom = validFrom;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Calendar getValidTo() {
		return validTo;
	}

	public void setValidTo(Calendar validTo) {
		this.validTo = validTo;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Calendar getRegistrationFrom() {
		return registrationFrom;
	}

	public void setRegistrationFrom(Calendar registrationFrom) {
		this.registrationFrom = registrationFrom;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Calendar getRegistrationTo() {
		return registrationTo;
	}

	public void setRegistrationTo(Calendar registrationTo) {
		this.registrationTo = registrationTo;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Blob getLogo() {
		return logo;
	}

	public void setLogo(Blob logo) {
		this.logo = logo;
	}

	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name = "startCapital.currency", column = @Column(name="portfolioSetting_startCapital_currency")),
		@AttributeOverride(name = "orderFee.currency", column = @Column(name="portfolioSetting_orderFee_currency")),
		@AttributeOverride(name = "portfolioFee.currency", column = @Column(name="portfolioSetting_portfolioFee_currency")),
		@AttributeOverride(name = "startCapital.value", column = @Column(name="portfolioSetting_startCapital_value")),
		@AttributeOverride(name = "orderFee.value", column = @Column(name="portfolioSetting_orderFee_value")),
		@AttributeOverride(name = "portfolioFee.value", column = @Column(name="portfolioSetting_portfolioFee_value"))
	})
	public PortfolioSetting getSetting() {
		return setting;
	}

	public void setSetting(PortfolioSetting setting) {
		this.setting = setting;
	}

	@ManyToMany
	public Set<ValuePaper> getAllowedValuePapers() {
		return allowedValuePapers;
	}

	public void setAllowedValuePapers(Set<ValuePaper> allowedValuePapers) {
		this.allowedValuePapers = allowedValuePapers;
	}
	
}
