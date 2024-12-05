package com.macys.uop.foundation.core.utils.task.support;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.macys.uop.foundation.core.utils.execution.ServiceContextUtil;
import com.macys.uop.foundation.core.utils.json.JsonUtils;
import com.macys.uop.foundation.core.utils.task.ExecutionContext;
import com.macys.uop.foundation.core.utils.task.TaskExecution;
import com.macys.uop.foundation.core.utils.task.TaskExecutionListener;
import com.macys.uop.foundation.core.utils.task.TaskParameters;
import com.macys.uop.foundation.core.utils.task.TaskStatus;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EventStateTaskExecutionListener implements TaskExecutionListener, ServiceContextUtil, EventStateUtil {

	@Value("${taskexecutor.eventstate.config.purgeRecordsOnSuccess:true}")
	private boolean purgeRecordsOnSuccess;

	@Value("${taskExecutor.register.before.task.enabled:false}")
	private boolean registerbeforeTask;
	
	
	private final IEventStateDao dao;
	private final JsonUtils jsonUtils;

	@Override
	public void beforeTask(TaskExecution taskExecution) {
		TaskParameters taskParameters = taskExecution.getTaskParameters();
		validate(taskParameters);
		
		EventState eventState = new EventState();
		eventState.setCorrelationId(getCorrelationId());
		eventState.setServiceName(getAppName());
		eventState.setTransactionId(taskParameters.getString(EventStateConstants.TRANSACTION_ID));
		eventState.setProcess(taskParameters.getString(EventStateConstants.PROCESS));
		eventState.setPayload(null);
		eventState.setStatus(EventStateConstants.NOT_STARTED);
		eventState.setCompletedSteps(null);
		eventState.setFailedStep(null);
		eventState.setOrderId(taskParameters.getString(EventStateConstants.ORDER_ID));
		eventState.setRecordId(taskParameters.getString(EventStateConstants.RECORD_ID));
		dao.upsert(eventState, taskExecution.getExecutionContext());
	}

	@Override
	public void afterTask(TaskExecution taskExecution) {
		TaskParameters taskParameters = taskExecution.getTaskParameters();
		ExecutionContext executionContext = taskExecution.getExecutionContext();
		validate(taskParameters);

		EventState eventState = new EventState();
		eventState.setCorrelationId(getCorrelationId());
		eventState.setServiceName(getAppName());
		eventState.setTransactionId(getTransactionId(taskParameters, executionContext));
		eventState.setProcess(taskParameters.getString(EventStateConstants.PROCESS));
		eventState.setPayload(taskExecution.getStatus().equals(TaskStatus.FAILURE.toString())
				? (getPayload(executionContext) != null ? jsonUtils.convertToJson(getPayload(executionContext)) : null)
				: null);
		eventState.setStatus(
				taskExecution.getStatus().equals(TaskStatus.SUCCESS.toString()) ? EventStateConstants.COMPLETED
						: EventStateConstants.FAILED);
		eventState.setCompletedSteps(taskExecution.getStepExecutions().stream()
				.filter(exec -> exec.getStatus().equals(TaskStatus.SUCCESS.toString())).map(exec -> exec.getName())
				.collect(Collectors.joining(",")));
		eventState.setFailedStep(taskExecution.getStepExecutions().stream()
				.filter(exec -> exec.getStatus().equals(TaskStatus.FAILURE.toString())).map(exec -> exec.getName())
				.collect(Collectors.joining(",")));
		eventState.setOrderId(getOrderId(taskParameters, executionContext));
		eventState.setRecordId(getRecordId(taskParameters, executionContext));
		
		if (purgeRecordsOnSuccess && eventState.getStatus().equals(EventStateConstants.COMPLETED) && registerbeforeTask) {
			dao.delete(eventState, taskExecution.getExecutionContext());
		} else if(!purgeRecordsOnSuccess || eventState.getStatus().equals(EventStateConstants.FAILED)) {
			dao.upsert(eventState, taskExecution.getExecutionContext());
		}
		
	}

}
