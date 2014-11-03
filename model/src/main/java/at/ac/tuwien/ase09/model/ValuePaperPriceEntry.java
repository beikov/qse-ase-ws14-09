package at.ac.tuwien.ase09.model;

import java.math.BigDecimal;
import java.util.Calendar;

import javax.persistence.Entity;

@Entity
public class ValuePaperPriceEntry extends BaseEntity<Long> {

	private ValuePaper valuePaper;
	private Calendar created;
	private BigDecimal price;
}
