package at.ac.tuwien.ase09.test;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.runners.model.InitializationError;

public class ClientSideDatabaseAwareArquillianRunner extends AbstractDatabaseAwareArquillianRunner {
	private static int databaseAwareTestRun = 1;
	
	public ClientSideDatabaseAwareArquillianRunner(Class<?> klass) throws InitializationError {
		super(klass);
	}
	
	@Override
	protected EntityManagerFactory resetDatabase(String persistenceUnitName) {
		Map<String, String> properties = new HashMap<String, String>();
        properties.put("hibernate.hbm2ddl.auto", "create-drop");
        properties.put("hibernate.ejb.entitymanager_factory_name", persistenceUnitName
            + databaseAwareTestRun++);
        properties.put("javax.persistence.jtaDataSource", null);
        properties.put("javax.persistence.transactionType", "RESOURCE_LOCAL");
        properties.put("javax.persistence.jdbc.driver", "org.h2.Driver");
        properties.put("javax.persistence.jdbc.user", "test");
        properties.put("javax.persistence.jdbc.password", "test");
        properties.put("javax.persistence.jdbc.url", "jdbc:h2:tcp://localhost/mem:test");
        
        return Persistence.createEntityManagerFactory(persistenceUnitName, properties);
	}
}
