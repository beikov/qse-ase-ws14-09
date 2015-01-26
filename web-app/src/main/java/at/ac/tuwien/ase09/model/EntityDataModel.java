package at.ac.tuwien.ase09.model;

import java.io.Serializable;
import java.util.List;

import javax.faces.model.ListDataModel;

import org.primefaces.model.SelectableDataModel;

public class EntityDataModel<T extends BaseEntity<? extends Serializable>> extends ListDataModel<T> implements SelectableDataModel<T> {

	public EntityDataModel() {
		super();
	}

	public EntityDataModel(List<T> list) {
		super(list);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<T> getWrappedData() {
		return (List<T>) super.getWrappedData();
	}

	@Override
	public Object getRowKey(T object) {
		if (object == null || object.getId() == null) {
			return null;
		}
		
		return object.getId().toString();
	}

	@Override
	public T getRowData(String rowKey) {
		for (T entity : getWrappedData()) {
			if (entity.getId().toString().equals(rowKey)) {
				return entity;
			}
		}
		
		return null;
	}

}
