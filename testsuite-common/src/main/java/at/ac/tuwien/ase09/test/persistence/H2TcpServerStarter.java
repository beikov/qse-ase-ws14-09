package at.ac.tuwien.ase09.test.persistence;

import java.sql.SQLException;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.h2.tools.Server;

@Singleton
@Startup
public class H2TcpServerStarter {
	
	@PostConstruct
	public void startH2TcpServer(){
		try {
			Server.createTcpServer().start();
			System.out.println("H2 TCP server started");
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
