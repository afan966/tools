package com.afan.tool.thread;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;
import com.afan.tool.string.StringUtil;

public class ThreadFactoryImpl implements ThreadFactory {
	private final AtomicLong threadIndex = new AtomicLong(0);
	private final String threadNamePrefix;

	public ThreadFactoryImpl(final String threadNamePrefix) {
		this.threadNamePrefix = threadNamePrefix;
	}

	public Thread newThread(Runnable r) {
		return new Thread(r, threadNamePrefix + StringUtil.UNDERLINE + this.threadIndex.incrementAndGet());

	}
}