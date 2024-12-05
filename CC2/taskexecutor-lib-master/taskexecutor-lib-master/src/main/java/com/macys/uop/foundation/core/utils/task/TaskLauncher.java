package com.macys.uop.foundation.core.utils.task;

public interface TaskLauncher {
	public TaskExecution run(final Task task, TaskParameters taskParameters);
}
