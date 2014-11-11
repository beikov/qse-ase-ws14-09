package at.ac.tuwien.ase09.data.stock.intraday;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.batch.api.BatchProperty;
import javax.batch.api.chunk.AbstractItemReader;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import at.ac.tuwien.ase09.data.JsoupUtils;
import at.ac.tuwien.ase09.data.factory.WebClientFactory;
import at.ac.tuwien.ase09.data.model.IntradayPrice;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

@Dependent
@Named("StockTablePriceReader")
public class StockTablePriceReader extends AbstractItemReader{
	@Inject
	@BatchProperty(name="baseUrl")
	private String baseUrl;
	
	@Inject
	@BatchProperty(name="indexName")
	private String indexName;

	private List<IntradayPrice> extractedPrices;
	
	@Override
	public Object readItem() throws Exception {
		if(extractedPrices != null){
			return null;
		}
		Document doc = JsoupUtils.getPage(baseUrl + "?TYPE=" + indexName);
		Elements isinCells = doc.select("#marketdata_list tbody td:nth-child(3)");
		Elements priceCells = doc.select("#marketdata_list tbody td:nth-child(5)");
		extractedPrices = new ArrayList<IntradayPrice>();
		for(int i = 0; i < isinCells.size(); i++){
			String[] isinCellParts = isinCells.get(i).text().split("\\s");
			String isin = isinCellParts[isinCellParts.length-1];
			String lastPriceStr = priceCells.get(i).text().replace(',', '.');
			extractedPrices.add(new IntradayPrice(isin, new BigDecimal(lastPriceStr)));
		}
		return extractedPrices;
	}

}
