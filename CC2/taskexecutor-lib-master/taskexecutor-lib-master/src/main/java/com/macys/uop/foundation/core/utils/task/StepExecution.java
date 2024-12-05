package com.macys.uop.foundation.core.utils.task;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class StepExecution {

	private String name;
	private String status = TaskStatus.NOT_RUN.name();
	private String executionId; // Stores the exception id if we are using cache for storing the whole exception
	private boolean skipped; // Checks if step is skipped
	private Date startTime; // Stores execution start
	private Date endTime; // Stores execution end
	private String errorMessage; // Stores error message
	@JsonIgnore
	private Throwable error;
	
	@JsonIgnore
	private TaskExecution taskExecution; // Stores task execution
	
	public StepExecution() {
	}
	
	public StepExecution(String name) {
		this.name = name;
	}
	

}
