package at.ac.tuwien.ase09.model;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import at.ac.tuwien.ase09.model.order.Order;
import at.ac.tuwien.ase09.model.transaction.TransactionEntry;
import at.ac.tuwien.ase09.naming.CustomNamingStrategy;

@Entity
public class Portfolio extends BaseEntity<Long> {
	private static final long serialVersionUID = 1L;

	private Long version;
	private String name;
	private User owner;
	private Calendar created;
	private Money currentCapital;
	private StockMarketGame game;
	private PortfolioSetting setting = new PortfolioSetting();
	private PortfolioVisibility visibility = new PortfolioVisibility();

	private Set<PortfolioValuePaper> valuePapers = new HashSet<>();
	private Set<TransactionEntry> transactionEntries = new HashSet<>();
	private Set<Order> orders = new HashSet<>();
	private Set<User> followers = new HashSet<>();

	@Version
	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Calendar getCreated() {
		return created;
	}

	public void setCreated(Calendar created) {
		this.created = created;
	}

	@Embedded
	public Money getCurrentCapital() {
		return currentCapital;
	}

	public void setCurrentCapital(Money currentCapital) {
		this.currentCapital = currentCapital;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	public StockMarketGame getGame() {
		return game;
	}

	public void setGame(StockMarketGame game) {
		this.game = game;
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

	@Embedded
	public PortfolioVisibility getVisibility() {
		return visibility;
	}

	public void setVisibility(PortfolioVisibility visibility) {
		this.visibility = visibility;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "portfolio")
	public Set<PortfolioValuePaper> getValuePapers() {
		return valuePapers;
	}

	public void setValuePapers(Set<PortfolioValuePaper> valuePapers) {
		this.valuePapers = valuePapers;
	}

	@OneToMany(mappedBy = "portfolio")
	public Set<TransactionEntry> getTransactionEntries() {
		return transactionEntries;
	}

	public void setTransactionEntries(Set<TransactionEntry> transactionEntries) {
		this.transactionEntries = transactionEntries;
	}

	@OneToMany(mappedBy = "portfolio")
	public Set<Order> getOrders() {
		return orders;
	}

	public void setOrders(Set<Order> orders) {
		this.orders = orders;
	}

	@ManyToMany
	@JoinTable(name = "user_user", 
		joinColumns = { @JoinColumn(name = "portfolio_id", referencedColumnName = CustomNamingStrategy.COLUMN_PREFIX + "id") }, 
		inverseJoinColumns = { @JoinColumn(name = "user_id", referencedColumnName = CustomNamingStrategy.COLUMN_PREFIX + "id") }
	)
	public Set<User> getFollowers() {
		return followers;
	}

	public void setFollowers(Set<User> followers) {
		this.followers = followers;
	}

}
