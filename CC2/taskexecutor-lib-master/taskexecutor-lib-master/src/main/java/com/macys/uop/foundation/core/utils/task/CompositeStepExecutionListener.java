package com.macys.uop.foundation.core.utils.task;

import java.util.ArrayList;
import java.util.List;

public class CompositeStepExecutionListener implements StepExecutionListener {

	List<StepExecutionListener> listeners = new ArrayList<>();

	public void register(StepExecutionListener listener) {
		this.listeners.add(listener);
	}

	@Override
	public void beforeStep(StepExecution stepExecution) {
		for (StepExecutionListener listener : listeners) {
			listener.beforeStep(stepExecution);
		}
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		for (StepExecutionListener listener : listeners) {
			listener.afterStep(stepExecution);
		}
		// TODO: Implement Exit Status
		return null;
	}

}