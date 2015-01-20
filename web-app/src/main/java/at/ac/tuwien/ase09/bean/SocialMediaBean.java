package at.ac.tuwien.ase09.bean;

import java.net.MalformedURLException;
import java.net.URL;

import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

@Named
@RequestScoped
public class SocialMediaBean {

	FacesContext context = FacesContext.getCurrentInstance();
	public String getAbsoluteApplicationUrl() {
	    HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
	    URL url=null;
	    URL newUrl=null;
		try {
			url = new URL(request.getRequestURL().toString());
			newUrl = new URL(url.getProtocol(),
                    url.getHost(),
                    url.getPort(),
                    request.getContextPath());
		} catch (MalformedURLException e) {
			System.out.println("Error while getting current Url");
		}
	    
	    return newUrl.toString();
	 }
}
