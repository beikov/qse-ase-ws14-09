package at.ac.tuwien.ase09.bean;

import java.sql.Blob;

import javax.enterprise.context.ApplicationScoped;
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

import at.ac.tuwien.ase09.data.StockMarketGameDataAccess;
import at.ac.tuwien.ase09.data.UserDataAccess;
import at.ac.tuwien.ase09.exception.AppException;
import at.ac.tuwien.ase09.model.Institution;
import at.ac.tuwien.ase09.model.StockMarketGame;
import at.ac.tuwien.ase09.model.User;

@Named
@ApplicationScoped
public class LogoBean {

	@Inject
	private UserDataAccess userDataAccess;
	@Inject
	private StockMarketGameDataAccess gameDataAccess;
	
	/*
	public void handleLogoUpload(FileUploadEvent event) {
		try {
			UploadedFile logo = event.getFile();
			byte[] uploadedLogo = IOUtils.toByteArray(logo.getInputstream());
			Blob newLogo = new SerialBlob(uploadedLogo);
			User user = (User)event.getComponent().getAttributes().get("user");
			StockMarketGame game = (StockMarketGame)event.getComponent().getAttributes().get("game");
			if ((user == null && game == null) || (user != null && game != null)) {
				throw new Exception("invalid attributes");
			}
			if (user != null) {
				user.setLogo(newLogo);
			} else {
				game.setLogo(newLogo);
			}
			
			FacesMessage message = new FacesMessage("Logo: " + logo.getFileName() + " erfolgreich hochgeladen.");
			FacesContext.getCurrentInstance().addMessage(null, message);
		} catch (Exception e) {
			e.printStackTrace();
			FacesMessage message = new FacesMessage("Fehler beim Speichern des neuen Logos!");
	        FacesContext.getCurrentInstance().addMessage(null, message);
		}
    }*/
	
	public StreamedContent getLogo() {
        FacesContext context = FacesContext.getCurrentInstance();

        if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
            return new DefaultStreamedContent();
        }
        else {
			try {
				String username = context.getExternalContext().getRequestParameterMap().get("username");
				String gameId = context.getExternalContext().getRequestParameterMap().get("gameId");
				if ((username == null && gameId == null) || (username != null && gameId != null)) {
					throw new Exception("invalid params");
				}
				if (username != null) {
					User user = userDataAccess.getUserByUsername(username);
					return new DefaultStreamedContent(user.getLogo().getBinaryStream());
				}
				StockMarketGame game = gameDataAccess.getStockMarketGameByID(Long.valueOf(gameId));
				return new DefaultStreamedContent(game.getLogo().getBinaryStream());
				
			} catch (Exception e) {
				e.printStackTrace();
				throw new AppException(e.getMessage());
			}
        }
    }
}
