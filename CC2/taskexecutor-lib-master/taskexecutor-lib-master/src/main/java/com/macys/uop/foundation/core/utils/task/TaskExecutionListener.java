package com.macys.uop.foundation.core.utils.task;

public interface TaskExecutionListener {

	/**
	 * Callback before a task executes.
	 *
	 */
	void beforeTask(TaskExecution taskExecution);

	/**
	 * Callback after a task executes.
	 *
	 */
	void afterTask(TaskExecution taskExecution);
}
