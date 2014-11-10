package at.ac.tuwien.ase09.data.bond.detail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import javax.batch.api.AbstractBatchlet;
import javax.batch.api.BatchProperty;
import javax.batch.runtime.context.JobContext;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import at.ac.tuwien.ase09.data.JsoupUtils;
import at.ac.tuwien.ase09.data.StepExitStatus;
import at.ac.tuwien.ase09.data.ValuePaperDataAccess;
import at.ac.tuwien.ase09.model.Stock;

@Dependent
@Named("BondDetailLinkReaderBatchlet")
public class BondDetailLinkReaderBatchlet extends AbstractBatchlet {
	private static final Logger LOG = Logger.getLogger(BondDetailLinkReaderBatchlet.class.getName());
	@Inject
	@BatchProperty
	private String indexName;
	
	@Inject
	private JobContext jobContext;
	
	@Inject
	private ValuePaperDataAccess valuePaperDataAccess;
	
	@Override
	public String process() throws Exception {
		Iterator<Stock> stockIter = valuePaperDataAccess.getStocksByIndex(indexName).iterator();
		List<String> bondDetailLinks = new ArrayList<String>();
		while(stockIter.hasNext()){
			Stock stock = stockIter.next();
			Document bondPage = JsoupUtils.tryGetPage(stock.getCertificatePageUrl());
			Elements linkCells = bondPage.select("#marketdata_list td:nth-child(2) a");
			for(Element link : linkCells){
				bondDetailLinks.add(link.attr("href"));
			}
		}
		
		jobContext.setTransientUserData(bondDetailLinks);
		LOG.info("Extracted " + bondDetailLinks.size() + " bond detail links");
		return StepExitStatus.COMPLETED.toString();
	}
}
