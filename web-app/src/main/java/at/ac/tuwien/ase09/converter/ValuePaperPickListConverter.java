package at.ac.tuwien.ase09.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import org.primefaces.component.picklist.PickList;
import org.primefaces.model.DualListModel;

import at.ac.tuwien.ase09.model.ValuePaper;

@FacesConverter(value = "valuePaperPickListConverter")
public class ValuePaperPickListConverter implements Converter{

	@Override
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {
		Object ret = null;
		if (arg1 instanceof PickList) {
			Object dualList = ((PickList) arg1).getValue();
			DualListModel<ValuePaper> dl = (DualListModel<ValuePaper>) dualList;
			for (Object o : dl.getSource()) {
				String id = "" + ((ValuePaper) o).getId();
				if (arg2.equals(id)) {
					ret = o;
					break;
				}
			}
			if (ret == null)
				for (Object o : dl.getTarget()) {
					String id = "" + ((ValuePaper) o).getId();
					if (arg2.equals(id)) {
						ret = o;
						break;
					}
				}
		}
		return ret;
	}

	@Override
	public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2) {
		String str = "";
		if (arg2 instanceof ValuePaper) {
			str = "" + ((ValuePaper) arg2).getId();
		}
		return str;
	}

}
