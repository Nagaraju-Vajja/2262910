package com.macys.uop.foundation.core.utils.task;

import java.util.HashMap;
import java.util.Map;

public class TaskParametersBuilder {

	private Map<String, TaskParameter> parameterMap = new HashMap<>();

	public TaskParametersBuilder addString(String key, String parameter) {
		this.parameterMap.put(key, new TaskParameter(parameter));
		return this;
	}
	
	public TaskParameters build() {
		return new TaskParameters(this.parameterMap);
	}

}
