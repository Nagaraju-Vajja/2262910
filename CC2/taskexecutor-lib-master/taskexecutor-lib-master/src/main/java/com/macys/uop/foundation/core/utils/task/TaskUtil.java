package com.macys.uop.foundation.core.utils.task;

import java.util.LinkedHashMap;

import com.macys.uop.foundation.core.utils.json.JsonUtils;

public interface TaskUtil {

	@SuppressWarnings("unchecked")
	default <T> T getContextMapValue(TaskExecution execution, String key, Class<T> requiredType, JsonUtils jsonUtils) {
		Object object = execution.getExecutionContext().get(key);
		if (object == null) {
			return null;
		}
		if (object instanceof LinkedHashMap) {
			T t = jsonUtils.convertFromJson(jsonUtils.convertToJson(object), requiredType);
			if (!(t instanceof LinkedHashMap)) {
				execution.getExecutionContext().put(key, t);
			}
			return t;
		}
		return (T) object;
	}

	default String getExecutionSummary(TaskExecution exec) {

		StringBuilder str = new StringBuilder();

		str.append(String.format(
				"\n--------------------------------------------------------------------------------------------------------------"));
		str.append(String.format("\n EXECUTION SUMMARY FOR %s\n", exec.getTaskName().toUpperCase()));
		str.append(String.format("\n %-20s  %s", "EXECUTION ID", exec.getId()));
		str.append(String.format("\n PARAMETERS:"));
		for (String key : exec.getTaskParameters().getKeys()) {
			str.append(String.format("\n      %-20s   %s", key, exec.getTaskParameters().getString(key)));
		}
		str.append(String.format(
				"\n--------------------------------------------------------------------------------------------------------------"));
		str.append(String.format("\n #   %-30s %-20s %-20s %-20s %-20s", "STEP", "RUN STATUS", "PREV STATUS",
				"TIME TAKEN", "ERROR"));
		str.append(String.format(
				"\n--------------------------------------------------------------------------------------------------------------"));
		int i = 1;
		for (StepExecution stepExecution : exec.getStepExecutions()) {
			str.append(String.format("\n %d   %-30s %-20s %-20s %-20s %-20s", i, stepExecution.getName(),
					stepExecution.isSkipped() ? "SKIPPED" : stepExecution.getStatus(),
					stepExecution.isSkipped() ? stepExecution.getStatus() : "",
					stepExecution.getEndTime() != null
							? ((stepExecution.getEndTime().getTime() - stepExecution.getStartTime().getTime()) / 1000)
									+ "s"
							: "",
					stepExecution.getErrorMessage() != null ? stepExecution.getErrorMessage() : ""));
			i++;
		}
		str.append(String.format(
				"\n--------------------------------------------------------------------------------------------------------------"));
		str.append(String.format("\n     %-30s %s", exec.getTaskName(), exec.getStatus()));
		str.append(String.format(
				"\n--------------------------------------------------------------------------------------------------------------\n\n"));

		return str.toString();

	}

}
