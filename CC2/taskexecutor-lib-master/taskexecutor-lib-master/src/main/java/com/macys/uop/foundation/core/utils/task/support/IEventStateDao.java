package com.macys.uop.foundation.core.utils.task.support;

import java.util.List;

import com.macys.uop.foundation.core.utils.task.ExecutionContext;

public interface IEventStateDao {

	void upsert(EventState eventState, ExecutionContext executionContext);

	void delete(EventState eventState, ExecutionContext executionContext);

	List<EventState> fetch(String correlationId, String serviceName, String process, String orderId, String recordId,
			String transactionId);

}
