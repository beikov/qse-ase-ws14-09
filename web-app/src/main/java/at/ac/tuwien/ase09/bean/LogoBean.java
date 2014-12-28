package at.ac.tuwien.ase09.bean;

import java.sql.Blob;
import java.sql.SQLException;

import javax.enterprise.context.ApplicationScoped;
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

import at.ac.tuwien.ase09.data.LogoDataAccess;
import at.ac.tuwien.ase09.exception.AppException;
import at.ac.tuwien.ase09.model.Institution;
import at.ac.tuwien.ase09.model.Logo;

@Named
@ApplicationScoped
public class LogoBean {

	@Inject
	private LogoDataAccess logoDataAccess;
	
	public void handleLogoUpload(FileUploadEvent event) {
		try {
			UploadedFile logo = event.getFile();
			byte[] uploadedLogo = IOUtils.toByteArray(logo.getInputstream());
			Blob newLogo = new SerialBlob(uploadedLogo);
			
			String entityClass = (String)event.getComponent().getAttributes().get("entityClass");
			Long entityId = (Long)event.getComponent().getAttributes().get("entityId");
			Logo entity = loadLogo(entityClass, entityId); 
			entity.setLogo(newLogo);
			FacesMessage message = new FacesMessage("Logo: " + logo.getFileName() + " erfolgreich hochgeladen.");
			FacesContext.getCurrentInstance().addMessage(null, message);
		} catch (Exception e) {
			e.printStackTrace();
			FacesMessage message = new FacesMessage("Fehler beim Speichern des neuen Logos!");
	        FacesContext.getCurrentInstance().addMessage(null, message);
		}
    }
	
	public StreamedContent getLogo() {
        FacesContext context = FacesContext.getCurrentInstance();

        if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
            return new DefaultStreamedContent();
        }
        else {
			try {
				String entityClass = context.getExternalContext().getRequestParameterMap().get("entityClass");
				Long entityId = Long.valueOf(context.getExternalContext().getRequestParameterMap().get("entityId"));
				Logo entity = loadLogo(entityClass, entityId);
				return new DefaultStreamedContent(entity.getLogo().getBinaryStream());
			} catch (Exception e) {
				e.printStackTrace();
				throw new AppException(e.getMessage());
			}
        }
    }
	
	private Logo loadLogo(String entityClass, Long id) throws ClassNotFoundException {
		@SuppressWarnings("unchecked")
		Class<Logo> logoClass = (Class<Logo>) Class.forName(entityClass);
		return logoDataAccess.getByClassAndId(logoClass, id);
	}
}
