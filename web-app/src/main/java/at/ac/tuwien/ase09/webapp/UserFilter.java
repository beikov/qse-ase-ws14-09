package at.ac.tuwien.ase09.webapp;

import java.io.IOException;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.keycloak.representations.idm.UserRepresentation;

import at.ac.tuwien.ase09.context.Login;
import at.ac.tuwien.ase09.data.UserDataAccess;
import at.ac.tuwien.ase09.exception.EntityNotFoundException;
import at.ac.tuwien.ase09.keycloak.AdminClient;
import at.ac.tuwien.ase09.keycloak.UserInfo;
import at.ac.tuwien.ase09.keycloak.AdminClient.Failure;
import at.ac.tuwien.ase09.model.User;
import at.ac.tuwien.ase09.service.UserService;

public class UserFilter implements Filter {

	@Inject
	@Login
	private Event<UserInfo> loginEvent;

	@Inject
	private UserDataAccess userDataAccess;

	@Inject
	private UserService userService;

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

		if (request instanceof HttpServletRequest) {
			
			HttpServletRequest httpRequest = (HttpServletRequest) request;

			try {
				
				UserInfo userInfo = AdminClient.getCurrentUser(httpRequest);
				
				if (userInfo != null) {
					
					User user = userDataAccess.getUserByUsername(userInfo.getUsername());
					
					if (user == null) {
						user = AdminClient.createUser(httpRequest);
						userService.saveUser(user);
					}
					
					userInfo.setUser(user);
					loginEvent.fire(userInfo);
				}
				
			} catch (Failure e) {
				throw new ServletException(e);
			}
		}
		chain.doFilter(request, response);
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
	}
}

