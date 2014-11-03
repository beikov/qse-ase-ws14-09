package at.ac.tuwien.ase09.model;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;

import at.ac.tuwien.ase09.model.order.Order;
import at.ac.tuwien.ase09.model.transaction.TransactionEntry;

@Entity
public class Portfolio extends BaseEntity<Long> {

	private Long version;
	private String name;
	private User owner;
	private Calendar created;
	private Money currentCapital;
	private StockMarketGame game;
	private PortfolioSetting setting = new PortfolioSetting();
	private PortfolioVisibility visibility = new PortfolioVisibility();
	
	private Set<ValuePaper> valuePapers = new HashSet<>();
	private Set<TransactionEntry> transactionEntries = new HashSet<>();
	private Set<Order> orders = new HashSet<>();
	private Set<User> followers = new HashSet<>();
	
}
