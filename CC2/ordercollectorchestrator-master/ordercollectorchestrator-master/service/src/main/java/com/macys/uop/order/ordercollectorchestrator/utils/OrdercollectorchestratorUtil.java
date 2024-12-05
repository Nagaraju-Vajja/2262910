package com.macys.uop.order.ordercollectorchestrator.utils;

import static com.macys.uop.foundation.core.utils.Constant.*;
import static com.macys.uop.order.ordercollectorchestrator.utils.OrdercollectorchestratorConstants.*;
import static com.macys.uop.order.ordercollectorchestrator.utils.OrderErrorCodes.*;
import static org.slf4j.LoggerFactory.getLogger;

import com.google.gson.Gson;
import com.macys.uop.abstraction.commonlookupmapper.enums.ReferringIdEnum;
import com.macys.uop.foundation.core.utils.CommonStatusCode;
import com.macys.uop.foundation.core.utils.eventlog.EventLogMessageBuilder;
import com.macys.uop.foundation.core.utils.exception.*;
import com.macys.uop.foundation.core.utils.exception.Error;
import com.macys.uop.foundation.core.utils.msg.subscriber.SubscriberUtil;
import com.macys.uop.foundation.core.utils.task.StepExecution;
import com.macys.uop.foundation.core.utils.task.TaskExecution;
import com.macys.uop.foundation.core.utils.task.TaskStatus;
import com.macys.uop.order.model.Lock;
import com.macys.uop.order.model.Order;
import com.macys.uop.order.model.OrderLine;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.zalando.problem.ThrowableProblem;

@Component
public interface OrdercollectorchestratorUtil extends ExceptionHandlerUtil, SubscriberUtil {

	/**
	 * method to get Status object
	 * @param httpStatus
	 * @param statusMessage
	 * @return Status
	 */
	default com.macys.uop.common.commonlib.Status getStatus(HttpStatus httpStatus, String statusMessage) {
		com.macys.uop.common.commonlib.Status status = new com.macys.uop.common.commonlib.Status();
		status.setResponseCode(String.valueOf(httpStatus.value()));
		status.setResponseMessage(statusMessage);
		return status;
	}

	/**
	 * utility to log and process fallback of service calls
	 * @param e
	 * @param referenceId
	 * @param step
	 * @param httpHeaders
	 * @param orchestratorErrorCodes
	 */
	default void handleError(Throwable e,
							String referenceId,
							String step,
							HttpHeaders httpHeaders,
							OrderErrorCodes orchestratorErrorCodes) {

		int statusCode = getStatusCode(e);
		RetryEnum retryEnum = RetryEnum.getRetryInfoByStep(step);

		String additionalInfo = "Exception Occurred while calling " +  retryEnum.getLocation() +" service, correlationId: "
			+ httpHeaders.getFirst(CORRELATIONID_HDR)
			+ " referenceId: "
			+ referenceId;
		getErrorLogMessageBuilder(orchestratorErrorCodes.getCode(), orchestratorErrorCodes.getDescription(), e, additionalInfo, LogHolder.LOGGER);
		throw createProblem(statusCode, getError(e, step));
	}

	/**
	 * Method to return message headers with updated orderid
	 * @param orderId
	 * @return HttpHeaders
	 */
	default HttpHeaders getDefaultHeaders(String orderId) {
		HttpHeaders headers = new HttpHeaders();
		headers.add(ORDERID_HDR, StringUtils.isNotEmpty(orderId) ? orderId : getOrderId());
		headers.add(CLIENTID_HDR, getAppName());
		headers.add(CORRELATIONID_HDR, getCorrelationId());
		headers.add(MESSAGEID_HDR, getMessageId());
		return headers;
	}

	/**
	 * Method to return headers with retry info
	 * @param headers
	 */
	default HttpHeaders setCommonHeaders(HttpHeaders headers) {
		if(StringUtils.isNotEmpty(getSingleValueHeaderParam(RETRY))) {
			headers.set(RETRY, getSingleValueHeaderParam(RETRY));
		}
		if(StringUtils.isNotEmpty(getSingleValueHeaderParam(FAILED_SUB_STATE))) {
			headers.set(FAILED_SUB_STATE, getSingleValueHeaderParam(FAILED_SUB_STATE));
		}
		if(StringUtils.isNotEmpty(getSingleValueHeaderParam(REFERENCE_ID))) {
			headers.set(REFERENCE_ID, getSingleValueHeaderParam(REFERENCE_ID));
		}
		return headers;
	}

	/**
	 * Method which returns default message header
	 * @return Map
	 */
	default Map<String, String> getDefaultMessageHeaders() {
		Map<String, String> messageHeaders = new HashMap<>();
		messageHeaders.put(ORDERID_HDR, getOrderId());
		messageHeaders.put(CLIENTID_HDR, getAppName());
		messageHeaders.put(CORRELATIONID_HDR, getCorrelationId());
		messageHeaders.put(MESSAGEID_HDR, getMessageId());
		return messageHeaders;
	}

	default Map<String, String> getDefaultMessageHeadersForCheckout(Map<String, String> messageHeaders, String referenceId) {
		messageHeaders.put(ORDERID_HDR, referenceId);
		messageHeaders.put(CLIENTID_HDR, getAppName());
		messageHeaders.put(CORRELATIONID_HDR, getCorrelationId());
		messageHeaders.put(MESSAGEID_HDR, getMessageId());
		return messageHeaders;
	}

	/**
	 * Method which returns default message header
	 * @param isRetry
	 * @return Map
	 */
	default Map<String, String> getErrorProcessorMessageHeaders(String isRetry) {
		Map<String, String> messageHeaders = getDefaultMessageHeaders();
		if(StringUtils.isNotEmpty(isRetry)) { messageHeaders.put(RETRY, isRetry); }
		return messageHeaders;
	}

	/**
	 * Method to return message headers with updated orderid
	 * @param orderId
	 * @return Map
	 */
	default Map<String, String> getDefaultMessageHeader(String orderId) {
		Map<String, String> messageHeaders = new HashMap<>();
		messageHeaders.put(ORDERID_HDR, orderId);
		messageHeaders.put(CLIENTID_HDR, getAppName());
		messageHeaders.put(CORRELATIONID_HDR, getCorrelationId());
		messageHeaders.put(MESSAGEID_HDR, getMessageId());
		return messageHeaders;
	}

	/**
	 * Method to Consturct the McHub Headers
	 * @param order
	 * @return Map
	 */
	default Map<String, String> getMcHubAckHeaders(Order order){
		String orderMethod = order.getOrderLines().stream()
			.map(OrderLine::getFulfillmentType)
			.distinct()
			.filter(StringUtils::isNotEmpty)
			.collect(Collectors.joining(","));

		Map<String, String> messageHeaders = new HashMap<>();
		messageHeaders.put(CORRELATIONID_HDR, getCorrelationId());
		messageHeaders.put(ORDERID_HDR, order.getOrderId());
		messageHeaders.put(MESSAGEID_HDR, getMessageId());
		messageHeaders.put(CLIENTID_HDR, SITE);
		if (getSingleValueHeaderParam(RES_NBR) != null) {
            messageHeaders.put(RES_NBR, getSingleValueHeaderParam(RES_NBR));
        }
		messageHeaders.put(EVENT_TIME_STAMP, Instant.now().toString());
		messageHeaders.put(REFERRING_ID, ReferringIdEnum.valueOf(order.getSellingChannelType()).getStatusCode());
		messageHeaders.put(SUB_CLIENT_ID, order.getSellingChannelType());
		messageHeaders.put(ORDER_METHOD, orderMethod);

		return messageHeaders;
	}

	/**
	 * Method to get failed step
	 * @param execution
	 * @return String
	 */
	default String getFailedStep(TaskExecution execution) {
		StepExecution failedStepExecution = execution.getStepExecutions().stream().filter(stepExecution ->
			stepExecution.getStatus().equalsIgnoreCase(TaskStatus.FAILURE.name())).findAny().orElse(null);
		return (ObjectUtils.isNotEmpty(failedStepExecution)) ? failedStepExecution.getName() : null;
	}

	/**
	 * utility to log and process errorporcessor Error
	 * @param execution
	 * @param errorObject
	 * @param errorResponse
	 * @param httpStatus
	 * @param httpHeaders
	 */
	default void buildErrorProcessorResponse(TaskExecution execution,
											 Error errorObject,
											 String errorResponse,
											 int httpStatus,
											 HttpHeaders httpHeaders) {

		ErrorDetail errorDetail = errorObject.getErrorDetails().stream().findFirst().orElse(null);

		String additionalInfo = "Error Info Posted to ErrorProcessor"
			+ " Error Processor RequestId : "
			+ errorResponse;
		getErrorLogMessageBuilder(errorObject.getCode(),
			errorObject.getMessage(), additionalInfo, LogHolder.LOGGER).build().logAsError();
		assert errorDetail != null;
		String errorCode = StringUtils.isNotEmpty(errorObject.getCode()) && errorObject.getCode().contains("UOP-ORVAL") ? ONE : errorObject.getCode();
		Error error = Error.builder()
			.withCode(errorCode)
			.withMessage(errorObject.getMessage())
				.withReferenceType(errorObject.getReferenceType())
				.withReferenceId(errorObject.getReferenceId())
			.withErrorDetail(ErrorDetail.builder()
				.withDomain(getAppName())
				.withReason(errorDetail.getReason())
				.withMessage(errorDetail.getMessage())
				.withLocation(errorDetail.getDomain())
				.build())
			.build();
		throw createProblem(httpStatus, error);
	}

	/**
	 * Method to get Error Message from TaskExecution
	 * @param throwable
	 * @param failedStep
	 * @return Error
	 */
	default Error getError(Throwable throwable, String failedStep) {
		String errorMessage = null;
		Error error = null;
		Gson gson = new Gson();
		String reason = getReasonCode(throwable);

		if (throwable instanceof HttpClientErrorException) {
			HttpClientErrorException exception = (HttpClientErrorException)throwable;
			if(ERROR_CODE.stream().noneMatch(exception.getResponseBodyAsString()::contains)) {
				error = constructError(SERVICE_UNAVAILABLE_ERROR, exception.getResponseBodyAsString(), failedStep,
					reason) ;
			} else {
				error = gson.fromJson(exception.getResponseBodyAsString(), Error.class);
			}
		} else if (throwable instanceof HttpServerErrorException) {
			HttpServerErrorException serverErrorException = (HttpServerErrorException)throwable;
			if(ERROR_CODE.stream().noneMatch(serverErrorException.getResponseBodyAsString()::contains)) {
				error = constructError(SERVICE_UNAVAILABLE_ERROR, serverErrorException.getResponseBodyAsString(), failedStep,
					reason) ;
			} else {
				error = gson.fromJson(serverErrorException.getResponseBodyAsString(), Error.class);
			}
		} else if (throwable instanceof InterruptedException) {
			InterruptedException interruptedException = (InterruptedException) throwable;
			OrderErrorCodes errorCd = OrderErrorCodes.getCodeByStepName(failedStep);
			error = constructError(errorCd, interruptedException.getMessage(), failedStep, reason);
		} else if (throwable instanceof ExecutionException) {
			ExecutionException executionException = (ExecutionException) throwable;
			OrderErrorCodes errorCd = OrderErrorCodes.getCodeByStepName(failedStep);
			error = constructError(errorCd, executionException.getMessage(), failedStep, reason);
		} else if (throwable instanceof ResourceAccessException) {
			ResourceAccessException resourceAccessException = (ResourceAccessException)throwable;
			error = constructError(CONNECTION_ERROR, resourceAccessException.getMessage(), failedStep,
				reason);
		}else if (throwable instanceof ThrowableProblem) {
			ThrowableProblem throwableProblem = (ThrowableProblem) throwable;
			if (throwableProblem.getParameters()!=null && throwableProblem.getParameters().get("error")!=null)
			{
				errorMessage = throwableProblem.getParameters().get("error").toString();
				error = gson.fromJson(errorMessage, Error.class);
			}
		} else if (throwable instanceof Exception) {
			Exception exception = (Exception)throwable;
			error = constructError(RUNTIME_EXCEPTION, exception.toString(), failedStep,
					reason);
		}
		return error;
	}

	/**
	 * Method to Construct an Error Object
	 * @param errorCode
	 * @param errorMessage
	 * @param failedStep
	 * @param reason
	 * @return Error
	 */
	default Error constructError(OrderErrorCodes errorCode, String errorMessage, String failedStep,
		String reason) {
		RetryEnum retryEnum = RetryEnum.getRetryInfoByStep(failedStep);
		boolean isCommoneError = COMMON_ERROR_CODE.contains(errorCode.getCode());
		String message = isCommoneError ? (errorCode.getDescription() + " " + retryEnum.getLocation()) :
			errorCode.getDescription();
		String additionalInfo = "Exception occurred While Calling " + retryEnum.getLocation() + " Error Message " + errorMessage;
		getErrorLogMessageBuilder(errorCode.getCode(),
			errorCode.getDescription(), additionalInfo, LogHolder.LOGGER).build().logAsError();
		return Error.builder()
			.withCode(errorCode.getCode())
			.withMessage(message)
			.withErrorDetail(ErrorDetail.builder()
				.withDomain(retryEnum.getStepName())
				.withReason(reason)
				.withMessage(errorMessage)
				.withLocation(retryEnum.getLocation())
				.build())
			.build();
	}


	/**
	 * Method to Construct and throw Business Errors
	 * @param step
	 * @param httpHeaders
	 * @param orderErrorCode
	 */
	default void throwBusinessError(String step, String message, HttpHeaders httpHeaders,
		OrderErrorCodes orderErrorCode) {
		RetryEnum retryEnum = RetryEnum.getRetryInfoByStep(step);
		getErrorLogMessageBuilder(CommonStatusCode.BAD_REQUEST_BODY.getCode(),
			CommonStatusCode.BAD_REQUEST_BODY.getDescription(), message, LogHolder.LOGGER).build().logAsError();
		Error error = Error.builder()
			.withCode(orderErrorCode.getCode())
			.withMessage(orderErrorCode.getDescription())
			.withErrorDetail(ErrorDetail.builder()
				.withDomain(getAppName())
				.withReason(CommonStatusCode.BAD_REQUEST_BODY.getDescription())
				.withMessage(message)
				.withLocation(retryEnum.getLocation())
				.build())
			.build();
		throw createProblem(org.zalando.problem.Status.BAD_REQUEST.getStatusCode(), error);
	}

	/**
	 * Method to Construct and throw Business Errors
	 * @param step
	 * @param httpHeaders
	 * @param orderErrorValidationCode
	 */
	default void throwBusinessError(String step, String message, HttpHeaders httpHeaders,
									OrderValidationErrorCodes orderErrorValidationCode, String referenceId, String referenceType) {
		RetryEnum retryEnum = RetryEnum.getRetryInfoByStep(step);
		getErrorLogMessageBuilder(CommonStatusCode.BAD_REQUEST_BODY.getCode(),
				CommonStatusCode.BAD_REQUEST_BODY.getDescription(), message, LogHolder.LOGGER).build().logAsError();
		Error error = Error.builder()
				.withReferenceId(referenceId)
				.withReferenceType(referenceType)
				.withCode(orderErrorValidationCode.getCode())
				.withMessage(orderErrorValidationCode.getDescription())
				.withErrorDetail(ErrorDetail.builder()
						.withDomain(getAppName())
						.withReason(CommonStatusCode.BAD_REQUEST_BODY.getDescription())
						.withMessage(message)
						.withLocation(retryEnum.getLocation())
						.build())
				.build();
		throw createProblem(org.zalando.problem.Status.BAD_REQUEST.getStatusCode(), error);
	}

	/**
	 * Method to return referenceType and referenceId
	 * @param order
	 * @return Map
	 */
	default Map<String, String> getReferenceMap(Order order) {
		Map<String, String> referenceMap = new HashMap<>();
		if(ObjectUtils.isNotEmpty(order.getSellerOrderId())) {
			referenceMap.put(SELLER_ORDER_ID, order.getSellerOrderId());
		} else if (ObjectUtils.isNotEmpty(order.getPartnerOrderId())) {
			referenceMap.put(PARTNER_ORDER_ID, order.getPartnerOrderId());
		}else if (ObjectUtils.isNotEmpty(order.getOrderId())) {
			referenceMap.put(ORDERID_HDR, order.getOrderId());
		}
		return referenceMap;
	}

	static final class LogHolder {
		private static final Logger LOGGER = getLogger(OrdercollectorchestratorUtil.class);
		private LogHolder() {
		}
	}
	
	 /**
     * This method use to add custom message headers
     *
     * @param messageHeaders
     * @param order
     * @return messageHeaders
     */
	default Map<String, String> addCustomMessageHeader(Map<String, String> messageHeaders, Order order) {
        if(ObjectUtils.isNotEmpty(order) && ObjectUtils.isNotEmpty(order.getOrderLines())) {
        	String marketingPartnerId = order.getOrderLines().get(0).getMarketingPartnerId();
        	if(StringUtils.isNotBlank(marketingPartnerId) && MARKETING_PARTNER_ID_MIRAKL.equalsIgnoreCase(marketingPartnerId)) {
        		messageHeaders.put(MARKETING_PARTNER_ID, marketingPartnerId);
        	}
        }
		if(StringUtils.isNotEmpty(order.getSellingChannelType())){
			messageHeaders.put(X_SUBSCLEINT_ID, order.getSellingChannelType());
		}
		messageHeaders.put(TRANSACTION_ID, ORDER_CREATE);
        if(hasRegistry(order)){
        	messageHeaders.put(REGISTRYFLAG,"true");
		}
		if(!CollectionUtils.isEmpty(order.getLocks())) {
			List<Lock> locks = order.getLocks().stream().filter(e -> FRAUD_LOCK_TYPE.equalsIgnoreCase(e.getLockType()) && FRAUD_LOCK_CODE_NOT_INTIATED.equalsIgnoreCase(e.getReasonCode())).collect(Collectors.toList());
			String lockReasonCode = !CollectionUtils.isEmpty(locks) && StringUtils.isNotEmpty(locks.get(0).getReasonCode()) ? locks.get(0).getReasonCode() : null;
			if(StringUtils.isNotEmpty(lockReasonCode)){
				messageHeaders.put(LOCK_REASON_CODE, lockReasonCode);
			}
			String lockId = !CollectionUtils.isEmpty(locks) && StringUtils.isNotEmpty(locks.get(0).getLockId()) ? locks.get(0).getLockId() : null;
			if(StringUtils.isNotEmpty(lockId)){
				messageHeaders.put(LOCK_ID, lockId);
			}
		}
		return messageHeaders;
	}
	
	/**
	 * Method to return message headers with updated fields
	 * @param collectOrder
	 * @param headers
	 * @return HttpHeaders
	 */
	default HttpHeaders addCustomMessageHeader(HttpHeaders headers, Order collectOrder) {
		if (ObjectUtils.isNotEmpty(collectOrder) && ObjectUtils.isNotEmpty(collectOrder.getOrderLines())) {
			if(StringUtils.isNotEmpty(collectOrder.getOrderLines().get(0).getMarketingPartnerId())) {
				headers.set(MARKETING_PARTNER_ID, collectOrder.getOrderLines().get(0).getMarketingPartnerId());
			}
		}
		return headers;
	}

	default boolean hasRegistry(Order order){
		boolean registryFlag=false;
		for (OrderLine line: order.getOrderLines()) {
			if(ObjectUtils.isNotEmpty(line.getRegistryId())){
				registryFlag=true;
				break;
			}
		}
		return registryFlag;
	}

	default void addHeaderToBuilder(EventLogMessageBuilder builder, Map<String, String> headers) {
		for (String key : headers.keySet()) {
			builder.withHeader(key, headers.get(key));
		}
	}
	default String getProperty(Map<String, String> propertyMap, String searchString) {
		return propertyMap.entrySet().stream().filter(e -> e.getKey().contains(searchString.toLowerCase()))
				.map(Map.Entry::getValue).findFirst().orElse(null);
	}
}

