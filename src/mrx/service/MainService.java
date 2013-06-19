package mrx.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import mrx.ConfigManager;
import mrx.service.RequestWrapper.PriorityEnum;
import mrx.service.shop.TBShopCallback;
import mrx.service.shop.TBShopFetcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainService extends Service {

	public static void main(String[] args) {
//		ConfigManager configManager = ConfigManager.getInstance();
		MainService is = new MainService();
		is.getLogger().info("start-------------------");
		is.fetch();
		is.extract();
		
		is.initOnGoing();
	}

	public void initOnGoing() {
		ScheduledThreadPoolExecutor pe = (ScheduledThreadPoolExecutor) Executors
				.newScheduledThreadPool(1);
		InitFetcherManager ccf = this.new InitFetcherManager();
		long ongoingCycleDelay = Long.valueOf(ConfigManager.getInstance()
				.getConfigItem(IautosConstant.ONGOING_CYCLE_DELAY, 86400));
		pe.scheduleAtFixedRate(ccf, 0, ongoingCycleDelay, TimeUnit.SECONDS);
	}

	@Override
	public void fetch() {
		Thread fetcher = new FetcherThread();
		fetcher.start();
	}

	@Override
	public void extract() {
		Thread extract = new ExtractThread();
		extract.start();
	}

	class InitFetcherManager implements Runnable {

		protected final Logger getLogger() {
			return LoggerFactory.getLogger(this.getClass());
		}

		private List<String> getInitUrls() {
			String[] s = { "http://shop67109681.taobao.com",
					"http://shop36505973.taobao.com",
					"http://shop33082137.taobao.com",
					"http://shop71710235.taobao.com" };
			return Arrays.asList(s);
		}

		@Override
		public void run() {

			List<Fetcher> fs = new ArrayList<Fetcher>();

			// add shop fetcher

			for (String url : this.getInitUrls()) {

				Fetcher f = new TBShopFetcher(new RequestWrapper(url,
						new TBShopCallback(), null, PriorityEnum.SHOP));
				fs.add(f);
			}

			Map<String, Integer> offered = new HashMap<String, Integer>();
			for (Object o : fs) {
				Fetcher fetcher = (Fetcher) o;
				String fetcherKey = fetcher.getClass().getName();
				boolean offeredFlag = false;
				do {

					try {
						offeredFlag = IautosResource.fetchQueue.offer(fetcher,
								500, TimeUnit.MILLISECONDS);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					getLogger().debug(
							"offer " + fetcher.getRequestWrapper().getUrl());

				} while (!offeredFlag);

				if (offered.containsKey(fetcherKey)) {
					offered.put(fetcherKey, offered.get(fetcherKey) + 1);
				} else {
					offered.put(fetcherKey, 1);
				}
			}

			for (Map.Entry<String, Integer> offer : offered.entrySet()) {
				getLogger().info(
						"add " + offer.getValue() + " fetcher task :"
								+ offer.getKey());
			}

		}

	}

}
