package com.macys.uop.foundation.core.utils.msg.publisher;

import static com.macys.uop.foundation.core.utils.Constant.CONTEXT_PUBLISHER_MESSAGE_LOGGING;
import static com.macys.uop.foundation.core.utils.Constant.MESSAGEID_HDR;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gcp.pubsub.core.PubSubTemplate;
import org.springframework.cloud.gcp.pubsub.support.converter.SimplePubSubMessageConverter;
import org.springframework.cloud.sleuth.annotation.NewSpan;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.concurrent.ListenableFuture;

import com.google.pubsub.v1.PubsubMessage;
import com.macys.uop.foundation.core.utils.logging.LogMessageBuilder;
import com.macys.uop.foundation.core.utils.logging.LogTypeEnum;
import com.macys.uop.foundation.core.utils.logging.LoggingUtil;
import com.macys.uop.foundation.core.utils.masking.ApplicationMaskingConfiguration;
import com.macys.uop.foundation.core.utils.masking.IDataMasker;
import com.macys.uop.foundation.core.utils.trace.TracerUtil;

import brave.SpanCustomizer;
import brave.Tracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * {@link MessagePublisherClient} Implementation.
 *
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class MessagePublisherClientImpl implements MessagePublisherClient, LoggingUtil, TracerUtil {
	private final Tracer tracer;
	private final SpanCustomizer spanCustomizer;
	private final PubSubTemplate pubSubTemplate;

	private SimplePubSubMessageConverter pubSubMessageConverter = new SimplePubSubMessageConverter();
	
	@Value("${messaging.publisher.logging.enabled:false}")
	private boolean isMessagingPublisherLoggingEnabled;

//	@Value("${messaging.publisher.partiallogging.enabled:false}")
//	private boolean isMessagingPublisherPartialLoggingEnabled;
		
	/**
	 * {@link MessagePublisherClient#sendMessage(String, Message)} Implementation.
	 */
	@Override
	@NewSpan("pubsub:publisher:common-client")
	public String sendMessage(String channelName, Message<String> message) throws InterruptedException, ExecutionException {
		return sendMessage(channelName, message, ApplicationMaskingConfiguration.getDefaultJsonMaskerInstance(), true);
	}
	
	/**
	 * {@link MessagePublisherClient#sendMessage(String, Message, boolean)} Implementation.
	 */
	@Override
	@NewSpan("pubsub:publisher:common-client")
	public String sendMessage(String channelName, Message<String> message, boolean loggingEnabled)
			throws InterruptedException, ExecutionException {
		return sendMessage(channelName, message, ApplicationMaskingConfiguration.getDefaultJsonMaskerInstance(), loggingEnabled);
	}
	
	/**
	 * {@link MessagePublisherClient#sendMessage(String, Message, IDataMasker)} Implementation.
	 */
	@Override
	@NewSpan("pubsub:publisher:common-client")
	public String sendMessage(String channelName, Message<String> message, IDataMasker dataMasker)
			throws InterruptedException, ExecutionException {
		return sendMessage(channelName, message, dataMasker, true);
	}	
	
	/**
	 * Common method to be called by all overridden methods
	 * 
	 * @param channelName Name of the channel where message will be sent.
	 * @param message Message payload.
	 * @param dataMasker {@link IDataMasker} instance
	 * @param loggingEnabled Method level logging flag
	 * 
	 * @return Generated Message Id
	 * 
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	@Override
	public String sendMessage(String channelName, Message<String> message, IDataMasker dataMasker, boolean loggingEnabled)
			throws InterruptedException, ExecutionException {
		String messageId = null;
		Assert.notNull(channelName, "Channel Name must not be null");
		Assert.notNull(message, "Message must not be null");
		Assert.notNull(message.getPayload(), "Message payload must not be null");
		
		String spanName = "pubsub:publisher:" + channelName;
		spanCustomizer.name(spanName);
		Map<String, String> headers = new HashMap<>();
		message.getHeaders().entrySet().stream().forEach(entry -> {
			String key = entry.getKey();
			String value = entry.getValue().toString();
			if(!key.toLowerCase().startsWith("goog")) {
				headers.put(key, value);
			}
		});
		injectHeaders(tracer.currentSpan().context(), headers);
		PubsubMessage pubSubMessage = pubSubMessageConverter.toPubSubMessage(message.getPayload(), headers);
		ListenableFuture<String> future = pubSubTemplate.publish(channelName, pubSubMessage);
		messageId = future.get();


		if(isMessagingPublisherLoggingEnabled && loggingEnabled) {
			LogMessageBuilder logMessageBuilder=getLogMessageBuilder(log)
			.withContext(CONTEXT_PUBLISHER_MESSAGE_LOGGING)
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
			.buildDisableChecking()
			.logAsInfo();
		} else {
			LogMessageBuilder logMessageBuilder=getLogMessageBuilder(log, messageId)
					.withContext(CONTEXT_PUBLISHER_MESSAGE_LOGGING)
					.withLogType(LogTypeEnum.NTFY)
					.withTopicName(channelName);

			if(dataMasker==null) {
				logMessageBuilder.withMaskingEnabled(false);
			} else {
				logMessageBuilder.withPubsubMessageDataMasker(dataMasker);
			}

			logMessageBuilder
					.buildDisableChecking()
					.logAsInfo();
		}
		
		return messageId;
	}
	
}
