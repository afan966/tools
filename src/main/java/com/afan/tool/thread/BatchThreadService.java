package com.afan.tool.thread;

public interface BatchThreadService {
	void work(int order, BatchThreadGroup group) throws Exception;
}
