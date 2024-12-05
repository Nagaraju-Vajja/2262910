package com.macys.uop.foundation.core.utils.task;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskExecution {

	private String id;
	private String taskName;
	private TaskParameters taskParameters;
	private String status = TaskStatus.NOT_RUN.name();
	
	private List<StepExecution> stepExecutions = new ArrayList<StepExecution>();
	
	@JsonIgnore
	private Throwable error;
	private ExecutionContext executionContext = new ExecutionContext();


	public TaskExecution() {
	}

	public TaskExecution(String id, String taskName) {
		this.setId(id);
		this.setTaskName(taskName);
	}

	public void addStepExecution(StepExecution stepExecutions) {
		this.getStepExecutions().add(stepExecutions);
	}

	public StepExecution getStepExecution(String stepName) {
		for (StepExecution stepExecution : stepExecutions) {
			if (stepExecution.getName().equals(stepName)) {
				return stepExecution;
			}
		}
		return null;
	}

	public String getStatus(String stepName) {
		StepExecution execution = getStepExecution(stepName);
		return execution != null ? execution.getStatus() : null;
	}


//	public static void main(String args[]) {
//		TaskExecution execution = new TaskExecution();
//		execution.setId("ccc2cc97-a350-4b47-a089-ef6f7a9c5c6f");
//		execution.setReferenceId("MACYSDEMO001");
//		execution.setTaskName("collectOrder");
//		execution.setStatus(TaskStatus.SUCCESS.name());
//
//		StepExecution stepExecution = new StepExecution("saveInputInEventLog");
//		stepExecution.setStatus(TaskStatus.SUCCESS.name());
//		stepExecution.setSkipped(true);
//		stepExecution.setStartTime(new Date());
//		stepExecution.setEndTime(new Date());
//		execution.addStepExecution(stepExecution);
//		StepExecution stepExecution2 = new StepExecution("createOrder");
//		stepExecution2.setStatus(TaskStatus.FAILURE.name());
//		stepExecution2.setStartTime(new Date());
//		stepExecution2.setEndTime(new Date());
//		stepExecution2.setErrorMessage("Duplicate check failed");
//		execution.addStepExecution(stepExecution2);
//
//		execution.print();
//
//	}
}
