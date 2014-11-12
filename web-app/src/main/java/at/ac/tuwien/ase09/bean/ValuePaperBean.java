package at.ac.tuwien.ase09.bean;

import java.io.Serializable;

import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import at.ac.tuwien.ase09.model.ValuePaperPriceEntry;
import at.ac.tuwien.ase09.service.ValuePaperPriceEntryService;;

@Named
@ViewScoped
public class ValuePaperBean implements Serializable{

	private static final long serialVersionUID = 1L;

	@Inject
	private ValuePaperPriceEntryService valuePaperPriceEntryService;

	public ValuePaperPriceEntry getLastPriceEntry() {
		return valuePaperPriceEntryService.getLastPriceEntry("AT0000911805");
	}

}
