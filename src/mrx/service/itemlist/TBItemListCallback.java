package mrx.service.itemlist;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mrx.FetcherConstants;
import mrx.service.Callback;
import mrx.service.Fetcher;
import mrx.service.RequestWrapper;
import mrx.service.RequestWrapper.PriorityEnum;
import mrx.service.item.TBItemCallback;
import mrx.service.shop.TBShopFetcher;

public class TBItemListCallback extends Callback {

	@Override
	public Map<String, Collection<?>> call() throws Exception {
		Map<String, Collection<?>> resultMap = new HashMap<String, Collection<?>>();
		Collection<Fetcher> fetchers = new ArrayList<Fetcher>();
		resultMap.put(FetcherConstants.Fetcher, fetchers);

		this.getLogger().info("...");

		List<String> itemUrlList = TBItemListExtractUtil.getItemUrls(
				this.getResponseWrapper(), this.getLogger());

		if (!itemUrlList.isEmpty()) {

			for (String itemUrl : itemUrlList) {
				Fetcher itemListFetcher = new TBShopFetcher(new RequestWrapper(
						itemUrl, new TBItemCallback(), this
								.getResponseWrapper().getReferRequestWrapper(),
						PriorityEnum.ITEM));
				fetchers.add(itemListFetcher);
			}
		}

		return resultMap;
	}

}
