package at.ac.tuwien.ase09.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceContext;

import org.hibernate.Transaction;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.DateAxis;
import org.primefaces.model.chart.LineChartModel;
import org.primefaces.model.chart.LineChartSeries;

import at.ac.tuwien.ase09.exception.EntityNotFoundException;
import at.ac.tuwien.ase09.model.Fund;
import at.ac.tuwien.ase09.model.Stock;
import at.ac.tuwien.ase09.model.StockBond;
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
	
	@PersistenceContext
	private EntityManager em;

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
		this.valuePaperAttributes = new LinkedHashMap<String, String>();

		if(valuePaper.getType() == ValuePaperType.STOCK){

			Stock s = (Stock)valuePaper;

			this.valuePaperAttributes.put("Währung:", s.getCurrency().getCurrencyCode());
			this.valuePaperAttributes.put("Index:", s.getIndex());
			this.valuePaperAttributes.put("Historische Preise:", s.getHistoricPricesPageUrl());
			this.valuePaperAttributes.put("Börse-Zertifikate:", s.getBoerseCertificatePageUrl());
			this.valuePaperAttributes.put("Finanzen-Zertifikate:", s.getFinanzenCertificatePageUrl());
		}
		
		if(valuePaper.getType() == ValuePaperType.BOND){

			StockBond sb = (StockBond)valuePaper;
			Stock baseStock = sb.getBaseStock();
			
			this.valuePaperAttributes.put("URL der historischen Preise:", sb.getHistoricPricesPageUrl());
			
			if(baseStock != null){
				
				this.valuePaperAttributes.put("Bezeichnung (Basis-Aktie):", baseStock.getName());
				this.valuePaperAttributes.put("ISIN (Basis-Aktie):", baseStock.getIsin());
				this.valuePaperAttributes.put("Typ (Basis-Aktie):", baseStock.getType().toString());
				this.valuePaperAttributes.put("Aktueller Kurs (Basis-Aktie):", valuePaperPriceEntryService.getLastPriceEntry(baseStock.getIsin()).getPrice().toString());
				this.valuePaperAttributes.put("Währung (Basis-Aktie):", baseStock.getCurrency().getCurrencyCode());
				this.valuePaperAttributes.put("Index (Basis-Aktie):", baseStock.getIndex());
				this.valuePaperAttributes.put("Details (Basis-Aktie):", baseStock.getDetailUrl());
				this.valuePaperAttributes.put("Historische Preise (Basis-Aktie):", baseStock.getHistoricPricesPageUrl());
				this.valuePaperAttributes.put("Börse-Zertifikate (Basis-Aktie):", baseStock.getBoerseCertificatePageUrl());
				this.valuePaperAttributes.put("Finanzen-Zertifikate (Basis-Aktie):", baseStock.getFinanzenCertificatePageUrl());	
			}

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

