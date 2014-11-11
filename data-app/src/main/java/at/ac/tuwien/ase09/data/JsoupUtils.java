package at.ac.tuwien.ase09.data;

import org.apache.commons.lang3.RandomUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class JsoupUtils {
	public static Document getPage(String url)
			throws Exception {
		Exception ex = null;
		for (int i = 0; i < 3; i++) {
			try {
				Document page = Jsoup.connect(url).get();
				return page;
			} catch (Exception e) {
				ex = e;
			}
			Thread.sleep(RandomUtils.nextInt(2, 5) * 1000);
		}
		throw ex;
	}
}
