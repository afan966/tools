package com.afan.tool.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.afan.tool.string.StringUtil;

/**
 * 轮询线程
 * 
 * @author cf
 *
 */
public abstract class PollingThread {
	private static final Logger logger = LoggerFactory.getLogger(PollingThread.class);
	private int poolsize = 1;
	private String name;
	private ExecutorService[] pools;

	protected boolean closing = false;
	private ThreadLocal<Object> bundles = new ThreadLocal<Object>();// 线程内共享副本

	public PollingThread(String name) {
		this.name = name;
	}

	public void start() {
		synchronized (bundles) {
		}
		pools = new ExecutorService[poolsize];
		for (int i = 0; i < poolsize; i++) {
			final int threadNum = i;
			pools[i] = Executors.newFixedThreadPool(1, new ThreadFactory() {
				public Thread newThread(Runnable r) {
					return new Thread(r, name + StringUtil.UNDERLINE + threadNum);
				}
			});

			doWork(pools[i], i);
			logger.debug("started {} >>>>>", name + StringUtil.UNDERLINE + threadNum);
		}
	}

	public void stop() {
		friendlyStop(false);
	}

	public void friendlyStop(boolean discart) {
		closing = true;
		if (!discart) {
			destory();
		}
		for (int i = 0; i < poolsize; i++) {
			pools[i].shutdown();// 不接受提交新的线程
			logger.debug("stoping. {} <<<<<", name + StringUtil.UNDERLINE + i);
		}
		try {
			for (int i = 0; i < poolsize; i++) {
				// 等待1秒看线程是不是结束了
				if (pools[i].awaitTermination(1, TimeUnit.SECONDS)) {
					logger.debug("friendly stoped. {} <<<<<", name + StringUtil.UNDERLINE + i);
				} else {
					pools[i].shutdownNow();
					// 再等待3秒看线程是不是结束了
					if (pools[i].awaitTermination(3, TimeUnit.SECONDS)) {
						logger.debug("force stoped. {} <<<<<", name + StringUtil.UNDERLINE + i);
					} else {
						logger.error("error can't stop {} <<<<<", name + StringUtil.UNDERLINE + i);
					}
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
			logger.error("", e);
			for (int i = 0; i < poolsize; i++) {
				pools[i].shutdownNow();
			}
		}
	}

	public void doWork(final ExecutorService exec, final int order) {
		exec.submit(new Runnable() {
			public void run() {
				work(exec, order);
			}
		});
	}

	public void work(ExecutorService exec, int order) {
		try {
			work(order);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				// 让CPU可以切换
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			doWork(exec, order);
		}
	}

	public abstract void work(int order);

	public abstract void destory();

	public Object getBundle() {
		return bundles.get();
	}

	public void setBundle(Object value) {
		bundles.set(value);
	}

	public int getPoolsize() {
		return poolsize;
	}

	public void setPoolsize(int poolsize) {
		this.poolsize = poolsize;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
