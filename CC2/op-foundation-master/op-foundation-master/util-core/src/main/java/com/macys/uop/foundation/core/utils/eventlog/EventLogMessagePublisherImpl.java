package com.macys.uop.foundation.core.utils.eventlog;

import static com.macys.uop.foundation.core.utils.Constant.CONTEXT_EVENTLOG_PUBLISHER_MESSAGE_LOGGING;
import static com.macys.uop.foundation.core.utils.Constant.MESSAGEID_HDR;

import java.time.Duration;
import java.util.Map;
import java.util.function.Supplier;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import org.zalando.problem.ThrowableProblem;

import com.macys.uop.foundation.core.utils.CommonStatusCode;
import com.macys.uop.foundation.core.utils.exception.ProblemUtil;
import com.macys.uop.foundation.core.utils.logging.LogTypeEnum;
import com.macys.uop.foundation.core.utils.logging.LoggingUtil;
import com.macys.uop.foundation.core.utils.masking.IDataMasker;
import com.macys.uop.foundation.core.utils.masking.MaskingUtil;
import com.macys.uop.foundation.core.utils.msg.publisher.MessagePublisherClient;

import io.github.resilience4j.decorators.Decorators;
import io.github.resilience4j.decorators.Decorators.DecorateSupplier;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


/**
 * Default {@link EventLogMessagePublisher} implementation class to synchronously publish Event Log information 
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class EventLogMessagePublisherImpl implements EventLogMessagePublisher, LoggingUtil, ProblemUtil, MaskingUtil {

	@Value("${event.logging.enabled:false}")
	private boolean isEventMessageLoggingEnabled;
	private final MessagePublisherClient messagePublisher;
	// It is not required to customize this configuration across all deployed microservices. It will hardly change.
	private final RetryConfig retryConfig = RetryConfig.custom().maxAttempts(3).waitDuration(Duration.ofMillis(2000)).build();
	
	/**
	 * {@link EventLogMessagePublisher#publishEventLogMessage(String, EventLogMessage, Map)}
	 */
	@Override
	public String publishEventLogMessage(String channelName, EventLogMessage eventlogMessage, Map<String, String> headers) {
		
		String maskedPayload=eventlogMessage.getRequestPayload();
        String contentType=getContentType();
        IDataMasker dataMasker=getMaskerInstance(contentType);
		if(dataMasker!=null) {
			maskedPayload=dataMasker.maskData(eventlogMessage.getRequestPayload());
		}
		
		eventlogMessage.setRequestPayload(maskedPayload);
		
		String payloadForLogging=eventlogMessage.toString();
		
		org.springframework.messaging.Message<String> message=com.macys.uop.foundation.core.utils.message.Message
				.<String>builder()
				.withPayload(payloadForLogging)
				.withHeaders(headers)
				.build();

		
		DecorateSupplier<String> decoratedSupplier=getDecoratedPublishMessageSupplier(channelName, message, payloadForLogging, retryConfig);
		String messageId=decoratedSupplier.get();
		
		if(messageId!=null) {
			if(isEventMessageLoggingEnabled) {
				getLogMessageBuilder(log)
						.withLogType(LogTypeEnum.LOG)
						.withContext(CONTEXT_EVENTLOG_PUBLISHER_MESSAGE_LOGGING)
						.withTopicName(channelName)
						.withPubsubMessage(payloadForLogging)
						.withAdditionalInfo("After OrderError Message Publish messageId:" + messageId)
						.withMaskingEnabled(false)
						.buildDisableChecking()
						.logAsInfo();
			} else{
				getLogMessageBuilder(log)
						.withLogType(LogTypeEnum.LOG)
						.withContext(CONTEXT_EVENTLOG_PUBLISHER_MESSAGE_LOGGING)
						.withTopicName(channelName)
						.withAdditionalInfo("After OrderError Message Publish messageId:" + messageId)
						.withMaskingEnabled(false)
						.buildDisableChecking()
						.logAsInfo();
			}
		}
		
		return messageId;
	}
	
	/**
	 * Utility method that creates {@link DecorateSupplier} with retry and fallback configuration
	 * 
	 * @param finalChannelName topic name
	 * @param message to be published
	 * @param payloadForLogging
	 * @param retryConfiguration
	 * 
	 * @return {@link DecorateSupplier}
	 */
	public DecorateSupplier<String> getDecoratedPublishMessageSupplier(String finalChannelName, Message<String> message, String payloadForLogging, RetryConfig retryConfiguration) {
		Supplier<String> supplier = () -> publishMessage(finalChannelName, message, payloadForLogging);
		return Decorators.ofSupplier(supplier)
				  .withRetry(Retry.of("eventlog-publisher-rt", retryConfiguration))
				  .withFallback(  
				      throwable -> eventLogMessagePublisherRTFallback(finalChannelName, message, payloadForLogging, throwable)
				  );
	}

	/**
	 * Method which uses {@link MessagePublisherClient} to publish EventLog message synchronously.
	 * <p> 
	 * In case of any exception, this method constructs and throws {@link ThrowableProblem}.
	 * 
	 * @param channelName Topic name where message till be published
	 * @param message Message payload
	 * @param maskedPayload Masked payload to be used for logging in case of exception
	 * 
	 * @return messageId Generated Message Id
	 */
	public String publishMessage(String channelName, Message<String> message, String maskedPayload) {
		String messageId=null;
		try {
			messageId=messagePublisher.sendMessage(channelName, message, false);
		} catch (Exception e) {
			getLogMessageBuilder(log)
			.withLogType(LogTypeEnum.ERROR)
			.withContext(CONTEXT_EVENTLOG_PUBLISHER_MESSAGE_LOGGING)
			.withTopicName(channelName)
			.withPubsubMessage(maskedPayload)
			.withErrorCode(CommonStatusCode.MESSAGE_PUBSLISH_ERROR.getCode())
			.withErrorMessage(CommonStatusCode.MESSAGE_PUBSLISH_ERROR.getDescription())
			.withStackTrace(ExceptionUtils.getStackTrace(e))
			.withAdditionalInfo("Error Publishing EventLog Message : "+e.getMessage())
			.withMaskingEnabled(false)
			.buildDisableChecking()
			.logAsError();
			
			throw new RuntimeException(e);
		}
		
		return messageId;
	}
	
	/**
	 *  EventLog message publish retry fallback method which 
	 *  <ul>
	 *  <li>logs the failure as log level Error</li>
	 *  <li>creates and throws {@link Problem}</li>
	 *  </ul>
	 *  
	 *  @param channelName Topic name where message till be published
	 *  @param message Message payload
	 *  @param maskedPayload Masked payload to be used for logging in case of exception
	 *  @param e Throwable 
	 * 
	 *  @return {@link Problem}
	 */
	public String eventLogMessagePublisherRTFallback(String channelName, Message<String> message, String maskedPayload, Throwable e) {
		getLogMessageBuilder(log)
		.withLogType(LogTypeEnum.ERROR)
		.withContext(CONTEXT_EVENTLOG_PUBLISHER_MESSAGE_LOGGING+" inside fallback method eventLogMessagePublisherRTFallback")
		.withTopicName(channelName)
		.withPubsubMessage(maskedPayload)
		.withErrorCode(CommonStatusCode.MESSAGE_PUBSLISH_ERROR.getCode())
		.withErrorMessage(CommonStatusCode.MESSAGE_PUBSLISH_ERROR.getDescription())
		.withStackTrace(ExceptionUtils.getStackTrace(e))
		.withAdditionalInfo("All retry attempts failed publishing EventLog message. Critical Error. Contact Administrator!")
		.withMaskingEnabled(false)
		.buildDisableChecking()
		.logAsError();
		
		throw createProblem(Status.INTERNAL_SERVER_ERROR.getStatusCode(), CommonStatusCode.MESSAGE_PUBSLISH_ERROR.getCode(), 
				CommonStatusCode.MESSAGE_PUBSLISH_ERROR.getDescription(), e);
	}

}
