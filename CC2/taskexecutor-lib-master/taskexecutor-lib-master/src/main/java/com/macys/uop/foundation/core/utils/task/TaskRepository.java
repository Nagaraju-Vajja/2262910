package com.macys.uop.foundation.core.utils.task;

public interface TaskRepository {
	
	TaskExecution createTaskExecution(Task task, TaskParameters taskParameters);
	
	TaskExecution getLastTaskExecution(Task task, TaskParameters taskParameters);
}
