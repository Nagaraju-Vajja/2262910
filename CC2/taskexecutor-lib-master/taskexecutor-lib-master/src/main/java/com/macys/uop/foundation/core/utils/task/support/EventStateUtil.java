package com.macys.uop.foundation.core.utils.task.support;

import org.apache.commons.lang3.ObjectUtils;

import com.macys.uop.foundation.core.utils.execution.ServiceContextUtil;
import com.macys.uop.foundation.core.utils.task.ExecutionContext;
import com.macys.uop.foundation.core.utils.task.TaskParameters;

public interface EventStateUtil extends ServiceContextUtil {

	default String getOrderId(TaskParameters taskParameters, ExecutionContext executionContext) {
		return executionContext.get(EventStateConstants.ORDER_ID) != null
				? executionContext.get(EventStateConstants.ORDER_ID).toString()
				: taskParameters.getString(EventStateConstants.ORDER_ID);
	}

	default String getTransactionId(TaskParameters taskParameters, ExecutionContext executionContext) {
		return executionContext.get(EventStateConstants.TRANSACTION_ID) != null
				? executionContext.get(EventStateConstants.TRANSACTION_ID).toString()
				: taskParameters.getString(EventStateConstants.TRANSACTION_ID);
	}

	default String getRecordId(TaskParameters taskParameters, ExecutionContext executionContext) {
		return executionContext.get(EventStateConstants.RECORD_ID) != null
				? executionContext.get(EventStateConstants.RECORD_ID).toString()
				: taskParameters.getString(EventStateConstants.RECORD_ID);
	}

	default Object getPayload(ExecutionContext executionContext) {
		Object payload = executionContext.get(EventStateConstants.PAYLOAD) != null
				? executionContext.get(EventStateConstants.PAYLOAD)
				: getServiceRequestContext().getBody();
		return payload != null ? payload : null;
	}

	default void validate(TaskParameters taskParameters) {

		if (ObjectUtils.isEmpty(getCorrelationId())) {
			throwException("CorrelationId is mandatory for event state entry, set it in message headers");
		}
		if (ObjectUtils.isEmpty(taskParameters.getString(EventStateConstants.ORDER_ID))) {
			throwException("OrderId is mandatory for event state entry, set it in task parameters");
		}
		if (ObjectUtils.isEmpty(taskParameters.getString(EventStateConstants.PROCESS))) {
			throwException("Process name is mandatory for event state entry, set it in task parameters");
		}
		if (ObjectUtils.isEmpty(taskParameters.getString(EventStateConstants.TRANSACTION_ID))) {
			throwException("Transaction ID is mandatory for event state entry, set it in task paramters");
		}

	}

	default void throwException(String errorMessage) {
		throw new RuntimeException(errorMessage);
	}

}
