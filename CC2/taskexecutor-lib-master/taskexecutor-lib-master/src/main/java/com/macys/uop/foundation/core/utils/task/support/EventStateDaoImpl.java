package com.macys.uop.foundation.core.utils.task.support;

import java.util.List;
import java.util.UUID;

import com.google.cloud.spanner.SpannerException;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.cloud.gcp.data.spanner.core.SpannerQueryOptions;
import org.springframework.cloud.gcp.data.spanner.core.SpannerTemplate;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.zalando.problem.Status;

import com.google.cloud.Timestamp;
import com.google.cloud.spanner.Statement;
import com.google.cloud.spanner.Statement.Builder;
import com.macys.uop.foundation.core.utils.CommonStatusCode;
import com.macys.uop.foundation.core.utils.exception.ErrorDetail;
import com.macys.uop.foundation.core.utils.exception.ProblemUtil;
import com.macys.uop.foundation.core.utils.execution.ServiceContextUtil;
import com.macys.uop.foundation.core.utils.logging.LogMessageBuilder;
import com.macys.uop.foundation.core.utils.logging.LogTypeEnum;
import com.macys.uop.foundation.core.utils.logging.LoggingUtil;
import com.macys.uop.foundation.core.utils.task.ExecutionContext;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventStateDaoImpl implements IEventStateDao, ServiceContextUtil, ProblemUtil, LoggingUtil {

	private final SpannerTemplate spannerTemplate;

	/**
	 * Creates or updates an existing event state record
	 */
	@Override
	@Retryable(value = {IllegalStateException.class, SpannerException.class})
	public void upsert(EventState eventState, ExecutionContext context) {

		eventState.setLastUpdatedBy(getClientId());
		eventState.setLastUpdatedTs(Timestamp.now());

		EventState savedValue = fetchSavedValue(eventState, context);

		if (savedValue == null) { // if new record, generate primary key and persist

			eventState.setEventStatePk(UUID.randomUUID().toString());
			eventState.setCreatedBy(getClientId());
			eventState.setCreatedTs(Timestamp.now());

//			getLogMessageBuilder(log).withContext("EventStateDaoImpl.upsert()")
//					.withAdditionalInfo("Inserting event state record -> " + eventState.toString())
//					.buildDisableChecking().logAsInfo();
			spannerTemplate.insert(eventState);

		} else { // if record exists, update the existing record
			
			eventState.setEventStatePk(savedValue.getEventStatePk());
			eventState.setCreatedBy(savedValue.getCreatedBy());
			eventState.setCreatedTs(savedValue.getCreatedTs());
			
//			getLogMessageBuilder(log).withContext("EventStateDaoImpl.upsert()")
//					.withAdditionalInfo("Updating event state record -> " + eventState.toString())
//					.buildDisableChecking().logAsInfo();
			spannerTemplate.update(eventState);
			
		}

		context.put(EventStateConstants.SAVEDVALUE, eventState); // persist to cache
	}

	
	/**
	 * Deletes an event state record
	 */
	@Override
	@Retryable(value = IllegalStateException.class) 
	public void delete(EventState eventState, ExecutionContext context) {

		EventState savedValue = fetchSavedValue(eventState, context);

		if (savedValue != null) {

			eventState.setEventStatePk(savedValue.getEventStatePk());
			eventState.setCreatedBy(savedValue.getCreatedBy());
			eventState.setCreatedTs(savedValue.getCreatedTs());
			
			eventState.setLastUpdatedBy(getClientId());
			eventState.setLastUpdatedTs(Timestamp.now());
			
			spannerTemplate.delete(eventState);
			
		}

		context.put(EventStateConstants.SAVEDVALUE, null); // Clear from cache
	}
	
    /**
     * Fall back method for upsert method. If there is a failure 
     * 
     * @param eventState
     * @param context
     * @param e
     * @throws InterruptedException
     */
	@Recover
    public void eventStateUpsertRTFallback(IllegalStateException e, EventState eventState, ExecutionContext context) throws InterruptedException {
        CommonStatusCode code = CommonStatusCode.EVENT_STATE_UPSERT_FAILURE;
        if (e.getMessage() != null && e.getMessage().contains("Pool has been closed")) {
            code = CommonStatusCode.EVENT_STATE_DB_POOL_CLOSED_ERROR;
        }
        new LogMessageBuilder()
        .withClientId(getClientId())
        .withMessageId(getMessageId())
        .withOrderId(getOrderId())
        .withCorrelationId(getCorrelationId())
        .withAppName(getAppName())
        .withCallerId(getCallerId())
        .withLogType(LogTypeEnum.ERROR)
        .withErrorCode(code.getCode())
        .withErrorMessage(code.getDescription())
        .withStackTrace(ExceptionUtils.getStackTrace(e))
        .withAdditionalInfo("Retry Fallback triggered during event state upsert -> " + eventState.toString())
        .withContext("EVENT STATE UPSERT")
        .buildDisableChecking()
        .logAsError();
        com.macys.uop.foundation.core.utils.exception.Error errorInfo = com.macys.uop.foundation.core.utils.exception.Error
                .builder()
                .withCode(code.getCode())
                .withMessage(code.getDescription())
                .withErrorDetail(ErrorDetail.builder()
                        .withDomain(getAppName())
                        .withReason("Event state upsert error")
                        .withMessage(e.toString())
                        .build())
                .build();
        throw createProblem(Status.INTERNAL_SERVER_ERROR.getStatusCode(), errorInfo);
    }

	
	/**
	 * Fetches event state record from cache or DB
	 * 
	 * @param eventState
	 * @param context
	 * @return savedEventState
	 */
	private EventState fetchSavedValue(EventState eventState, ExecutionContext context) {

		EventState savedValue = (EventState) context.get(EventStateConstants.SAVEDVALUE); // fetch from Cache

		if (savedValue == null) { // if record not in cache

			List<EventState> response = fetch(eventState.getCorrelationId(), eventState.getServiceName(),
					eventState.getProcess(), eventState.getOrderId(), eventState.getRecordId(),
					eventState.getTransactionId()); // fetch from DB

			if (response.size() == 1) {
				savedValue = response.get(0);
			} else if (response.size() > 1){
				// throw exception if more than one record is found
				throw new RuntimeException("No of event state records more than 1");
			}

		}
		return savedValue;
	}

	/**
	 * Fetches event state record based on the 6 parameters
	 */
	@Override
	public List<EventState> fetch(String correlationId, String serviceName, String process, String orderId,
			String recordId, String transactionId) {

		String sql = "Select eventStatePk,correlationId,serviceName,completedSteps,failedStep,status,transactionId,createdBy,createdTs,lastUpdatedBy,lastUpdatedTs,process,payload,orderId,recordId from EventState where serviceName=@serviceName and process=@process and orderId=@orderId";

		if (ObjectUtils.isNotEmpty(correlationId)) {
			sql += " and correlationId = @correlationId";
		}
		if (ObjectUtils.isNotEmpty(recordId)) {
			sql += " and recordId = @recordId";
		}
		if (ObjectUtils.isNotEmpty(transactionId)) {
			sql += " and transactionId = @transactionId";
		}
		Builder builder = Statement.newBuilder(sql);

		if (ObjectUtils.isNotEmpty(correlationId)) {
			builder.bind("correlationId").to(correlationId);
		}
		if (ObjectUtils.isNotEmpty(recordId)) {
			builder.bind("recordId").to(recordId);
		}
		if (ObjectUtils.isNotEmpty(transactionId)) {
			builder.bind("transactionId").to(transactionId);
		}
		Statement selectStatement = builder.bind("serviceName").to(serviceName).bind("process").to(process)
				.bind("orderId").to(orderId).build();
		List<EventState> response = spannerTemplate.query(EventState.class, selectStatement, new SpannerQueryOptions());
		return response;
	}

}
