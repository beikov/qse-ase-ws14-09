package at.ac.tuwien.ase09.model;

import java.io.Serializable;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class BaseEntity<I> implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private I id;

	public BaseEntity() {
		super();
	}

	@Id
	@GeneratedValue
	public I getId() {
		return id;
	}

	public void setId(I id) {
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
		if (getNoProxyClass(getClass()) != getNoProxyClass(obj.getClass()))
			return false;
		BaseEntity<?> other = (BaseEntity<?>) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	private static Class<?> getNoProxyClass(Class<?> clazz) {
		while (clazz.getName().contains("javassist")) {
			clazz = clazz.getSuperclass();
		}
		
		return clazz;
	}
	
	
}
