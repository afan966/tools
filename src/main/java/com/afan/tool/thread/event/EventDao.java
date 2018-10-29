package com.afan.tool.thread.event;

import java.util.List;

/**
 * eventDao接口
 * 
 * @author cf
 *
 */
public interface EventDao<T> {

	public static enum Status {
		INIT(0), PRESENDED(1);
		int status;

		Status(int status) {
			this.status = status;
		}

		public static Status getEnum(int code) throws RuntimeException {
			Status[] pesEnum = Status.values();
			for (int i = 0; i < pesEnum.length; i++) {
				if (pesEnum[i].getCode() == code) {
					return pesEnum[i];
				}
			}
			return null;
		}

		public int getCode() {
			return status;
		}
	}

	void initStatus(Status statusSrc, Status statusDesc) throws Exception;

	List<T> query(int poolSize, int threadNo, int capacity) throws Exception;

	void updateStatus(long id, Status status) throws Exception;

	void delete(long id) throws Exception;
	
	void batchDelete(List<T> events) throws Exception;

}
