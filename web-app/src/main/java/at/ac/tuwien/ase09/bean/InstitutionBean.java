package at.ac.tuwien.ase09.bean;

import java.io.IOException;
import java.io.Serializable;

import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import at.ac.tuwien.ase09.context.WebUserContext;
import at.ac.tuwien.ase09.data.InstitutionDataAccess;
import at.ac.tuwien.ase09.exception.EntityNotFoundException;
import at.ac.tuwien.ase09.model.Institution;
import at.ac.tuwien.ase09.service.InstitutionService;

@Named
@RequestScoped
public class InstitutionBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Inject
	private WebUserContext userContext;
	
	@Inject
	private InstitutionDataAccess institutionDataAccess;
	
	@Inject 
	private InstitutionService institutionService;
	
	private Long institutionId;
	
	private Institution institution;

	private boolean editMode;
	
	//@PostConstruct
	public void init() throws IOException {
		try {
			institution = institutionDataAccess.getById(institutionId);
		} catch(EntityNotFoundException e) {
			FacesContext context = FacesContext.getCurrentInstance(); 
			context.getExternalContext().responseSendError(404, "Die Institution mit der Id '" + institutionId + "' konnte nicht gefunden werden");
			context.responseComplete();
		}
		
		editMode = false;
	}
	
	public void validateInstitutionParam() throws IOException {
		FacesContext context = FacesContext.getCurrentInstance();
		if (!context.isPostback() && context.isValidationFailed()) {
			context.getExternalContext().responseSendError(500, "Fehlerhafte Institutions-Id");
			context.responseComplete();
		}
	}
	
	public void toggleEditMode() {
	    editMode  = !editMode;
	}
	
	
	
	public void saveUpdates() {
		editMode = false;
		try {
			institutionService.update(institution);
			FacesMessage message = new FacesMessage("Änderungen erfolgreich gespeichert");
	        FacesContext.getCurrentInstance().addMessage(null, message);
		} catch (Exception e) {
			FacesMessage message = new FacesMessage("Fehler beim Speichern der Änderungen");
	        FacesContext.getCurrentInstance().addMessage(null, message);
			e.printStackTrace();
		}
	}
	
	public Institution getInstitution() {
		return institution;
	}
	
	
	public boolean getEditMode() {
		return editMode;
	}

	public Long getInstitutionId() {
		return institutionId;
	}

	public void setInstitutionId(Long institutionId) {
		this.institutionId = institutionId;
	}
	
	
}
