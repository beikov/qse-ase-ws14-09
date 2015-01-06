package at.ac.tuwien.ase09.test;

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

public class TestUserFilter implements Filter {

	@Inject
	@Login
	private Event<UserInfo> loginEvent;
	@Inject
	private UserDataAccess userDataAccess;
	
	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		if (request instanceof HttpServletRequest) {
			HttpServletRequest httpRequest = (HttpServletRequest) request;

			String username = httpRequest.getParameter("user");
			if(username != null){
				UserInfo userInfo = new UserInfo();
				userInfo.setUser(userDataAccess.getUserByUsername(username));
				loginEvent.fire(userInfo);
			}
		}

		chain.doFilter(request, response);
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
	}
}
