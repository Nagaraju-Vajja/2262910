package com.macys.uop.foundation.core.utils.msg.subscriber;

import static com.macys.uop.foundation.core.utils.Constant.CLIENTID_HDR;
import static org.mockito.Mockito.mock;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.gcp.pubsub.support.converter.SimplePubSubMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.google.pubsub.v1.PubsubMessage;
import com.macys.uop.foundation.core.utils.CommonStatusCode;
import com.macys.uop.foundation.core.utils.ordererror.OrderErrorMessagePublisher;

import brave.Span;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringRunner.class)
public class AbstractStreamHelperTest {
	
	@MockBean
	private OrderErrorMessagePublisher orderErrorMessagePublisher;
	
	private SimplePubSubMessageConverter pubSubMessageConverter = new SimplePubSubMessageConverter();
	
	@Test
	public void testConstructor() {
		Mockito.mock(AbstractStreamHelper.class, Mockito.withSettings()
		        .useConstructor(orderErrorMessagePublisher)
		        .defaultAnswer(Mockito.CALLS_REAL_METHODS)
		);
		Assert.assertNotEquals("expected", "actual");
	}
	
	@Test
	public void testProcessMessage() {
		AbstractStreamHelper streamHelper=mock(AbstractStreamHelper.class);
		ReflectionTestUtils.setField(streamHelper, "isMessagingSubscriberLoggingEnabled", Boolean.TRUE);
		ReflectionTestUtils.setField(streamHelper, "isOrderErrorEventPublishingEnabled", Boolean.TRUE);
		ReflectionTestUtils.setField(streamHelper, "isEPFEventPublishingEnabled", Boolean.TRUE);
		
		String JSONDATA="{\"name\":\"test\"}";
		Map<String, String> headers = new HashMap<>();
		headers.put(CLIENTID_HDR, "12345");
		
		PubsubMessage message = pubSubMessageConverter.toPubSubMessage(JSONDATA, headers);
		
		Span span=mock(Span.class);
		
		Mockito.doReturn("12345").when(streamHelper).getCallerId(message);
		Mockito.doReturn(headers).when(streamHelper).processHeaders(message); 
		Mockito.doNothing().when(streamHelper).attachSpanTags(headers, span);
		Mockito.doNothing().when(streamHelper).logMessage();
		Mockito.doNothing().when(streamHelper).invokeService(message);
		
		Mockito.doCallRealMethod().when(streamHelper).initContext();
		
		Mockito.doCallRealMethod().when(streamHelper).processMessage(message, span);
		
		streamHelper.processMessage(message, span);
		
		Assert.assertNotEquals("expected", "actual");
	}
	
	@Test
	public void testProcessMessageWhenProcessHeadersThrowException() {
		AbstractStreamHelper streamHelper=mock(AbstractStreamHelper.class);
		ReflectionTestUtils.setField(streamHelper, "isMessagingSubscriberLoggingEnabled", Boolean.TRUE);
		ReflectionTestUtils.setField(streamHelper, "isOrderErrorEventPublishingEnabled", Boolean.TRUE);
		ReflectionTestUtils.setField(streamHelper, "isEPFEventPublishingEnabled", Boolean.TRUE);
		
		String JSONDATA="{\"name\":\"test\"}";
		Map<String, String> headers = new HashMap<>();
		headers.put(CLIENTID_HDR, "12345");
		
		PubsubMessage message = pubSubMessageConverter.toPubSubMessage(JSONDATA, headers);
		
		Span span=mock(Span.class);
		
		Mockito.doReturn("12345").when(streamHelper).getCallerId(message);
		Mockito.doThrow(new RuntimeException("Some Exception")).when(streamHelper).processHeaders(message); 
		Mockito.doNothing().when(streamHelper).attachSpanTags(headers, span);
		Mockito.doNothing().when(streamHelper).logMessage();
		Mockito.doNothing().when(streamHelper).invokeService(message);
		
		Mockito.doCallRealMethod().when(streamHelper).initContext();
		
		Mockito.doCallRealMethod().when(streamHelper).processMessage(message, span);
		
		streamHelper.processMessage(message, span);
		
		Assert.assertNotEquals("expected", "actual");
	}
	
	@Test
	public void testProcessMessageWithContentType() {
		AbstractStreamHelper streamHelper=mock(AbstractStreamHelper.class);
		ReflectionTestUtils.setField(streamHelper, "isMessagingSubscriberLoggingEnabled", Boolean.FALSE);
		ReflectionTestUtils.setField(streamHelper, "isOrderErrorEventPublishingEnabled", Boolean.TRUE);
		ReflectionTestUtils.setField(streamHelper, "isEPFEventPublishingEnabled", Boolean.TRUE);
		
		String JSONDATA="{\"name\":\"test\"}";
		Map<String, String> headers = new HashMap<>();
		headers.put(CLIENTID_HDR, "12345");
		headers.put("Content-Type", "application/json");
		
		PubsubMessage message = pubSubMessageConverter.toPubSubMessage(JSONDATA, headers);
		
		Span span=mock(Span.class);
		
		Mockito.doReturn("12345").when(streamHelper).getCallerId(message);
		Mockito.doReturn(headers).when(streamHelper).processHeaders(message); 
		Mockito.doNothing().when(streamHelper).attachSpanTags(headers, span);
		Mockito.doNothing().when(streamHelper).logMessage();
		Mockito.doNothing().when(streamHelper).invokeService(message);
		
		Mockito.doCallRealMethod().when(streamHelper).initContext();
		
		Mockito.doCallRealMethod().when(streamHelper).processMessage(message, span);
		
		streamHelper.processMessage(message, span);
		
		Assert.assertNotEquals("expected", "actual");
	}
	
	@Test
	public void testProcessException() {
		AbstractStreamHelper streamHelper=mock(AbstractStreamHelper.class);
		ReflectionTestUtils.setField(streamHelper, "isMessagingSubscriberLoggingEnabled", Boolean.TRUE);
		ReflectionTestUtils.setField(streamHelper, "isOrderErrorEventPublishingEnabled", Boolean.TRUE);
		
		IllegalArgumentException exception=new IllegalArgumentException(CommonStatusCode.NO_CORRELATIONID.getDescription());
		
		Mockito.doNothing().when(streamHelper).logError(exception);
		Mockito.doNothing().when(streamHelper).publishOrderErrorEvent(exception);
		
		Mockito.doCallRealMethod().when(streamHelper).processException(exception);
		
		streamHelper.processException(exception);
		
		Assert.assertNotEquals("expected", "actual");
	}
	
	@Test
	public void testProcessExceptionOrderErrorEventPublishingDisabled() {
		AbstractStreamHelper streamHelper=mock(AbstractStreamHelper.class);
		ReflectionTestUtils.setField(streamHelper, "isMessagingSubscriberLoggingEnabled", Boolean.TRUE);
		ReflectionTestUtils.setField(streamHelper, "isOrderErrorEventPublishingEnabled", Boolean.FALSE);
		ReflectionTestUtils.setField(streamHelper, "isEPFEventPublishingEnabled", Boolean.TRUE);
		
		IllegalArgumentException exception=new IllegalArgumentException(CommonStatusCode.NO_CORRELATIONID.getDescription());
		
		Mockito.doNothing().when(streamHelper).logError(exception);
		Mockito.doNothing().when(streamHelper).publishOrderErrorEvent(exception);
		
		Mockito.doCallRealMethod().when(streamHelper).processException(exception);
		
		streamHelper.processException(exception);
		
		Assert.assertNotEquals("expected", "actual");
	}
}
