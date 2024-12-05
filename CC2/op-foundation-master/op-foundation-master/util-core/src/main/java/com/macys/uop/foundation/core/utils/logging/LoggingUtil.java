package com.macys.uop.foundation.core.utils.logging;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;

import com.macys.uop.foundation.core.utils.execution.ServiceContextUtil;

/**
 * Purpose of this interface is to provide default overloaded helper methods for quickly creating LogMessageBuilder
 *
 */
public interface LoggingUtil extends ServiceContextUtil
{
	/**
	 * Initializes LogMessageBuilder by populating mandatory/common header information from Context.
	 * <br>
	 * This method is currently deprecated in favor of {@link LoggingUtil#getLogMessageBuilder(Logger)} 
	 * 
	 * @return LogMessageBuilder
	 */
	@Deprecated
	default LogMessageBuilder getLogMessageBuilder() {
		return new LogMessageBuilder()
		.withClientId(getClientId())
		.withMessageId(getMessageId())
		.withOrderId(getOrderId())
		.withCorrelationId(getCorrelationId())
		.withAppName(getAppName())
		.withCallerId(getCallerId());
	}
	
	/**
	 * Initializes LogMessageBuilder by populating mandatory/common header information from Context along with custom {@link Logger} instance
	 *  
	 * @param logger {@link Logger} instance to be used for logging 
	 * @return LogMessageBuilder
	 */
	default LogMessageBuilder getLogMessageBuilder(Logger logger) {
		return new LogMessageBuilder()
		.withClientId(getClientId())
		.withMessageId(getMessageId())
		.withOrderId(getOrderId())
		.withCorrelationId(getCorrelationId())
		.withAppName(getAppName())
		.withCallerId(getCallerId())
		.withLogger(logger);
	}

	default LogMessageBuilder getLogMessageBuilder(Logger logger, String messageId) {
		return new LogMessageBuilder()
				.withClientId(getClientId())
				.withMessageId(messageId)
				.withOrderId(getOrderId())
				.withCorrelationId(getCorrelationId())
				.withAppName(getAppName())
				.withCallerId(getCallerId())
				.withLogger(logger);
	}
	
	/**
	 * Initializes LogMessageBuilder by populating error and throwable information  
	 * <br>
	 * This method is currently deprecated in favor of {@link LoggingUtil#getErrorLogMessageBuilder(String, String, Throwable, Logger)} 
	 * 
	 * @param errorCode
	 * @param errorMessage
	 * @param throwable
	 * @return LogMessageBuilder
	 */
	@Deprecated
	default LogMessageBuilder getErrorLogMessageBuilder(String errorCode, String errorMessage, Throwable throwable) {
		return getErrorLogMessageBuilder(errorCode, errorMessage, throwable, null, null);
	}
	
	/**
	 *  Initializes LogMessageBuilder by populating error and throwable information along with custom {@link Logger} instance
	 *  
	 * @param errorCode
	 * @param errorMessage
	 * @param throwable
	 * @param logger custom {@link Logger} instance
	 * @return LogMessageBuilder
	 */
	default LogMessageBuilder getErrorLogMessageBuilder(String errorCode, String errorMessage, Throwable throwable, Logger logger) {
		return getErrorLogMessageBuilder(errorCode, errorMessage, throwable, null, logger);
	}
	
	/**
	 * Initializes LogMessageBuilder with default header value populated along with error, throwable and additional information.
	 * <br>
	 * This method is currently deprecated in favor of {@link LoggingUtil#getErrorLogMessageBuilder(String, String, Throwable, String, Logger)} 
	 * 
	 * @param errorCode
	 * @param errorMessage
	 * @param throwable
	 * @param additionalInfo
	 * @return LogMessageBuilder
	 */
	@Deprecated
	default LogMessageBuilder getErrorLogMessageBuilder(String errorCode, String errorMessage, Throwable throwable, String additionalInfo) {
		return getLogMessageBuilder()
				.withLogType(LogTypeEnum.ERROR)
				.withErrorCode(errorCode)
				.withErrorMessage(errorMessage)
				.withStackTrace(ExceptionUtils.getStackTrace(throwable))
				.withAdditionalInfo(additionalInfo);
	}
	
	/**
	 * Initializes LogMessageBuilder with default header value populated along with error, throwable, additional information along with custom {@link Logger} instance
	 * 
	 * @param errorCode
	 * @param errorMessage
	 * @param throwable
	 * @param additionalInfo
	 * @param logger custom {@link Logger} instance
	 * @return LogMessageBuilder
	 */
	default LogMessageBuilder getErrorLogMessageBuilder(String errorCode, String errorMessage, Throwable throwable, String additionalInfo, Logger logger) {
		return getLogMessageBuilder(logger)
				.withLogType(LogTypeEnum.ERROR)
				.withErrorCode(errorCode)
				.withErrorMessage(errorMessage)
				.withStackTrace(ExceptionUtils.getStackTrace(throwable))
				.withAdditionalInfo(additionalInfo);
	}
	
	/**
	 * Initializes LogMessageBuilder with default header value populated along with error and additional information
	 * <br>
	 * This method is currently deprecated in favor of {@link LoggingUtil#getErrorLogMessageBuilder(String, String, String, Logger)} 
	 * 
	 * @param errorCode
	 * @param errorMessage
	 * @param additionalInfo
	 * @return LogMessageBuilder
	 */
	@Deprecated
	default LogMessageBuilder getErrorLogMessageBuilder(String errorCode, String errorMessage, String additionalInfo) {
		return getLogMessageBuilder()
				.withLogType(LogTypeEnum.ERROR)
				.withErrorCode(errorCode)
				.withErrorMessage(errorMessage)
				.withAdditionalInfo(additionalInfo);
	}
	
	/**
	 * Initializes LogMessageBuilder with default header value populated along with error and additional information along with custom {@link Logger} instance
	 * 
	 * @param errorCode
	 * @param errorMessage
	 * @param additionalInfo
	 * @param logger custom {@link Logger} instance
	 * @return LogMessageBuilder
	 */
	default LogMessageBuilder getErrorLogMessageBuilder(String errorCode, String errorMessage, String additionalInfo, Logger logger) {
		return getLogMessageBuilder(logger)
				.withLogType(LogTypeEnum.ERROR)
				.withErrorCode(errorCode)
				.withErrorMessage(errorMessage)
				.withAdditionalInfo(additionalInfo);
	}
}
