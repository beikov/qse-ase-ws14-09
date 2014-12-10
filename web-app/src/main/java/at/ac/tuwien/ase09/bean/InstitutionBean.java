package at.ac.tuwien.ase09.bean;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.Blob;
import java.sql.SQLException;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
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
	
	private byte[] uploadedLogo;
	
	//private UploadedFile uploadedFile;
	
	
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
			uploadedLogo = serialize(logo);
			byte[] foto = IOUtils.toByteArray(logo.getInputstream());
	        System.out.println(foto);
			Blob newLogo = new SerialBlob(foto);
			
			institution.setLogo(newLogo);
			institutionService.update(institution);
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
	
	public DefaultStreamedContent getInstitutionLogo() {
		try {
			return new DefaultStreamedContent(institution.getLogo().getBinaryStream(), "image/png");
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public StreamedContent getImage() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();

        if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
            // So, we're rendering the HTML. Return a stub StreamedContent so that it will generate right URL.
            return new DefaultStreamedContent();
        }
        else {
            // So, browser is requesting the image. Return a real StreamedContent with the image bytes.
            try {
				return new DefaultStreamedContent(new ByteArrayInputStream(institution.getLogo().getBytes(0, (int)institution.getLogo().length())));
			} catch (SQLException e) {
				e.printStackTrace();
				return null;
			}
        }
    }
	
	public boolean getEditMode() {
		return editMode;
	}
}
