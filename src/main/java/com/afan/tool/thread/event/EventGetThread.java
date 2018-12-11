package com.afan.tool.thread.event;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.afan.tool.thread.PollingThread;

/**
 * 事件放入队列
 * @author afan
 *
 * @param <T>
 */
public class EventGetThread<T extends Event> extends PollingThread {

	private static final Logger logger = LoggerFactory.getLogger(EventGetThread.class);
	private int capacity = 100;
	private EventDao<T> eventDao;
	private EventProcessThread<T> processThread;

	public EventGetThread() {
		super("EventGetThread");
	}

	public void start() {
		try {
			eventDao.initStatus(EventDao.Status.PRESENDED, EventDao.Status.INIT);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("", e);
		}
		super.start();
	}

	public void work(int order) {
		List<T> records = null;
		try {
			records = eventDao.query(getPoolsize(), order, capacity);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("", e);
		}

		if (records == null || records.size() == 0) {
			try {
				Thread.sleep(999);
			} catch (InterruptedException e) {
			}
			return;
		}

		if (processThread.getProcessMode() != 0) {
			try {
				eventDao.batchDelete(records);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		for (int i = 0; i < records.size(); i++) {
			try {
				T temp = records.get(i);
				int sendorder = getProcessThread(temp);
				if (processThread.addTask(sendorder, temp)) {
					eventDao.updateStatus(temp.getId(), EventDao.Status.PRESENDED);
				}
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("", e);
			}
		}
	}

	//子类重写这个方法可实现不同的分配规则
	protected int getProcessThread(T temp) {
		int order = 0;
		int max = 0;
		for (int i = 0; i < processThread.getPoolsize(); i++) {
			int remainCapacity = processThread.getRemainCapacity(i);
			if (max < remainCapacity) {
				max = remainCapacity;
				order = i;
			}
		}
		return order;
	}

	@Override
	public void destory() {

	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public EventDao<T> getEventDao() {
		return eventDao;
	}

	public void setEventDao(EventDao<T> eventDao) {
		this.eventDao = eventDao;
	}

	public void setProcessThread(EventProcessThread<T> processThread) {
		this.processThread = processThread;
	}

}
