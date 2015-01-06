package at.ac.tuwien.ase09.bean;

import java.io.IOException;
import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import at.ac.tuwien.ase09.context.UserContext;
import at.ac.tuwien.ase09.data.InstitutionDataAccess;
import at.ac.tuwien.ase09.exception.AppException;
import at.ac.tuwien.ase09.exception.EntityNotFoundException;
import at.ac.tuwien.ase09.model.Institution;
import at.ac.tuwien.ase09.service.InstitutionService;

@Named
@RequestScoped
public class InstitutionSettingsBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Inject
	private UserContext userContext;
	
	@Inject
	private InstitutionDataAccess institutionDataAccess;
	
	@Inject 
	private InstitutionService institutionService;
	
	private Long institutionId;
	
	private Institution institution;

	@PostConstruct
	public void init() throws IOException {
		try {
			institution = institutionDataAccess.getByAdmin(userContext.getUser().getUsername());
		} catch(EntityNotFoundException e) {
			FacesContext.getCurrentInstance().getExternalContext().responseSendError(404, "Keine Institution zum Bearbeiten gefunden");
			FacesContext.getCurrentInstance().responseComplete();
		} catch(AppException e) {
			FacesContext.getCurrentInstance().getExternalContext().responseSendError(500, "Fehler beim Laden der Institution");
			FacesContext.getCurrentInstance().responseComplete();
		}
	}
	
	public void saveChanges() {
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
		
	public Long getInstitutionId() {
		return institutionId;
	}

	public void setInstitutionId(Long institutionId) {
		this.institutionId = institutionId;
	}
	
}
