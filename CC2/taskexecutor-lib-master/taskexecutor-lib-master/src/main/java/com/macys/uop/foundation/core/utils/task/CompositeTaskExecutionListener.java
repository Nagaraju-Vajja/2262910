package com.macys.uop.foundation.core.utils.task;

import java.util.ArrayList;
import java.util.List;

public class CompositeTaskExecutionListener implements TaskExecutionListener {

	List<TaskExecutionListener> listeners = new ArrayList<>();

	public void register(TaskExecutionListener listener) {
		this.listeners.add(listener);
	}

	@Override
	public void beforeTask(TaskExecution taskExecution) {
		for (TaskExecutionListener listener : listeners) {
			listener.beforeTask(taskExecution);
		}
	}

	@Override
	public void afterTask(TaskExecution taskExecution) {
		for (TaskExecutionListener listener : listeners) {
			listener.afterTask(taskExecution);
		}
	}

}