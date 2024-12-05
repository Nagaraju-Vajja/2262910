package com.macys.uop.foundation.core.utils.logging;

import static com.macys.uop.foundation.core.utils.Constant.CONTEXT_DEFAULT_LOGGING;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.springframework.util.Assert;

import com.macys.uop.foundation.core.utils.exception.ProblemUtil;
import com.macys.uop.foundation.core.utils.masking.ApplicationMaskingConfiguration;
import com.macys.uop.foundation.core.utils.masking.IDataMasker;

/**
 * Provides Builder Pattern to construct {@link  LogMessage}
 * <br>
 * with* methods are part of building {@link  LogMessage} information 	
 */
public class LogMessageBuilder implements ProblemUtil {

	private String orderId;
	private String clientId;
	private String messageId;
	private String correlationId;
	private String appName;
	private String callerId;
	private LogTypeEnum logType = LogTypeEnum.LOG;
	private String requestBody;
	private String responseBody;
	private String transactionName;
	private String operationType;
	private String topicName;
	private String pubsubMessage;
	private String eventType;
	private String eventMessage;
	private String errorCode;
	private String errorMessage;
	private String stackTrace;
	private String additionalInfo;
	private String statusCode;
	private String statusMessage;
	private String context=CONTEXT_DEFAULT_LOGGING;
	private String endpointUrl;
	private Map<String,String> headerAttribute=new HashMap<>();
	private IDataMasker requestBodyDataMasker;
	private IDataMasker responseBodyDataMasker;
	private IDataMasker pubsubMessageDataMasker;
	private boolean maskingEnabled=true;
	private Logger logger;

	/**
	 * Builds the Log Message with mandatory field checking.
	 * Throws exception if mandatory checking fails.
	 * 
	 * @return LogMessage
	 */
	public LogMessage build() {
		Assert.hasText(orderId, "'orderId' must not be empty");
		Assert.hasText(clientId, "'clientId' must not be empty");
		Assert.hasText(messageId, "'messageId' must not be empty");
		Assert.hasText(correlationId, "'correlationId' must not be empty");
		Assert.hasText(appName, "'appName' must not be empty");

		return constructLogMessage();
	}
	
	/**
	 * Builds the Log Message without mandatory field checking.
	 * Throws exception if mandatory checking fails.
	 * 
	 * @return LogMessage
	 */
	public LogMessage buildDisableChecking() {
		return constructLogMessage();
	}
	
	/**
	 * Construct the Log Message 
	 * 
	 * @return LogMessage
	 */
	private LogMessage constructLogMessage()
	{
		LogMessage logMessage = new LogMessage();
		logMessage.setOrderId(orderId);
		logMessage.setClientId(clientId);
		logMessage.setMessageId(messageId);
		logMessage.setCorrelationId(correlationId);
		logMessage.setAppName(appName);
		logMessage.setCallerId(callerId);
		logMessage.setLogType(logType);
		logMessage.setRequestBody(requestBody);
		logMessage.setResponseBody(responseBody);
		logMessage.setTransactionName(transactionName);
		logMessage.setOperationType(operationType);
		logMessage.setTopicName(topicName);
		logMessage.setPubsubMessage(pubsubMessage);
		logMessage.setEventType(eventType);
		logMessage.setEventMessage(eventMessage);
		logMessage.setErrorCode(errorCode);
		logMessage.setErrorMessage(errorMessage);
		logMessage.setStackTrace(stackTrace);
		logMessage.setAdditionalInfo(additionalInfo);
		logMessage.setStatusCode(statusCode);
		logMessage.setStatusMessage(statusMessage);
		logMessage.setContext(context);
		logMessage.setEndpointUrl(endpointUrl);
		logMessage.setHeaderAttribute(headerAttribute);
		logMessage.setLogger(logger);
		
		if(!ApplicationMaskingConfiguration.isMaskingEnabled()) {
			maskingEnabled=false;
		}
		
		// When masking is enabled and developer has not set any specific IJsonMasker implementation, 
		// then by default Json Masker is set. 
		if(maskingEnabled && requestBodyDataMasker==null) {
			requestBodyDataMasker=ApplicationMaskingConfiguration.getDefaultJsonMaskerInstance();
		}
		if(maskingEnabled && responseBodyDataMasker==null) {
			responseBodyDataMasker=ApplicationMaskingConfiguration.getDefaultJsonMaskerInstance();
		}
		if(maskingEnabled && pubsubMessageDataMasker==null) {
			pubsubMessageDataMasker=ApplicationMaskingConfiguration.getDefaultJsonMaskerInstance();
		}
		
		// For the use case, when there are issues in configuration file and default dataMasker becomes null,
		// we disable masking to avoid Runtime Parsing Exception.
		if(requestBodyDataMasker==null || responseBodyDataMasker==null || pubsubMessageDataMasker==null) {
			maskingEnabled=false;
		}
		
		logMessage.setMaskingEnabled(maskingEnabled);
		logMessage.setRequestBodyDataMasker(requestBodyDataMasker);
		logMessage.setResponseBodyDataMasker(responseBodyDataMasker);
		logMessage.setPubsubMessageDataMasker(pubsubMessageDataMasker);
		
		return logMessage;
	}

	public LogMessageBuilder withOrderId(final String orderId) {
		this.orderId = orderId;
		return this;
	}

	public LogMessageBuilder withClientId(final String clientId) {
		this.clientId = clientId;
		return this;
	}
	
	public LogMessageBuilder withCallerId(final String callerId) {
		this.callerId = callerId;
		return this;
	}

	public LogMessageBuilder withMessageId(final String messageId) {
		this.messageId = messageId;
		return this;
	}

	public LogMessageBuilder withCorrelationId(final String correlationId) {
		this.correlationId = correlationId;
		return this;
	}

	public LogMessageBuilder withAppName(final String appName) {
		this.appName = appName;
		return this;
	}

	public LogMessageBuilder withLogType(final LogTypeEnum logType) {
		this.logType = logType;
		return this;
	}

	public LogMessageBuilder withRequestBody(final String requestBody) {
		this.requestBody = requestBody;
		return this;
	}

	public LogMessageBuilder withResponseBody(final String responseBody) {
		this.responseBody = responseBody;
		return this;
	}

	public LogMessageBuilder withTransactionName(final String transactionName) {
		this.transactionName = transactionName;
		return this;
	}

	public LogMessageBuilder withOperationType(final String operationType) {
		this.operationType = operationType;
		return this;
	}

	public LogMessageBuilder withTopicName(final String topicName) {
		this.topicName = topicName;
		return this;
	}

	public LogMessageBuilder withPubsubMessage(final String pubsubMessage) {
		this.pubsubMessage = pubsubMessage;
		return this;
	}

	public LogMessageBuilder withEventType(final String eventType) {
		this.eventType = eventType;
		return this;
	}

	public LogMessageBuilder withEventMessage(final String eventMessage) {
		this.eventMessage = eventMessage;
		return this;
	}

	public LogMessageBuilder withErrorCode(final String errorCode) {
		this.errorCode = errorCode;
		return this;
	}

	public LogMessageBuilder withErrorMessage(final String errorMessage) {
		this.errorMessage = errorMessage;
		return this;
	}

	public LogMessageBuilder withStackTrace(final String stackTrace) {
		this.stackTrace = stackTrace;
		return this;
	}

	public LogMessageBuilder withAdditionalInfo(final String additionalInfo) {
		this.additionalInfo = additionalInfo;
		return this;
	}
	
	public LogMessageBuilder withStatusCode(final String statusCode) {
		this.statusCode = statusCode;
		return this;
	}
	
	public LogMessageBuilder withStatusMessage(final String statusMessage) {
		this.statusMessage = statusMessage;
		return this;
	}
	
	public LogMessageBuilder withContext(final String context) {
		this.context = context;
		return this;
	}
	
	public LogMessageBuilder withEndpointUrl(final String endpointUrl) {
		this.endpointUrl = endpointUrl;
		return this;
	}
	
	public LogMessageBuilder withHeaderAttribute(final String key, final String value) {
		this.headerAttribute.put(key, value);
		return this;
	}
	
	public LogMessageBuilder withHeaderAttribute(final Map<String,String> headerAttributeMap) {
		this.headerAttribute.putAll(headerAttributeMap);
		return this;
	}
	
	public LogMessageBuilder withRequestBodyDataMasker(final IDataMasker requestBodyDataMasker) {
		Assert.notNull(requestBodyDataMasker, "'requestBodyDataMasker' must not be null");
		this.requestBodyDataMasker = requestBodyDataMasker;
		return this;
	}
	
	public LogMessageBuilder withResponseBodyDataMasker(final IDataMasker responseBodyDataMasker) {
		Assert.notNull(responseBodyDataMasker, "'responseBodyDataMasker' must not be null");
		this.responseBodyDataMasker = responseBodyDataMasker;
		return this;
	}
	
	public LogMessageBuilder withPubsubMessageDataMasker(final IDataMasker pubsubMessageDataMasker) {
		Assert.notNull(pubsubMessageDataMasker, "'pubsubMessageDataMasker' must not be null");
		this.pubsubMessageDataMasker = pubsubMessageDataMasker;
		return this;
	}
	
	public LogMessageBuilder withMaskingEnabled(final boolean maskingEnabled) {
		this.maskingEnabled = maskingEnabled;
		return this;
	}
	
	public LogMessageBuilder withLogger(final Logger logger) {
		this.logger = logger;
		return this;
	}
}
