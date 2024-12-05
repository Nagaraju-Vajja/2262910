package com.macys.uop.foundation.core.utils.task;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.HttpStatusCodeException;

import com.macys.uop.foundation.core.utils.Constant;
/**
 * Default implementation of a command interface
 * 
 * 1. Extend this class and implement the handle method
 * 
 * 2. Handle method will contain the business implementation
 * 
 * @author BH16963
 */
public abstract class AbstractStep implements Step {

	/**
	 * Name of the command
	 */
	private String name;

	@Value("${taskExecutor.register.each.step.enabled:false}")
	private boolean registerEachStep;

	private CompositeStepExecutionListener stepExecutionListener = new CompositeStepExecutionListener();

	/**
	 * Constructor for DefaultCommand. Cannot create a command without a unique name
	 * 
	 * @param name
	 */
	public AbstractStep(String name) {
		this.name = name;
	}

	/**
	 * Gets the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the default isRepeatable return to false as most commands are not
	 * repeatable.
	 */
	@Override
	public boolean isRepeatable() {
		return false;
	}

	/**
	 * Register a step execution listener
	 *
	 */
	public void registerStepExecutionListener(StepExecutionListener listener) {
		this.getStepExecutionListener().register(listener);
	}

	/**
	 * Register a list of step execution listener
	 *
	 */
	public void setStepExecutionListeners(StepExecutionListener[] listeners) {
		for (int i = 0; i < listeners.length; i++) {
			registerStepExecutionListener(listeners[i]);
		}
	}

	public CompositeStepExecutionListener getStepExecutionListener() {
		return stepExecutionListener;
	}

	public void execute(StepExecution stepExecution) {
		try {
			stepExecution.setStartTime(new Date());
			getStepExecutionListener().beforeStep(stepExecution);
			handle(stepExecution);
			stepExecution.setEndTime(new Date());
			stepExecution.setStatus(TaskStatus.SUCCESS.name());
			if(registerEachStep) {
				getStepExecutionListener().afterStep(stepExecution);
			}
		} catch (Throwable e) {
			stepExecution.setEndTime(new Date());
			stepExecution.setErrorMessage(e.toString());
			if (e instanceof HttpStatusCodeException) {
				List<String> list = ((HttpStatusCodeException) e).getResponseHeaders().get(Constant.EXECUTIONID_HDR);
				if (list != null && list.size() > 0) {
					stepExecution.setExecutionId(list.get(0));
				}
			}
			stepExecution.setStatus(TaskStatus.FAILURE.name());
			stepExecution.setError(e);
		}

	}

	public abstract void handle(StepExecution stepExecution) throws Exception;

}