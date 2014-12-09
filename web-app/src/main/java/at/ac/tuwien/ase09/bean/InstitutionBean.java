package at.ac.tuwien.ase09.bean;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.Blob;
import java.sql.SQLException;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.rowset.serial.SerialBlob;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import at.ac.tuwien.ase09.context.WebUserContext;
import at.ac.tuwien.ase09.data.InstitutionDataAccess;
import at.ac.tuwien.ase09.model.Institution;
import at.ac.tuwien.ase09.model.User;
import at.ac.tuwien.ase09.service.InstitutionService;

@Named
@ViewScoped
public class InstitutionBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Inject
	private WebUserContext userContext;
	
	@Inject
	private InstitutionDataAccess institutionDataAccess;
	
	@Inject 
	private InstitutionService institutionService;
	
	private Institution institution;

	private boolean editMode;
	
	private byte[] uploadedLogo;
	
	
	@PostConstruct
	public void init() {
		User user = userContext.getUser();
		institution = institutionDataAccess.getByAdmin(user.getUsername());
		editMode = false;
	}
	
	public void toggleEditMode() {
	    editMode  = !editMode;
	}
	
	public void saveUpdates() {
		editMode = false;
		try {
			Blob newLogo = new SerialBlob(uploadedLogo);
			institution.setLogo(newLogo);
			institutionService.update(institution);
			FacesMessage message = new FacesMessage("Änderungen erfolgreich gespeichert");
	        FacesContext.getCurrentInstance().addMessage(null, message);
		} catch (Exception e) {
			FacesMessage message = new FacesMessage("Fehler beim Speichern der Änderungen");
	        FacesContext.getCurrentInstance().addMessage(null, message);
			e.printStackTrace();
		}
		
	}
	
	public byte[] serialize(UploadedFile object) {  
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
        }  
  
        return byteObject;  
    }  
	
	public void handleLogoUpload(FileUploadEvent event) {
		try {
			UploadedFile logo = event.getFile();
			uploadedLogo = logo.getContents();
			FacesMessage message = new FacesMessage("Logo:", logo.getFileName() + " erfolgreich hochgeladen.");
			FacesContext.getCurrentInstance().addMessage(null, message);
		} catch (Exception e) {
			e.printStackTrace();
			FacesMessage message = new FacesMessage("Fehler beim Speichern des neuen Logos!");
	        FacesContext.getCurrentInstance().addMessage(null, message);
		}
    }
	
	public Institution getInstitution() {
		return institution;
	}
	
	public boolean getEditMode() {
		return editMode;
	}
}
