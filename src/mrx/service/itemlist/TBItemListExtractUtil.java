package mrx.service.itemlist;

import java.util.ArrayList;
import java.util.List;

import mrx.service.ResponseWrapper;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;

public class TBItemListExtractUtil {

	public static final List<String> getItemUrls(
			ResponseWrapper responseWrapper, Logger logger) {
		List<String> urlStrings = new ArrayList<String>();
		Element doc = responseWrapper.getDoc();
		Elements itemElements = doc.select("dl.item");
		for (Element itemElement : itemElements) {
			Element aElement = itemElement.select("a.item-name").first();
			String urlString = aElement.attr("href");
			urlStrings.add(urlString);
		}
		return urlStrings;
	}

}
