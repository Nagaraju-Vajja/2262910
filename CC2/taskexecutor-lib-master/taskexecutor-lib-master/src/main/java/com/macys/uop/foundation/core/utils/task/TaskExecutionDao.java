package com.macys.uop.foundation.core.utils.task;

public interface TaskExecutionDao {

	void saveTaskExecution(TaskExecution taskExecution);

	void deleteTaskExecution(String taskId);

	TaskExecution fetchTaskExecution(String taskId);
}
