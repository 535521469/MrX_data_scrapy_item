package mrx.service.shop;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import mrx.FetcherConstants;
import mrx.service.Callback;
import mrx.service.Fetcher;
import mrx.service.RequestWrapper;
import mrx.service.RequestWrapper.PriorityEnum;
import mrx.service.itemlist.TBItemListCallback;

public class TBShopCallback extends Callback {

	@Override
	public Map<String, Collection<?>> call() throws Exception {

		Collection<Fetcher> fetchers = new ArrayList<Fetcher>();

		String url = TBShopExtractUtil.extractUrl(this.getResponseWrapper(),
				this.getLogger());

		Fetcher itemListFetcher = new TBShopFetcher(new RequestWrapper(url,
				new TBItemListCallback(), this.getResponseWrapper()
						.getReferRequestWrapper(), PriorityEnum.ITEM_LIST));

		fetchers.add(itemListFetcher);

		Map<String, Collection<?>> resultMap = new HashMap<String, Collection<?>>();
		resultMap.put(FetcherConstants.Fetcher, fetchers);

		return resultMap;
	}

}
