package at.ac.tuwien.ase09.bean;

import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.rowset.serial.SerialBlob;

import org.apache.commons.io.IOUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import at.ac.tuwien.ase09.context.WebUserContext;
import at.ac.tuwien.ase09.data.InstitutionDataAccess;
import at.ac.tuwien.ase09.data.StockMarketGameDataAccess;
import at.ac.tuwien.ase09.exception.AppException;
import at.ac.tuwien.ase09.model.Institution;
import at.ac.tuwien.ase09.model.Money;
import at.ac.tuwien.ase09.model.PortfolioSetting;
import at.ac.tuwien.ase09.model.StockMarketGame;
import at.ac.tuwien.ase09.model.User;
import at.ac.tuwien.ase09.service.StockMarketGameService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
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

	private Long stockMarketGameId;

	private StockMarketGame stockMarketGame;

	private User loggedInUser;
	private Institution userInstitution;

	private String name;
	private Date validFrom;
	private Date validTo;
	private Date registrationFrom;
	private Date registrationTo;
	private String text;
	private Blob logo;

	private BigDecimal startCapital;
	private BigDecimal orderFee;
	private BigDecimal portfolioFee;
	private BigDecimal capitalReturnTax;



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
	public User getLoggedInUser() {
		return loggedInUser;
	}
	public void setLoggedInUser(User loggedInUser) {
		this.loggedInUser = loggedInUser;
	}
	public Institution getUserInstitution() {
		return userInstitution;
	}
	public void setUserInstitution(Institution userInstitution) {
		this.userInstitution = userInstitution;
	}



	public void init() {

		loggedInUser = userContext.getUser();

		if(loggedInUser != null){		
			userInstitution = institutionDataAccess.getByAdmin(loggedInUser.getUsername());
		}

		loadStockMarketGame();

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
		}
		else{

			orderFee = new BigDecimal(0);
			portfolioFee = new BigDecimal(0);
			capitalReturnTax = new BigDecimal(0);
		}
	}

	public boolean isStockMarketGameAdmin(){
		if(stockMarketGame != null && stockMarketGame.getOwner() != null){
			return stockMarketGame.getOwner().getId() == userInstitution.getId();
		}
		return true;
	}

	public void loadStockMarketGame(){
		if(stockMarketGameId != null){
			stockMarketGame = stockMarketGameDataAccess.getStockMarketGameByID(stockMarketGameId);
		}
	}

	public void createStockMarketGame(){

		if( startCapital == null){
			startCapital = new BigDecimal(0);
		}else if( startCapital.compareTo(new BigDecimal(0)) == -1 ){
			FacesMessage facesMessage = new FacesMessage("Fehler: Startkapital muss größer als 0 sein!");
			FacesContext.getCurrentInstance().addMessage("createForm:startCapital", facesMessage);
			return;
		}

		if( orderFee.compareTo(new BigDecimal(0)) == -1){
			FacesMessage facesMessage = new FacesMessage("Fehler: Ordergebühr muss größer gleich 0  sein!");
			FacesContext.getCurrentInstance().addMessage("createForm:orderFee", facesMessage);
			return;
		}

		if( portfolioFee.compareTo(new BigDecimal(0)) == -1){
			FacesMessage facesMessage = new FacesMessage("Fehler: Portfoliogebühr muss größer gleich 0  sein!");
			FacesContext.getCurrentInstance().addMessage("createForm:portfolioFee", facesMessage);
			return;
		}

		if( capitalReturnTax.compareTo(new BigDecimal(0)) == -1 || capitalReturnTax.compareTo(new BigDecimal(99)) == 1){
			FacesMessage facesMessage = new FacesMessage("Fehler: Kapitalertragssteuer muss zwischen 0% und 99% liegen!");
			FacesContext.getCurrentInstance().addMessage("createForm:capitalReturnTax", facesMessage);
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

			//FacesContext.getCurrentInstance().getExternalContext().redirect("list.xhtml");
		}
		catch (Exception e) {

			FacesMessage message = new FacesMessage("Fehler beim Speichern des Börsenspieles");
			FacesContext.getCurrentInstance().addMessage(null, message);

			e.printStackTrace();
		}

	}

	public void handleLogoUpload(FileUploadEvent event) {

		try {
			UploadedFile logo = event.getFile();
			byte[] uploadedLogo = IOUtils.toByteArray(logo.getInputstream());
			Blob newLogo = new SerialBlob(uploadedLogo);

			stockMarketGame.setLogo(newLogo);

			FacesMessage message = new FacesMessage("Logo: " + logo.getFileName() + " erfolgreich hochgeladen");
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
			//String admin = context.getExternalContext().getRequestParameterMap().get("admin");
			//Institution i = institutionDataAccess.getByAdmin(admin);

			try {
				return new DefaultStreamedContent(logo.getBinaryStream());
				//return new DefaultStreamedContent(i.getLogo().getBinaryStream());

			} catch (SQLException e) {
				e.printStackTrace();
				throw new AppException(e.getMessage());
			}
		}
	}



	public static byte[] serialize(UploadedFile object) {  

		ByteArrayOutputStream baos = null;  
		ObjectOutputStream out = null;  
		byte[] byteObject = null;  

		try {  
			baos = new ByteArrayOutputStream();  
			out = new ObjectOutputStream(baos);  
			out.writeObject(object);  
			out.close();  
			byteObject = baos.toByteArray();  
		} catch (Throwable e) {
			System.out.println(e.getMessage());
		}  

		return byteObject;  
	} 


	public static Calendar dateToCalendar(Date date){ 
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal;
	}

}
