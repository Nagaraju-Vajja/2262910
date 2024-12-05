package com.macys.uop.foundation.core.utils.task;

public class SimpleTaskLauncher implements TaskLauncher{
	
	private TaskRepository taskRepository = null;
	
	@Override
	public TaskExecution run(final Task task, TaskParameters taskParameters) {

		TaskExecution execution = getTaskRepository().getLastTaskExecution(task, taskParameters);
		
		if (execution == null) {
			execution = getTaskRepository().createTaskExecution(task, taskParameters);
		}
		
		task.execute(execution);
		
		return execution;
	}

	public TaskRepository getTaskRepository() {
		return taskRepository;
	}

	public void setTaskRepository(TaskRepository taskRepository) {
		this.taskRepository = taskRepository;
	}

	
}
