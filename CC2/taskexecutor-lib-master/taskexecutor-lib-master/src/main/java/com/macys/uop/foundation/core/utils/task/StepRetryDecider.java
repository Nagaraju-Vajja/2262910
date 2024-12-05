package com.macys.uop.foundation.core.utils.task;

/**
 * Default Command Retry Strategy Implementation. if command is repeatable or
 * previous execution was not a success run the command
 * 
 * Override this class to provide your own implementation. But this class would
 * be useful for 90% of the use cases
 * 
 * @author BH16963
 *
 */
public class StepRetryDecider implements ShouldStepRunDecider {

	/**
	 * Retry strategy: if command is repeatable or previous execution was not a
	 * success run the command
	 */
	@Override
	public boolean decide(StepExecution stepExecution) {

		TaskStatus status = TaskStatus.valueOf(stepExecution.getStatus());
		switch (status) {
		case NOT_RUN:
			return true;
		case FAILURE:
			return true;
		case SUCCESS:
			return false;
		}

		return true;
	}
}