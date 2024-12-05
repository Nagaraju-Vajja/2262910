package com.macys.uop.foundation.core.utils.task;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskParameter {
	private Object parameter;
	private ParameterType parameterType;

	public TaskParameter() {
		this.parameter = null;
		this.parameterType = null;
	}
	
	public TaskParameter(String parameter) {
		this.parameter = parameter;
		parameterType = ParameterType.STRING;
	}

	@JsonIgnore
	public Object getValue() {
		return parameter;
	}

	@JsonIgnore
	public ParameterType getType() {
		return parameterType;
	}
	
	public enum ParameterType {
		STRING;
	}
}
