To prepare your Wildfly 8.1.0.Final server for the application you have to make the following configuration:
	Create the database user
		1. Open pgAdmin III
		2. Connect to localhost, right click on the item "Login Roles" and click on "New Login Role..."
		3. Set the Role name to "portfolioadmin" and the Password as well as Password (again) in the Definition tab to "portfolioadmin"
		4. Click OK
	
	Create the database
		1. Open pgAdmin III
		2. Connect to localhost, right click on the item "Databases" and click on "New Database..."
		3. Set the name "portfolio" and select the "portfolioadmin" as the Owner
		4. Click OK
	
	Add the JDBC driver
		1. Download the PostgreSQL JDBC Driver from http://central.maven.org/maven2/org/postgresql/postgresql/9.3-1102-jdbc41/postgresql-9.3-1102-jdbc41.jar
		2. Go to WILDFLY_HOME/modules/system/layers/base/org and create a new folder named "postgresql". Within this new folder create a folder named "main".
		3. Copy the postgresql-9.3-1102-jdbc41.jar into WILDFLY_HOME/modules/system/layers/base/org/postgres/main
		4. Create a module.xml in the same folder with the following content
			<?xml version="1.0" encoding="UTF-8"?>
			<module xmlns="urn:jboss:module:1.3" name="org.postgresql">
				<resources>
					<resource-root path="postgresql-9.3-1102-jdbc41.jar"/>
				</resources>
				<dependencies>
					<module name="javax.api"/>
					<module name="javax.transaction.api"/>
				</dependencies>
			</module>
		5. Open WILDFLY_HOME/standalone/configuration/standalone.xml
		6. Look for the subsystem with the xmlns "urn:jboss:domain:datasources:2.0"
		7. Add the following driver definition into datasources/drivers
			<driver name="org.postgresql" module="org.postgresql">
				<xa-datasource-class>org.postgresql.xa.PGXADataSource</xa-datasource-class>
			</driver>

	Configure the JDBC datasource for the web-app
		1. Open WILDFLY_HOME/standalone/configuration/standalone.xml
		2. Look for the subsystem with the xmlns "urn:jboss:domain:datasources:2.0"
		3. Add the following datasource definition into datasources
			<datasource jndi-name="java:jboss/datasources/PostgreSQLDS" pool-name="PostgreSQLDS" enabled="true" use-java-context="true">
				<connection-url>jdbc:postgresql://localhost:5432/portfolio</connection-url>
				<driver>org.postgresql</driver>
				<transaction-isolation>TRANSACTION_READ_COMMITTED</transaction-isolation>
				<pool>
					<min-pool-size>1</min-pool-size>
					<max-pool-size>4</max-pool-size>
					<prefill>true</prefill>
				</pool>
				<security>
					<user-name>portfolioadmin</user-name>
					<password>portfolioadmin</password>
				</security>
			</datasource>

	Configure the JDBC datasource for the keycloak-server
		1. Open WILDFLY_HOME/standalone/configuration/standalone.xml
		2. Look for the subsystem with the xmlns "urn:jboss:domain:datasources:2.0"
		3. Add the following datasource definition into datasources
			<datasource jndi-name="java:jboss/datasources/KeycloakDS" pool-name="KeycloakDS" enabled="true" use-java-context="true">
				<connection-url>jdbc:postgresql://localhost:5432/portfolio</connection-url>
				<driver>org.postgresql</driver>
				<transaction-isolation>TRANSACTION_READ_COMMITTED</transaction-isolation>
				<pool>
					<min-pool-size>1</min-pool-size>
					<max-pool-size>4</max-pool-size>
					<prefill>true</prefill>
				</pool>
				<security>
					<user-name>portfolioadmin</user-name>
					<password>portfolioadmin</password>
				</security>
			</datasource>
			
	Configure Keycloak
		1. Deploy the EAR via Eclipse and open the URL http://localhost:8080/auth
		2. Login with admin/admin
		3. Change the password to "portfolioadmin"
		4. Click the button "Add Realm" which is located at the top right.
		5. Click the button "Choose a "JSON File..." and select the file located at WORKSPACE/portfolio-web-realm.json
		6. Click the button "Upload"