package com.macys.uop.foundation.core.utils.msg.publisher;

import static com.macys.uop.foundation.core.utils.Constant.CLIENTID_HDR;
import static com.macys.uop.foundation.core.utils.Constant.CONTEXT_ASYNC_PUBLISHER_MESSAGE_LOGGING;
import static com.macys.uop.foundation.core.utils.Constant.CORRELATIONID_HDR;
import static com.macys.uop.foundation.core.utils.Constant.MESSAGEID_HDR;
import static com.macys.uop.foundation.core.utils.Constant.ORDERID_HDR;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gcp.pubsub.core.PubSubTemplate;
import org.springframework.cloud.gcp.pubsub.support.converter.SimplePubSubMessageConverter;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.zalando.problem.Status;

import com.google.pubsub.v1.PubsubMessage;
import com.macys.uop.foundation.core.utils.CommonStatusCode;
import com.macys.uop.foundation.core.utils.exception.ProblemUtil;
import com.macys.uop.foundation.core.utils.logging.LogMessageBuilder;
import com.macys.uop.foundation.core.utils.logging.LogTypeEnum;
import com.macys.uop.foundation.core.utils.masking.ApplicationMaskingConfiguration;
import com.macys.uop.foundation.core.utils.masking.IDataMasker;
import com.macys.uop.foundation.core.utils.trace.TracerUtil;

import io.github.resilience4j.decorators.Decorators;
import io.github.resilience4j.decorators.Decorators.DecorateSupplier;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import lombok.extern.slf4j.Slf4j;

/**
 * Default implementation for {@link AsyncMessagePublisherClient}
 *
 */
@Component
@Slf4j
public class AsyncMessagePublisherClientImpl implements AsyncMessagePublisherClient, TracerUtil, ProblemUtil {
	
	private final PubSubTemplate pubSubTemplate;
	private final Executor messagePublishExecutor;

	private final SimplePubSubMessageConverter pubSubMessageConverter = new SimplePubSubMessageConverter();
	private final RetryConfig retryConfig = RetryConfig.custom().maxAttempts(3).waitDuration(Duration.ofMillis(2000)).build();

	@Value("${messaging.publisher.logging.enabled:true}")
	private boolean isMessagingPublisherLoggingEnabled;
	
	@Value("${spring.application.name:default}")
	private String applicationName;
	
	public AsyncMessagePublisherClientImpl(PubSubTemplate pubSubTemplate,
			@Qualifier("messagePublishExecutor") Executor messagePublishExecutor) {
		super();
		this.pubSubTemplate = pubSubTemplate;
		this.messagePublishExecutor=messagePublishExecutor;
	}
	
	/**
	 * {@link AsyncMessagePublisherClient#sendMessage(String, Message)}
	 */
	@Override
	public void sendMessage(String channelName, Message<String> message) {
		sendMessage(channelName, message, ApplicationMaskingConfiguration.getDefaultJsonMaskerInstance(), true);
	}

	/**
	 * {@link AsyncMessagePublisherClient#sendMessage(String, Message, boolean)}
	 */
	@Override
	public void sendMessage(String channelName, Message<String> message, boolean loggingEnabled) {
		sendMessage(channelName, message, ApplicationMaskingConfiguration.getDefaultJsonMaskerInstance(), loggingEnabled);
	}

	/**
	 * {@link AsyncMessagePublisherClient#sendMessage(String, Message, IDataMasker)}
	 */
	@Override
	public void sendMessage(String channelName, Message<String> message, IDataMasker dataMasker) {
		sendMessage(channelName, message, dataMasker, true);
	}

	/**
	 * {@link AsyncMessagePublisherClient#sendMessage(String, Message, IDataMasker, boolean)}
	 */
	@Override
	public void sendMessage(String channelName, Message<String> message, IDataMasker dataMasker,
			boolean loggingEnabled) {

		Assert.notNull(channelName, "Channel Name must not be null");
		Assert.notNull(message, "Message must not be null");
		Assert.notNull(message.getPayload(), "Message payload must not be null");
		
		Map<String, String> headers = new HashMap<>();
		message.getHeaders().entrySet().stream().forEach(entry -> {
			String key = entry.getKey();
			String value = entry.getValue() == null? null:entry.getValue().toString();
			if(!StringUtils.isAllBlank(value)) {
				headers.put(key, value);
			}	
		});
		PubsubMessage pubSubMessage = pubSubMessageConverter.toPubSubMessage(message.getPayload(), headers);
		if(isMessagingPublisherLoggingEnabled && loggingEnabled) {
			LogMessageBuilder logMessageBuilder=getLogMessageBuilder(pubSubMessage)
			.withContext(CONTEXT_ASYNC_PUBLISHER_MESSAGE_LOGGING)
			.withLogType(LogTypeEnum.NTFY)
			.withTopicName(channelName)
			.withHeaderAttribute(headers)
			.withPubsubMessage(message.getPayload());
			
			if(dataMasker==null) {
				logMessageBuilder.withMaskingEnabled(false);
			} else {
				logMessageBuilder.withPubsubMessageDataMasker(dataMasker);
			}
			
			logMessageBuilder
			.withLogger(log)
			.buildDisableChecking()
			.logAsInfo();
		}
		messagePublishExecutor.execute(() -> publishMessage(channelName, pubSubMessage)); 
	}
	
	/**
	 * Method that publishes message through {@link PubSubTemplate#publish(String, PubsubMessage)} . 
	 * <br>
	 * This method does a blocking IO. Message publishing is wrapped through Resilience4j Retry.
	 * 
	 * @param channelName Name of the channel on which message will be published.
	 * @param pubSubMessage PubSubMessage
	 * 
	 * @return published MessageId
	 */
	private String publishMessage(String channelName, PubsubMessage pubSubMessage) {
		Supplier<String> supplier = () -> {
			String publishedMessageId=null;
			try {
				publishedMessageId=pubSubTemplate.publish(channelName, pubSubMessage).get();
				getLogMessageBuilder(pubSubMessage)
				.withLogType(LogTypeEnum.LOG)
				.withContext(CONTEXT_ASYNC_PUBLISHER_MESSAGE_LOGGING)
				.withTopicName(channelName)
				.withAdditionalInfo("message published with messageId="+publishedMessageId)
				.withLogger(log)
				.buildDisableChecking()
				.logAsInfo();
			} catch(Exception e) {
				throw createProblem(Status.INTERNAL_SERVER_ERROR.getStatusCode(), CommonStatusCode.MESSAGE_PUBSLISH_ERROR.getCode(), 
						CommonStatusCode.MESSAGE_PUBSLISH_ERROR.getDescription(), e);
			}
			return publishedMessageId;
		};
	
		DecorateSupplier<String> decoratedSupplier=Decorators.ofSupplier(supplier)
			  .withRetry(Retry.of("async-publisher-rt", retryConfig))
			  .withFallback(  
			      throwable -> publishMessageRTFallback(channelName, pubSubMessage, throwable)
			  );

		return decoratedSupplier.get();
	}
	
	/**
	 * Fallback method that will be called after all retries exhausted. This method print this failure as critical error.  
	 * 
	 * @param channelName Name of the channel on which message will be published.
	 * @param pubSubMessage {@link PubSubMessage}
	 * @param e {@link Throwable}
	 * 
	 * @return null
	 */
	private String publishMessageRTFallback(String channelName, PubsubMessage pubSubMessage, Throwable e) {
		Map<String,String> messageHeaders=pubSubMessage.getAttributesMap();
		getLogMessageBuilder(pubSubMessage)
		.withLogType(LogTypeEnum.ERROR)
		.withContext(CONTEXT_ASYNC_PUBLISHER_MESSAGE_LOGGING+" inside fallback method publishMessageRTFallback")
		.withTopicName(channelName)
		.withHeaderAttribute(messageHeaders)
		.withErrorCode(CommonStatusCode.MESSAGE_PUBSLISH_ERROR.getCode())
		.withErrorMessage(CommonStatusCode.MESSAGE_PUBSLISH_ERROR.getDescription())
		.withStackTrace(ExceptionUtils.getStackTrace(e))
		.withAdditionalInfo("All retry attempts failed publishing message. Critical Error. Contact Administrator!")
		.withMaskingEnabled(false)
		.withLogger(log)
		.buildDisableChecking()
		.logAsError();
		return null;
	}
	
	/**
	 * Utility method that constructs {@link LogMessageBuilder} from {@link PubSubMessage}
	 * 
	 * @param pubSubMessage {@link PubSubMessage}
	 * 
	 * @return  {@link LogMessageBuilder}
	 */
	private LogMessageBuilder getLogMessageBuilder(PubsubMessage pubSubMessage) {
		Map<String,String> messageHeaders=pubSubMessage.getAttributesMap();
		
		String clientId=messageHeaders.get(CLIENTID_HDR)==null?messageHeaders.get(CLIENTID_HDR.toLowerCase()):messageHeaders.get(CLIENTID_HDR);
		String messageId=messageHeaders.get(MESSAGEID_HDR)==null?messageHeaders.get(MESSAGEID_HDR.toLowerCase()):messageHeaders.get(MESSAGEID_HDR);
		String orderId=messageHeaders.get(ORDERID_HDR)==null?messageHeaders.get(ORDERID_HDR.toLowerCase()):messageHeaders.get(ORDERID_HDR);
		String correlationId=messageHeaders.get(CORRELATIONID_HDR)==null?messageHeaders.get(CORRELATIONID_HDR.toLowerCase()):messageHeaders.get(CORRELATIONID_HDR);
		
		return new LogMessageBuilder()
				.withClientId(clientId)
				.withMessageId(messageId)
				.withOrderId(orderId)
				.withCorrelationId(correlationId)
				.withAppName(applicationName);
	}
}
