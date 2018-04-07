package com.afan.tool.thread;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.afan.tool.string.StringUtil;

/**
 * 批量线程组
 * 
 * @author cf
 * 
 */
public class BatchThreadGroup {
	private static final Logger logger = LoggerFactory.getLogger(BatchThreadGroup.class);
	private String name;
	private CountDownLatch threadSignal;
	private ExecutorService pool;
	private int threadpoolsize;

	private boolean completed = false;

	public BatchThreadGroup(final String name, int threadpoolsize) {
		this.name = name;
		this.threadpoolsize = threadpoolsize;
		this.threadSignal = new CountDownLatch(threadpoolsize);
		pool = Executors.newFixedThreadPool(threadpoolsize, new ThreadFactoryImpl(name));
	}

	public long batchExec(final BatchThreadService service, final long timeout) throws TimeoutException {
		try {
			final BatchThreadGroup group = this;
			long t1 = System.currentTimeMillis();
			for (int i = 0; i < getThreadpoolsize(); i++) {
				final int order = i;

				pool.submit(new Runnable() {
					public void run() {
						try {
							service.work(order, group);
						} catch (Throwable e) {
							e.printStackTrace();
						} finally {
							threadSignal.countDown();
							logger.debug("completed {}", name + StringUtil.UNDERLINE + order);
						}
					}
				});
				logger.debug("stared {} >>>>>", name + StringUtil.UNDERLINE + order);
			}
			try {
				if (timeout == 0)
					threadSignal.await();
				else if (!threadSignal.await(timeout, TimeUnit.MILLISECONDS)) {
					throw new TimeoutException();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			long t2 = System.currentTimeMillis();
			logger.debug("{} compeleted time:{}ms", name, (t2 - t1));
			return t2 - t1;
		} finally {
			completed = true;
		}
	}

	public boolean isCompleted() {
		return completed;
	}

	public void forceStop() {
		this.completed = true;
		pool.shutdownNow();
		logger.debug("stoped. {} <<<<<", name);
	}

	public void graceStop() {
		this.completed = true;
	}

	public int getThreadpoolsize() {
		return this.threadpoolsize;
	}

	public static void main(String[] args) {
		final BatchThreadGroup b = new BatchThreadGroup("sss", 10);
		new Thread(new Runnable() {
			public void run() {
				while (!b.isCompleted()) {
					// System.out.println("waiting...");
					// b.graceStop();
				}
			}
		}).start();
		try {
			long ds = b.batchExec(new BatchThreadService() {
				public void work(int order, BatchThreadGroup group) throws Exception {
					try {
						// while (!group.isCompleted()) {
						Thread.sleep(4000);
						// }
					} catch (InterruptedException e) {
						// e.printStackTrace();
					}
					System.out.println(order + "." + b.getThreadpoolsize());
					// int i =1/0;
				}
			}, 0);
			System.out.println(ds);
		} catch (TimeoutException e) {
			e.printStackTrace();
		}
		b.forceStop();

	}
}
