package at.ac.tuwien.ase09.data.stock.analystOpinion;

import java.util.ArrayList;
import java.util.List;

import javax.batch.api.chunk.ItemProcessor;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import at.ac.tuwien.ase09.data.ValuePaperDataAccess;
import at.ac.tuwien.ase09.exception.EntityNotFoundException;
import at.ac.tuwien.ase09.model.AnalystOpinion;
import at.ac.tuwien.ase09.model.NewsItem;


@Dependent
@Named
public class AnalystOpinionProcessor implements ItemProcessor {

	@Inject
	private ValuePaperDataAccess valuePaperDataAccess;
	
	@Override
	public Object processItem(Object item) throws Exception {
		List<AnalystOpinion> analystOpinions = (List<AnalystOpinion>) item;
		if(analystOpinions.isEmpty()){
			return null;
		}
		List<AnalystOpinion> newAnalystOpinions = new ArrayList<>();
		for(AnalystOpinion analystOpinion : analystOpinions){
			try{
				valuePaperDataAccess.getAnalystOpinionForStock(analystOpinion.getStock().getCode(), analystOpinion.getCreated(), analystOpinion.getSource());
				// the news item already exists
				// since the items are chronologically ordered we imply that all following items must exist as well
				// hence we can end the loop
				break;
			} catch(EntityNotFoundException nfe){
				// ignore
			}
			// the news item does not exist
			newAnalystOpinions.add(analystOpinion);
		}
		
		if(newAnalystOpinions.isEmpty()){
			return null;
		}else{
			return newAnalystOpinions;
		}
	}

}
