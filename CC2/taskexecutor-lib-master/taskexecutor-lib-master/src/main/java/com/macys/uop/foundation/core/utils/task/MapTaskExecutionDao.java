package com.macys.uop.foundation.core.utils.task;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Map implementation of TaskExecutionDao. Stores executions in a map. * 
 * 
 * @author BH16963
 *
 */
public class MapTaskExecutionDao implements TaskExecutionDao {

	private final ConcurrentMap<String, TaskExecution> executionsById = new ConcurrentHashMap<>();

	@Override
	public void saveTaskExecution(TaskExecution taskExecution) {
		executionsById.put(taskExecution.getId(), taskExecution);

	}

	@Override
	public void deleteTaskExecution(String taskId) {
		executionsById.remove(taskId);
	}

	@Override
	public TaskExecution fetchTaskExecution(String taskId) {
		return executionsById.get(taskId);
	}

}