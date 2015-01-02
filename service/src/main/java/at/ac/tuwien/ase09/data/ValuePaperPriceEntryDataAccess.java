package at.ac.tuwien.ase09.data;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import at.ac.tuwien.ase09.exception.AppException;
import at.ac.tuwien.ase09.exception.EntityNotFoundException;
import at.ac.tuwien.ase09.model.Portfolio;
import at.ac.tuwien.ase09.model.ValuePaperHistoryEntry;
import at.ac.tuwien.ase09.model.ValuePaperPriceEntry;

@Stateless
public class ValuePaperPriceEntryDataAccess {

	@Inject
	private EntityManager em;
	
	public ValuePaperPriceEntry getLastPriceEntry(String code){
		List<ValuePaperPriceEntry> priceEntryList = null;
		try{
			priceEntryList = em.createQuery("SELECT price FROM ValuePaperPriceEntry price JOIN price.valuePaper vp WHERE vp.code=:code ORDER BY price.created DESC", ValuePaperPriceEntry.class)
				.setParameter("code", code)
				.getResultList();
		}catch(Exception e){
			throw new AppException(e);
		}
		if(priceEntryList.isEmpty()){
			throw new EntityNotFoundException();
		}
		return priceEntryList.get(0);
	}
	
	public List<Calendar> getHistoricPriceEntryDates(String code) {
		try {
			return em
					.createQuery(
							"SELECT pe.date FROM ValuePaperHistoryEntry pe JOIN pe.valuePaper vp WHERE vp.code = :code",
							Calendar.class)
					.setParameter("code", code).getResultList();
		} catch (Exception e) {
			throw new AppException(e);
		}
	}
	
	public List<ValuePaperHistoryEntry> getValuePaperPriceHistoryEntries(String code){
		try {
			return em
					.createQuery("SELECT vphe FROM ValuePaperHistoryEntry vphe JOIN vphe.valuePaper vp WHERE vp.code = :code",
							ValuePaperHistoryEntry.class)
					.setParameter("code", code).getResultList();
		} catch (Exception e) {
			throw new AppException(e);
		}
	}

	public List<ValuePaperHistoryEntry> getValuePaperHistoryEntriesForPortfolioAfterDate(Portfolio portfolio, Calendar date) {
		try {
			String query = "SELECT he "
					+ "FROM PortfolioValuePaper pvp, ValuePaperHistoryEntry he "
					+ "WHERE pvp.valuePaper = he.valuePaper AND "
					+ "pvp.portfolio = :portfolio AND he.date >= :date "
					+ "ORDER BY he.date";
			List<ValuePaperHistoryEntry> result = em.createQuery(query, ValuePaperHistoryEntry.class)
					.setParameter("portfolio", portfolio).setParameter("date", date).getResultList(); 
			return result;
		} catch(Exception e) {
			throw new AppException(e);
		}
	}
	
	public List<ValuePaperHistoryEntry> getHistoricValuePaperPricesByPortfolioId(Long id) {
		try {
			String query = "SELECT pe "
					+ "FROM Portfolio p, PortfolioValuePaper vp, ValuePaperHistoryEntry pe "
					+ "WHERE pe.valuePaper = vp.valuePaper AND "
					+ "p.id = :id AND pe.date >= p.created "
					+ "ORDER BY pe.date";
			return em.createQuery(query, ValuePaperHistoryEntry.class).setParameter("id", id).getResultList();
		} catch(Exception e) {
			throw new AppException(e);
		}
	}
	
	public BigDecimal getDayHighPrice(String code){
		Calendar dayStart = Calendar.getInstance();
		dayStart.set(Calendar.MILLISECOND, dayStart.getMinimum(Calendar.MILLISECOND));
		dayStart.set(Calendar.SECOND, dayStart.getMinimum(Calendar.SECOND));
		dayStart.set(Calendar.MINUTE, dayStart.getMinimum(Calendar.MINUTE));
		dayStart.set(Calendar.HOUR, dayStart.getMinimum(Calendar.HOUR));
		try{
			return em.createQuery("SELECT MAX(pe.price) FROM ValuePaperPriceEntry pe JOIN pe.valuePaper vp WHERE vp.code = :code AND pe.created >= :dayStart", BigDecimal.class)
				.setParameter("dayStart", dayStart)
				.setParameter("code", code)
				.getSingleResult();
		}catch(NoResultException e){
			throw new EntityNotFoundException(e);
		}catch(Exception e){
			throw new AppException(e);
		}
	}
	
	public BigDecimal getDayLowPrice(String code){
		Calendar dayStart = Calendar.getInstance();
		dayStart.set(Calendar.MILLISECOND, dayStart.getMinimum(Calendar.MILLISECOND));
		dayStart.set(Calendar.SECOND, dayStart.getMinimum(Calendar.SECOND));
		dayStart.set(Calendar.MINUTE, dayStart.getMinimum(Calendar.MINUTE));
		dayStart.set(Calendar.HOUR, dayStart.getMinimum(Calendar.HOUR));
		try{
			return em.createQuery("SELECT MIN(pe.price) FROM ValuePaperPriceEntry pe JOIN pe.valuePaper vp WHERE vp.code = :code AND pe.created >= :dayStart", BigDecimal.class)
				.setParameter("dayStart", dayStart)
				.setParameter("code", code)
				.getSingleResult();
		}catch(Exception e){
			throw new AppException(e);
		}
	}
	
	public ValuePaperHistoryEntry getHistoricPriceEntry(String code, Calendar date){
		try{
			return em.createQuery("SELECT pe FROM ValuePaperHistoryEntry pe JOIN pe.valuePaper vp WHERE vp.code = :code AND pe.created = :date", ValuePaperHistoryEntry.class)
				.setParameter("date", date)
				.setParameter("code", code)
				.getSingleResult();
		}catch(Exception e){
			throw new AppException(e);
		}
	}
}
