package at.ac.tuwien.ase09.data;

import java.util.List;

import javax.ejb.Stateless;

import at.ac.tuwien.ase09.model.Stock;

@Stateless
public class StockDataAccess extends AbstractDataAccess {
	
	public List<Stock> getStocks() {
		return em.createQuery("SELECT stock FROM Stock stock", Stock.class).getResultList();
	}
}
