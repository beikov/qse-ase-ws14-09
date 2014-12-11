package at.ac.tuwien.ase09.data.stock.nasdaq.detail;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.batch.api.AbstractBatchlet;
import javax.batch.api.BatchProperty;
import javax.batch.runtime.context.JobContext;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import net.ricecode.similarity.JaroWinklerStrategy;
import net.ricecode.similarity.SimilarityStrategy;
import net.ricecode.similarity.StringSimilarityService;
import net.ricecode.similarity.StringSimilarityServiceImpl;

import org.jsoup.Connection.Method;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import at.ac.tuwien.ase09.data.JsoupUtils;
import at.ac.tuwien.ase09.data.StepExitStatus;
import at.ac.tuwien.ase09.data.model.StockDetailLinkModel;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@Dependent
@Named
public class NasdaqSymbolReaderBatchlet extends AbstractBatchlet {
	private static final Logger LOG = Logger.getLogger(NasdaqSymbolReaderBatchlet.class.getName());
	private static final String CNBC_STOCK_DETAIL_QUERY1 = "http://quote.cnbc.com/quote-html-webservice/quote.htm?partnerId=2&requestMethod=quick&exthrs=1&noform=1&fund=1&output=jsonp&symbols=AAPL|ADBE|ADI|ADP|ADSK|AKAM|ALTR|ALXN|AMAT|AMGN|AMZN|ATVI|AVGO|BBBY|BIDU|BIIB|BRCM|CA|CELG|CERN|CHKP|CHRW|CHTR|CMCSA|COST|CSCO|CTRX|CTSH|CTXS|DISCA";
	private static final String CNBC_STOCK_DETAIL_QUERY2 = "http://quote.cnbc.com/quote-html-webservice/quote.htm?partnerId=2&requestMethod=quick&exthrs=1&noform=1&fund=1&output=jsonp&symbols=DISH|DLTR|DTV|EBAY|EQIX|ESRX|EXPD|EXPE|FAST|FB|FFIV|FISV|FOSL|FOXA|GILD|GMCR|GOOG|GOOGL|GRMN|HSIC|INTC|INTU|ILMN|ISRG|KLAC|KRFT|LBTYA|QVCA|LLTC|LMCA";
	private static final String CNBC_STOCK_DETAIL_QUERY3 = "http://quote.cnbc.com/quote-html-webservice/quote.htm?partnerId=2&requestMethod=quick&exthrs=1&noform=1&fund=1&output=jsonp&symbols=MAR|MAT|MDLZ|MNST|MSFT|MU|MXIM|MYL|NFLX|NTAP|NVDA|NXPI|ORLY|PAYX|PCAR|PCLN|QCOM|REGN|ROST|SBAC|SBUX|SIAL|SNDK|SPLS|SRCL|STX|SYMC|TSCO|TSLA|TRIP";
	private static final String CNBC_STOCK_DETAIL_QUERY4 = "http://quote.cnbc.com/quote-html-webservice/quote.htm?partnerId=2&requestMethod=quick&exthrs=1&noform=1&fund=1&output=jsonp&symbols=TXN|VIAB|VIP|VOD|VRSK|VRTX|WDC|WFM|WYNN|XLNX|YHOO";
	
	@Inject
	@BatchProperty(name = "nasdaqListUrl")
	private String nasdaqListUrl;
	@Inject
	@BatchProperty(name = "finanzenNetIndexPath")
	private String finanzenNetIndexPath;
	
	@Inject
	@BatchProperty(name = "finanzenNetUrl")
	private String finanzenNetUrl;
	
	@Inject
	private JobContext jobContext;
	
	private final JsonParser jsonParser = new JsonParser();

	@Override
	public String process() throws Exception {
		List<TemporaryStockDetailLinkModel> tmpStockDetailLinkModels = new ArrayList<NasdaqSymbolReaderBatchlet.TemporaryStockDetailLinkModel>();
		Document finanzenNet = JsoupUtils.getPage(finanzenNetUrl + finanzenNetIndexPath);
		Elements linkCells = finanzenNet.select("#index-list-container td:nth-child(1)");
		// finanzen.net table
		for (Element current : linkCells) {
			Element linkElem = current.select("a").first();
			String isin = current.ownText();
			String companyName = linkElem.text();
			tmpStockDetailLinkModels.add(new TemporaryStockDetailLinkModel(finanzenNetUrl + linkElem.attr("href"), isin, companyName));
		}

		JsonArray quickQuotes = getQuickQuotes(CNBC_STOCK_DETAIL_QUERY1);
		quickQuotes.addAll(getQuickQuotes(CNBC_STOCK_DETAIL_QUERY2));
		quickQuotes.addAll(getQuickQuotes(CNBC_STOCK_DETAIL_QUERY3));
		quickQuotes.addAll(getQuickQuotes(CNBC_STOCK_DETAIL_QUERY4));
	    
		for(int i = 0; i < quickQuotes.size(); i++){
			JsonObject quickQuote = quickQuotes.get(i).getAsJsonObject();
			String companyName = quickQuote.get("name").getAsString();
			String simplifiedCompanyName = companyName.replaceAll("[\\s.]", "").toLowerCase();
			
			// run through models and find the corresponding entry based on company name similarity
			// stupid but there seems to be no other way
			TemporaryStockDetailLinkModel matchingModel = null;
			for(TemporaryStockDetailLinkModel tmpModel : tmpStockDetailLinkModels){
				String simplifiedModelCompanyName = tmpModel.companyName.replaceAll("[\\s.]", "").toLowerCase();
				if((tmpModel.companyName.contains(companyName) || companyName.contains(tmpModel.companyName) || simplifiedCompanyName.contains(simplifiedModelCompanyName) || simplifiedModelCompanyName.contains(simplifiedCompanyName) || getSimilarity(simplifiedCompanyName, simplifiedModelCompanyName) > 0.85) &&
						tmpModel.tickerSymbol == null){
					matchingModel = tmpModel;
					break;
				}
			}
			if(matchingModel != null){
				matchingModel.tickerSymbol = quickQuote.get("symbol").getAsString();
			}else{
				LOG.info("Could not find symbol matching for symbol " + companyName);
			}
		}
		
		// take only such entries where we found a matching ticker symbol
		List<StockDetailLinkModel> stockDetailLinkModels = tmpStockDetailLinkModels.stream().filter(tmpModel -> tmpModel.tickerSymbol != null).map(tmpModel -> new StockDetailLinkModel(tmpModel.detailLink, tmpModel.companyName, tmpModel.isin, tmpModel.tickerSymbol)).collect(Collectors.toList());
		jobContext.setTransientUserData(stockDetailLinkModels);
		LOG.info("Extracted " + tmpStockDetailLinkModels.size() + " stock symbol models");
		return StepExitStatus.COMPLETED.toString();
	}
	
	private double getSimilarity(String s1, String s2){
		SimilarityStrategy strategy = new JaroWinklerStrategy();
		StringSimilarityService service = new StringSimilarityServiceImpl(strategy);
		return service.score(s1, s2); // Score is 0.90
	}
	
	private class TemporaryStockDetailLinkModel {
		private final String detailLink;
		private final String isin;
		private final String companyName;
		private String tickerSymbol;
		
		public TemporaryStockDetailLinkModel(String detailLink, String isin,
				String companyName) {
			super();
			this.detailLink = detailLink;
			this.isin = isin;
			this.companyName = companyName;
		}
	}
	
	private JsonArray getQuickQuotes(String url) throws Exception{
		String cnbcNasdaqDetails = JsoupUtils.getPageAndIgnoreContentType(url, Method.GET, 3000);
		cnbcNasdaqDetails = cnbcNasdaqDetails.substring(cnbcNasdaqDetails.indexOf('(') + 1, cnbcNasdaqDetails.lastIndexOf(')'));
	    JsonObject detailsObject = jsonParser.parse(cnbcNasdaqDetails).getAsJsonObject();
	    return detailsObject.getAsJsonObject("QuickQuoteResult").getAsJsonArray("QuickQuote");
	}

}
