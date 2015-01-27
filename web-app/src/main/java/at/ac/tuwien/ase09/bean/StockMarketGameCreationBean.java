package at.ac.tuwien.ase09.bean;

import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.rowset.serial.SerialBlob;

import org.apache.commons.io.IOUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.TransferEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.DualListModel;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import at.ac.tuwien.ase09.context.UserAccount;
import at.ac.tuwien.ase09.context.WebUserContext;
import at.ac.tuwien.ase09.data.InstitutionDataAccess;
import at.ac.tuwien.ase09.data.StockMarketGameDataAccess;
import at.ac.tuwien.ase09.data.ValuePaperScreenerAccess;
import at.ac.tuwien.ase09.exception.AppException;
import at.ac.tuwien.ase09.exception.EntityNotFoundException;
import at.ac.tuwien.ase09.model.Fund;
import at.ac.tuwien.ase09.model.Institution;
import at.ac.tuwien.ase09.model.Money;
import at.ac.tuwien.ase09.model.PortfolioSetting;
import at.ac.tuwien.ase09.model.Stock;
import at.ac.tuwien.ase09.model.StockBond;
import at.ac.tuwien.ase09.model.StockMarketGame;
import at.ac.tuwien.ase09.model.ValuePaper;
import at.ac.tuwien.ase09.model.ValuePaperType;
import at.ac.tuwien.ase09.service.StockMarketGameService;

import java.io.IOException;
import java.io.Serializable;

@Named
@ViewScoped
public class StockMarketGameCreationBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private WebUserContext userContext;

	@Inject
	private StockMarketGameDataAccess stockMarketGameDataAccess;

	@Inject
	private InstitutionDataAccess institutionDataAccess;

	@Inject
	private StockMarketGameService stockMarketGameService;

	@Inject
	private ValuePaperScreenerAccess valuePaperScreenerDataAccess;

	private Long stockMarketGameId;

	private StockMarketGame stockMarketGame;

	private Institution userInstitution;

	//StockMarketGame-Attributes
	private String name;
	private Date validFrom;
	private Date validTo;
	private Date registrationFrom;
	private Date registrationTo;
	private String text;
	private Blob logo;

	//Portfolio-Setting-Attributes
	private BigDecimal startCapital;
	private BigDecimal orderFee;
	private BigDecimal portfolioFee;
	private BigDecimal capitalReturnTax;

	//Allowed ValuePapers-Attributes
	private ValuePaper searchValuePaper;
	private ValuePaperType valuePaperType;
	private Boolean isTypeSpecificated = false;
	private String valuePaperName, valuePaperCode, valuePaperCountry, valuePaperCurrencyCode, valuePaperIndex;

	private DualListModel<ValuePaper> allowedValuePapersListModel;
	private List<ValuePaper> allowedValuePapersSource;
	private List<ValuePaper> allowedValuePapersTarget;

	private List<ValuePaper> allowedValuePapersFinal;

	//private boolean initCalled = false;



	public DualListModel<ValuePaper> getAllowedValuePapersListModel() {
		return allowedValuePapersListModel;
	}
	public void setAllowedValuePapersListModel(
			DualListModel<ValuePaper> allowedValuePapersListModel) {
		this.allowedValuePapersListModel = allowedValuePapersListModel;
	}
	public ValuePaper getSearchValuePaper() {
		return searchValuePaper;
	}
	public void setSearchValuePaper(ValuePaper searchValuePaper) {
		this.searchValuePaper = searchValuePaper;
	}
	public List<ValuePaper> getAllowedValuePapersSource() {
		return allowedValuePapersSource;
	}
	public void setAllowedValuePapersSource(
			List<ValuePaper> allowedValuePapersSource) {
		this.allowedValuePapersSource = allowedValuePapersSource;
	}
	public List<ValuePaper> getAllowedValuePapersTarget() {
		return allowedValuePapersTarget;
	}
	public void setAllowedValuePapersTarget(
			List<ValuePaper> allowedValuePapersTarget) {
		this.allowedValuePapersTarget = allowedValuePapersTarget;
	}
	public String getValuePaperIndex() {
		return valuePaperIndex;
	}
	public void setValuePaperIndex(String valuePaperIndex) {
		this.valuePaperIndex = valuePaperIndex;
	}
	public ValuePaperType getValuePaperType() {
		return valuePaperType;
	}
	public void setValuePaperType(ValuePaperType valuePaperType) {
		this.valuePaperType = valuePaperType;
	}
	public Boolean getIsTypeSpecificated() {
		return isTypeSpecificated;
	}
	public void setIsTypeSpecificated(Boolean isTypeSpecificated) {
		this.isTypeSpecificated = isTypeSpecificated;
	}
	public String getValuePaperName() {
		return valuePaperName;
	}
	public void setValuePaperName(String valuePaperName) {
		this.valuePaperName = valuePaperName;
	}
	public String getValuePaperCode() {
		return valuePaperCode;
	}
	public void setValuePaperCode(String valuePaperCode) {
		this.valuePaperCode = valuePaperCode;
	}
	public String getValuePaperCountry() {
		return valuePaperCountry;
	}
	public void setValuePaperCountry(String valuePaperCountry) {
		this.valuePaperCountry = valuePaperCountry;
	}
	public String getValuePaperCurrencyCode() {
		return valuePaperCurrencyCode;
	}
	public void setValuePaperCurrencyCode(String valuePaperCurrencyCode) {
		this.valuePaperCurrencyCode = valuePaperCurrencyCode;
	}
	public Long getStockMarketGameId() {
		return stockMarketGameId;
	}
	public void setStockMarketGameId(Long stockMarketGameId) {
		this.stockMarketGameId = stockMarketGameId;
	}
	public StockMarketGame getStockMarketGame() {
		return stockMarketGame;
	}
	public void setStockMarketGame(StockMarketGame stockMarketGame) {
		this.stockMarketGame = stockMarketGame;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Date getValidFrom() {
		return validFrom;
	}
	public void setValidFrom(Date validFrom) {
		this.validFrom = validFrom;
	}
	public Date getValidTo() {
		return validTo;
	}
	public void setValidTo(Date validTo) {
		this.validTo = validTo;
	}
	public Date getRegistrationFrom() {
		return registrationFrom;
	}
	public void setRegistrationFrom(Date registrationFrom) {
		this.registrationFrom = registrationFrom;
	}
	public Date getRegistrationTo() {
		return registrationTo;
	}
	public void setRegistrationTo(Date registrationTo) {
		this.registrationTo = registrationTo;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public Blob getLogo() {
		return logo;
	}
	public void setLogo(Blob logo) {
		this.logo = logo;
	}
	public BigDecimal getStartCapital() {
		return startCapital;
	}
	public void setStartCapital(BigDecimal startCapital) {
		this.startCapital = startCapital;
	}
	public BigDecimal getOrderFee() {
		return orderFee;
	}
	public void setOrderFee(BigDecimal orderFee) {
		this.orderFee = orderFee;
	}
	public BigDecimal getPortfolioFee() {
		return portfolioFee;
	}
	public void setPortfolioFee(BigDecimal portfolioFee) {
		this.portfolioFee = portfolioFee;
	}
	public BigDecimal getCapitalReturnTax() {
		return capitalReturnTax;
	}
	public void setCapitalReturnTax(BigDecimal capitalReturnTax) {
		this.capitalReturnTax = capitalReturnTax;
	}
	public Institution getUserInstitution() {
		return userInstitution;
	}
	public void setUserInstitution(Institution userInstitution) {
		this.userInstitution = userInstitution;
	}



	public void init() throws IOException {

		UserAccount loggedInUser = userContext.getUser();

		if(loggedInUser != null){
			try{
				userInstitution = institutionDataAccess.getByAdmin(loggedInUser.getUsername());
			}
			catch(EntityNotFoundException e){			
				FacesContext context = FacesContext.getCurrentInstance();
				context.getExternalContext().responseSendError(403, "Börsenspiele können nur von Institutionen erstellt/bearbeitet werden.");
				context.responseComplete();
				return;
			}
		}

		loadStockMarketGame();
		
		if(stockMarketGame != null){
			if(!isStockMarketGameAdmin()){
				FacesContext context = FacesContext.getCurrentInstance();
				context.getExternalContext().responseSendError(403, "Dieses Börsenspiel kann nur von der Institution, die das Börsenspiel erstellt hat, bearbeitet werden.");
				context.responseComplete();
				return;
			}
		}

		allowedValuePapersSource = new ArrayList<ValuePaper>();
		allowedValuePapersTarget = new ArrayList<ValuePaper>();

		allowedValuePapersFinal = new ArrayList<ValuePaper>();

		allowedValuePapersListModel = new DualListModel<ValuePaper>(allowedValuePapersSource, allowedValuePapersTarget);

		if(stockMarketGame != null){

			name = stockMarketGame.getName();
			validFrom = stockMarketGame.getValidFrom().getTime();
			validTo = stockMarketGame.getValidTo().getTime();
			registrationFrom = stockMarketGame.getRegistrationFrom().getTime();
			registrationTo = stockMarketGame.getRegistrationTo().getTime();
			text = stockMarketGame.getText();
			logo = stockMarketGame.getLogo();

			startCapital = stockMarketGame.getSetting().getStartCapital().getValue();
			orderFee = stockMarketGame.getSetting().getOrderFee().getValue();
			portfolioFee = stockMarketGame.getSetting().getPortfolioFee().getValue();
			capitalReturnTax = stockMarketGame.getSetting().getCapitalReturnTax();

			allowedValuePapersFinal = new ArrayList<ValuePaper>(stockMarketGame.getAllowedValuePapers());
			allowedValuePapersListModel.setTarget(new ArrayList<ValuePaper>(stockMarketGame.getAllowedValuePapers()));
		}
		else{

			orderFee = new BigDecimal(0);
			portfolioFee = new BigDecimal(0);
			capitalReturnTax = new BigDecimal(0);
		}		

	}

	public boolean isAllowedToEditStockMarketGameSettings(){
		if(stockMarketGame != null){
			if(stockMarketGame.getValidFrom().after(StockMarketGameCreationBean.dateToCalendar(new Date()))){
				return true;
			}
			else{
				return false;
			}
		}
		else{
			return true;
		}
	}
	
	public boolean isStockMarketGameAdmin(){	
		if(stockMarketGame != null && stockMarketGame.getOwner() != null){
			return stockMarketGame.getOwner().getId().equals(userInstitution.getId());
		}
		return true;
	}

	public void loadStockMarketGame(){
		if(stockMarketGameId != null){
			try{
				stockMarketGame = stockMarketGameDataAccess.getStockMarketGameByID(stockMarketGameId);
			}
			catch(EntityNotFoundException e){}
		}
	}

	public ValuePaperType[] getValuePaperTypes(){
		return ValuePaperType.values();
	}

	public List<Currency> getUsedCurrencies() {
		return valuePaperScreenerDataAccess.getUsedCurrencyCodes();
	}
	public List<String> getUsedIndexes() {
		return valuePaperScreenerDataAccess.getUsedIndexes();
	}
	public List<String> getUsedCountries(){
		
		List<String> countries = valuePaperScreenerDataAccess.getUsedCountries();
		
		if(countries.contains(null)){
			countries.remove(null);
		}
		
		return countries;
	}

	public void createStockMarketGame(){
		
		if(!isAllowedToEditStockMarketGameSettings()){
			return;
		}

		if( startCapital == null){
			startCapital = new BigDecimal(0);
		}else if( startCapital.compareTo(new BigDecimal(0)) == -1 ){
			FacesMessage facesMessage = new FacesMessage("Fehler: Startkapital muss größer als 0 sein!");
			FacesContext.getCurrentInstance().addMessage("stockmarketgame_create:startCapital", facesMessage);
			return;
		}

		if( orderFee.compareTo(new BigDecimal(0)) == -1){
			FacesMessage facesMessage = new FacesMessage("Fehler: Ordergebühr muss größer gleich 0  sein!");
			FacesContext.getCurrentInstance().addMessage("stockmarketgame_create:orderFee", facesMessage);
			return;
		}

		if( portfolioFee.compareTo(new BigDecimal(0)) == -1){
			FacesMessage facesMessage = new FacesMessage("Fehler: Portfoliogebühr muss größer gleich 0  sein!");
			FacesContext.getCurrentInstance().addMessage("stockmarketgame_create:portfolioFee", facesMessage);
			return;
		}

		if( capitalReturnTax.compareTo(new BigDecimal(0)) == -1 || capitalReturnTax.compareTo(new BigDecimal(99)) == 1){
			FacesMessage facesMessage = new FacesMessage("Fehler: Kapitalertragssteuer muss zwischen 0% und 99% liegen!");
			FacesContext.getCurrentInstance().addMessage("stockmarketgame_create:capitalReturnTax", facesMessage);
			return;
		}
		
		if(new Date(registrationFrom.getTime()+1).after(registrationTo)){
			FacesMessage facesMessage = new FacesMessage("Fehler: Registrierungsbeginndatum muss vor Registrierungsenddatum liegen!");
			FacesContext.getCurrentInstance().addMessage("stockmarketgame_create:registrationFrom", facesMessage);
			return;
		}
		
		if(new Date(validFrom.getTime()+1).after(validTo)){
			FacesMessage facesMessage = new FacesMessage("Fehler: Börsenspielbeginndatum muss vor Börsenspielenddatum liegen!");
			FacesContext.getCurrentInstance().addMessage("stockmarketgame_create:validFrom", facesMessage);
			return;
		}

		if(new Date(registrationTo.getTime()+1).after(validFrom)){
			FacesMessage facesMessage = new FacesMessage("Fehler: Registrierungsenddatum muss vor Börsenspielbeginndatum liegen!");
			FacesContext.getCurrentInstance().addMessage("stockmarketgame_create:registrationTo", facesMessage);
			return;
		}
		
		
		if(stockMarketGame == null){
			stockMarketGame = new StockMarketGame();
		}

		stockMarketGame.setName(name);
		stockMarketGame.setText(text);
		stockMarketGame.setLogo(logo);
		stockMarketGame.setOwner(userInstitution);
		stockMarketGame.setValidFrom(StockMarketGameCreationBean.dateToCalendar(validFrom));
		stockMarketGame.setValidTo(StockMarketGameCreationBean.dateToCalendar(validTo));
		stockMarketGame.setRegistrationFrom(StockMarketGameCreationBean.dateToCalendar(registrationFrom));
		stockMarketGame.setRegistrationTo(StockMarketGameCreationBean.dateToCalendar(registrationTo));

		stockMarketGame.setAllowedValuePapers(new LinkedHashSet<ValuePaper>(allowedValuePapersFinal));

		PortfolioSetting portfolioSetting = new PortfolioSetting();

		portfolioSetting.setStartCapital(new Money(startCapital, Currency.getInstance("EUR")));
		portfolioSetting.setOrderFee(new Money(orderFee, Currency.getInstance("EUR")));
		portfolioSetting.setPortfolioFee(new Money(portfolioFee, Currency.getInstance("EUR")));
		portfolioSetting.setCapitalReturnTax(capitalReturnTax);

		stockMarketGame.setSetting(portfolioSetting);

		try{

			stockMarketGameService.saveStockMarketGame(stockMarketGame);

			FacesMessage message = new FacesMessage("Börsenspiel erfolgreich gespeichert");
			FacesContext.getCurrentInstance().addMessage(null, message);

			FacesContext.getCurrentInstance().getExternalContext().redirect("stockmarketgameview.xhtml?gameId="+stockMarketGame.getId());
		}
		catch (Exception e) {

			FacesMessage message = new FacesMessage("Fehler beim Speichern des Börsenspieles");
			FacesContext.getCurrentInstance().addMessage(null, message);

			e.printStackTrace();
		}

	}

	public void handleLogoUpload(FileUploadEvent event) {

		try {
			UploadedFile logoFile = event.getFile();
			byte[] uploadedLogo = IOUtils.toByteArray(logoFile.getInputstream());
			Blob newLogo = new SerialBlob(uploadedLogo);

			logo = newLogo;

			FacesMessage message = new FacesMessage("Logo: " + logoFile.getFileName() + " erfolgreich hochgeladen");
			FacesContext.getCurrentInstance().addMessage(null, message);

		} catch (Exception e) {
			FacesMessage message = new FacesMessage("Fehler beim Speichern des neuen Logos");
			FacesContext.getCurrentInstance().addMessage(null, message);

			e.printStackTrace();
		}
	}


	public StreamedContent getImage() {

		FacesContext context = FacesContext.getCurrentInstance();

		if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {

			// So, we're rendering the HTML. Return a stub StreamedContent so that it will generate right URL.
			return new DefaultStreamedContent();
		}

		else {

			// So, browser is requesting the image. Return a real StreamedContent with the image bytes.
			String id = context.getExternalContext().getRequestParameterMap().get("id");
			StockMarketGame smg = stockMarketGameDataAccess.getStockMarketGameByID(Long.parseLong(id));

			try {
				return new DefaultStreamedContent(smg.getLogo().getBinaryStream());
				//return new DefaultStreamedContent(i.getLogo().getBinaryStream());

			} catch (SQLException e) {
				e.printStackTrace();
				throw new AppException(e.getMessage());
			}
		}
	}

	public void searchValuePapers(){
		if(valuePaperType != null){
			isTypeSpecificated = true;

			if(valuePaperType.equals(ValuePaperType.BOND)){
				searchValuePaper = new StockBond();
			}

			else if(valuePaperType.equals(ValuePaperType.STOCK)){
				searchValuePaper = new Stock();
			}

			else{
				searchValuePaper = new Fund();
			}

		}
		else{
			isTypeSpecificated=false;
			searchValuePaper = new Stock();
		}

		if (searchValuePaper.getType() == ValuePaperType.STOCK) {
			((Stock)searchValuePaper).setCountry(valuePaperCountry);
			((Stock)searchValuePaper).setIndex(valuePaperIndex);
		}

		searchValuePaper.setCode(valuePaperCode);
		searchValuePaper.setName(valuePaperName);


		if((searchValuePaper.getType() == ValuePaperType.STOCK || searchValuePaper.getType() == ValuePaperType.FUND) && valuePaperCurrencyCode != null && !valuePaperCurrencyCode.isEmpty()) {
			try{
				((Stock)searchValuePaper).setCurrency(Currency.getInstance(valuePaperCurrencyCode));
			}

			catch(IllegalArgumentException e){
				FacesMessage facesMessage = new FacesMessage("Error: Currency-Code does not exist");
				FacesContext.getCurrentInstance().addMessage("searchValuePapers:currency", facesMessage);
			}
		}


		allowedValuePapersTarget = allowedValuePapersListModel.getTarget();

		allowedValuePapersSource = valuePaperScreenerDataAccess.findByValuePaper(searchValuePaper, isTypeSpecificated);

		allowedValuePapersSource.removeAll(allowedValuePapersTarget);

		allowedValuePapersListModel.setSource(allowedValuePapersSource);
		allowedValuePapersListModel.setTarget(allowedValuePapersTarget);
	}

	public void onTransfer(TransferEvent event) {	
		allowedValuePapersFinal = allowedValuePapersListModel.getTarget();
	}  

	public static Calendar dateToCalendar(Date date){ 
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal;
	}

}
