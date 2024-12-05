package com.macys.uop.foundation.core.utils.task;

import java.util.Date;
import java.util.List;

import org.springframework.web.client.HttpStatusCodeException;

import com.macys.uop.foundation.core.utils.Constant;

/**
 * Simple Implementation of command. Runs the series of steps and stops
 * execution as soon as it encounters an error. The exception is captured in the
 * execution object.
 * 
 * Implements ShouldCommandRun Strategy
 * 
 * @author BH16963
 *
 */
public class SimpleTask extends AbstractTask {

	public SimpleTask(String name) {
		setName(name);
		setShouldStepRunDecider(new StepRetryDecider());
	}

	@Override
	public void doExecute(TaskExecution execution) {

		for (Step step : steps) {
			StepExecution stepExecution = execution.getStepExecution(step.getName());
			if (stepExecution == null) {
				stepExecution = new StepExecution(step.getName());
				execution.addStepExecution(stepExecution);
				stepExecution.setTaskExecution(execution);
			}
			stepExecution.setSkipped(!shouldStepRun(stepExecution));
		}

		for (StepExecution stepExecution : execution.getStepExecutions()) {
			boolean failureFlag = false;
			if (!stepExecution.isSkipped()) {
				Step step = getStep(stepExecution.getName());
				step.execute(stepExecution);
				if (stepExecution.getStatus().equals(TaskStatus.FAILURE.name())) {
					execution.setError(stepExecution.getError());
					execution.setStatus(TaskStatus.FAILURE.name());
					failureFlag = true;
					break;
				} else {
					execution.setStatus(TaskStatus.IN_PROGRESS.name());
				}
			}
			execution.setStatus(failureFlag ? TaskStatus.FAILURE.name() : TaskStatus.SUCCESS.name());
		}

	}

	/**
	 * Decides if a step should run or not based on the given strategies
	 * 
	 * @param command
	 * @return boolean
	 */
	private boolean shouldStepRun(StepExecution stepExecution) {
		if (getShouldStepRunDecider() != null) {
			return getShouldStepRunDecider().decide(stepExecution);
		}
		return true;
	}

}
