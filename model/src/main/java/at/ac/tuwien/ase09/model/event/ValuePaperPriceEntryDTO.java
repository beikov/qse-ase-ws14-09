package at.ac.tuwien.ase09.model.event;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;

import at.ac.tuwien.ase09.model.ValuePaperPriceEntry;

public class ValuePaperPriceEntryDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private Long valuePaperId;
	private Calendar created;
	private BigDecimal price;
	
	public ValuePaperPriceEntryDTO(ValuePaperPriceEntry entry) {
		this.id = entry.getId();
		this.valuePaperId = entry.getValuePaper().getId();
		this.created = entry.getCreated();
		this.price = entry.getPrice();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getValuePaperId() {
		return valuePaperId;
	}

	public void setValuePaperId(Long valuePaperId) {
		this.valuePaperId = valuePaperId;
	}

	public Calendar getCreated() {
		return created;
	}

	public void setCreated(Calendar created) {
		this.created = created;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ValuePaperPriceEntryDTO other = (ValuePaperPriceEntryDTO) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}
