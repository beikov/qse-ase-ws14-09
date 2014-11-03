package at.ac.tuwien.ase09.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class ValuePaperEntry implements Serializable {

	private ValuePaperEntryKey id;

	public ValuePaperEntry() {
		super();
	}

	public ValuePaperEntry(ValuePaperEntryKey id) {
		super();
		this.id = id;
	}

	@Id
	public ValuePaperEntryKey getId() {
		return id;
	}

	public void setId(ValuePaperEntryKey id) {
		this.id = id;
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
		ValuePaperEntry other = (ValuePaperEntry) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
