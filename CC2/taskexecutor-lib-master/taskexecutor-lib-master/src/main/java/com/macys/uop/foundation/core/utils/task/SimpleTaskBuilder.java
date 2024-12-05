package com.macys.uop.foundation.core.utils.task;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.StringUtils;

public class SimpleTaskBuilder{

	private String taskName;
	private List<Step> steps = new ArrayList<Step>();
	private List<TaskExecutionListener> listeners = new ArrayList<TaskExecutionListener>();
	private String csvFlow;

	public SimpleTaskBuilder(String name) {
		this.taskName = name;
	}

	public SimpleTaskBuilder addStep(Step step) {
		this.steps.add(step);
		return this;
	}

	public SimpleTaskBuilder addListener(TaskExecutionListener listeners) {
		this.listeners.add(listeners);
		return this;
	}
	
	
	public SimpleTaskBuilder withCSVFlow(String csvFlow) {
		this.csvFlow = csvFlow;
		return this;
	}

	public SimpleTask build() {

		List<Step> taskSteps = new ArrayList<Step>();
		if (!StringUtils.isEmpty(csvFlow)) { // If csv is present build with only those steps
			String[] stepNames = csvFlow.split(",");
			for (String stepName : stepNames) {
				Step step = getStep(stepName);
				if (step != null) {
					taskSteps.add(step);
				}
			}
		} else {
			taskSteps.addAll(steps); // else, add all steps entered
		}

		SimpleTask task = new SimpleTask(taskName);
		task.setSteps(taskSteps);
		task.setTaskExecutionListeners(listeners.toArray(new TaskExecutionListener[0]));
		return task;
	}

	private Step getStep(String stepName) {
		for (Step step : steps) {
			if (step.getName().equals(stepName)) {
				return step;
			}
		}
		return null;
	}

}
