package at.ac.tuwien.ase09.data.bean;

import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.batch.operations.JobOperator;
import javax.batch.runtime.BatchRuntime;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.persistence.EntityManager;

@Singleton
@Startup
public class TestSingleton {
	@Inject
	private EntityManager em;
	
	@PostConstruct
	public void init(){
		JobOperator jobOperator = BatchRuntime.getJobOperator();
		Properties props = new Properties();
		props.put("successorJobIds", "detailBondExtractionATX");
//		jobOperator.start("analystOpinionExtraction", null);
//		jobOperator.start("detailStockExtractionATX", null);
//		jobOperator.start("intradayBondExtractionATX", null);
	}
}
