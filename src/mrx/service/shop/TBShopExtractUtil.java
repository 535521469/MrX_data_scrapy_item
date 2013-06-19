package mrx.service.shop;

import java.util.HashMap;
import java.util.Map;

import mrx.service.ResponseWrapper;

import org.apache.commons.lang.StringUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;

import com.google.common.base.Joiner;

public class TBShopExtractUtil {

	/**
	 * <form id="J_TBSearchForm" action="http://s.taobao.com/search" class="">
	 * <label for="shop-q">œÎ’“ ≤√¥±¶±¥?</label> <input type="text" id="shop-q"
	 * name="q" autocomplete="off" class="shop-text"> <input type="hidden"
	 * name="searcy_type" value="item"> <input type="hidden" value="newHeader"
	 * name="s_from"> <input type="hidden" name="source"> <input type="hidden"
	 * name="ssid" value="s5-e"> <input type="hidden" name="search" value="y">
	 * <span class="instore"> <button class="searchmy"
	 * data-action="http://moonwxy.taobao.com/" hidefocus="true"
	 * type="button">À—±æµÍ</button> </span> <span class="global"> <button
	 * class="searchtb" type="submit">À—Ã‘±¶</button> </span> <b
	 * class="shop-radius-l"></b> <input type="hidden" name="initiative_id"
	 * value="shopz_20130619"></form>
	 * 
	 * @param responseWrapper
	 * @return
	 */
	private static final String extractUrl2(Element searchFormElement,
			Logger logger) {
		String actionUrlString = null;

		if (null != searchFormElement) {

			actionUrlString = searchFormElement.attr("action");

			if (!StringUtils.isBlank(actionUrlString)) {
				Map<String, String> paramsMap = new HashMap<String, String>();
				Elements inputElements = searchFormElement
						.select("input[name]");

				if (null != inputElements) {
					actionUrlString += "?";
				}
				for (Element inputElement : inputElements) {
					String valString = inputElement.attr("value");
					String nameString = inputElement.attr("name");
					paramsMap.put(nameString, valString);
				}

				String paramsString = Joiner.on("&").withKeyValueSeparator("=")
						.join(paramsMap);

				String urlString = actionUrlString + paramsString;
				logger.debug(urlString);
				return urlString;

			}
		}

		return actionUrlString;
	}

	/**
	 * <form id="J_TShopSearchForm" class="search-form"
	 * action="http://s.taobao.com/search"> <label for="shop-q"
	 * class="search-tips">œÎ’“ ≤√¥±¶±¥?</label> <input type="text" id="shop-q"
	 * name="q" autocomplete="off" class="search-input" style="width: 398px;">
	 * <input type="hidden" name="searcy_type" value="item"> <input
	 * type="hidden" value="newHeader" name="s_from"> <input type="hidden"
	 * name="source"> <input type="hidden" name="ssid" value="s5-e"> <input
	 * type="hidden" name="search" value="y"> <span
	 * class="search-button searchtb">À—Ã‘±¶</span> <span
	 * class="search-button searchmy"
	 * data-action="http://zuidefu.taobao.com/search.htm" hidefocus="true"> À—±æµÍ
	 * </span> </form>
	 * 
	 * @param responseWrapper
	 * @return
	 */
	private static final String extractUrl1(Element searchFormElement,
			Logger logger) {
		String actionUrlString = null;

		if (null != searchFormElement) {

			Elements spanElements = searchFormElement.select("span");

			Map<String, String> paramsMap = new HashMap<String, String>();

			for (Element spanElement : spanElements) {
				String spanTxtString = StringUtils.trim(spanElement.text());
				// \u641C\u672C\u5E97 : sou ben dian
				if ("\u641C\u672C\u5E97".equals(spanTxtString)) {
					actionUrlString = spanElement.attr("data-action");
					break;
				}
			}

			if (!StringUtils.isBlank(actionUrlString)) {
				Elements inputElements = searchFormElement
						.select("input[name]");

				if (null != inputElements) {
					actionUrlString += "?";
				}
				for (Element inputElement : inputElements) {
					String valString = inputElement.attr("value");
					String nameString = inputElement.attr("name");
					paramsMap.put(nameString, valString);
					actionUrlString = actionUrlString + nameString + valString;
				}

				String paramsString = Joiner.on("&").withKeyValueSeparator("=")
						.join(paramsMap);

				String urlString = actionUrlString + paramsString;
				logger.debug(urlString);
				return urlString;
			}
		}
		return actionUrlString;
	}

	public static final String extractUrl(ResponseWrapper responseWrapper,
			Logger logger) {

		String actionUrlString = null;

		Element doc = responseWrapper.getDoc();

		Element searchFormElement = doc.select("#J_TShopSearchForm").first();
		if (StringUtils.isBlank(actionUrlString) && null != searchFormElement) {
			actionUrlString = extractUrl1(searchFormElement, logger);
		}

		if (StringUtils.isBlank(actionUrlString)) {
			searchFormElement = doc.select("#J_TBSearchForm").first();
		}

		if (StringUtils.isBlank(actionUrlString) && null != searchFormElement) {
			actionUrlString = extractUrl2(searchFormElement, logger);
		}

		return actionUrlString;

	}
}
