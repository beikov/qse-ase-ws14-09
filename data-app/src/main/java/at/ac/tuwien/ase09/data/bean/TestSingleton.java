package at.ac.tuwien.ase09.data.bean;

import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.batch.operations.JobOperator;
import javax.batch.runtime.BatchRuntime;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import at.ac.tuwien.ase09.model.User;

@Singleton
@Startup
public class TestSingleton {
	@PersistenceContext
	private EntityManager em;
	
	@PostConstruct
	public void init(){
		JobOperator jobOperator = BatchRuntime.getJobOperator();
		Properties props = new Properties();
		props.put("successorJobIds", "intraDayATXExtraction;detailBondExtractionATX");
//		jobOperator.start("detailStockExtractionATX", props);
//		jobOperator.start("detailBondExtractionATX", null);
	}
}
