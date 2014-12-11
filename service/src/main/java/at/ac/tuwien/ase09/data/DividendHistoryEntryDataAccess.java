package at.ac.tuwien.ase09.data;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import at.ac.tuwien.ase09.exception.AppException;
import at.ac.tuwien.ase09.exception.EntityNotFoundException;
import at.ac.tuwien.ase09.model.DividendHistoryEntry;

@Stateless
public class DividendHistoryEntryDataAccess {
	@Inject
	private EntityManager em;

	public List<DividendHistoryEntry> getDividendHistoryEntryByValuePaperCode(String code){

		try{
			return em.createQuery("SELECT dhe FROM DividendHistoryEntry dhe WHERE dhe.stock.code = :code", DividendHistoryEntry.class).setParameter("code", code).getResultList();
		}catch(Exception e){
			throw new AppException(e);
		}
	}
}
