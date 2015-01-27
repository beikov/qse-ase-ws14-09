package at.ac.tuwien.ase09.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;

import at.ac.tuwien.ase09.data.ValuePaperDataAccess;
import at.ac.tuwien.ase09.model.ValuePaper;

@FacesConverter(value = "valuePaperConverter")
public class ValuePaperConverter implements Converter {
	
	@Inject
	private ValuePaperDataAccess valuePaperDataAccess;
	
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (value == null) 
			return null;
		
		return valuePaperDataAccess.getValuePaperById(Long.valueOf(value), null);
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if(value == null){
			return null;
		}
		return ((ValuePaper) value).getId().toString();
	}
}
