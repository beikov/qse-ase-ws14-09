package at.ac.tuwien.ase09.webapp;

import java.util.EnumSet;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.SessionTrackingMode;

public class ConfigServletContextListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		ServletContext sc = sce.getServletContext();
		sc.setSessionTrackingModes(EnumSet.of(SessionTrackingMode.COOKIE));
		sc.getSessionCookieConfig().setPath("/");
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
	}

}
