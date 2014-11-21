package at.ac.tuwien.ase09.data.stock.nasdaq.detail;

import java.util.ArrayList;
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

@Dependent
@Named
public class NasdaqSymbolReaderBatchlet extends AbstractBatchlet {
	private static final Logger LOG = Logger.getLogger(NasdaqSymbolReaderBatchlet.class.getName());
	@Inject
	@BatchProperty(name = "nasdaqListUrl")
	private String nasdaqListUrl;
	
	@Inject
	private JobContext jobContext;

	@Override
	public String process() throws Exception {
		Document boerse = JsoupUtils.getPage(nasdaqListUrl);

		Elements symbolCells = boerse
				.select("table.data.quoteTable td.first.text a");
		List<String> symbols = new ArrayList<>();
		
		for (Element symbolCell : symbolCells){
			symbols.add(symbolCell.text());
		}
		
		jobContext.setTransientUserData(symbols);
		
		LOG.info("Extracted " + symbols.size() + " stock symbols");
		
		return StepExitStatus.COMPLETED.toString();
	}

}
