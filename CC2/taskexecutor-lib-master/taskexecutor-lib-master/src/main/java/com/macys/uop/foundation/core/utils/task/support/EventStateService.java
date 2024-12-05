package com.macys.uop.foundation.core.utils.task.support;

import java.util.List;
import java.util.UUID;

import org.springframework.cloud.gcp.data.spanner.core.SpannerQueryOptions;
import org.springframework.cloud.gcp.data.spanner.core.SpannerTemplate;
import org.springframework.stereotype.Component;

import com.google.cloud.Timestamp;
import com.google.cloud.spanner.Statement;
import com.macys.uop.foundation.core.utils.exception.ProblemUtil;
import com.macys.uop.foundation.core.utils.execution.ServiceContextUtil;
import com.macys.uop.foundation.core.utils.logging.LoggingUtil;
import com.macys.uop.foundation.core.utils.task.StepExecutionListener;
import com.macys.uop.foundation.core.utils.task.TaskExecutionListener;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EventStateService  {

    private final EventStateTaskExecutionListener taskListener;

    private final EventStateStepExecutionListener stepListener;
    
    private final IEventStateDao dao;
    

	public TaskExecutionListener getTaskListener() {
		return taskListener;
	}


	public StepExecutionListener getStepListener() {
		return stepListener;
	}
	
	public List<EventState> fetch(String correlationId, String serviceName, String process, String orderId, String recordId, String transactionId) {
		return dao.fetch(correlationId, serviceName, process, orderId, recordId, transactionId);
	}

	public String getPayload(String correlationId, String serviceName, String process, String orderId, String recordId, String transactionId) {
		List<EventState> list = dao.fetch(correlationId, serviceName, process, orderId, recordId, transactionId);
		if (list.size() == 1) {
			return list.get(0).getPayload();
		}
		return null;
	}
	
}
