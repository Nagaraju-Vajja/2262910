package com.macys.uop.foundation.core.utils.task;

public interface StepExecutionListener {

	void beforeStep(StepExecution stepExecution);

	ExitStatus afterStep(StepExecution stepExecution);
}
