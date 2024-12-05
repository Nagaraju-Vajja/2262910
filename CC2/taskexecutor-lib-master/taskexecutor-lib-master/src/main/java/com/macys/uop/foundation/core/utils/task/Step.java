package com.macys.uop.foundation.core.utils.task;

/**
 * An interface for a standard command step.
 * 
 * 1. A step should have a name which is unique within the command
 * 
 * This interface can be extended to add more functionality.
 * 
 * @author BH16963
 */
public interface Step {
	/**
	 * The name of a command must be unique within the list of commands.
	 * 
	 * @return Name
	 */
	String getName();

	/**
	 * Must contain the business implementation. Throw the exception if an error is
	 * encountered. THe command runner will mark the command as failed if there is a
	 * failure
	 */
	void execute(StepExecution stepExecution);
	
	/**
	 * Specifies if the command can be run multiple times without impacting the system.
	 * Set the default to false as most commands are not repeatable.
	 */
	boolean isRepeatable();

}