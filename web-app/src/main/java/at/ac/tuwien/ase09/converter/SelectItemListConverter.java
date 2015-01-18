package at.ac.tuwien.ase09.converter;

import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;

import at.ac.tuwien.ase09.model.BaseEntity;

public class SelectItemListConverter implements Converter {
	
	private final List<SelectItem> list;

	public SelectItemListConverter(List<SelectItem> list) {
		this.list = list;
	}

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (value == null) {
			return null;
		}
		
		for (int i = 0; i < list.size(); i++) {
			final BaseEntity<?> entity = (BaseEntity<?>) list.get(i).getValue();
			if (entity.getId().toString().equals(value)) {
				return entity;
			}
		}
		
		return null;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		Object id = getId(value);
		
		if (id == null) {
			return null;
		} else {
			return id.toString();
		}
	}
	
	private Object getId(Object value) {
		if (value == null) {
			return null;
		} else if (value instanceof BaseEntity<?>) {
			return ((BaseEntity<?>) value).getId();
		} else {
			throw new IllegalArgumentException("Invalid entity [" + value + "] given. Only supporting base entities!");
		}
	}

}
