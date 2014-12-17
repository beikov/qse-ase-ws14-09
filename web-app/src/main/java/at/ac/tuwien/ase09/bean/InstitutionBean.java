package at.ac.tuwien.ase09.bean;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.Blob;
import java.sql.SQLException;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
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
import at.ac.tuwien.ase09.exception.AppException;
import at.ac.tuwien.ase09.model.Institution;
import at.ac.tuwien.ase09.model.User;
import at.ac.tuwien.ase09.service.InstitutionService;

@Named
@SessionScoped
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
	
	//@PostConstruct
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
			byte[] uploadedLogo = IOUtils.toByteArray(logo.getInputstream());
			Blob newLogo = new SerialBlob(uploadedLogo);
			
			institution.setLogo(newLogo);
			//institutionService.update(institution);
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
	
	public StreamedContent getImage() {
        FacesContext context = FacesContext.getCurrentInstance();

        if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
            // So, we're rendering the HTML. Return a stub StreamedContent so that it will generate right URL.
            return new DefaultStreamedContent();
        }
        else {
            // So, browser is requesting the image. Return a real StreamedContent with the image bytes.
        	String admin = context.getExternalContext().getRequestParameterMap().get("admin");
        	Institution i = institutionDataAccess.getByAdmin(admin);
        	
			try {
				return new DefaultStreamedContent(i.getLogo().getBinaryStream());
			} catch (SQLException e) {
				e.printStackTrace();
				throw new AppException(e.getMessage());
			}
        }
    }
	
	public boolean getEditMode() {
		return editMode;
	}
}
