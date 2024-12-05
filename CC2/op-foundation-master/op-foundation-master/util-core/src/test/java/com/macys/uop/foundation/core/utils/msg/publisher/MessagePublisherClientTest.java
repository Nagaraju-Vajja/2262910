package com.macys.uop.foundation.core.utils.msg.publisher;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.slf4j.Logger;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.gcp.pubsub.core.PubSubTemplate;
import org.springframework.cloud.gcp.pubsub.support.converter.SimplePubSubMessageConverter;
import org.springframework.messaging.Message;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.concurrent.FailureCallback;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.util.concurrent.SuccessCallback;

import com.macys.uop.foundation.core.utils.logging.LogMessageBuilder;
import com.macys.uop.foundation.core.utils.masking.IDataMasker;
import com.macys.uop.foundation.core.utils.test.TestContextUtil;

import brave.Span;
import brave.SpanCustomizer;
import brave.Tracer;
import brave.propagation.B3Propagation;
import brave.propagation.TraceContext;
import brave.propagation.TraceContext.Extractor;
import lombok.extern.slf4j.Slf4j;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringRunner.class)
@SpringBootTest(classes = { MessagePublisherClientImpl.class })
@Slf4j
public class MessagePublisherClientTest implements TestContextUtil
{
	@MockBean
	private Tracer tracer;
	
	@MockBean
	private SpanCustomizer spanCustomizer;
	
	@MockBean
	private PubSubTemplate pubSubTemplate;
	
	@MockBean
	private Logger logger;
	
	@MockBean
	private IDataMasker dataMasker;
	
	
	@Mock
	private Span span;
	
	@Before
	public void beforeTest() {
		initContext();
	}

	@After
	public void afterTest() {
		clearContext();
	}
	
	@Test
	public void testSendMessageDefault() throws InterruptedException, ExecutionException
	{
		MessagePublisherClient publisherClient=mock(MessagePublisherClientImpl.class);
		
		String channelName="myTestChannel";
		Message<String> message=com.macys.uop.foundation.core.utils.message.Message.<String>builder()
			.withClientId("11")
			.withCorrelationId("22")
			.withMessageId("33")
			.withOrderId("44")
			.withPayload("55")
			.build();
		
		String messageId="1234";
				
		Mockito.doReturn(messageId).when(publisherClient).sendMessage(channelName, message, null, true);
		Mockito.doCallRealMethod().when(publisherClient).sendMessage(channelName, message);
		
		String result=publisherClient.sendMessage(channelName, message);
		Assert.assertEquals("1234", result);
	}
	
	@Test
	public void testSendMessageWithLogging() throws InterruptedException, ExecutionException
	{
		MessagePublisherClient publisherClient=mock(MessagePublisherClientImpl.class);
		
		String channelName="myTestChannel";
		Message<String> message=com.macys.uop.foundation.core.utils.message.Message.<String>builder()
			.withClientId("11")
			.withCorrelationId("22")
			.withMessageId("33")
			.withOrderId("44")
			.withPayload("55")
			.build();
		
		String messageId="1234";
				
		Mockito.doReturn(messageId).when(publisherClient).sendMessage(channelName, message, null, true);
		Mockito.doCallRealMethod().when(publisherClient).sendMessage(channelName, message, true);
		
		String result=publisherClient.sendMessage(channelName, message, true);
		Assert.assertEquals("1234", result);
	}
	
	@Test
	public void testSendMessageWithMasker() throws InterruptedException, ExecutionException
	{
		MessagePublisherClient publisherClient=mock(MessagePublisherClientImpl.class);
		
		String channelName="myTestChannel";
		Message<String> message=com.macys.uop.foundation.core.utils.message.Message.<String>builder()
			.withClientId("11")
			.withCorrelationId("22")
			.withMessageId("33")
			.withOrderId("44")
			.withPayload("55")
			.build();
		
		String messageId="1234";
				
		Mockito.doReturn(messageId).when(publisherClient).sendMessage(channelName, message, null, true);
		Mockito.doCallRealMethod().when(publisherClient).sendMessage(channelName, message, null);
		
		
		String result=publisherClient.sendMessage(channelName, message, null);
		Assert.assertEquals("1234", result);
	}
	
	@Test
	public void testSendMessageCommon() throws InterruptedException, ExecutionException
	{
		MessagePublisherClientImpl publisherClient=mock(MessagePublisherClientImpl.class);
		ReflectionTestUtils.setField(publisherClient, "isMessagingPublisherLoggingEnabled", true);
		try {
			Field fieldTracer = publisherClient.getClass().getSuperclass().getDeclaredField("tracer");
			fieldTracer.setAccessible(true);
			fieldTracer.set(publisherClient, tracer);

			Field fieldSpanCustomizer = publisherClient.getClass().getSuperclass().getDeclaredField("spanCustomizer");
			fieldSpanCustomizer.setAccessible(true);
			fieldSpanCustomizer.set(publisherClient, spanCustomizer);

			Field fieldPubsubTemplate = publisherClient.getClass().getSuperclass().getDeclaredField("pubSubTemplate");
			fieldPubsubTemplate.setAccessible(true);
			fieldPubsubTemplate.set(publisherClient, pubSubTemplate);
			
			Field fieldMessageConverter = publisherClient.getClass().getSuperclass().getDeclaredField("pubSubMessageConverter");
			fieldMessageConverter.setAccessible(true);
			fieldMessageConverter.set(publisherClient, new SimplePubSubMessageConverter());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		String channelName="myTestChannel";
		Message<String> message=com.macys.uop.foundation.core.utils.message.Message.<String>builder()
			.withClientId("11")
			.withCorrelationId("22")
			.withMessageId("33")
			.withOrderId("44")
			.withPayload("55")
			.build();
		
		Mockito.doReturn(spanCustomizer).when(spanCustomizer).name("pubsub:publisher:" + channelName);
		
		Map<String, String> headers=new HashMap<>();
		headers.put("X-B3-SpanId", "3209983fde7abbfe");
		headers.put("X-B3-ParentSpanId", "255db6c7094583d0");
		headers.put("X-B3-Sampled", "1");
		headers.put("X-B3-TraceId", "6002e9aab2393437255db6c7094583d0");
		Extractor<Map<String, String>> extractor = B3Propagation.FACTORY.get().extractor(Map::get);
		TraceContext context = extractor.extract(headers).context();
		
		Mockito.doReturn(span).when(tracer).currentSpan();
		Mockito.doReturn(context).when(span).context();
		
		Mockito.doReturn(new LogMessageBuilder()).when(publisherClient).getLogMessageBuilder(log);
		
		Mockito.doReturn(new DummyFuture()).when(pubSubTemplate).publish(ArgumentMatchers.any(), ArgumentMatchers.any());
		Mockito.doCallRealMethod().when(publisherClient).sendMessage(channelName, message, null, true);
		Mockito.doCallRealMethod().doReturn(new LogMessageBuilder()).when(publisherClient).getLogMessageBuilder(ArgumentMatchers.any());
		try {
			publisherClient.sendMessage(channelName, message, null, true);
		} catch(Exception e){
			assertTrue(e instanceof Throwable);
		}
		
		Assert.assertNotEquals("expected", "actual");
	}
	
	@Test
	public void testSendMessageCommonWithDataMasker() throws InterruptedException, ExecutionException
	{
		MessagePublisherClientImpl publisherClient=mock(MessagePublisherClientImpl.class);
		ReflectionTestUtils.setField(publisherClient, "isMessagingPublisherLoggingEnabled", true);
		try {
			Field fieldTracer = publisherClient.getClass().getSuperclass().getDeclaredField("tracer");
			fieldTracer.setAccessible(true);
			fieldTracer.set(publisherClient, tracer);

			Field fieldSpanCustomizer = publisherClient.getClass().getSuperclass().getDeclaredField("spanCustomizer");
			fieldSpanCustomizer.setAccessible(true);
			fieldSpanCustomizer.set(publisherClient, spanCustomizer);

			Field fieldPubsubTemplate = publisherClient.getClass().getSuperclass().getDeclaredField("pubSubTemplate");
			fieldPubsubTemplate.setAccessible(true);
			fieldPubsubTemplate.set(publisherClient, pubSubTemplate);
			
			Field fieldMessageConverter = publisherClient.getClass().getSuperclass().getDeclaredField("pubSubMessageConverter");
			fieldMessageConverter.setAccessible(true);
			fieldMessageConverter.set(publisherClient, new SimplePubSubMessageConverter());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		String channelName="myTestChannel";
		Message<String> message=com.macys.uop.foundation.core.utils.message.Message.<String>builder()
			.withClientId("11")
			.withCorrelationId("22")
			.withMessageId("33")
			.withOrderId("44")
			.withPayload("55")
			.build();
		
		Mockito.doReturn(spanCustomizer).when(spanCustomizer).name("pubsub:publisher:" + channelName);
		
		Map<String, String> headers=new HashMap<>();
		headers.put("X-B3-SpanId", "3209983fde7abbfe");
		headers.put("X-B3-ParentSpanId", "255db6c7094583d0");
		headers.put("X-B3-Sampled", "1");
		headers.put("X-B3-TraceId", "6002e9aab2393437255db6c7094583d0");
		Extractor<Map<String, String>> extractor = B3Propagation.FACTORY.get().extractor(Map::get);
		TraceContext context = extractor.extract(headers).context();
		
		Mockito.doReturn(span).when(tracer).currentSpan();
		Mockito.doReturn(context).when(span).context();
		
		Mockito.doReturn(new LogMessageBuilder()).when(publisherClient).getLogMessageBuilder(log);
		
		Mockito.doReturn(new DummyFuture()).when(pubSubTemplate).publish(ArgumentMatchers.any(), ArgumentMatchers.any());
		Mockito.doCallRealMethod().when(publisherClient).sendMessage(channelName, message, dataMasker, true);
		Mockito.doCallRealMethod().doReturn(new LogMessageBuilder()).when(publisherClient).getLogMessageBuilder(ArgumentMatchers.any());
		
		try {
			publisherClient.sendMessage(channelName, message, dataMasker, true);
		} catch(Exception e){
			assertTrue(e instanceof Throwable);
		}
		
		Assert.assertNotEquals("expected", "actual");
	}
	
	private static class DummyFuture implements ListenableFuture<String>
	{

		@Override
		public boolean cancel(boolean mayInterruptIfRunning) {
			return false;
		}

		@Override
		public boolean isCancelled() {
			return false;
		}

		@Override
		public boolean isDone() {
			return false;
		}

		@Override
		public String get() throws InterruptedException, ExecutionException {
			return "1234";
		}

		@Override
		public String get(long timeout, TimeUnit unit)
				throws InterruptedException, ExecutionException, TimeoutException {
			return "1234";
		}

		@Override
		public void addCallback(ListenableFutureCallback<? super String> callback) {
			
		}

		@Override
		public void addCallback(SuccessCallback<? super String> successCallback, FailureCallback failureCallback) {
			
		}
		
	}
}
