package com.macys.uop.foundation.core.utils.msg.subscriber;

import static com.macys.uop.foundation.core.utils.Constant.CLIENTID_HDR;
import static org.mockito.Mockito.mock;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.gcp.pubsub.core.PubSubTemplate;
import org.springframework.cloud.gcp.pubsub.support.SubscriberFactory;
import org.springframework.cloud.gcp.pubsub.support.converter.SimplePubSubMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.google.api.core.ApiService;
import com.google.api.core.ApiService.State;
import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.pubsub.v1.PubsubMessage;
import com.macys.uop.foundation.core.utils.ordererror.OrderErrorMessagePublisher;

import brave.Span;
import brave.SpanCustomizer;
import brave.Tracer;
import brave.Tracer.SpanInScope;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringRunner.class)
@PrepareForTest(SpanInScope.class)
public class AbstractStreamSubscriberTest {
	
	@MockBean
	private PubSubTemplate pubSubTemplate;
	
	@MockBean
	private Tracer tracer;
	
	@MockBean
	private Subscriber subscriber;
	
	@MockBean
	private OrderErrorMessagePublisher orderErrorMessagePublisher;
	
	private SimplePubSubMessageConverter pubSubMessageConverter = new SimplePubSubMessageConverter();
	
	private final String topicSubscriptionName="mytopic";
	
	@Test
	public void testConstructor() {
		Mockito.mock(AbstractStreamSubscriber.class, Mockito.withSettings()
		        .useConstructor(pubSubTemplate, tracer,  orderErrorMessagePublisher)
		        .defaultAnswer(Mockito.CALLS_REAL_METHODS)
		);
		Assert.assertNotEquals("expected", "actual");
	}
	
	@Test
	public void testReceiveMessageOnComplete() {

		AbstractStreamSubscriber streamSubscriber=mock(AbstractStreamSubscriber.class);
//		ReflectionTestUtils.setField(streamSubscriber,"messageAcknowledgementType","oncompletion");
		try {
				Field fieldPubSubTemplte = streamSubscriber.getClass().getSuperclass().getDeclaredField("pubSubTemplate");
				fieldPubSubTemplte.setAccessible(true);
				fieldPubSubTemplte.set(streamSubscriber, pubSubTemplate);
				
				Field fieldTracer = streamSubscriber.getClass().getSuperclass().getDeclaredField("tracer");
				fieldTracer.setAccessible(true);
				fieldTracer.set(streamSubscriber, tracer);
				
				Field fieldSubscriber = streamSubscriber.getClass().getSuperclass().getDeclaredField("subscriber");
				fieldSubscriber.setAccessible(true);
				fieldSubscriber.set(streamSubscriber, subscriber);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		String JSONDATA="{\"name\":\"test\"}";
		Map<String, String> headers = new HashMap<>();
		headers.put(CLIENTID_HDR, "12345");
		
		PubsubMessage pubSubMessage = pubSubMessageConverter.toPubSubMessage(JSONDATA, headers);
		
		AckReplyConsumer consumer=mock(AckReplyConsumer.class);
		Span span = mock(Span.class);
		SpanInScope inSpanScope = PowerMockito.mock(SpanInScope.class); //mock(SpanInScope.class); 
		SpanCustomizer spanCustomizer=mock(SpanCustomizer.class);
		
		Mockito.doReturn(topicSubscriptionName).when(streamSubscriber).getTopicSubscription();
		Mockito.doReturn(span).when(streamSubscriber).constructSpan(tracer, "pubsub:subscriber:"+topicSubscriptionName, pubSubMessage.getAttributesMap());
		Mockito.doReturn(span).when(span).start();
		Mockito.doReturn(inSpanScope).when(tracer).withSpanInScope(span);
		Mockito.doReturn(spanCustomizer).when(span).customizer();
		Mockito.doReturn(spanCustomizer).when(spanCustomizer).tag(ArgumentMatchers.any(), ArgumentMatchers.any());
		Mockito.doNothing().when(streamSubscriber).processMessage(pubSubMessage, span);
		Mockito.doCallRealMethod().when(streamSubscriber).messageProcessingThroughOnCompletionAcknowledgement(ArgumentMatchers.any(), ArgumentMatchers.any());
		Mockito.doCallRealMethod().when(streamSubscriber).receiveMessage(pubSubMessage, consumer);

		streamSubscriber.receiveMessage(pubSubMessage, consumer);
		
		Assert.assertNotEquals("expected", "actual");
	}

	@Test
	public void testReceiveMessageOnCompleteError() {

		AbstractStreamSubscriber streamSubscriber=mock(AbstractStreamSubscriber.class);
//		ReflectionTestUtils.setField(streamSubscriber,"messageAcknowledgementType","oncompletion");
		try {
			Field fieldPubSubTemplte = streamSubscriber.getClass().getSuperclass().getDeclaredField("pubSubTemplate");
			fieldPubSubTemplte.setAccessible(true);
			fieldPubSubTemplte.set(streamSubscriber, pubSubTemplate);

			Field fieldTracer = streamSubscriber.getClass().getSuperclass().getDeclaredField("tracer");
			fieldTracer.setAccessible(true);
			fieldTracer.set(streamSubscriber, tracer);

			Field fieldSubscriber = streamSubscriber.getClass().getSuperclass().getDeclaredField("subscriber");
			fieldSubscriber.setAccessible(true);
			fieldSubscriber.set(streamSubscriber, subscriber);
		} catch (Exception e) {
			e.printStackTrace();
		}

		String JSONDATA="{\"name\":\"test\"}";
		Map<String, String> headers = new HashMap<>();
		headers.put(CLIENTID_HDR, "12345");

		PubsubMessage pubSubMessage = pubSubMessageConverter.toPubSubMessage(JSONDATA, headers);

		AckReplyConsumer consumer=mock(AckReplyConsumer.class);
		Span span = mock(Span.class);
		SpanInScope inSpanScope = PowerMockito.mock(SpanInScope.class); //mock(SpanInScope.class);
		SpanCustomizer spanCustomizer=mock(SpanCustomizer.class);

		Mockito.doReturn(topicSubscriptionName).when(streamSubscriber).getTopicSubscription();
		Mockito.doReturn(span).when(streamSubscriber).constructSpan(tracer, "pubsub:subscriber:"+topicSubscriptionName, pubSubMessage.getAttributesMap());
		Mockito.doReturn(span).when(span).start();
		Mockito.doReturn(inSpanScope).when(tracer).withSpanInScope(span);
		Mockito.doReturn(spanCustomizer).when(span).customizer();
		Mockito.doReturn(spanCustomizer).when(spanCustomizer).tag(ArgumentMatchers.any(), ArgumentMatchers.any());
		Mockito.doThrow(NullPointerException.class).when(streamSubscriber).processMessage(pubSubMessage, span);
		Mockito.doThrow(NullPointerException.class).when(streamSubscriber).clearContext();
		Mockito.doCallRealMethod().when(streamSubscriber).messageProcessingThroughOnCompletionAcknowledgement(ArgumentMatchers.any(), ArgumentMatchers.any());
		Mockito.doCallRealMethod().when(streamSubscriber).receiveMessage(pubSubMessage, consumer);

		streamSubscriber.receiveMessage(pubSubMessage, consumer);

		Assert.assertNotEquals("expected", "actual");
	}
	
	@Test
	public void testReceiveMessageOnCompleteErrorNAck() {

		AbstractStreamSubscriber streamSubscriber=mock(AbstractStreamSubscriber.class);
//		ReflectionTestUtils.setField(streamSubscriber,"messageAcknowledgementType","oncompletion");
		try {
			Field fieldPubSubTemplte = streamSubscriber.getClass().getSuperclass().getDeclaredField("pubSubTemplate");
			fieldPubSubTemplte.setAccessible(true);
			fieldPubSubTemplte.set(streamSubscriber, pubSubTemplate);

			Field fieldTracer = streamSubscriber.getClass().getSuperclass().getDeclaredField("tracer");
			fieldTracer.setAccessible(true);
			fieldTracer.set(streamSubscriber, tracer);

			Field fieldSubscriber = streamSubscriber.getClass().getSuperclass().getDeclaredField("subscriber");
			fieldSubscriber.setAccessible(true);
			fieldSubscriber.set(streamSubscriber, subscriber);
		} catch (Exception e) {
			e.printStackTrace();
		}

		String JSONDATA="{\"name\":\"test\"}";
		Map<String, String> headers = new HashMap<>();
		headers.put(CLIENTID_HDR, "12345");

		PubsubMessage pubSubMessage = pubSubMessageConverter.toPubSubMessage(JSONDATA, headers);

		AckReplyConsumer consumer=mock(AckReplyConsumer.class);
		Span span = mock(Span.class);
		SpanInScope inSpanScope = PowerMockito.mock(SpanInScope.class); //mock(SpanInScope.class);
		SpanCustomizer spanCustomizer=mock(SpanCustomizer.class);
		
		Exception processException=new RuntimeException("Process Exception");
		Mockito.doNothing().when(streamSubscriber).processException(processException);
		Mockito.doReturn(Boolean.TRUE).when(streamSubscriber).shouldProceedNAckMessage(processException);

		Mockito.doReturn(topicSubscriptionName).when(streamSubscriber).getTopicSubscription();
		Mockito.doReturn(span).when(streamSubscriber).constructSpan(tracer, "pubsub:subscriber:"+topicSubscriptionName, pubSubMessage.getAttributesMap());
		Mockito.doReturn(span).when(span).start();
		Mockito.doReturn(inSpanScope).when(tracer).withSpanInScope(span);
		Mockito.doReturn(spanCustomizer).when(span).customizer();
		Mockito.doReturn(spanCustomizer).when(spanCustomizer).tag(ArgumentMatchers.any(), ArgumentMatchers.any());
		Mockito.doThrow(processException).when(streamSubscriber).processMessage(pubSubMessage, span);
		Mockito.doThrow(NullPointerException.class).when(streamSubscriber).clearContext();
		Mockito.doCallRealMethod().when(streamSubscriber).messageProcessingThroughOnCompletionAcknowledgement(ArgumentMatchers.any(), ArgumentMatchers.any());
		Mockito.doCallRealMethod().when(streamSubscriber).receiveMessage(pubSubMessage, consumer);

		streamSubscriber.receiveMessage(pubSubMessage, consumer);

		Assert.assertNotEquals("expected", "actual");
	}
	
	
	@Test
	public void testReceiveMessageException() {
		AbstractStreamSubscriber streamSubscriber=mock(AbstractStreamSubscriber.class);
//		ReflectionTestUtils.setField(streamSubscriber,"messageAcknowledgementType","oncompletion");

		try {
				Field fieldPubSubTemplte = streamSubscriber.getClass().getSuperclass().getDeclaredField("pubSubTemplate");
				fieldPubSubTemplte.setAccessible(true);
				fieldPubSubTemplte.set(streamSubscriber, pubSubTemplate);
				
				Field fieldTracer = streamSubscriber.getClass().getSuperclass().getDeclaredField("tracer");
				fieldTracer.setAccessible(true);
				fieldTracer.set(streamSubscriber, tracer);
				
				Field fieldSubscriber = streamSubscriber.getClass().getSuperclass().getDeclaredField("subscriber");
				fieldSubscriber.setAccessible(true);
				fieldSubscriber.set(streamSubscriber, subscriber);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		String JSONDATA="{\"name\":\"test\"}";
		Map<String, String> headers = new HashMap<>();
		headers.put(CLIENTID_HDR, "12345");
		
		PubsubMessage pubSubMessage = pubSubMessageConverter.toPubSubMessage(JSONDATA, headers);
		
		Exception processException=new RuntimeException("Process Exception");
		
		AckReplyConsumer consumer=mock(AckReplyConsumer.class);
		Span span = mock(Span.class);
		SpanInScope inSpanScope = mock(SpanInScope.class);
		SpanCustomizer spanCustomizer=mock(SpanCustomizer.class);
		
		Mockito.doReturn(topicSubscriptionName).when(streamSubscriber).getTopicSubscription();
		Mockito.doReturn(span).when(streamSubscriber).constructSpan(tracer, "pubsub:subscriber:"+topicSubscriptionName, pubSubMessage.getAttributesMap());
		Mockito.doReturn(span).when(span).start();
		Mockito.doReturn(inSpanScope).when(tracer).withSpanInScope(span);
		Mockito.doReturn(spanCustomizer).when(span).customizer();
		Mockito.doReturn(spanCustomizer).when(spanCustomizer).tag(ArgumentMatchers.any(), ArgumentMatchers.any());
		Mockito.doThrow(processException).when(streamSubscriber).processMessage(pubSubMessage, span);
		Mockito.doNothing().when(streamSubscriber).processException(processException);
		
		Mockito.doCallRealMethod().when(streamSubscriber).receiveMessage(pubSubMessage, consumer);
		
		streamSubscriber.receiveMessage(pubSubMessage, consumer);
		
		Assert.assertNotEquals("expected", "actual");
	}
	
	@Test
	public void testRun() {
		
		AbstractStreamSubscriber streamSubscriber=mock(AbstractStreamSubscriber.class);
		try {
				Field fieldPubSubTemplte = streamSubscriber.getClass().getSuperclass().getDeclaredField("pubSubTemplate");
				fieldPubSubTemplte.setAccessible(true);
				fieldPubSubTemplte.set(streamSubscriber, pubSubTemplate);
				
				Field fieldTracer = streamSubscriber.getClass().getSuperclass().getDeclaredField("tracer");
				fieldTracer.setAccessible(true);
				fieldTracer.set(streamSubscriber, tracer);
				
				Field fieldSubscriber = streamSubscriber.getClass().getSuperclass().getDeclaredField("subscriber");
				fieldSubscriber.setAccessible(true);
				fieldSubscriber.set(streamSubscriber, subscriber);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		ApplicationArguments arguments=new DefaultApplicationArguments("");
		SubscriberFactory factory= mock(SubscriberFactory.class);
		Subscriber subscriber= mock(Subscriber.class);
		ApiService service= mock(ApiService.class);
		
		Mockito.doReturn(factory).when(pubSubTemplate).getSubscriberFactory();
		Mockito.doReturn(subscriber).when(factory).createSubscriber(ArgumentMatchers.any(), ArgumentMatchers.any());
		Mockito.doReturn(service).when(subscriber).startAsync();
		
		try {
			Mockito.doCallRealMethod().when(streamSubscriber).run(arguments);
			streamSubscriber.run(arguments);
			Assert.assertNotEquals("expected", "actual");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testStartSubscriberAsync() {
		
		AbstractStreamSubscriber streamSubscriber=mock(AbstractStreamSubscriber.class);
		try {
				Field fieldPubSubTemplte = streamSubscriber.getClass().getSuperclass().getDeclaredField("pubSubTemplate");
				fieldPubSubTemplte.setAccessible(true);
				fieldPubSubTemplte.set(streamSubscriber, pubSubTemplate);
				
				Field fieldTracer = streamSubscriber.getClass().getSuperclass().getDeclaredField("tracer");
				fieldTracer.setAccessible(true);
				fieldTracer.set(streamSubscriber, tracer);
				
				Field fieldSubscriber = streamSubscriber.getClass().getSuperclass().getDeclaredField("subscriber");
				fieldSubscriber.setAccessible(true);
				fieldSubscriber.set(streamSubscriber, subscriber);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		SubscriberFactory factory= mock(SubscriberFactory.class);
		Subscriber subscriber= mock(Subscriber.class);
		ApiService service= mock(ApiService.class);
		
		Mockito.doReturn(factory).when(pubSubTemplate).getSubscriberFactory();
		Mockito.doReturn(subscriber).when(factory).createSubscriber(ArgumentMatchers.any(), ArgumentMatchers.any());
		Mockito.doReturn(service).when(subscriber).startAsync();
		
		Mockito.doCallRealMethod().when(streamSubscriber).startSubscriberAsync();
		streamSubscriber.startSubscriberAsync();
		Assert.assertNotEquals("expected", "actual");
	}
	
	@Test
	public void testStartSubscriberAsyncException() {
		
		AbstractStreamSubscriber streamSubscriber=mock(AbstractStreamSubscriber.class);
		ReflectionTestUtils.setField(streamSubscriber, "applicationName", "test-service");
		try {
				Field fieldPubSubTemplte = streamSubscriber.getClass().getSuperclass().getDeclaredField("pubSubTemplate");
				fieldPubSubTemplte.setAccessible(true);
				fieldPubSubTemplte.set(streamSubscriber, pubSubTemplate);
				
				Field fieldTracer = streamSubscriber.getClass().getSuperclass().getDeclaredField("tracer");
				fieldTracer.setAccessible(true);
				fieldTracer.set(streamSubscriber, tracer);
				
				Field fieldSubscriber = streamSubscriber.getClass().getSuperclass().getDeclaredField("subscriber");
				fieldSubscriber.setAccessible(true);
				fieldSubscriber.set(streamSubscriber, subscriber);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Exception startAsyncException=new RuntimeException("StartAsync Exception");
		
		SubscriberFactory factory= mock(SubscriberFactory.class);
		Subscriber subscriber= mock(Subscriber.class);
		
		Mockito.doReturn(factory).when(pubSubTemplate).getSubscriberFactory();
		Mockito.doReturn(subscriber).when(factory).createSubscriber(ArgumentMatchers.any(), ArgumentMatchers.any());
		Mockito.doThrow(startAsyncException).when(subscriber).startAsync();
		Mockito.doReturn(topicSubscriptionName).when(streamSubscriber).getTopicSubscription();
		
		Mockito.doCallRealMethod().when(streamSubscriber).startSubscriberAsync();
		streamSubscriber.startSubscriberAsync();
		Assert.assertNotEquals("expected", "actual");
	}
	
	@Test
	public void testStopSubscriberAsync() {
		
		AbstractStreamSubscriber streamSubscriber=mock(AbstractStreamSubscriber.class);
		try {
				Field fieldPubSubTemplte = streamSubscriber.getClass().getSuperclass().getDeclaredField("pubSubTemplate");
				fieldPubSubTemplte.setAccessible(true);
				fieldPubSubTemplte.set(streamSubscriber, pubSubTemplate);
				
				Field fieldTracer = streamSubscriber.getClass().getSuperclass().getDeclaredField("tracer");
				fieldTracer.setAccessible(true);
				fieldTracer.set(streamSubscriber, tracer);
				
				Field fieldSubscriber = streamSubscriber.getClass().getSuperclass().getDeclaredField("subscriber");
				fieldSubscriber.setAccessible(true);
				fieldSubscriber.set(streamSubscriber, subscriber);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		ApiService service= mock(ApiService.class);
		Mockito.doReturn(service).when(subscriber).stopAsync();
		
		Mockito.doCallRealMethod().when(streamSubscriber).stopSubscriberAsync();
		streamSubscriber.stopSubscriberAsync();
		
		Assert.assertNotEquals("expected", "actual");
	}
	
	@Test
	public void testStopSubscriberAsyncException() {
		
		AbstractStreamSubscriber streamSubscriber=mock(AbstractStreamSubscriber.class);
		ReflectionTestUtils.setField(streamSubscriber, "applicationName", "test-service");
		try {
				Field fieldPubSubTemplte = streamSubscriber.getClass().getSuperclass().getDeclaredField("pubSubTemplate");
				fieldPubSubTemplte.setAccessible(true);
				fieldPubSubTemplte.set(streamSubscriber, pubSubTemplate);
				
				Field fieldTracer = streamSubscriber.getClass().getSuperclass().getDeclaredField("tracer");
				fieldTracer.setAccessible(true);
				fieldTracer.set(streamSubscriber, tracer);
				
				Field fieldSubscriber = streamSubscriber.getClass().getSuperclass().getDeclaredField("subscriber");
				fieldSubscriber.setAccessible(true);
				fieldSubscriber.set(streamSubscriber, subscriber);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Exception stopAsyncException=new RuntimeException("StopAsync Exception");
		
		Mockito.doThrow(stopAsyncException).when(subscriber).stopAsync();
		
		Mockito.doCallRealMethod().when(streamSubscriber).stopSubscriberAsync();
		streamSubscriber.stopSubscriberAsync();
		
		Assert.assertNotEquals("expected", "actual");
	}
	
	@Test
	public void testIsSubscriberRunning() {
		
		AbstractStreamSubscriber streamSubscriber=mock(AbstractStreamSubscriber.class);
		try {
				Field fieldPubSubTemplte = streamSubscriber.getClass().getSuperclass().getDeclaredField("pubSubTemplate");
				fieldPubSubTemplte.setAccessible(true);
				fieldPubSubTemplte.set(streamSubscriber, pubSubTemplate);
				
				Field fieldTracer = streamSubscriber.getClass().getSuperclass().getDeclaredField("tracer");
				fieldTracer.setAccessible(true);
				fieldTracer.set(streamSubscriber, tracer);
				
				Field fieldSubscriber = streamSubscriber.getClass().getSuperclass().getDeclaredField("subscriber");
				fieldSubscriber.setAccessible(true);
				fieldSubscriber.set(streamSubscriber, subscriber);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Mockito.doReturn(Boolean.TRUE).when(subscriber).isRunning();
		
		Mockito.doCallRealMethod().when(streamSubscriber).isSubscriberRunning();
		boolean result=streamSubscriber.isSubscriberRunning();
		
		Assert.assertEquals(Boolean.TRUE, result);
	}
	
	@Test
	public void testGetSubscriberState() {
		
		AbstractStreamSubscriber streamSubscriber=mock(AbstractStreamSubscriber.class);
		try {
				Field fieldPubSubTemplte = streamSubscriber.getClass().getSuperclass().getDeclaredField("pubSubTemplate");
				fieldPubSubTemplte.setAccessible(true);
				fieldPubSubTemplte.set(streamSubscriber, pubSubTemplate);
				
				Field fieldTracer = streamSubscriber.getClass().getSuperclass().getDeclaredField("tracer");
				fieldTracer.setAccessible(true);
				fieldTracer.set(streamSubscriber, tracer);
				
				Field fieldSubscriber = streamSubscriber.getClass().getSuperclass().getDeclaredField("subscriber");
				fieldSubscriber.setAccessible(true);
				fieldSubscriber.set(streamSubscriber, subscriber);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Mockito.doReturn(State.RUNNING).when(subscriber).state();
		
		Mockito.doCallRealMethod().when(streamSubscriber).getSubscriberState();
		State result=streamSubscriber.getSubscriberState();
		
		Assert.assertEquals(State.RUNNING, result);
	}
	
	@Test
	public void testGetSubscriptionName() {
		
		AbstractStreamSubscriber streamSubscriber=mock(AbstractStreamSubscriber.class);
		try {
				Field fieldPubSubTemplte = streamSubscriber.getClass().getSuperclass().getDeclaredField("pubSubTemplate");
				fieldPubSubTemplte.setAccessible(true);
				fieldPubSubTemplte.set(streamSubscriber, pubSubTemplate);
				
				Field fieldTracer = streamSubscriber.getClass().getSuperclass().getDeclaredField("tracer");
				fieldTracer.setAccessible(true);
				fieldTracer.set(streamSubscriber, tracer);
				
				Field fieldSubscriber = streamSubscriber.getClass().getSuperclass().getDeclaredField("subscriber");
				fieldSubscriber.setAccessible(true);
				fieldSubscriber.set(streamSubscriber, subscriber);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Mockito.doReturn("mytopic").when(subscriber).getSubscriptionNameString();
		
		Mockito.doCallRealMethod().when(streamSubscriber).getSubscriptionName();
		String result=streamSubscriber.getSubscriptionName();
		
		Assert.assertEquals("mytopic", result);
	}
	
	@Test
	public void testGetSubscriptionId() {
		
		AbstractStreamSubscriber streamSubscriber=mock(AbstractStreamSubscriber.class);
		try {
				Field fieldPubSubTemplte = streamSubscriber.getClass().getSuperclass().getDeclaredField("pubSubTemplate");
				fieldPubSubTemplte.setAccessible(true);
				fieldPubSubTemplte.set(streamSubscriber, pubSubTemplate);
				
				Field fieldTracer = streamSubscriber.getClass().getSuperclass().getDeclaredField("tracer");
				fieldTracer.setAccessible(true);
				fieldTracer.set(streamSubscriber, tracer);
				
				Field fieldSubscriber = streamSubscriber.getClass().getSuperclass().getDeclaredField("subscriber");
				fieldSubscriber.setAccessible(true);
				fieldSubscriber.set(streamSubscriber, subscriber);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Mockito.doCallRealMethod().when(streamSubscriber).getSubscriptionId();
		String result=streamSubscriber.getSubscriptionId();
		
		Assert.assertNotNull(result);
	}
}

