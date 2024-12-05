package com.macys.uop.foundation.core.utils.task;

/**
 * Represents a process to be run. The execute method should run the command
 * based on an external config.
 * 
 * @author BH16963
 *
 */
public interface Task {

	/**
	 * Command Name
	 * 
	 * @return name
	 */
	String getName();

	/**
	 * Should run the command based on the external config. Don't throw any
	 * exceptions, instead record it in the execution object.
	 * 
	 * @param execution
	 * @param config
	 */
	void execute(TaskExecution execution);


}
