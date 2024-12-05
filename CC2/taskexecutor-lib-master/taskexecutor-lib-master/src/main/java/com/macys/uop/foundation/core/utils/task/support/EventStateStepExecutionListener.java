package com.macys.uop.foundation.core.utils.task.support;

import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.macys.uop.foundation.core.utils.execution.ServiceContextUtil;
import com.macys.uop.foundation.core.utils.json.JsonUtils;
import com.macys.uop.foundation.core.utils.task.ExecutionContext;
import com.macys.uop.foundation.core.utils.task.ExitStatus;
import com.macys.uop.foundation.core.utils.task.StepExecution;
import com.macys.uop.foundation.core.utils.task.StepExecutionListener;
import com.macys.uop.foundation.core.utils.task.TaskParameters;
import com.macys.uop.foundation.core.utils.task.TaskStatus;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EventStateStepExecutionListener implements StepExecutionListener, ServiceContextUtil, EventStateUtil {

	private final IEventStateDao dao;
	private final JsonUtils jsonUtils;

	@Override
	public void beforeStep(StepExecution stepExecution) {
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {

		TaskParameters taskParameters = stepExecution.getTaskExecution().getTaskParameters();
		ExecutionContext executionContext = stepExecution.getTaskExecution().getExecutionContext();
		validate(taskParameters);

		EventState eventState = new EventState();
		eventState.setCorrelationId(getCorrelationId());
		eventState.setServiceName(getAppName());
		eventState.setTransactionId(getTransactionId(taskParameters, executionContext));
		eventState.setProcess(taskParameters.getString(EventStateConstants.PROCESS));
		eventState.setPayload(stepExecution.getStatus().equals(TaskStatus.FAILURE.toString())
				? (getPayload(executionContext) != null ? jsonUtils.convertToJson(getPayload(executionContext)) : null)
				: null);
		eventState.setStatus(
				stepExecution.getStatus().equals(TaskStatus.FAILURE.toString()) ? EventStateConstants.FAILED
						: EventStateConstants.IN_PROGRESS);
		eventState.setCompletedSteps(stepExecution.getTaskExecution().getStepExecutions().stream()
				.filter(exec -> exec.getStatus().equals(TaskStatus.SUCCESS.toString())).map(exec -> exec.getName())
				.collect(Collectors.joining(",")));
		eventState.setFailedStep(stepExecution.getTaskExecution().getStepExecutions().stream()
				.filter(exec -> exec.getStatus().equals(TaskStatus.FAILURE.toString())).map(exec -> exec.getName())
				.collect(Collectors.joining(",")));
		eventState.setOrderId(getOrderId(taskParameters, executionContext));
		eventState.setRecordId(getRecordId(taskParameters, executionContext));
		dao.upsert(eventState, stepExecution.getTaskExecution().getExecutionContext());
		return null;
	}

}
