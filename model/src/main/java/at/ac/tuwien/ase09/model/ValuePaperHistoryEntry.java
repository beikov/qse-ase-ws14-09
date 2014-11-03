package at.ac.tuwien.ase09.model;

import java.math.BigDecimal;
import java.util.Calendar;

import javax.persistence.Entity;

@Entity
public class ValuePaperHistoryEntry extends BaseEntity<Long> {

	private ValuePaper valuePaper;
	private Calendar date;
	private BigDecimal dayLowPrice;
	private BigDecimal dayHighPrice;
	private BigDecimal beginPrice;
	private BigDecimal endPrice;
	private Integer marketVolume;
}
