package com.macys.uop.foundation.core.utils.task;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskParameters {
	private final Map<String, TaskParameter> parameters;

	public TaskParameters() {
		this.parameters = new HashMap<>();
	}

	public TaskParameters(Map<String, TaskParameter> parameters) {
		this.parameters = parameters;
	}

	public String getString(String key) {
		Object value = parameters.get(key) != null ? parameters.get(key).getValue() : null;
		return value == null ? null : value.toString();
	}

	public String getString(String key, String defaultValue) {
		if (parameters.containsKey(key)) {
			return getString(key);
		} else {
			return defaultValue;
		}
	}

	@JsonIgnore
	public Collection<String> getKeys() {
		return parameters.keySet();
	}
}
