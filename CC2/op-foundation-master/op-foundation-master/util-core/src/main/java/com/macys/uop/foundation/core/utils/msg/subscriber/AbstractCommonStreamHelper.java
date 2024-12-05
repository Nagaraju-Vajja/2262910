package com.macys.uop.foundation.core.utils.msg.subscriber;

import static com.macys.uop.foundation.core.utils.Constant.CALLERID_DEFAULT_VALUE;
import static com.macys.uop.foundation.core.utils.Constant.CLIENTID_HDR;
import static com.macys.uop.foundation.core.utils.Constant.CONTEXT_SUBSCRIBER_ERROR_MESSAGE_LOGGING;
import static com.macys.uop.foundation.core.utils.Constant.CORRELATIONID_HDR;
import static com.macys.uop.foundation.core.utils.Constant.EPF_CONTENT_TYPE_KEY;
import static com.macys.uop.foundation.core.utils.Constant.EPF_ORIGIN_KEY;
import static com.macys.uop.foundation.core.utils.Constant.EPF_PAYLOAD_KEY;
import static com.macys.uop.foundation.core.utils.Constant.EPF_REQUEST_URL_KEY;
import static com.macys.uop.foundation.core.utils.Constant.EXTERNAL_MESSAGE_INPUT_STATUS_CODE;
import static com.macys.uop.foundation.core.utils.Constant.EXTERNAL_MESSAGE_INPUT_TRANSACTION_CHANNEL_TYPE;
import static com.macys.uop.foundation.core.utils.Constant.EXTERNAL_MESSAGE_INPUT_TRANSACTION_DESC;
import static com.macys.uop.foundation.core.utils.Constant.EXTERNAL_MESSAGE_INPUT_TRANSACTION_ID;
import static com.macys.uop.foundation.core.utils.Constant.MESSAGEID_HDR;
import static com.macys.uop.foundation.core.utils.Constant.ORDERID_HDR;
import static com.macys.uop.foundation.core.utils.Constant.ORDERID_HDR_DEFAULT_VALUE; 
import static com.macys.uop.foundation.core.utils.Constant.MSG_PROCESSING_DURATION_TEXT_ORIGIN_PUBSUB;
import static com.macys.uop.foundation.core.utils.Constant.CONTEXT_SUBSCRIBER_MESSAGE_RECEIVED_LOGGING;
import static com.macys.uop.foundation.core.utils.Constant.CONTEXT_SUBSCRIBER_INVOKE_SERVICE_LOGGING;
import static com.macys.uop.foundation.core.utils.Constant.CONTEXT_SUBSCRIBER_EXTMSGINPUT_EVENTLOG_PUBLISHING_LOGGING;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.zalando.problem.ThrowableProblem;

import com.google.pubsub.v1.PubsubMessage;
import com.macys.uop.foundation.core.utils.CommonStatusCode;
import com.macys.uop.foundation.core.utils.Constant;
import com.macys.uop.foundation.core.utils.common.StringUtil;
import com.macys.uop.foundation.core.utils.epf.EPFMessagePublisher;
import com.macys.uop.foundation.core.utils.eventlog.EventLogMessage;
import com.macys.uop.foundation.core.utils.eventlog.EventLogMessagePublisher;
import com.macys.uop.foundation.core.utils.exception.Error;
import com.macys.uop.foundation.core.utils.exception.ErrorDetail;
import com.macys.uop.foundation.core.utils.exception.ExceptionUtil;
import com.macys.uop.foundation.core.utils.execution.StreamContextUtil;
import com.macys.uop.foundation.core.utils.logging.LogMessageBuilder;
import com.macys.uop.foundation.core.utils.logging.LogTypeEnum;
import com.macys.uop.foundation.core.utils.masking.ApplicationMaskingConfiguration;
import com.macys.uop.foundation.core.utils.masking.IDataMasker;
import com.macys.uop.foundation.core.utils.ordererror.OrderErrorMessage;
import com.macys.uop.foundation.core.utils.ordererror.OrderErrorMessageBuilder;
import com.macys.uop.foundation.core.utils.ordererror.OrderErrorMessagePublisher;
import com.macys.uop.foundation.core.utils.spring.SpringContextBridge;

import brave.Span;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Abstract class to hold common methods that are required by {@link AbstractStreamSubscriber}
 *
 */
@Component
@RequiredArgsConstructor
@Slf4j
public abstract class AbstractCommonStreamHelper implements StreamContextUtil, SubscriberUtil, ExceptionUtil, StringUtil
{
	@Value("${spring.application.name:default}")
	protected String applicationName;
	
	@Value("${spantag.header.names:#{null}}")
	protected String spanTagHeaderNames;
	
	@Value("${eventlog.publish.channel.name:event_onsuccess_dev}")
	private String eventLogChannelName;
	
	@Value("${messaging.subscriber.duration.logging.enabled:true}")
	private boolean isMSDurationLoggingEnabled;
	
	@Value("${messaging.subscriber.invokeservice.duration.logging.enabled:true}")
	private boolean isMSInvokeServiceDurationLoggingEnabled;
	
	@Value("${messaging.subscriber.extmsginputeventlogpublishing.duration.logging.enabled:true}")
	private boolean isMSExtMsgInputEventLogPublishingDurationLoggingEnabled;
	
	@Value("${pubsub.epf.event.publishing.valid.errorcodes.list:#{null}}")
	private String epfEventPublishingValidErrorCodesList;
	
	@Value("${pubsub.msg.nack.valid.errorcodes.list:#{null}}")
	private String msgNAckValidErrorCodesList;
	
	private final OrderErrorMessagePublisher orderErrorMessagePublisher;
	
	/**
	 * Subscribers can override this method to set subscription topic name
	 * 
	 * @return the subscription topic name
	 */
	protected abstract String getTopicSubscription();
	
	/**
	 * Subscribers can override this method to set external message input event log publish enable value
	 * 
	 * @return Boolean value true or false. Default value is false.
	 */
	protected Boolean isExtMsgInputEventLogPublishingEnabled() {
		return Boolean.FALSE;
	}
	
	/**
	 * Subscribers can override this method to set Content-Type 
	 * 
	 * @return Payload Content-Type  
	 */
	protected String getPayloadContentType() {
		return MediaType.APPLICATION_JSON_VALUE;
	}
	
	/**
	 * Get {@link Constant#CLIENTID_HDR} header value from Pub/Sub Message. 
	 * <p>
	 * It is used to preserve original {@link Constant#CLIENTID_HDR} value.
	 * 
	 * @param message Incoming Pub/Sub Message
	 * 
	 * @return {@link Constant#CLIENTID_HDR} header value, if not found default value {@link Constant#CALLERID_DEFAULT_VALUE}
	 */
	protected String getCallerId(PubsubMessage message) {
		return message.getAttributesOrDefault(CLIENTID_HDR, CALLERID_DEFAULT_VALUE);
	}
	
	/**
	 * Purpose of this method is to extract mandatory headers from incoming Pub/Sub message and set default values as required.
	 * <p> 
	 * <b>This is a default implementation. Domain services may override this method to set default values as required.</b>
	 * 
	 * @param message Incoming Pub/Sub Message
	 * 
	 * @return processed header values 
	 */
	protected Map<String, String> processHeaders(PubsubMessage message) {
		return processHeadersDefault(message);
	}
	
	
	/**
	 * Default implementation method provided by foundation to extract mandatory headers from incoming Pub/Sub 
	 * message and set default values as required.
	 * 
	 * @param message Incoming Pub/Sub Message
	 * 
	 * @return processed header values 
	 */
	protected Map<String, String> processHeadersDefault(PubsubMessage message) {
		// extract headers
		Map<String, String> headers = new HashMap<>();
		message.getAttributesMap().entrySet().stream().forEach(entry -> {
			String key = entry.getKey();
			String value = entry.getValue();
			headers.put(key, value);
		});
		
		// If orderId header is not present add default value "orderId"
		if (!headers.containsKey(ORDERID_HDR)) {
			headers.put(ORDERID_HDR, ORDERID_HDR_DEFAULT_VALUE);
		}
		
		// Always overwrite messageId value with message.getMessageId()
		headers.put(MESSAGEID_HDR, message.getMessageId());
		
		// Always overwrite existing clientId header value with application name
		headers.put(CLIENTID_HDR, applicationName);
		
		// Nothing to do with correlationId.
		
		return headers;
	}
	
	/**
	 * A new child span is created when subscriber receives a message.
	 * <p>
	 * This method attach span tags based on the comma separated header names provided in spantag.header.names property.
	 * <p>
	 * If property is not defined then no tags are attached to the subscriber span.
	 *  
	 * @param headers processed headers 
	 * @param span subscriber Span for which tags will be attached.
	 */
	protected void attachSpanTags(Map<String, String> headers, Span span) {
		if (!StringUtils.isAllBlank(spanTagHeaderNames) && !CollectionUtils.isEmpty(headers) && span!=null) {
			String trimmedSpanTagHeaderNames = StringUtils.trimToEmpty(spanTagHeaderNames);
			String[] spanTagHeaderNamesArray = trimmedSpanTagHeaderNames.split(",");
			for (int i = 0; i < spanTagHeaderNamesArray.length; i++) {
				String spanTagHeaderName = spanTagHeaderNamesArray[i];
				String spanTagHeaderNameValue = headers.get(spanTagHeaderName) != null
						? headers.get(spanTagHeaderName)
						: "";
				if (!spanTagHeaderNameValue.isEmpty()) {
					span.customizer().tag(spanTagHeaderName, spanTagHeaderNameValue);
				}
			}
		}
	}
	
	/**
	 * Returns the appropriate IDataMasker based on the content type
	 * 
	 * @return IDataMasker
	 */
	protected IDataMasker getIDataMasker() {
		IDataMasker masker=null;
		if(isContentTypeApplicationJson()) {
			masker=ApplicationMaskingConfiguration.getDefaultJsonMaskerInstance();
		}
		if(isContentTypeApplicationXml()) {
			masker=ApplicationMaskingConfiguration.getDefaultXmlMaskerInstance();
		}
		return masker;
	}
	
	/**
	 * Utility method to log error message. This method also takes care if {@code throwable instanceof ThrowableProblem}
	 * 
	 * @param throwable Throwable
	 * 
	 */
	protected void logError(Throwable throwable) {
		IDataMasker masker=getIDataMasker();
		if (throwable instanceof ThrowableProblem) {
			ThrowableProblem problem=(ThrowableProblem) throwable;
			logProblem(problem);
		} else {
			LogMessageBuilder logMessageBuilder = new LogMessageBuilder()
			.withClientId(getClientId())
			.withMessageId(getMessageId())
			.withOrderId(getOrderId())
			.withCorrelationId(getCorrelationId())
			.withAppName(getAppName())
			.withCallerId(getCallerId())
			.withLogType(LogTypeEnum.ERROR)
			.withContext(CONTEXT_SUBSCRIBER_ERROR_MESSAGE_LOGGING)
			.withTopicName(getTopicSubscription())
			.withErrorCode(CommonStatusCode.APPLICATION_ERROR.getCode())
			.withErrorMessage(CommonStatusCode.APPLICATION_ERROR.getDescription())
			.withStackTrace(ExceptionUtils.getStackTrace(throwable))
			.withPubsubMessage(getPayload());
		
			if(masker!=null) {
				logMessageBuilder.withPubsubMessageDataMasker(masker);
			} else {
				logMessageBuilder.withMaskingEnabled(false);
			}
			
			logMessageBuilder.withLogger(log).buildDisableChecking().logAsError();
		}
	}
	
	/**
	 * Logs ThrowableProblem
	 * 
	 * @param problem ThrowableProblem
	 */
	protected void logProblem(ThrowableProblem problem) {
		IDataMasker masker=getIDataMasker();
		if (problem.getParameters().get(Constant.PROBLEM_ERROR_KEY) != null) {
			com.macys.uop.foundation.core.utils.exception.Error error = (Error) problem.getParameters().get(Constant.PROBLEM_ERROR_KEY);
			if(error!=null) {
				LogMessageBuilder logMessageBuilder = new LogMessageBuilder()
				.withClientId(getClientId())
				.withMessageId(getMessageId())
				.withOrderId(getOrderId())
				.withCorrelationId(getCorrelationId())
				.withAppName(getAppName())
				.withCallerId(getCallerId())
				.withLogType(LogTypeEnum.ERROR)
				.withContext(CONTEXT_SUBSCRIBER_ERROR_MESSAGE_LOGGING)
				.withTopicName(getTopicSubscription())
				.withErrorCode(error.getCode())
				.withErrorMessage(error.getMessage())
				.withStackTrace(ExceptionUtils.getStackTrace(problem))
				.withPubsubMessage(getPayload());
				
				if(masker!=null) {
					logMessageBuilder.withPubsubMessageDataMasker(masker);
				} else {
					logMessageBuilder.withMaskingEnabled(false);
				}
				
				logMessageBuilder.withLogger(log).buildDisableChecking().logAsError();
			}
		} else {
			LogMessageBuilder logMessageBuilder = new LogMessageBuilder()
			.withClientId(getClientId())
			.withMessageId(getMessageId())
			.withOrderId(getOrderId())
			.withCorrelationId(getCorrelationId())
			.withAppName(getAppName())
			.withCallerId(getCallerId())
			.withLogType(LogTypeEnum.ERROR)
			.withContext(CONTEXT_SUBSCRIBER_ERROR_MESSAGE_LOGGING)
			.withTopicName(getTopicSubscription())
			.withStackTrace(ExceptionUtils.getStackTrace(problem))
			.withPubsubMessage(getPayload());
			
			if(masker!=null) {
				logMessageBuilder.withPubsubMessageDataMasker(masker);
			} else {
				logMessageBuilder.withMaskingEnabled(false);
			}
			logMessageBuilder.withLogger(log).buildDisableChecking().logAsError();
		}
	}
	
	/**
	 * Utility method to log incoming Pub/Sub message
	 */
	protected void logMessage() {
		IDataMasker masker=getIDataMasker();
		LogMessageBuilder logMessageBuilder = new LogMessageBuilder()
		.withClientId(getClientId())
		.withMessageId(getMessageId())
		.withOrderId(getOrderId())
		.withCorrelationId(getCorrelationId())
		.withAppName(getAppName())
		.withCallerId(getCallerId())
		.withContext(CONTEXT_SUBSCRIBER_MESSAGE_RECEIVED_LOGGING)
		.withLogType(LogTypeEnum.NTFY)
		.withTopicName(getTopicSubscription())
		.withHeaderAttribute(getMessageHeaders())
		.withPubsubMessage(getPayload());
		
		if(masker!=null) {
			logMessageBuilder.withPubsubMessageDataMasker(masker);
		} else {
			logMessageBuilder.withMaskingEnabled(false);
		}
		
		logMessageBuilder.withLogger(log).buildDisableChecking().logAsInfo();
	}

	protected void logMessagePartial() {
		IDataMasker masker=getIDataMasker();
		LogMessageBuilder logMessageBuilder = new LogMessageBuilder()
				.withClientId(getClientId())
				.withMessageId(getMessageId())
				.withOrderId(getOrderId())
				.withCorrelationId(getCorrelationId())
				.withAppName(getAppName())
				.withCallerId(getCallerId())
				.withContext(CONTEXT_SUBSCRIBER_MESSAGE_RECEIVED_LOGGING)
				.withLogType(LogTypeEnum.NTFY)
				.withTopicName(getTopicSubscription());

		if(masker!=null) {
			logMessageBuilder.withPubsubMessageDataMasker(masker);
		} else {
			logMessageBuilder.withMaskingEnabled(false);
		}

		logMessageBuilder.withLogger(log).buildDisableChecking().logAsInfo();
	}


	/**
	 * Utility method to create {@link OrderErrorMessage} through {@link OrderErrorMessageBuilder} and publish it.
	 * 
	 * @param throwable Throwable
	 */
	protected void publishOrderErrorEvent(Throwable throwable) {
		
		com.macys.uop.foundation.core.utils.exception.Error error=null;
		
		if (throwable instanceof ThrowableProblem) {
			ThrowableProblem problem=(ThrowableProblem)throwable;
			if (problem.getParameters().get(Constant.PROBLEM_ERROR_KEY) != null) {
				error = (Error) problem.getParameters().get(Constant.PROBLEM_ERROR_KEY);
			} else {
				error = com.macys.uop.foundation.core.utils.exception.Error.builder()
						.withMessage(problem.getDetail() != null ? problem.getDetail()
								: CommonStatusCode.BAD_REQUEST.getDescription())
						.withCode(CommonStatusCode.BAD_REQUEST.getCode()).build();
			}
		}
		else {
			error = com.macys.uop.foundation.core.utils.exception.Error
					.builder().withMessage(getMessage(throwable)).withCode(CommonStatusCode.INTERNAL_SERVICE_ERROR.getCode())
					.build();
		}
		
		String stackTrace = ExceptionUtils.getStackTrace(throwable);
		OrderErrorMessageBuilder builder=OrderErrorMessage.builder()
			.withCorrelationId(getCorrelationId())
			.withOrderId(getOrderId())
			.withCreatedBy(getAppName())
			.withCreatedTs(Instant.now().toString())
			.withLastUpdatedBy(getAppName())
			.withLastUpdatedTs(Instant.now().toString())
			.withMessage(getPayload())
			.withErrorCode(error.getCode())
			.withErrorDesc(error.toString())
			.withServiceName(getAppName())
			.withStackTrace(stackTrace)
			.withEntityRefId(getOrderId())
			.withEntityRefType("Order")
			.withTopicName(getRequestURL() == null ? null: extractTopicNameFromRequestURL(getRequestURL()))
			.withMessageContentType(getContentType());
	
			if(getClientId()!=null) {
				builder.withMessageHeader(CLIENTID_HDR, getClientId());
			}
			if(getCorrelationId()!=null) {
				builder.withMessageHeader(CORRELATIONID_HDR, getCorrelationId());
			}
			if(getMessageId()!=null) {
				builder.withMessageHeader(MESSAGEID_HDR, getMessageId());
			}
			if(getOrderId()!=null) {
				builder.withMessageHeader(ORDERID_HDR, getOrderId());
			}
			
			orderErrorMessagePublisher.publishOrderErrorMessage(builder.build());
	}
	
	/**
	 * Utility method for publishing EPF event. 
	 * <br>
	 * Message publishing will only happen for foundation raised/thrown error.
	 * 
	 * @param throwable Throwable
	 */
	protected void publishEPFEvent(Throwable throwable) {
		
		com.macys.uop.foundation.core.utils.exception.Error error=null;
		
		if (throwable instanceof ThrowableProblem) {
			ThrowableProblem problem=(ThrowableProblem)throwable;
			if (problem.getParameters().get(Constant.PROBLEM_ERROR_KEY) != null) {
				error = (Error) problem.getParameters().get(Constant.PROBLEM_ERROR_KEY);
			} else {
				error = com.macys.uop.foundation.core.utils.exception.Error.builder()
						.withMessage(problem.getDetail() != null ? problem.getDetail()
								: CommonStatusCode.BAD_REQUEST.getDescription())
						.withCode(CommonStatusCode.BAD_REQUEST.getCode()).build();
			}
		}
		else {
			error = com.macys.uop.foundation.core.utils.exception.Error
					.builder().withMessage(getMessage(throwable)).withCode(CommonStatusCode.INTERNAL_SERVICE_ERROR.getCode())
					.build();
		}
		
		// If ErrorDetail information not available then populate default value.
		if(CollectionUtils.isEmpty(error.getErrorDetails())) {
			ErrorDetail errorDetail=ErrorDetail.builder()
					.withDomain(getAppName())
					.withReason(error.getCode())
					.withMessage(error.getMessage())
					.build();
			List<ErrorDetail> errorDetails = new ArrayList<>();
			errorDetails.add(errorDetail);
			error.setErrorDetails(errorDetails);
		} 
		
		List<String> errorCodesList = getEPFEventPublishingValidErrorCodes(); 
		if(errorCodesList!=null && errorCodesList.contains(error.getCode())) {
			
			Map<String,String> headers=new HashMap<>();
			headers.putAll(getMessageHeaders());
			
			Map<String,String> additionalEPFInfo=new HashMap<>();
			additionalEPFInfo.put(EPF_PAYLOAD_KEY, getPayload());
			additionalEPFInfo.put(EPF_CONTENT_TYPE_KEY, getContentType());
			additionalEPFInfo.put(EPF_ORIGIN_KEY, getOrigin().toString());
			additionalEPFInfo.put(EPF_REQUEST_URL_KEY, getRequestURL());
			
			EPFMessagePublisher epfMessagePublisher=SpringContextBridge.getBean(EPFMessagePublisher.class);
			epfMessagePublisher.publishEPFMessage(error, Constant.INTERNAL_SERVICE_ERROR_CODE, headers, additionalEPFInfo);
		}
		
	}
	
	/**
	 * 
	 */
	/**
	 * Utility method to log message overall processing duration 
	 * 
	 * @param startTime Start Time
	 * @param endTime End Time
	 * @param context Logging Context
	 */
	protected void logMessageProcessingDuration(Instant startTime, Instant endTime, String context) {
		Assert.notNull(startTime, "'startTime' must not be null");
		Assert.notNull(endTime, "'endTime' must not be null");
		
		if(isMSDurationLoggingEnabled) {
			new LogMessageBuilder()
					.withClientId(getClientId())
					.withMessageId(getMessageId())
					.withOrderId(getOrderId())
					.withCorrelationId(getCorrelationId())
					.withAppName(getAppName())
					.withCallerId(getCallerId())
					.withContext(context)
					.withLogType(LogTypeEnum.NTFY)
					.withTopicName(getTopicSubscription())
					.withHeaderAttribute(getMessageHeaders())
					.withAdditionalInfo(constructMsgProcessingDurationText4Logging(startTime, endTime, MSG_PROCESSING_DURATION_TEXT_ORIGIN_PUBSUB))
					.withLogger(log)
					.buildDisableChecking()
					.logAsInfo();		
		}
	}
	
	/**
	 * Utility method to log message processing duration for service invocation
	 * 
	 * @param startTime Start Time
	 * @param endTime End Time
	 */
	protected void logMessageProcessingInvokeServiceDuration(Instant startTime, Instant endTime) {
		Assert.notNull(startTime, "'startTime' must not be null");
		Assert.notNull(endTime, "'endTime' must not be null");
		
		if(isMSInvokeServiceDurationLoggingEnabled) {
			long totalDuration= Duration.between(startTime, endTime).toMillis();
			
			StringBuilder msgBucket=new StringBuilder();
			msgBucket.append("Message Processing Invoke Service Initiated At: "+startTime.toString()+" :: ");
			msgBucket.append("Message Processing Invoke Service Completed At: "+endTime.toString()+" :: ");
			msgBucket.append("Message Processing Invoke Service Time Taken In Millis: "+totalDuration);
			
			new LogMessageBuilder()
					.withClientId(getClientId())
					.withMessageId(getMessageId())
					.withOrderId(getOrderId())
					.withCorrelationId(getCorrelationId())
					.withAppName(getAppName())
					.withCallerId(getCallerId())
					.withContext(CONTEXT_SUBSCRIBER_INVOKE_SERVICE_LOGGING)
					.withLogType(LogTypeEnum.NTFY)
					.withTopicName(getTopicSubscription())
					.withHeaderAttribute(getMessageHeaders())
					.withAdditionalInfo(msgBucket.toString())
					.withLogger(log)
					.buildDisableChecking()
					.logAsInfo();		
		}
	}
	
	/**
	 * Utility method to log message processing External Message Input EventLog Publishing duration
	 * 
	 * @param startTime Start Time
	 * @param endTime End Time
	 */
	protected void logExtMsgInputEventLogPublishingDuration(Instant startTime, Instant endTime) {
		Assert.notNull(startTime, "'startTime' must not be null");
		Assert.notNull(endTime, "'endTime' must not be null");
		
		if(isMSExtMsgInputEventLogPublishingDurationLoggingEnabled) {
			long totalDuration= Duration.between(startTime, endTime).toMillis();
			
			StringBuilder msgBucket=new StringBuilder();
			msgBucket.append("Message Processing ExtMsgInput EventLog Publishing Initiated At: "+startTime.toString()+" :: ");
			msgBucket.append("Message Processing ExtMsgInput EventLog Publishing Completed At: "+endTime.toString()+" :: ");
			msgBucket.append("Message Processing ExtMsgInput EventLog Publishing Time Taken In Millis: "+totalDuration);
			
			new LogMessageBuilder()
					.withClientId(getClientId())
					.withMessageId(getMessageId())
					.withOrderId(getOrderId())
					.withCorrelationId(getCorrelationId())
					.withAppName(getAppName())
					.withCallerId(getCallerId())
					.withContext(CONTEXT_SUBSCRIBER_EXTMSGINPUT_EVENTLOG_PUBLISHING_LOGGING)
					.withLogType(LogTypeEnum.NTFY)
					.withTopicName(getTopicSubscription())
					.withHeaderAttribute(getMessageHeaders())
					.withAdditionalInfo(msgBucket.toString())
					.withLogger(log)
					.buildDisableChecking()
					.logAsInfo();		
		}
	}
	
	/**
	 *  Utility method that helps publishing event log message for edge services
	 */
	protected void publishEventLog4ExternalMsgInput() {
		if(isExtMsgInputEventLogPublishingEnabled()) {
			Instant startTime=Instant.now();
			
			EventLogMessage eventlogMessage = EventLogMessage.builder()
					.withCreatedBy(getAppName())
					.withTransactionId(EXTERNAL_MESSAGE_INPUT_TRANSACTION_ID)
					.withTransactionDesc(EXTERNAL_MESSAGE_INPUT_TRANSACTION_DESC)
					.withTransactionTime(Instant.now().toString()) 
					.withChannelName(extractTopicNameFromRequestURL(getRequestURL()))
					.withChannelType(EXTERNAL_MESSAGE_INPUT_TRANSACTION_CHANNEL_TYPE)
					.withHeader(ORDERID_HDR, getOrderId())
					.withHeader(CLIENTID_HDR, getClientId())
					.withHeader(CORRELATIONID_HDR, getCorrelationId())
					.withHeader(MESSAGEID_HDR, getMessageId())
					.withRequestPayload(getPayload())
					.withStatusCode(EXTERNAL_MESSAGE_INPUT_STATUS_CODE)
					.build();
			
			EventLogMessagePublisher eventLogMessagePublisher=SpringContextBridge.getBean(EventLogMessagePublisher.class);
			eventLogMessagePublisher.publishEventLogMessage(eventLogChannelName, eventlogMessage, getMessageHeaders());
			
			logExtMsgInputEventLogPublishingDuration(startTime, Instant.now());
		}
	}
	
	/**
	 * Populate list of Error Codes for which EPF event will be published.
	 * By default valid list will contain {@link ommonStatusCode.NO_CORRELATIONID} and {@link CommonStatusCode.MSSAGE_DUPLICATION_CHECK_ERROR} error codes.
	 * 
	 * @return List of Error Codes.
	 */
	protected List<String> getEPFEventPublishingValidErrorCodes() {
		List<String> errorCodesList = null; 
		if(StringUtils.isAllBlank(epfEventPublishingValidErrorCodesList)) {
			errorCodesList=new ArrayList<>();
			errorCodesList.add(CommonStatusCode.NO_CORRELATIONID.getCode());
			errorCodesList.add(CommonStatusCode.MSSAGE_DUPLICATION_CHECK_ERROR.getCode());
		} else {
			String[] errorCodesArr=epfEventPublishingValidErrorCodesList.split(","); 
			if(errorCodesArr!=null && errorCodesArr.length>0) {
				errorCodesList=Arrays.asList(errorCodesArr);
			} else {
				errorCodesList=new ArrayList<>();
				errorCodesList.add(CommonStatusCode.NO_CORRELATIONID.getCode());
				errorCodesList.add(CommonStatusCode.MSSAGE_DUPLICATION_CHECK_ERROR.getCode());
			}
		}
		return errorCodesList;
	}
	
	/**
	 * Utility method which evaluates if the exception is eligible for NAcking the message 
	 * 
	 * @param ex Exception
	 * @return True if message to be NAcked else False
	 */
	protected boolean shouldProceedNAckMessage(Exception ex) { 
		boolean shouldProceed = false;
		if (!StringUtils.isAllBlank(msgNAckValidErrorCodesList) && (ex instanceof ThrowableProblem)) {
			ThrowableProblem problem=(ThrowableProblem)ex;
			if (problem.getParameters().get(Constant.PROBLEM_ERROR_KEY) != null) {
				com.macys.uop.foundation.core.utils.exception.Error error = (Error) problem.getParameters().get(Constant.PROBLEM_ERROR_KEY);
				String errorCode=error.getCode();
				String[] errorCodesArr=msgNAckValidErrorCodesList.split(",");
				if(errorCode!=null && errorCodesArr!=null && errorCodesArr.length>0) {
					shouldProceed=Arrays.asList(errorCodesArr).contains(errorCode);
				}
			}	
		} 
		return shouldProceed;
	}
	
	/**
	 * Utility method which evaluates if the exception is eligible for NAcking the message 
	 * 
	 * @param ex Exception
	 * @return True if message to be NAcked else False
	 */
	protected boolean isCircuitBreakerError(Exception ex) { 
		boolean shouldProceed = false;
		if (ex instanceof ThrowableProblem) {
			ThrowableProblem problem=(ThrowableProblem)ex;
			if (problem.getParameters().get(Constant.PROBLEM_ERROR_KEY) != null) {
				com.macys.uop.foundation.core.utils.exception.Error error = (Error) problem.getParameters().get(Constant.PROBLEM_ERROR_KEY);
				String reasonCode=null;
				
				if (error.getErrorDetails() != null && error.getErrorDetails().size() > 0) {
					reasonCode = error.getErrorDetails().get(0).getReason();
				}
				
				if (CommonStatusCode.CIRCUIT_BREAKER_OPEN.getCode().equals(reasonCode)) {
					shouldProceed = true;
				}
			}	
		} 
		return shouldProceed;
	}
	
	/**
	 * Gives us the CB Name in the error
	 * 
	 * @param ex Exception
	 * @return True if message to be NAcked else False
	 */
	protected String getCbName(Exception ex) { 
		String cbName = null;
		if (ex instanceof ThrowableProblem) {
			ThrowableProblem problem=(ThrowableProblem)ex;
			if (problem.getParameters().get(Constant.PROBLEM_ERROR_KEY) != null) {
				com.macys.uop.foundation.core.utils.exception.Error error = (Error) problem.getParameters().get(Constant.PROBLEM_ERROR_KEY);
				String errorCode=error.getCode();
				if (CommonStatusCode.CIRCUIT_BREAKER_OPEN.getCode().equals(errorCode)) {
					cbName = error.getErrorDetails().get(0).getLocation();
				}
			}	
		} 
		return cbName;
	}
	
	/**
	 * Gives us the wait duration in open state
	 * 
	 * 
	 * @param ex Exception
	 * @return wait duration in open state in millis
	 */
	protected long getWaitDurationInOpenStateInMillis(Exception ex) { 
		String cbName = getCbName(ex);
		
		String configName = SpringContextBridge
				.getProperty("resilience4j.circuitbreaker.instances."+cbName+".baseConfig");
		String waitDurationInOpenState = SpringContextBridge
				.getProperty("resilience4j.circuitbreaker.configs." + configName + ".waitDurationInOpenState");
		if (StringUtils.isEmpty(waitDurationInOpenState)) {
			waitDurationInOpenState = "10s";
		}
		return Duration.parse("PT" + waitDurationInOpenState).toMillis();
	}
}
