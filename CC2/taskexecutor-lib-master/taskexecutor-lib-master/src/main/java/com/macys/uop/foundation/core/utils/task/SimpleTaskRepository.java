package com.macys.uop.foundation.core.utils.task;

import java.util.UUID;

import org.springframework.util.StringUtils;

public class SimpleTaskRepository implements TaskRepository {

	private TaskExecutionDao taskExecutionDao;

	public SimpleTaskRepository(TaskExecutionDao taskExecutionDao) {
		super();
		this.taskExecutionDao = taskExecutionDao;
	}

	@Override
	public TaskExecution createTaskExecution(Task task, TaskParameters taskParameters) {

		TaskExecution execution = new TaskExecution();
		execution.setId(UUID.randomUUID().toString());
		execution.setTaskName(task.getName());
		execution.setExecutionContext(new ExecutionContext());
		execution.setTaskParameters(taskParameters);
		return execution;
	}

	@Override
	public TaskExecution getLastTaskExecution(Task task, TaskParameters taskParameters) {
		String executionId = taskParameters.getString("executionId");
		if (!StringUtils.isEmpty(executionId)) {
			return taskExecutionDao.fetchTaskExecution(executionId);
		}
		return null;
	}

	public TaskExecutionDao getTaskExecutionDao() {
		return taskExecutionDao;
	}

	public void setTaskExecutionDao(TaskExecutionDao taskExecutionDao) {
		this.taskExecutionDao = taskExecutionDao;
	}

}
