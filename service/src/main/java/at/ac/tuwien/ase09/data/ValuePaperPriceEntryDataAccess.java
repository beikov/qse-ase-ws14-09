package at.ac.tuwien.ase09.data;

import java.util.Calendar;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import at.ac.tuwien.ase09.exception.AppException;
import at.ac.tuwien.ase09.exception.EntityNotFoundException;
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
	
	public List<ValuePaperHistoryEntry> getValuePaperPriceHistoryEntries(String isin){
		try {
			return em
					.createQuery(
							"SELECT vphe FROM ValuePaperHistoryEntry vphe JOIN vphe.valuePaper vp WHERE vp.isin = :isin",
							ValuePaperHistoryEntry.class)
					.setParameter("isin", isin).getResultList();
		} catch (Exception e) {
			throw new AppException(e);
		}
	}
}
