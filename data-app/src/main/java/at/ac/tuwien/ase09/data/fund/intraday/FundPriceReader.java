package at.ac.tuwien.ase09.data.fund.intraday;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.batch.api.chunk.AbstractItemReader;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import org.jsoup.Connection.Method;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import at.ac.tuwien.ase09.data.JsoupUtils;
import at.ac.tuwien.ase09.data.ValuePaperDataAccess;
import at.ac.tuwien.ase09.data.model.IntradayPrice;
import at.ac.tuwien.ase09.model.Fund;
import at.ac.tuwien.ase09.model.StockBond;

@Dependent
@Named
public class FundPriceReader extends AbstractItemReader{
	private List<Fund> funds;
	
	private int itemNumber;
	
	@Inject
	private ValuePaperDataAccess valuePaperDataAccess;
	
	@Override
	public void open(Serializable checkpoint) throws Exception {
		if(checkpoint == null){
			funds = valuePaperDataAccess.getValuePapers(Fund.class);
			itemNumber = 0;
		}else{
			itemNumber = (Integer) checkpoint;
		}
	}
	
	@Override
	public Object readItem() throws Exception {
		if(itemNumber >= funds.size()){
			return null;
		}
		Fund fund = funds.get(itemNumber);
		Document fundDetailPage = JsoupUtils.getPage(fund.getDetailUrl(), Method.POST, 10000);
		Elements tableRowElems = fundDetailPage.select("tr");
		Map<String, String> tableRows =tableRowElems.stream()
			.filter(elem -> elem.children().stream().filter(child -> "colorth".equals(child.className())).count() == 1)
			.collect(Collectors.toMap(elem -> elem.getElementsByClass("labelB").get(0).text(), elem -> elem.getElementsByClass("label").get(0).text()));
		String priceStr = tableRows.get("Ausgabepreis").replaceAll("\\.", "").replace(',', '.');

		itemNumber++;
		return new IntradayPrice(fund.getCode(), new BigDecimal(priceStr));
	}

}
