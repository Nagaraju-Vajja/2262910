package com.macys.uop.foundation.core.utils.msg.subscriber;

import static com.macys.uop.foundation.core.utils.Constant.CLIENTID_HDR;
import static com.macys.uop.foundation.core.utils.Constant.CONTEXT_SUBSCRIBER_ERROR_MESSAGE_LOGGING;
import static com.macys.uop.foundation.core.utils.Constant.CORRELATIONID_HDR;
import static com.macys.uop.foundation.core.utils.Constant.MESSAGEID_HDR;
import static com.macys.uop.foundation.core.utils.Constant.ORDERID_HDR;
import static com.macys.uop.foundation.core.utils.Constant.SERVICE_REQUEST_CONTEXT;

import java.time.Instant;
import java.util.Map;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import com.google.pubsub.v1.PubsubMessage;
import com.macys.uop.foundation.core.utils.CommonStatusCode;
import com.macys.uop.foundation.core.utils.Constant;
import com.macys.uop.foundation.core.utils.execution.RequestOriginEnum;
import com.macys.uop.foundation.core.utils.execution.ServiceRequestContext;
import com.macys.uop.foundation.core.utils.logging.LogMessageBuilder;
import com.macys.uop.foundation.core.utils.logging.LogTypeEnum;
import com.macys.uop.foundation.core.utils.ordererror.OrderErrorMessagePublisher;
import com.macys.uop.foundation.core.utils.trace.TracerUtil;

import brave.Span;
import lombok.extern.slf4j.Slf4j;

/**
 * Abstract class to hold common methods that are required by {@link AbstractStreamSubscriber}
 *
 */
@Slf4j
public abstract class AbstractStreamHelper extends AbstractCommonStreamHelper implements TracerUtil {
	
	@Value("${messaging.subscriber.logging.enabled:false}")
	private boolean isMessagingSubscriberLoggingEnabled;
	
	@Value("${ordererror.event.publishing.enabled:true}")
	private boolean isOrderErrorEventPublishingEnabled;
	
	@Value("${epf.event.publishing.enabled:true}")
	private boolean isEPFEventPublishingEnabled;
	
	public AbstractStreamHelper(OrderErrorMessagePublisher orderErrorMessagePublisher) {
		super(orderErrorMessagePublisher);
	}
	
	/**
	 * Purpose of this method is to process the incoming Pub/Sub message.  
	 * <p>
	 * <ul>
	 * <li>Preserve incoming {@link Constant#CLIENTID_HDR} header</li>
	 * <li>Extract headers and set default values as required.</li>
	 * <li>Attach SpanTags</li>
	 * <li>Initialize context</li>
	 * <li>Populate context</li>
	 * <li>Log message</li>
	 * </ul>
	 * @param message Incoming Pub/Sub Message
	 * 
	 * @param span subscriber Span for which tags will be attached.
	 */
	protected void processMessage(PubsubMessage message, Span span) {
		
		// initialize context 
		initContext();

		//Incoming clientId header value is preserved for further reference if required
		String callerId=getCallerId(message);
		
		
		// Extract headers and set default values as required. 
		// If processHeaders(message) method overridden by domain throws exception then default processHeadersDefault(message) method is called.
		Map<String, String> headers=null;
		try {
			headers=processHeaders(message);
		} catch(Exception e) {
			headers=processHeadersDefault(message);
			new LogMessageBuilder()
					.withClientId(headers.get(CLIENTID_HDR))
					.withMessageId(headers.get(MESSAGEID_HDR))
					.withOrderId(headers.get(ORDERID_HDR))
					.withCorrelationId(headers.get(CORRELATIONID_HDR))
					.withAppName(applicationName)
					.withCallerId(callerId)
					.withLogType(LogTypeEnum.ERROR)
					.withContext(CONTEXT_SUBSCRIBER_ERROR_MESSAGE_LOGGING)
					.withTopicName(getTopicSubscription())
					.withErrorCode(CommonStatusCode.APPLICATION_ERROR.getCode())
					.withErrorMessage(CommonStatusCode.APPLICATION_ERROR.getDescription())
					.withStackTrace(ExceptionUtils.getStackTrace(e))
					.withAdditionalInfo("Critical exception! Exception caught from user overridden method processHeaders(message)! Foundation fallbacked to default!")
					.withLogger(log)
					.buildDisableChecking()
					.logAsError();
		}
		
		// Attach SpanTags
		attachSpanTags(headers, span);
		
		Object objContext=RequestContextHolder.getRequestAttributes().getAttribute(SERVICE_REQUEST_CONTEXT, RequestAttributes.SCOPE_REQUEST);
		ServiceRequestContext requestContext=(ServiceRequestContext) objContext;
		
		// populate context
		if(message.getData()!=null) {
			requestContext.setBody(message.getData().toStringUtf8());
		}	
		copyMessageHeadersToRequestContext(headers);
		requestContext.setMessageHeaders(headers);
		requestContext.setOrigin(RequestOriginEnum.MESSAGING);
		requestContext.setUrl("message:" + getTopicSubscription());
		requestContext.setApplicationName(applicationName);
		requestContext.setReceivedTime(Instant.now().toString());
		requestContext.setCallerId(callerId);
		
		// Check Content-Type header in Pub/Sub message. 
		// If available then use it. Else call getPayloadContentType() to get the value.
		String payloadContentType=null;
		String headerSentContentType=headers.get("Content-Type");
		if(headerSentContentType!=null) {
			payloadContentType=headerSentContentType;
		}
		if(payloadContentType==null) {
			payloadContentType=getPayloadContentType();
		}
		requestContext.setContentType(payloadContentType);
		
		// log message
		if(isMessagingSubscriberLoggingEnabled) {
			logMessage();
		} else {
			logMessagePartial();
		}
	}
	
	/**
	 * If exception is thrown during message processing i,e through execution of {@link AbstractStreamHelper#processMessage(PubsubMessage, Span)} method
	 * <br>
	 * the following things are done as part of exception handling.
	 * <p>
	 * <ul>
	 * <li>Log error message</li>
	 * <li>If OrderError Event Publishing is enabled then publish OrderError Event message</li>
	 * </ul>
	 * 
	 * @param e Throwable
	 */
	protected void processException(Exception e) {
		// log error
		logError(e);
		// publish order error message
		if(isOrderErrorEventPublishingEnabled) {
			publishOrderErrorEvent(e);
		}
		// publish EPF message
		if(isEPFEventPublishingEnabled) {
			publishEPFEvent(e);
		}
	}
	
	/**
	 * Method to be implemented by the message processing client class
	 * 
	 * @param message PubsubMessage
	 */
	protected abstract void invokeService(PubsubMessage message);
}
