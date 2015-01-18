/**
 * 
 */
package at.ac.tuwien.ase09.test;

import java.io.Serializable;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.Asset;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.impl.base.asset.AssetUtil;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStage;
import org.junit.runner.RunWith;

import at.ac.tuwien.ase09.currency.CurrencyConversionService;
import at.ac.tuwien.ase09.naming.CustomNamingStrategy;
import at.ac.tuwien.ase09.test.currency.CurrencyConversionHolder;
import at.ac.tuwien.ase09.test.currency.TestCurrencyConversionService;
import at.ac.tuwien.ase09.test.persistence.DataManager;
import at.ac.tuwien.ase09.test.persistence.H2TcpServerStarter;
import at.ac.tuwien.ase09.test.persistence.TestEntityManagerProducer;

public abstract class AbstractContainerTest<T extends AbstractContainerTest<T>> implements Serializable {

    private static final long serialVersionUID = -7248288932170947951L;

    protected static WebArchive createContainerTestBaseDeployment() {
    	PomEquippedResolveStage resolver = Maven.resolver().loadPomFromFile("pom.xml");
    	
    	return ShrinkWrap.create(WebArchive.class)
			.addAsWebInfResource("test-jboss-deployment-structure.xml", "jboss-deployment-structure.xml")
			.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
			
			/* persistence */
			.addAsWebInfResource("h2-ds.xml")
			.addAsResource("META-INF/deployment-persistence.xml", "META-INF/persistence.xml")
			.addClass(CustomNamingStrategy.class)
			.addPackages(true, "at.ac.tuwien.ase09.model")
			.addAsResource("META-INF/entityClasses.xml")
			.addClass(TestEntityManagerProducer.class)
			.addClass(H2TcpServerStarter.class)
			.addClass(DataManager.class)
			
			/* currency */
			.addClass(CurrencyConversionService.class)
			.addClass(TestCurrencyConversionService.class)
			.addClass(CurrencyConversionHolder.class)
			
			/* exceptions */
			.addPackage("at.ac.tuwien.ase09.exception")
			
			/* common test dependencies */
			.addAsLibraries(resolver.resolve("com.blazebit:blaze-common-utils").withTransitivity().asFile())
			.addAsLibraries(resolver.resolve("com.googlecode.catch-exception:catch-exception").withTransitivity().asFile());
    }
}
