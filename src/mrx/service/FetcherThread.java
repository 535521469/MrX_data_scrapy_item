package mrx.service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import mrx.ConfigManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FetcherThread extends Thread {

	protected final Logger getLogger() {
		return LoggerFactory.getLogger(this.getClass());
	}

	@Override
	public void run() {

		ThreadPoolExecutor pe = IautosResource.threadPool;

		Fetcher fetcher = null;

		int fetcher_idle_sleep = ConfigManager.getInstance().getConfigItem(
				IautosConstant.FETCHER_IDLE_SLEEP, 30);

		while (!isInterrupted()) {
			try {

				fetcher = IautosResource.fetchQueue.poll(fetcher_idle_sleep,
						TimeUnit.SECONDS);
				if (null == fetcher) {
					getLogger().info("non fetchable ...");
					continue;
				}

				if (fetcher.getRequestWrapper().getReferRequestWrappers()
						.size() > 0) {

					getLogger().debug(
							"fetch priority "
									+ fetcher.getRequestWrapper().getPriority()
									+ " "
									+ fetcher.getRequestWrapper().getUrl()
									+ " refer to "
									+ fetcher.getRequestWrapper()
											.getLastRequestUrl());
				} else {
					getLogger().debug(
							"fetch priority "
									+ fetcher.getRequestWrapper().getPriority()
									+ " "
									+ fetcher.getRequestWrapper().getUrl());
				}

				Future<ResponseWrapper> fu = pe.submit(fetcher);

				try {
					ResponseWrapper responseWrapper = fu.get();
					if (null != responseWrapper) {
						Callback cb = fetcher.getRequestWrapper().getCallback();
						cb.setResponseWrapper(responseWrapper);
						boolean offeredFlag = false;

						do {
							offeredFlag = IautosResource.extractQueue.offer(cb,
									500, TimeUnit.MILLISECONDS);

							getLogger().debug(
									"offer callback "
											+ cb.getClass()
											+ " refer to "
											+ cb.getResponseWrapper()
													.getReferRequestWrapper()
													.getUrl());

						} while (!offeredFlag);
					} else {
						getLogger().debug(
								"ignore fetch :"
										+ fetcher.getRequestWrapper().getUrl());
					}
				} catch (ExecutionException e) {
					e.printStackTrace();
					continue;
				}

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}
}
