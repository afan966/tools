package com.afan.tool.thread.event;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.afan.tool.thread.PollingThread;

/**
 * 队列消费事件
 * @author afan
 *
 * @param <T>
 */
public class EventProcessThread<T extends Event> extends PollingThread {
	private static final Logger logger = LoggerFactory.getLogger(EventProcessThread.class);
	private int waitTimeout = 1000;
	private int capacity = 100;
	private Set<Integer> running = new HashSet<Integer>();
	private EventDao<T> eventDao;
	private List<LinkedBlockingQueue<T>> tasks;
	private EventService service;
	private AtomicInteger counter = new AtomicInteger();

	private int processMode = 0;//处理完删除掉

	public EventProcessThread() {
		super("EventProcessThread");
	}

	public void start() {
		tasks = new ArrayList<LinkedBlockingQueue<T>>();
		for (int i = 0; i < getPoolsize(); i++) {
			tasks.add(new LinkedBlockingQueue<T>(capacity));
		}
		super.start();
	}

	public boolean addTask(int order, T temp) {
		try {
			tasks.get(order).put(temp);
			return true;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return false;
	}

	public int getRemainCapacity(int order) {
		return tasks.get(order).remainingCapacity() - (running.contains(order) ? 1 : 0);
	}

	public void work(int order) {
		T temp;
		try {
			temp = tasks.get(order).poll(waitTimeout, TimeUnit.MILLISECONDS);
			if (null != temp) {
				try {
					running.add(order);
					dispatchEvent(temp);
					if (processMode == 0){
						eventDao.delete(temp.getId());
					}
				} catch (Exception e) {
					e.printStackTrace();
					if (processMode == 0)
						eventDao.updateStatus(temp.getId(), EventDao.Status.INIT);
				} finally {
					running.remove(order);
					counter.incrementAndGet();
				}
			}
		} catch (InterruptedException e) {
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("", e);
		}
	}

	void dispatchEvent(T event) throws Exception {
		service.exec(event);
	}

	@Override
	public void destory() {

	}

	public int getWaitTimeout() {
		return waitTimeout;
	}

	public void setWaitTimeout(int waitTimeout) {
		this.waitTimeout = waitTimeout;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public EventService getService() {
		return service;
	}

	public void setService(EventService service) {
		this.service = service;
	}

	public EventDao<T> getEventDao() {
		return eventDao;
	}

	public void setEventDao(EventDao<T> eventDao) {
		this.eventDao = eventDao;
	}

	public List<Integer> getTaskSize() {
		List<Integer> size = new ArrayList<Integer>();
		for (LinkedBlockingQueue<T> list : tasks) {
			size.add(list.size());
		}
		return size;
	}

	public AtomicInteger getCounter() {
		return counter;
	}

	public int getProcessMode() {
		return processMode;
	}

	public void setProcessMode(int processMode) {
		this.processMode = processMode;
	}
}
