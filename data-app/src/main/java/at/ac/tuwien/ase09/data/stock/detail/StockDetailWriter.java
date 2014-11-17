package at.ac.tuwien.ase09.data.stock.detail;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import at.ac.tuwien.ase09.data.AbstractEntityWriter;
import at.ac.tuwien.ase09.data.ValuePaperDataAccess;
import at.ac.tuwien.ase09.exception.EntityNotFoundException;
import at.ac.tuwien.ase09.model.Stock;

@Dependent
@Named("StockDetailWriter")
public class StockDetailWriter extends AbstractEntityWriter {
	@Inject
	private ValuePaperDataAccess valuePaperDataAccess;
	
	@Override
	protected void persistEntity(Object entity) {
		Stock stock = (Stock) entity;
		Stock existingStock = null;
		try{
			existingStock = valuePaperDataAccess.getStockByIsin(stock.getIsin());
			// existingStock is managed
		}catch(EntityNotFoundException nfe){
			// ignore
		}
		// TODO: set id on entity or copy fields to attached existingEntity?
		if(existingStock != null){
			existingStock.setBoerseCertificatePageUrl(stock.getBoerseCertificatePageUrl());
			existingStock.setFinanzenCertificatePageUrl(stock.getFinanzenCertificatePageUrl());
			existingStock.setHistoricPricesPageUrl(stock.getHistoricPricesPageUrl());
			existingStock.setCurrency(stock.getCurrency());
			existingStock.setIndex(stock.getIndex());
			existingStock.setIsin(stock.getIsin());
			existingStock.setName(stock.getName());
			existingStock.setCountry(stock.getCountry());
		}else{
			em.persist(stock);
		}
	}

}
