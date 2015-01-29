package at.ac.tuwien.ase09.test;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStage;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

import at.ac.tuwien.ase09.context.UserAccount;
import at.ac.tuwien.ase09.context.UserContext;
import at.ac.tuwien.ase09.keycloak.UserInfo;
import at.ac.tuwien.ase09.model.User;
import at.ac.tuwien.ase09.webapp.CharsetEncodingFilter;

@RunWith(ClientSideDatabaseAwareArquillianRunner.class)
public abstract class AbstractSeleniumTest extends AbstractContainerTest<AbstractSeleniumTest>{
	private static int testRun = 1;
	
	static {
    	System.setProperty("webdriver.chrome.driver", "src/test/resources/chromedriver.exe");
    }
	
    @Drone
    protected WebDriver driver;
    
    @ArquillianResource
	protected URL baseURL;

	protected EntityManager em;
	
	protected User defaultUser;
	
    @Before
	public void createPersistence(){
        createDefaultTestData();
	}
    
    protected void createDefaultTestData(){
    	EntityTransaction tx = em.getTransaction();
    	tx.begin();
    	defaultUser = new User();
    	defaultUser.setUsername("DEFAULT");
    	em.persist(defaultUser);
    	tx.commit();
    }
    
    protected static WebArchive createSeleniumTestBaseDeployment(){
    	final PomEquippedResolveStage resolver = Maven.resolver().loadPomFromFile("pom.xml");
		final String webapp_src = "src/main/webapp";
		return createContainerTestBaseDeployment()
			/* data access / service*/
			.addPackage("at.ac.tuwien.ase09.data")
			.addPackage("at.ac.tuwien.ase09.service")
			.addPackage("at.ac.tuwien.ase09.notification")
			.addPackage("at.ac.tuwien.ase09.filter")
			/* servlet filter */
			.addClass(CharsetEncodingFilter.class)
			.addClass(TestUserFilter.class)
			/* primefaces */
			.addAsLibraries(resolver.resolve("org.primefaces:primefaces").withTransitivity().asFile())
			/* boostrap */
			// retarded shrink wrap is not able to add directories as web resource...
			.addAsWebResource(new File(webapp_src + "/resources/bootstrap/css/bootstrap.css"), "/resources/bootstrap/css/bootstrap.css")
			.addAsWebResource(new File(webapp_src + "/resources/bootstrap/css/bootstrap.css.map"), "/resources/bootstrap/css/bootstrap.css.map")
			.addAsWebResource(new File(webapp_src + "/resources/bootstrap/css/bootstrap.min.css"), "/resources/bootstrap/css/bootstrap.min.css")
			.addAsWebResource(new File(webapp_src + "/resources/bootstrap/css/bootstrap-theme.css"), "/resources/bootstrap/css/bootstrap-theme.css")
			.addAsWebResource(new File(webapp_src + "/resources/bootstrap/css/bootstrap-theme.css.map"), "/resources/bootstrap/css/bootstrap-theme.css.map")
			.addAsWebResource(new File(webapp_src + "/resources/bootstrap/css/bootstrap-theme.min.css"), "/resources/bootstrap/css/bootstrap-theme.min.css")
			.addAsWebResource(new File(webapp_src + "/resources/bootstrap/fonts/glyphicons-halflings-regular.eot"), "/resources/bootstrap/fonts/glyphicons-halflings-regular.eot")
			.addAsWebResource(new File(webapp_src + "/resources/bootstrap/fonts/glyphicons-halflings-regular.svg"), "/resources/bootstrap/fonts/glyphicons-halflings-regular.svg")
			.addAsWebResource(new File(webapp_src + "/resources/bootstrap/fonts/glyphicons-halflings-regular.ttf"), "/resources/bootstrap/fonts/glyphicons-halflings-regular.ttf")
			.addAsWebResource(new File(webapp_src + "/resources/bootstrap/fonts/glyphicons-halflings-regular.woff"), "/resources/bootstrap/fonts/glyphicons-halflings-regular.woff")
			.addAsWebResource(new File(webapp_src + "/resources/bootstrap/js/bootstrap.js"), "/resources/bootstrap/js/bootstrap.js")
			.addAsWebResource(new File(webapp_src + "/resources/bootstrap/js/bootstrap.min.js"), "/resources/bootstrap/js/bootstrap.min.js")
			.addAsWebResource(new File(webapp_src + "/resources/bootstrap/js/npm.js"), "/resources/bootstrap/js/npm.js")
			
			/* keycloak */
			.addClass(UserInfo.class)
			
			.addClasses(UserContext.class, UserAccount.class, TestUserContext.class)
			.addAsWebInfResource(new File(webapp_src + "/WEB-INF/templates/template.xhtml"), "/templates/template.xhtml")
			.addAsWebInfResource(new File(webapp_src + "/WEB-INF/includes/navi.xhtml"), "/includes/navi.xhtml")
			.setWebXML("test-web.xml");
    }
    
}