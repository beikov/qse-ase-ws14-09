package at.ac.tuwien.ase09.data;

import java.util.List;

import javax.ejb.Stateless;

import at.ac.tuwien.ase09.model.Stock;

@Stateless
public class StockDataAccess extends AbstractDataAccess {

	public List<Stock> getAllowedStocks() {
		return em.createQuery(
				"SELECT stock "
				+ "FROM Portfolio p "
				+ "JOIN p.game g "
				+ "JOIN TREAT(g.allowedValuePapers AS Stock) stock "
				+ "WHERE :portfolioId IS NULL OR p.id = :portfolioId", Stock.class)
			.setParameter("portfolioId", userContext.getContextId())
			.getResultList();
	}
}
