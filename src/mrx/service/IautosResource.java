package mrx.service;

import java.util.Comparator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IautosResource {

	public static ThreadPoolExecutor threadPool = (ThreadPoolExecutor) Executors
			.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1);

	protected static final Logger getLogger() {
		return LoggerFactory.getLogger(IautosResource.class);
	}

	public static BlockingQueue<Fetcher> fetchQueue = new PriorityBlockingQueue<Fetcher>(
			512, new Comparator<Fetcher>() {

				@Override
				public int compare(Fetcher f1, Fetcher f2) {
					return f1.getRequestWrapper().getPriority()
							- f2.getRequestWrapper().getPriority();
				}
			});

	public static BlockingQueue<Callback> extractQueue = new LinkedBlockingQueue<Callback>();


}
