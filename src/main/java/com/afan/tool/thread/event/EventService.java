package com.afan.tool.thread.event;

/**
 * event处理接口
 * @author cf
 *
 */
public interface EventService {
	
	void exec(Event event) throws Exception;
}