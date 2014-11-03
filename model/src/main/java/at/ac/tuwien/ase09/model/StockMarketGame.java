package at.ac.tuwien.ase09.model;

import java.sql.Blob;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;

@Entity
public class StockMarketGame extends BaseEntity<Long> {

	private String name;
	private Institution owner;
	private Calendar validFrom;
	private Calendar validTo;
	private Calendar registrationFrom;
	private Calendar registrationTo;
	private String text;
	private Blob logo;
	private PortfolioSetting setting;
	
	private Set<ValuePaperEntry> allowedValuePapers = new HashSet<>();
	
}
