package at.ac.tuwien.ase09.data.stock.atx.detail;

import java.util.ArrayList;
import java.util.HashMap;
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
import at.ac.tuwien.ase09.data.model.StockDetailLinkModel;
import at.ac.tuwien.ase09.model.Stock;

@Dependent
@Named("StockDetailLinkReaderBatchlet")
public class StockDetailLinkReaderBatchlet extends AbstractBatchlet {
	private static final Logger LOG = Logger.getLogger(StockDetailLinkReaderBatchlet.class.getName());

	@Inject
	@BatchProperty(name = "finanzenNetIndexPath")
	private String finanzenNetIndexPath;
	
	@Inject
	@BatchProperty(name = "finanzenNetUrl")
	private String finanzenNetUrl;

	@Inject
	private JobContext jobContext;
	
	@Override
	public String process() throws Exception {
		List<StockDetailLinkModel> stockDetailLinks = new ArrayList<>();
		
		Document finanzenNet = JsoupUtils.getPage(finanzenNetUrl + finanzenNetIndexPath);
		Elements linkCells = finanzenNet.select("#index-list-container td:nth-child(1)");
		// finanzen.net table
		for (Element current : linkCells) {
			Element linkElem = current.select("a").first();
			String isin = current.ownText();
			String companyName = linkElem.text();
			stockDetailLinks.add(new StockDetailLinkModel(finanzenNetUrl + linkElem.attr("href"), companyName, isin, null));
		}
		
		jobContext.setTransientUserData(stockDetailLinks);
		
		LOG.info("Extracted " + stockDetailLinks.size() + " stock detail links");
		
		return StepExitStatus.COMPLETED.toString();
	}

}
