package at.ac.tuwien.ase09.data;

import org.apache.commons.lang3.RandomUtils;
import org.jsoup.Connection.Method;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class JsoupUtils {
	public static String getPageAndIgnoreContentType(String url, Method method, int timeout)
			throws Exception {
		Exception ex = null;
		for (int i = 0; i < 3; i++) {
			try {
				return Jsoup.connect(url).timeout(timeout).method(method).ignoreContentType(true).execute().body();
			} catch (Exception e) {
				ex = e;
			}
			Thread.sleep(RandomUtils.nextInt(2, 5) * 1000);
		}
		throw ex;
	}
	
	public static Document getPage(String url, Method method, int timeout)
			throws Exception {
		Exception ex = null;
		for (int i = 0; i < 3; i++) {
			try {
				return Jsoup.connect(url).timeout(timeout).method(method).execute().parse();
			} catch (Exception e) {
				ex = e;
			}
			Thread.sleep(RandomUtils.nextInt(2, 5) * 1000);
		}
		throw ex;
	}

	public static Document getPage(String url)
			throws Exception {
		return getPage(url, Method.GET, 3000);
	}
}
