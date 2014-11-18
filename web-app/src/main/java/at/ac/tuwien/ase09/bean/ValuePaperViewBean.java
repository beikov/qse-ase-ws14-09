package at.ac.tuwien.ase09.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.DateAxis;
import org.primefaces.model.chart.LineChartModel;
import org.primefaces.model.chart.LineChartSeries;

import at.ac.tuwien.ase09.exception.EntityNotFoundException;
import at.ac.tuwien.ase09.model.Bond;
import at.ac.tuwien.ase09.model.Fund;
import at.ac.tuwien.ase09.model.Stock;
import at.ac.tuwien.ase09.model.ValuePaper;
import at.ac.tuwien.ase09.model.ValuePaperPriceEntry;
import at.ac.tuwien.ase09.model.ValuePaperHistoryEntry;
import at.ac.tuwien.ase09.model.ValuePaperType;
import at.ac.tuwien.ase09.service.ValuePaperPriceEntryService;
import at.ac.tuwien.ase09.service.ValuePaperService;

@Named
@RequestScoped
public class ValuePaperViewBean implements Serializable{

	private static final long serialVersionUID = 1L;

	private ValuePaper valuePaper = null;

	private String valuePaperIsin;

	private LineChartModel valuePaperHistoricPriceLineChartModel = null;

	private Map<String, String> valuePaperAttributes = null;

	@Inject
	private ValuePaperPriceEntryService valuePaperPriceEntryService;

	@Inject
	private ValuePaperService valuePaperService;

	public void init() {
		loadValuePaper(valuePaperIsin);

		if(this.valuePaper != null){
			loadValuePaperAttributes();
			createLineChartModels();
		}
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

	public LineChartModel getValuePaperHistoricPriceLineChartModel() {
		return valuePaperHistoricPriceLineChartModel;
	}

	public void setValuePaperHistoricPriceLineChartModel(
			LineChartModel valuePaperHistoricPriceLineChartModel) {
		this.valuePaperHistoricPriceLineChartModel = valuePaperHistoricPriceLineChartModel;
	}

	public Map<String, String> getValuePaperAttributes() {
		return valuePaperAttributes;
	}

	public void setValuePaperAttributes(Map<String, String> valuePaperAttributes) {
		this.valuePaperAttributes = valuePaperAttributes;
	}

	public ValuePaperPriceEntry getLastPriceEntry() {

		try{
			return valuePaperPriceEntryService.getLastPriceEntry(valuePaper.getIsin());
		}
		catch(EntityNotFoundException e){
			return null;
		}
	}

	private void loadValuePaper(String valuePaperIsin) {

		try{
			this.valuePaper = valuePaperService.getValuePaperByIsin(valuePaperIsin);
		}
		catch(EntityNotFoundException e){
			this.valuePaper = null;
		}

	}

	private void loadValuePaperAttributes() {
		this.valuePaperAttributes = new TreeMap<String, String>();

		if(valuePaper.getType() == ValuePaperType.STOCK){

			Stock s = (Stock)valuePaper;

			this.valuePaperAttributes.put("Börse-CertificatePageUrl", s.getBoerseCertificatePageUrl());
			this.valuePaperAttributes.put("Finanzen-CertificatePageUrl", s.getFinanzenCertificatePageUrl());
			this.valuePaperAttributes.put("Index", s.getIndex());
		}
		if(valuePaper.getType() == ValuePaperType.BOND){

			Bond b = (Bond)valuePaper;

		}
		if(valuePaper.getType() == ValuePaperType.FUND){

			Fund f = (Fund)valuePaper;

		}
	}

	private void createLineChartModels(){

		valuePaperHistoricPriceLineChartModel = new LineChartModel();
		LineChartSeries series1 = new LineChartSeries();
		series1.setLabel(valuePaper.getName());

		try{

			List<ValuePaperHistoryEntry> historyPriceList = valuePaperPriceEntryService.getValuePaperPriceHistoryEntries(valuePaper.getIsin());

			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

			for (ValuePaperHistoryEntry vphe : historyPriceList) {
				String date = format.format(vphe.getDate().getTime());
				BigDecimal value = (vphe.getDayHighPrice().add(vphe.getDayLowPrice())).divide(new BigDecimal(2));
				series1.set(date, value);
			}

			ValuePaperPriceEntry currentPriceEntry = valuePaperPriceEntryService.getLastPriceEntry(valuePaper.getIsin());

			series1.set(format.format(currentPriceEntry.getCreated().getTime()), currentPriceEntry.getPrice());

			valuePaperHistoricPriceLineChartModel.addSeries(series1);
			valuePaperHistoricPriceLineChartModel.setTitle("Kursverlauf");
			valuePaperHistoricPriceLineChartModel.setZoom(true);
			valuePaperHistoricPriceLineChartModel.getAxis(AxisType.Y).setLabel("Wert");

			DateAxis axis = new DateAxis("Datum");
			axis.setTickAngle(-50);
			axis.setTickInterval("0");

			currentPriceEntry.getCreated().add(Calendar.DATE, 1);

			axis.setMax(format.format(currentPriceEntry.getCreated().getTime()));
			axis.setTickFormat("%b %#d, %y");

			valuePaperHistoricPriceLineChartModel.getAxes().put(AxisType.X, axis);

		}
		catch(EntityNotFoundException e){
			valuePaperHistoricPriceLineChartModel = null;
		}
	}

}

