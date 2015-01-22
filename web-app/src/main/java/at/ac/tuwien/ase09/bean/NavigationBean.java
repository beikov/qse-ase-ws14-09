package at.ac.tuwien.ase09.bean;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import at.ac.tuwien.ase09.keycloak.AdminClient;

@Named
@RequestScoped
public class NavigationBean {

	private String loginUrl;
	private String logoutUrl;
	private String registerUrl;
	
	@Inject
	private HttpServletRequest req;
	
	public String getLoginUrl() {
		if (loginUrl == null) {
//			loginUrl = AdminClient.getLoginUrl(req);
			loginUrl = "/protected";
		}
		return loginUrl;
	}
	
	public String getLogoutUrl() {
		if (logoutUrl == null) {
			logoutUrl = AdminClient.getLogoutUrl(req);
		}
		return logoutUrl;
	}
	
	public String getRegisterUrl() {
		if (registerUrl == null) {
//			registerUrl = AdminClient.getRegisterUrl(req);
			loginUrl = "/protected";
		}
		return registerUrl;
	}
}
