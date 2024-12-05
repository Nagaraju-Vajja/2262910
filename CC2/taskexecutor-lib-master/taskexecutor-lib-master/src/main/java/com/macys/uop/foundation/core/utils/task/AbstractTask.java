package com.macys.uop.foundation.core.utils.task;

import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractTask implements Task {

	protected List<Step> steps = new ArrayList<Step>();
	protected String name;
	private ShouldStepRunDecider shouldStepRunDecider;
	private CompositeTaskExecutionListener taskExecutionListener = new CompositeTaskExecutionListener();

	@Value("${taskExecutor.register.before.task.enabled:false}")
	private boolean registerbeforeTask;
	
	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Step> getSteps() {
		return steps;
	}

	public void setSteps(List<Step> steps) {
		this.steps = steps;
	}
	
	public void addStep(Step step) {
		steps.add(step);
	}

	public Step getStep(String stepName) {
		for (Step step : steps) {
			if (step.getName().equals(stepName)) {
				return step;
			}
		}
		return null;
	}

	public ShouldStepRunDecider getShouldStepRunDecider() {
		return shouldStepRunDecider;
	}

	public void setShouldStepRunDecider(ShouldStepRunDecider shouldStepRunStrategy) {
		this.shouldStepRunDecider = shouldStepRunStrategy;
	}
	
	/**
	 * Register a step execution listener
	 *
	 */
	public void registerTaskExecutionListener(TaskExecutionListener listener) {
		this.getTaskExecutionListener().register(listener);
	}

	/**
	 * Register a list of step execution listener
	 *
	 */
	public void setTaskExecutionListeners(TaskExecutionListener[] listeners) {
		for (int i = 0; i < listeners.length; i++) {
			registerTaskExecutionListener(listeners[i]);
		}
	}
	
	

	public CompositeTaskExecutionListener getTaskExecutionListener() {
		return taskExecutionListener;
	}

	@Override
	public void execute(TaskExecution execution) {
		if(registerbeforeTask) {
			getTaskExecutionListener().beforeTask(execution);
		}
		doExecute(execution);
		getTaskExecutionListener().afterTask(execution);
	}
	
	public abstract void doExecute(TaskExecution execution);
}

