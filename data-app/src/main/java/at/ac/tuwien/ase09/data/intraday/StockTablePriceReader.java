package at.ac.tuwien.ase09.data.intraday;

import javax.batch.api.BatchProperty;
import javax.batch.api.chunk.AbstractItemReader;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

@Dependent
@Named("StockTablePriceReader")
public class StockTablePriceReader extends AbstractItemReader{
	@Inject
	@BatchProperty(name="baseUrl")
	private String baseUrl;
	
	@Override
	public Object readItem() throws Exception {
		Document doc = Jsoup.connect(baseUrl).get();
		Elements tableRows = doc.select("div.content tbody tr");
		tableRows.remove(0);	// remove header row
		for(Element tableRow : tableRows){
			String text = tableRow.select(":nth-child(2)").text();
		}
		// TODO Auto-generated method stub
		return null;
	}

}
