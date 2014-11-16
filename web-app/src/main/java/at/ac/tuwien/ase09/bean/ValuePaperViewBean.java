package at.ac.tuwien.ase09.bean;

import java.io.Serializable;

import javax.enterprise.context.RequestScoped;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import at.ac.tuwien.ase09.model.ValuePaper;
import at.ac.tuwien.ase09.model.ValuePaperPriceEntry;
import at.ac.tuwien.ase09.service.ValuePaperPriceEntryService;
import at.ac.tuwien.ase09.service.ValuePaperService;

@Named
@RequestScoped
public class ValuePaperViewBean implements Serializable{

	private static final long serialVersionUID = 1L;

	private ValuePaper valuePaper;

	private String valuePaperIsin;

	@Inject
	private ValuePaperPriceEntryService valuePaperPriceEntryService;
	
	@Inject
	private ValuePaperService valuePaperService;

	public void init() {

		loadValuePaper(valuePaperIsin);

	}
	
	public String getValuePaperIsin() {
		return valuePaperIsin;
	}

	public void setValuePaperIsin(String valuePaperIsin) {
		this.valuePaperIsin = valuePaperIsin;
	}

	public ValuePaper getValuePaper() {
		return valuePaper;
	}

	public void setValuePaper(ValuePaper valuePaper) {
		this.valuePaper = valuePaper;
	}
	
	private void loadValuePaper(String valuePaperIsin) {		
		this.valuePaper = valuePaperService.getValuePaperByIsin(valuePaperIsin);
		//this.valuePaper = valuePaperService.getValuePaperByIsin("AT0000730007");
	}


	public ValuePaperPriceEntry getLastPriceEntry() {
		return valuePaperPriceEntryService.getLastPriceEntry(valuePaper.getIsin());
	}

}
