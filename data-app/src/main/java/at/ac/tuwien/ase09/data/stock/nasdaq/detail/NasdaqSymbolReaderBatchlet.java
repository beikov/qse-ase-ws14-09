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
import at.ac.tuwien.ase09.data.model.SymbolModel;

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

		Elements symbolTableRows = boerse
				.select("table.data.quoteTable tr");
		List<SymbolModel> symbolModels = new ArrayList<>();
		
		for (Element symbolTableRow : symbolTableRows){
			String symbol = symbolTableRow.getElementsByAttributeValue("data-field", "symbol").text();
			String name = symbolTableRow.getElementsByAttributeValue("data-field", "name").text();
			symbolModels.add(new SymbolModel(symbol, name));
		}
		
		jobContext.setTransientUserData(symbolModels);
		
		LOG.info("Extracted " + symbolModels.size() + " stock symbol models");
		
		return StepExitStatus.COMPLETED.toString();
	}

}