package com.macys.uop.foundation.core.utils.logging;

import static com.macys.uop.foundation.core.utils.Constant.CONTEXT_DEFAULT_LOGGING;
import static com.macys.uop.foundation.core.utils.Constant.LOG_LINE_SIZE_EXCEEDED_MESSAGE;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import com.macys.uop.foundation.core.utils.common.GlobalApplicationBucket;
import com.macys.uop.foundation.core.utils.common.StringUtil;
import com.macys.uop.foundation.core.utils.masking.IDataMasker;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * Class that represents a Log Message. 
 * <br>
 * Refer to https://confluence.federated.fds/display/OCOM/UOP+Logging+Framework for field description. 
 * <br>
 * If no context value is set by default "NOT ASSIGNED" will be logged.
 * <br>
 * Lombok provided Slf4j wrapper used to log messages.
 */
@Data
@Slf4j
public class LogMessage implements StringUtil
{
	private String orderId;
	private String clientId;
	private String messageId;
	private String correlationId;
	private String appName;
	private String callerId;
	private LogTypeEnum logType=LogTypeEnum.LOG; 
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
	 * Provides builder to build log message 
	 * 
	 * @return LogMessageBuilder
	 */
	public static LogMessageBuilder builder() {
		return new LogMessageBuilder();
	}
		
	/**
	 *  Log in Info level if Info level logging is enabled.
	 *  For backward compatibility if logger instance is not set through builder pattern then global logger instance will be used.
	 */
	public void logAsInfo() {
		if(logger!=null) {
			if(logger.isInfoEnabled()) {
				logger.info(getLogMessage());
			}
		} else {
			if(log.isInfoEnabled()) {
				log.info(getLogMessage());
			}
		}
	}
	
	/**
	 *  Log in Debug level if Debug level logging is enabled.
	 *  For backward compatibility if logger instance is not set through builder pattern then global logger instance will be used.
	 */
	public void logAsDebug() {
		if(logger!=null) {
			if(logger.isDebugEnabled()) {
				logger.debug(getLogMessage());
			}
		} else {
			if(log.isDebugEnabled()) {
				log.debug(getLogMessage());
			}
		}
	}
	
	/**
	 *  Log in Warn level if Warn level logging is enabled.
	 *  For backward compatibility if logger instance is not set through builder pattern then global logger instance will be used.
	 */
	public void logAsWarn() {
		if(logger!=null) {
			if(logger.isWarnEnabled()) {
				logger.warn(getLogMessage());
			}
		} else {
			if(log.isWarnEnabled()) {
				log.warn(getLogMessage());
			}
		}
	}
	
	/**
	 *  Log in Error level if Error level logging is enabled.
	 *  For backward compatibility if logger instance is not set through builder pattern then global logger instance will be used.
	 */
	public void logAsError() {
		if(logger!=null) {
			if(logger.isErrorEnabled()) {
				logger.error(getLogMessage());
			}
		} else {
			if(log.isErrorEnabled()) {
				log.error(getLogMessage());
			}
		}
	}
	
	/**
	 *  Log in Trace level if Trace level logging is enabled.
	 *  For backward compatibility if logger instance is not set through builder pattern then global logger instance will be used.
	 */
	public void logAsTrace() {
		if(logger!=null) {
			if(logger.isTraceEnabled()) {
				logger.trace(getLogMessage());
			}
		} else {
			if(log.isTraceEnabled()) {
				log.trace(getLogMessage());
			}
		}
	}
	
	/**
	 * Default log message when log message size goes beyond configured value. 
	 * 
	 * @return log message
	 */
	public String toStringWhenLogSizeExceeded(int logMessageSize) {
		
		StringBuilder builder=new  StringBuilder();
		builder.append("{");
		builder.append("\"").append("context").append("\"").append(" : ").append("\"").append(context).append("\"").append(" , ");
		builder.append("\"").append("logType").append("\"").append(" : ").append("\"").append(logType).append("\"").append(" , ");
		builder.append("\"").append("orderId").append("\"").append(" : ").append("\"").append(orderId).append("\"").append(" , ");
		builder.append("\"").append("clientId").append("\"").append(" : ").append("\"").append(clientId).append("\"").append(" , ");
		builder.append("\"").append("messageId").append("\"").append(" : ").append("\"").append(messageId).append("\"").append(" , ");
		builder.append("\"").append("correlationId").append("\"").append(" : ").append("\"").append(correlationId).append("\"").append(" , ");
		
		if(!StringUtils.isAllBlank(callerId))
			builder.append("\"").append("callerId").append("\"").append(" : ").append("\"").append(callerId).append("\"").append(" , ");
		
		if(!StringUtils.isAllBlank(transactionName))
			builder.append("\"").append("transactionName").append("\"").append(" : ").append("\"").append(transactionName).append("\"").append(" , ");
		
		if(!StringUtils.isAllBlank(operationType))
			builder.append("\"").append("operationType").append("\"").append(" : ").append("\"").append(operationType).append("\"").append(" , ");
		
		if(!StringUtils.isAllBlank(topicName))
			builder.append("\"").append("topicName").append("\"").append(" : ").append("\"").append(topicName).append("\"").append(" , ");
		
		if(!StringUtils.isAllBlank(eventType))
			builder.append("\"").append("eventType").append("\"").append(" : ").append("\"").append(eventType).append("\"").append(" , ");
		
		if(!StringUtils.isAllBlank(eventMessage))
			builder.append("\"").append("eventMessage").append("\"").append(" : ").append("\"").append(quoteAsString(eventMessage)).append("\"").append(" , ");
		
		if(!StringUtils.isAllBlank(errorCode))
			builder.append("\"").append("errorCode").append("\"").append(" : ").append("\"").append(errorCode).append("\"").append(" , ");
		
		if(!StringUtils.isAllBlank(errorMessage))
			builder.append("\"").append("errorMessage").append("\"").append(" : ").append("\"").append(quoteAsString(errorMessage)).append("\"").append(" , ");

		builder.append("\"").append("additionalInfo").append("\"").append(" : ").append("\"").append(quoteAsString(LOG_LINE_SIZE_EXCEEDED_MESSAGE+logMessageSize)).append("\"").append(" , ");

		if(!StringUtils.isAllBlank(statusCode))
			builder.append("\"").append("statusCode").append("\"").append(" : ").append("\"").append(statusCode).append("\"").append(" , ");
		
		if(!StringUtils.isAllBlank(statusMessage))
			builder.append("\"").append("statusMessage").append("\"").append(" : ").append("\"").append(quoteAsString(statusMessage)).append("\"").append(" , ");
		
		if(!StringUtils.isAllBlank(endpointUrl))
			builder.append("\"").append("endpointUrl").append("\"").append(" : ").append("\"").append(quoteAsString(endpointUrl)).append("\"").append(" , ");
		
		if(!headerAttribute.isEmpty()) {
			appendHeaderAttribute(builder);
		}	
		
		builder.append("\"").append("appName").append("\"").append(" : ").append("\"").append(appName).append("\"");
		
		builder.append("}");
		
		return builder.toString();
	}
	
	/**
	 * Instead of JsonUtils, StringBuilder is used to construct the Json.
	 */
	@Override
	public String toString() {
		StringBuilder builder=new  StringBuilder();
		builder.append("{");
		builder.append("\"").append("context").append("\"").append(" : ").append("\"").append(context).append("\"").append(" , ");
		builder.append("\"").append("logType").append("\"").append(" : ").append("\"").append(logType).append("\"").append(" , ");
		builder.append("\"").append("orderId").append("\"").append(" : ").append("\"").append(orderId).append("\"").append(" , ");
		builder.append("\"").append("clientId").append("\"").append(" : ").append("\"").append(clientId).append("\"").append(" , ");
		builder.append("\"").append("messageId").append("\"").append(" : ").append("\"").append(messageId).append("\"").append(" , ");
		builder.append("\"").append("correlationId").append("\"").append(" : ").append("\"").append(correlationId).append("\"").append(" , ");
		
		if(!StringUtils.isAllBlank(callerId))
			builder.append("\"").append("callerId").append("\"").append(" : ").append("\"").append(callerId).append("\"").append(" , ");
		
		if(!StringUtils.isAllBlank(transactionName))
			builder.append("\"").append("transactionName").append("\"").append(" : ").append("\"").append(transactionName).append("\"").append(" , ");
		
		if(!StringUtils.isAllBlank(operationType))
			builder.append("\"").append("operationType").append("\"").append(" : ").append("\"").append(operationType).append("\"").append(" , ");
		
		if(!StringUtils.isAllBlank(topicName))
			builder.append("\"").append("topicName").append("\"").append(" : ").append("\"").append(topicName).append("\"").append(" , ");
		
		if(!StringUtils.isAllBlank(pubsubMessage))
			builder.append("\"").append("pubsubMessage").append("\"").append(" : ").append("\"").append(quoteAsString(maskPubSubMessage(pubsubMessage))).append("\"").append(" , ");
		
		if(!StringUtils.isAllBlank(eventType))
			builder.append("\"").append("eventType").append("\"").append(" : ").append("\"").append(eventType).append("\"").append(" , ");
		
		if(!StringUtils.isAllBlank(eventMessage))
			builder.append("\"").append("eventMessage").append("\"").append(" : ").append("\"").append(quoteAsString(eventMessage)).append("\"").append(" , ");
		
		if(!StringUtils.isAllBlank(errorCode))
			builder.append("\"").append("errorCode").append("\"").append(" : ").append("\"").append(errorCode).append("\"").append(" , ");
		
		if(!StringUtils.isAllBlank(errorMessage))
			builder.append("\"").append("errorMessage").append("\"").append(" : ").append("\"").append(quoteAsString(errorMessage)).append("\"").append(" , ");
		
		if(!StringUtils.isAllBlank(stackTrace))
			builder.append("\"").append("stackTrace").append("\"").append(" : ").append("\"").append(quoteAsString(stackTrace)).append("\"").append(" , ");
		
		if(!StringUtils.isAllBlank(additionalInfo))
			builder.append("\"").append("additionalInfo").append("\"").append(" : ").append("\"").append(quoteAsString(additionalInfo)).append("\"").append(" , ");
		
		if(!StringUtils.isAllBlank(statusCode))
			builder.append("\"").append("statusCode").append("\"").append(" : ").append("\"").append(statusCode).append("\"").append(" , ");
		
		if(!StringUtils.isAllBlank(statusMessage))
			builder.append("\"").append("statusMessage").append("\"").append(" : ").append("\"").append(quoteAsString(statusMessage)).append("\"").append(" , ");
		
		if(!StringUtils.isAllBlank(endpointUrl))
			builder.append("\"").append("endpointUrl").append("\"").append(" : ").append("\"").append(quoteAsString(endpointUrl)).append("\"").append(" , ");
		
		if(!headerAttribute.isEmpty()) {
			appendHeaderAttribute(builder);
		}	
		
		if(!StringUtils.isAllBlank(requestBody))
			builder.append("\"").append("requestBody").append("\"").append(" : ").append("\"").append(quoteAsString(maskResquestBody(requestBody))).append("\"").append(" , ");
		
		if(!StringUtils.isAllBlank(responseBody))
			builder.append("\"").append("responseBody").append("\"").append(" : ").append("\"").append(quoteAsString(maskResponseBody(responseBody))).append("\"").append(" , ");
		
		builder.append("\"").append("appName").append("\"").append(" : ").append("\"").append(appName).append("\"");
		
		builder.append("}");
		
		return builder.toString();
	}
	
	/**
	 * Append header headerAttribute information in Json format 
	 * 
	 * @param builder {@link StringBuilder}
	 */
	private void appendHeaderAttribute(StringBuilder builder) {
		builder.append("\"headerAttribute\": { ");
		int index = 0;
		int size=headerAttribute.size();
		Iterator<Entry<String, String>> it = headerAttribute.entrySet().iterator();
	    while (it.hasNext()) {
	    	Entry<String, String> pair = it.next();
	        if (index == (size - 1)) {
	        	builder.append("\"").append(pair.getKey()).append("\"").append(" : ").append("\"").append(pair.getValue()).append("\"").append(" ");
			} else {
				builder.append("\"").append(pair.getKey()).append("\"").append(" : ").append("\"").append(pair.getValue()).append("\"").append(" , ");
			}
			index++;
	    }
		builder.append("}, ");
	}
	
	/**
	 * Masks requestBody payload
	 * <p>
	 * <b>If exception is caught while masking payload, then it is not printed. Payload may contain sensitive information.</b>
	 * 
	 * @param payload Input
	 * 
	 * @return masked payload if masking flag is enabled else returns original payload as is
	 */
	private String maskResquestBody(String payload) {
		String result=payload;
		if(maskingEnabled && !StringUtils.isAllBlank(payload)) {
			try {
				result=requestBodyDataMasker.maskData(payload);
			} catch(Exception e) {
				log.error("Exception occurred while masking Request Body data during LOGGING. It is unexpected! Validate payload. Exception is trapped. ", e);
			}
		} 
		return result;
	}
	
	/**
	 * Masks responseBody payload.
	 * <p>
	 * <b>If exception is caught while masking payload, then it is not printed. Payload may contain sensitive information.</b>
	 * 
	 * @param payload Input
	 * 
	 * @return masked payload if masking flag is enabled else returns original payload as is
	 */
	private String maskResponseBody(String payload) {
		String result=payload;
		if(maskingEnabled && !StringUtils.isAllBlank(payload)) {
			try {
				result=responseBodyDataMasker.maskData(payload);
			} catch(Exception e) {
				log.error("Exception occurred while masking Response Body data during LOGGING. It is unexpected! Validate payload. Exception is trapped. ", e);
			}
		} 
		return result;
	}
	
	/**
	 * Masks pubsubMessage payload
	 * <p>
	 * <b>If exception is caught while masking payload, then it is not printed. Payload may contain sensitive information.</b>
	 * 
	 * @param payload Input
	 * 
	 * @return masked payload if masking flag is enabled else returns original payload as is
	 */
	private String maskPubSubMessage(String payload) {
		String result=payload;
		if(maskingEnabled && !StringUtils.isAllBlank(payload)) {
			try {
				result=pubsubMessageDataMasker.maskData(payload);
			} catch(Exception e) {
				log.error("Exception occurred while masking PubSub Message data during LOGGING. It is unexpected! Validate payload. Exception is trapped. ", e);
			}
		} 
		return result;
	}
	
	/**
	 * This method helps in calculating the log message length and trim message if log message size goes beyond configured value.
	 * 
	 * @return message to be logged
	 */
	private String getLogMessage() {
		String logMessage=toString();
		int maxLogLineSizeInKb=GlobalApplicationBucket.getMaxLogLineSizeInKb();
		if(maxLogLineSizeInKb==0) {
			return logMessage;
		} else {
			int lengthInBytes=logMessage.getBytes().length;
			int maxLengthInBytes=maxLogLineSizeInKb*1024;
			if(lengthInBytes < maxLengthInBytes) {
				return logMessage;
			} else {
				return toStringWhenLogSizeExceeded(lengthInBytes);
			}
		}
	}
	
}


