package at.ac.tuwien.ase09.model.transaction;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import at.ac.tuwien.ase09.model.ValuePaper;

@Entity
@DiscriminatorValue(TransactionType.TYPE_PAYOUT)
public class PayoutTransactionEntry extends TransactionEntry {
	private static final long serialVersionUID = 1L;

	private ValuePaper valuePaper;
	
	@Override
	@Transient
	public TransactionType getType() {
		return TransactionType.PAYOUT;
	}

	@ManyToOne(optional=false, fetch=FetchType.LAZY)
	public ValuePaper getValuePaper() {
		return valuePaper;
	}

	public void setValuePaper(ValuePaper valuePaper) {
		this.valuePaper = valuePaper;
	}
	
}
