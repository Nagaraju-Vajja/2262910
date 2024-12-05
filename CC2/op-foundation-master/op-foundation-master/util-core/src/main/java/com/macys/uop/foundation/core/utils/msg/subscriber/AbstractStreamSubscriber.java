package com.macys.uop.foundation.core.utils.msg.subscriber;

import static com.macys.uop.foundation.core.utils.Constant.CONTEXT_SUBSCRIBER_ERROR_MESSAGE_LOGGING;
import static com.macys.uop.foundation.core.utils.Constant.CONTEXT_SUBSCRIBER_MESSAGE_CLIENT_ACKNOWLEDGE_LOGGING;
import static com.macys.uop.foundation.core.utils.Constant.CONTEXT_SUBSCRIBER_MESSAGE_CLIENT_NACKNOWLEDGE_LOGGING;
import static com.macys.uop.foundation.core.utils.Constant.MSG_PROCESSING_DURATION_TEXT_ORIGIN_PUBSUB;
import static com.macys.uop.foundation.core.utils.Constant.CONTEXT_MESSAGE_SUBSCRIBER_RESTART;

import java.time.Instant;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.cloud.gcp.pubsub.core.PubSubTemplate;
import org.springframework.util.Assert;

import com.google.api.core.ApiService.State;
import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.pubsub.v1.PubsubMessage;
import com.macys.uop.foundation.core.utils.CommonStatusCode;
import com.macys.uop.foundation.core.utils.logging.LogMessageBuilder;
import com.macys.uop.foundation.core.utils.logging.LogTypeEnum;
import com.macys.uop.foundation.core.utils.ordererror.OrderErrorMessagePublisher;

import brave.Span;
import brave.Tracer;
import brave.Tracer.SpanInScope;
import lombok.extern.slf4j.Slf4j;

/**
 * Abstract class to be extended by all Pub/Sub subscribers.
 *
 */
@Slf4j
public abstract class AbstractStreamSubscriber extends AbstractStreamHelper
		implements ApplicationRunner, com.google.cloud.pubsub.v1.MessageReceiver, SubscriberServiceApi {

	private final PubSubTemplate pubSubTemplate;
	private final Tracer tracer;
	protected Subscriber subscriber;
	
	private static final String SUBSCRIBER_NOT_NULL_ASSERT_MSG="'subscriber' must not be null! It is not initialized properly!";
	
	public AbstractStreamSubscriber(PubSubTemplate pubSubTemplate, Tracer tracer,  OrderErrorMessagePublisher orderErrorMessagePublisher) {
		super(orderErrorMessagePublisher);
		this.pubSubTemplate=pubSubTemplate;
		this.tracer=tracer;
	}
	
	/**
	 * Overridden method {@link MessageReceiver#receiveMessage(PubsubMessage, AckReplyConsumer)} called when a message is received by the subscriber. 
	 * <br>
	 * Based on the message acknowledgement type i,e 'immediate' or 'oncompletion' appropriate implementation method is called.
	 * <br>
	 * Default message acknowledgement type is 'immediate'
	 * 
	 * @param message {@link PubsubMessage}
	 * @param consumer {@link AckReplyConsumer}
	 */
	@Override
	public void receiveMessage(PubsubMessage message, AckReplyConsumer consumer) {
		messageProcessingThroughOnCompletionAcknowledgement(message, consumer);
	}
	
	
	
	/**
	 * Utility method that helps in message processing through client/oncompletion message acknowledgement.
	 * <br> 
	 * This method does the following :
	 * <p>
	 * <ul>
	 * <li>Create child span based on the Pub/Sub headers received or create root span</li>
	 * <li>Process the incoming message. Refer to {@link AbstractStreamHelper#processMessage(PubsubMessage, Span)} documentation </li>
	 * <li>Invoke subscriber implemented business logic through invokeService method implementation</li>
	 * <li>Handle exception. Refer to {@link AbstractStreamHelper#processException(Exception)} documentation</li>
	 * <li>Finishes the span</li>
	 * <li>Clears the context</li>
	 * <li>Acknowledge or NAcknowledge message based on the error code</li>
	 * <li>In case of exception {@link AbstractStreamHelper#processException(Exception)} method is called.</li>
	 * </ul>
	 * <br>
	 * Methods are made protected so that they can be customized if required by specific domain service 
	 * 
	 * @param message {@link PubsubMessage}
	 * @param consumer {@link AckReplyConsumer}
	 * 
	 */
	protected void messageProcessingThroughOnCompletionAcknowledgement(PubsubMessage message, AckReplyConsumer consumer) {
		boolean shouldNAckMessage=false;
		boolean isCircuitBreakerError=false;
		long waitDurationInOpenStatInMillis=0;
		Instant messageReceivedAt=Instant.now();
		Span span = constructSpan(tracer, "pubsub:subscriber:" + getTopicSubscription(), message.getAttributesMap());
		SpanInScope ws = tracer.withSpanInScope(span.start());
		try  {
			processMessage(message, span);
			publishEventLog4ExternalMsgInput();
			Instant invokeServiceInitiatedAt=Instant.now();
			invokeService(message);
			logMessageProcessingInvokeServiceDuration(invokeServiceInitiatedAt, Instant.now());
		} catch (Exception ex) {
			shouldNAckMessage=shouldProceedNAckMessage(ex);
			isCircuitBreakerError=isCircuitBreakerError(ex); // if UOP-GEN-E05039 then circuit breaker error
			if (isCircuitBreakerError) {
				waitDurationInOpenStatInMillis = getWaitDurationInOpenStateInMillis(ex);
			}
			processException(ex);
		} finally {
			try {
				if(shouldNAckMessage || isCircuitBreakerError) {
					consumer.nack();
					logMessageProcessingDuration(messageReceivedAt, Instant.now(), CONTEXT_SUBSCRIBER_MESSAGE_CLIENT_NACKNOWLEDGE_LOGGING);
					if (isCircuitBreakerError) {
						conditionallyStopAndStartSubscriber(waitDurationInOpenStatInMillis);
					}
				} else {
					consumer.ack();
					logMessageProcessingDuration(messageReceivedAt, Instant.now(), CONTEXT_SUBSCRIBER_MESSAGE_CLIENT_ACKNOWLEDGE_LOGGING);
				}
				ws.close();
				span.finish();
				clearContext();
			} catch (Exception e) {
				processException(e);
			}
		}
	}

	private synchronized void conditionallyStopAndStartSubscriber(long waitDurationInOpenStatInMillis) throws InterruptedException {
		if (subscriber.isRunning()) {
			synchronized(this) {
				stopSubscriberAsync();
			}
			Thread.sleep(waitDurationInOpenStatInMillis);
			if (!subscriber.isRunning()) {
				synchronized(this) {
					startSubscriberAsync();
				}
			}
		}

	}

	/**
	 * {@link ApplicationRunner#run()}
	 */
	@Override
	public void run(ApplicationArguments args) throws Exception {
		subscriber=pubSubTemplate.getSubscriberFactory().createSubscriber(getTopicSubscription(), this);
		subscriber.startAsync();
	}
	
	/**
	 * {@link SubscriberServiceApi#startSubscriberAsync()}
	 */
	@Override
	public void startSubscriberAsync() {
		try {
			subscriber = pubSubTemplate.getSubscriberFactory().createSubscriber(getTopicSubscription(), this);
			subscriber.startAsync();
			new LogMessageBuilder()
			.withClientId(getClientId())
			.withMessageId(getMessageId())
			.withOrderId(getOrderId())
			.withCorrelationId(getCorrelationId())
			.withAppName(getAppName())
			.withCallerId(getCallerId())
			.withContext(CONTEXT_MESSAGE_SUBSCRIBER_RESTART)
			.withLogType(LogTypeEnum.NTFY)
			.withTopicName(getTopicSubscription())
			.withHeaderAttribute(getMessageHeaders())
			.withAdditionalInfo(CommonStatusCode.MESSAGE_SUBSCRIBER_STARTED.getCode() + ":" + CommonStatusCode.MESSAGE_SUBSCRIBER_STARTED.getDescription())
			.withLogger(log)
			.buildDisableChecking()
			.logAsInfo();
		} catch (Exception ex) {
			new LogMessageBuilder()
			.withAppName(applicationName)
			.withLogType(LogTypeEnum.ERROR)
			.withContext(CONTEXT_SUBSCRIBER_ERROR_MESSAGE_LOGGING)
			.withTopicName(getTopicSubscription())
			.withErrorCode(CommonStatusCode.MESSAGE_SUBSCRIBER_STARTING_ERROR.getCode())
			.withErrorMessage(CommonStatusCode.MESSAGE_SUBSCRIBER_STARTING_ERROR.getDescription())
			.withStackTrace(ExceptionUtils.getStackTrace(ex))
			.withLogger(log)
			.buildDisableChecking()
			.logAsError();
		}
	}
	
	/**
	 * {@link SubscriberServiceApi#stopSubscriberAsync()}
	 */
	@Override
	public void stopSubscriberAsync() {
		Assert.notNull(subscriber, SUBSCRIBER_NOT_NULL_ASSERT_MSG);
		try {
			subscriber.stopAsync();
			new LogMessageBuilder()
			.withClientId(getClientId())
			.withMessageId(getMessageId())
			.withOrderId(getOrderId())
			.withCorrelationId(getCorrelationId())
			.withAppName(getAppName())
			.withCallerId(getCallerId())
			.withContext(CONTEXT_MESSAGE_SUBSCRIBER_RESTART)
			.withLogType(LogTypeEnum.NTFY)
			.withTopicName(getTopicSubscription())
			.withAdditionalInfo(CommonStatusCode.MESSAGE_SUBSCRIBER_STOPPED.getCode() + ":" + CommonStatusCode.MESSAGE_SUBSCRIBER_STOPPED.getDescription())
			.withLogger(log)
			.buildDisableChecking()
			.logAsInfo();
		} catch (Exception ex) {
			new LogMessageBuilder()
			.withAppName(applicationName)
			.withLogType(LogTypeEnum.ERROR)
			.withContext(CONTEXT_SUBSCRIBER_ERROR_MESSAGE_LOGGING)
			.withTopicName(getTopicSubscription())
			.withErrorCode(CommonStatusCode.MESSAGE_SUBSCRIBER_STARTING_ERROR.getCode())
			.withErrorMessage(CommonStatusCode.MESSAGE_SUBSCRIBER_STARTING_ERROR.getDescription())
			.withStackTrace(ExceptionUtils.getStackTrace(ex))
			.withLogger(log)
			.buildDisableChecking()
			.logAsError();
		}
	}
	
	/**
	 * {@link SubscriberServiceApi#isSubscriberRunning()}
	 */
	@Override
	public boolean isSubscriberRunning() {
		Assert.notNull(subscriber, SUBSCRIBER_NOT_NULL_ASSERT_MSG);
		return subscriber.isRunning();
	}
	
	/**
	 * {@link SubscriberServiceApi#getSubscriberState()}
	 */
	@Override
	public State getSubscriberState() {
		Assert.notNull(subscriber, SUBSCRIBER_NOT_NULL_ASSERT_MSG);
		return subscriber.state();
	}
	
	/**
	 * {@link SubscriberServiceApi#getSubscriptionName()}
	 */
	@Override
	public String getSubscriptionName() {
		Assert.notNull(subscriber, SUBSCRIBER_NOT_NULL_ASSERT_MSG);
		return subscriber.getSubscriptionNameString();
	}
	
	/**
	 * {@link SubscriberServiceApi#getSubscriptionId()}
	 */
	@Override
	public String getSubscriptionId() {
		return Integer.toHexString(this.getClass().getName().hashCode());
	}
}
