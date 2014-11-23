package at.ac.tuwien.ase09.data.stock.atx.detail;

import java.util.HashMap;
import java.util.Map;
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

@Dependent
@Named("StockDetailLinkReaderBatchlet")
public class StockDetailLinkReaderBatchlet extends AbstractBatchlet {
	private static final Logger LOG = Logger.getLogger(StockDetailLinkReaderBatchlet.class.getName());
	@Inject
	@BatchProperty(name = "boerseUrl")
	private String boerseUrl;
	
	@Inject
	@BatchProperty(name = "finanzenNetIndexPath")
	private String finanzenNetIndexPath;
	
	@Inject
	@BatchProperty(name = "finanzenNetUrl")
	private String finanzenNetUrl;

	@Inject
	@BatchProperty(name = "indexName")
	private String indexName;
	
	@Inject
	private JobContext jobContext;
	
	@Override
	public String process() throws Exception {
		Document boerse = JsoupUtils.getPage(boerseUrl + "?TYPE=" + indexName);

		Elements linkCells = boerse
				.select("#marketdata_list tbody td:nth-child(3)");
		Map<String, String[]> stockDetailLinks = new HashMap<>();
		
		// boerse table
		for (Element current : linkCells){
			String[] cellLines = current.text().split("\\s");
			String isin = cellLines[cellLines.length - 1];
			stockDetailLinks.put(isin, new String[]{current.select("a").get(0).attr("href"), null});
		}
		
		Document finanzenNet = JsoupUtils.getPage(finanzenNetUrl + finanzenNetIndexPath + "/" + indexName);
		linkCells = finanzenNet.select("#index-list-container td:nth-child(1)");
		// finanzen.net table
		for (Element current : linkCells) {
			String[] cellLines = current.text().split("\\s");
			String isin = cellLines[cellLines.length - 1];
			String[] mapEntry = stockDetailLinks.get(isin);
			if(mapEntry != null){
				// get finanzen.net details link
				mapEntry[1] = finanzenNetUrl + current.select("a").get(0).attr("href");
			}
		}
		
		jobContext.setTransientUserData(stockDetailLinks);
		
		LOG.info("Extracted " + stockDetailLinks.size() + " stock detail links");
		
		return StepExitStatus.COMPLETED.toString();
	}

}
