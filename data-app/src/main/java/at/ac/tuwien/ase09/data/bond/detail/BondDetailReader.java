package at.ac.tuwien.ase09.data.bond.detail;

import java.io.Serializable;
import java.util.Currency;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.batch.api.chunk.AbstractItemReader;
import javax.batch.runtime.context.StepContext;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import at.ac.tuwien.ase09.data.JsoupUtils;
import at.ac.tuwien.ase09.model.Bond;
import at.ac.tuwien.ase09.model.Stock;

@Dependent
@Named("BondDetailReader")
public class BondDetailReader extends AbstractItemReader {
	@Inject
	private StepContext stepContext;
	
	private List<String> bondDetailLinks;
	private final Currency currency = Currency.getInstance("EUR");
	
	private Integer linkNumber;
	
	@Override
	public void open(Serializable checkpoint) throws Exception {
		bondDetailLinks = ((List<String>) stepContext.getPersistentUserData());
		if(checkpoint != null){
			linkNumber = (Integer) checkpoint;
		}else{
			linkNumber = 0;
		}
	}
	
	@Override
	public Object readItem() throws Exception {
		if(linkNumber >= bondDetailLinks.size()){
			return null;
		}
		String bondDetailLink = bondDetailLinks.get(linkNumber);
		Document detailPage = JsoupUtils.tryGetPage(bondDetailLink);
		Elements elements = detailPage.select("div.summary div.left td:nth-child(1)");
		String name = elements.get(0).text();
		String isin = elements.get(1).text().replaceAll("ISIN: ", "");
		String currencyCode = detailPage.select("div.col100 tr:nth-child(8) td").get(0).text();
		Bond bond = new Bond();
		
		bond.setIsin(isin);
		bond.setCurrency(Currency.getInstance(currencyCode));
		bond.setName(name);
		
		linkNumber++;
		return bond;
	}
	
	@Override
	public Serializable checkpointInfo() throws Exception {
		return linkNumber;
	}
	
}
