package at.ac.tuwien.ase09.data.stock.detail;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.batch.api.AbstractBatchlet;
import javax.batch.api.BatchProperty;
import javax.batch.api.chunk.AbstractItemReader;
import javax.batch.runtime.context.JobContext;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.RandomUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import at.ac.tuwien.ase09.data.JsoupUtils;
import at.ac.tuwien.ase09.data.factory.WebClientFactory;
import at.ac.tuwien.ase09.data.model.IntradayPrice;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

@Dependent
@Named("StockDetailLinkReaderBatchlet")
public class StockDetailLinkReaderBatchlet extends AbstractBatchlet {
	private static final Logger LOG = Logger.getLogger(StockDetailLinkReaderBatchlet.class.getName());
	@Inject
	@BatchProperty(name = "boerseUrl")
	private String boerseUrl;

	@Inject
	@BatchProperty(name = "indexName")
	private String indexName;
	
	@Inject
	private JobContext jobContext;

	@Override
	public String process() throws Exception {
		Document boerse = JsoupUtils.tryGetPage(boerseUrl + "?TYPE=" + indexName);

		Elements linkCells = boerse
				.select("#marketdata_list tbody td:nth-child(3) a");
		List<String> stockDetailLinks = new ArrayList<String>();
		for (int i = 0; i < linkCells.size(); i++) {
			stockDetailLinks.add(linkCells.get(i).attr("href"));
		}
		
		jobContext.setTransientUserData(stockDetailLinks);
		
		LOG.info("Extracted " + stockDetailLinks.size() + " stock detail links");
		
		return "COMPLETED";
	}

}
