package at.ac.tuwien.ase09.data.fund.detail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.batch.api.AbstractBatchlet;
import javax.batch.api.BatchProperty;
import javax.batch.runtime.context.JobContext;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.Connection.Method;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import at.ac.tuwien.ase09.data.JsoupUtils;
import at.ac.tuwien.ase09.data.StepExitStatus;
import at.ac.tuwien.ase09.data.ValuePaperDataAccess;
import at.ac.tuwien.ase09.data.model.FundDetailLinkModel;
import at.ac.tuwien.ase09.model.Stock;

@Dependent
@Named
public class FundDetailLinkReaderBatchlet extends AbstractBatchlet {
	private static final Logger LOG = Logger.getLogger(FundDetailLinkReaderBatchlet.class.getName());
	private static final Pattern keyPattern = Pattern.compile("@key=\\\\'([.0-9]+)");
	private static final String detailLinkParameterTemplate = "ab_id=&addoncontext=&checks=%23&context=%2Fpool_data%2Ffonds_stammdaten%2Ffonds&dxpath=*%5Bhead%5B*%5D%20and%20tab%5Bfps%5B*%5D%5D%5D&dxsl=%2Fpresent%2Fstammdaten%2Ffund_sd_fps.xsl&help_page=3&infotext=null&items_bis=&items_von=&name=Preisabfrage%20inl%C3%A4ndische%20Fonds&nav=&ordner=&others=&pdf_xsl=&resetpath=&rxpath=&search_id=&source=&stich_date=&sxpath=*%5B%40key%3D'#{keyPlaceholder}%7C'%5D&sxsl=&templateContext=null&title_query=null&todo=detail&type=search&withdownloadoption=null";
	private static final String simpleDetailLinkPageParameters = "context=/pool_data/fonds_suche/fonds&directnav=preisabf&dxpath=*[allg[isin%20and%20bezeichnung%20and%20kag[name]%20and%20whrg[iso]]][kenngr[preis[preis_r[wert]%20and%20preis_e[wert]]]]&dxsl=/present/result/preis_list_public.xsl&name=Preisabfrage%20inländische%20Fonds&rxpath=&s_allg__Status__status=A&s_allg__bezeichnung=&s_allg__isin=*&s_allg__region=INL&todo=search&type=search";
	@Inject
	@BatchProperty(name="profitwebUrl")
	private String profitwebUrl;
	
	@Inject
	private JobContext jobContext;
	
	@Override
	public String process() throws Exception {
		List<FundDetailLinkModel> fundDetailLinks = new ArrayList<>();
		Document smallDetailLinkPage = JsoupUtils.getPage(profitwebUrl + "?" + simpleDetailLinkPageParameters, Method.POST, 10000);
		Element linkNumberField = smallDetailLinkPage.select("table.level_start td.blaettern:nth-child(2)").get(0);
		int linkNumber = Integer.parseInt(linkNumberField.text().split(" ")[0]);
		LOG.info("Loading full fund list...");
//		Document fullDetailLinkPage = smallDetailLinkPage; // for testing
		Document fullDetailLinkPage = JsoupUtils.getPage(profitwebUrl + "?" + simpleDetailLinkPageParameters + "&items_von=0&items_bis=" + linkNumber, Method.POST, 60000);
		Elements detailLinks = fullDetailLinkPage.select("#level_1_results tr:not(.table_header) td:nth-child(16) a");
		for(Element detailLink : detailLinks){
			String rawDetailLink = detailLink.attr("href");
			Matcher keyMatcher = keyPattern.matcher(rawDetailLink);
			if(keyMatcher.find()){
				String key = keyMatcher.group(1);
				fundDetailLinks.add(new FundDetailLinkModel(profitwebUrl + "?" + detailLinkParameterTemplate.replaceAll("\\#\\{keyPlaceholder\\}", key), key));
			}
			
		}
		
		jobContext.setTransientUserData(fundDetailLinks);
		LOG.info("Extracted " + fundDetailLinks.size() + " fund detail links");
		return StepExitStatus.COMPLETED.toString();
	}
}
