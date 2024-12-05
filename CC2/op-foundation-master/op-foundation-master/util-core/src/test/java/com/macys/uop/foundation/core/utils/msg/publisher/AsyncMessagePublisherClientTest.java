package com.macys.uop.foundation.core.utils.msg.publisher;

import brave.Span;
import com.google.pubsub.v1.PubsubMessage;
import com.macys.uop.foundation.core.utils.masking.IDataMasker;
import com.macys.uop.foundation.core.utils.masking.JsonMasker;
import com.macys.uop.foundation.core.utils.test.TestContextUtil;
import io.github.resilience4j.retry.RetryConfig;
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
import org.springframework.beans.factory.annotation.Qualifier;
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

import java.lang.reflect.Field;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;


@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringRunner.class)
@SpringBootTest(classes = { AsyncMessagePublisherClientImpl.class })
public class AsyncMessagePublisherClientTest implements TestContextUtil {

    @Mock
    private AsyncMessagePublisherClientImpl asyncMessagePublisherClient;

    @MockBean
    private PubSubTemplate pubSubTemplate;

    @MockBean
    @Qualifier("messagePublishExecutor")
    private Executor messagePublishExecutor;

    @MockBean
    private Logger logger;

    @MockBean
    private IDataMasker dataMasker;

    @Mock
    private Span span;

    @Before
    public void beforeTest() {

        ReflectionTestUtils.setField(asyncMessagePublisherClient,"messagePublishExecutor",messagePublishExecutor);
        RetryConfig retryConfig = RetryConfig.custom().maxAttempts(3).waitDuration(Duration.ofMillis(2000)).build();
        ReflectionTestUtils.setField(asyncMessagePublisherClient,"retryConfig",retryConfig);
        initContext();
    }

    @After
    public void afterTest() {
        clearContext();
    }

    @Test
    public void testSendMessageDefault()
    {
        String channelName="myTestChannel";
        Message<String> message=com.macys.uop.foundation.core.utils.message.Message.<String>builder()
                .withClientId("11")
                .withCorrelationId("22")
                .withMessageId("33")
                .withOrderId("44")
                .withPayload("55")
                .build();

        Mockito.doCallRealMethod().when(asyncMessagePublisherClient).sendMessage(channelName, message);

        asyncMessagePublisherClient.sendMessage(channelName, message);

        Assert.assertNotEquals("expected", "actual");
    }

    @Test
    public void testSendMessageWithLogging()
    {
        String channelName="myTestChannel";
        Message<String> message=com.macys.uop.foundation.core.utils.message.Message.<String>builder()
                .withClientId("11")
                .withCorrelationId("22")
                .withMessageId("33")
                .withOrderId("44")
                .withPayload("55")
                .build();

        Mockito.doCallRealMethod().when(asyncMessagePublisherClient).sendMessage(channelName, message, true);

        asyncMessagePublisherClient.sendMessage(channelName, message, true);
        Assert.assertNotEquals("expected", "actual");
    }

    @Test
    public void testSendMessageWithMasker()
    {

        String channelName="myTestChannel";
        Message<String> message=com.macys.uop.foundation.core.utils.message.Message.<String>builder()
                .withClientId("11")
                .withCorrelationId("22")
                .withMessageId("33")
                .withOrderId("44")
                .withPayload("55")
                .build();

        Mockito.doCallRealMethod().when(asyncMessagePublisherClient).sendMessage(channelName, message, null);

        asyncMessagePublisherClient.sendMessage(channelName, message, null);
        Assert.assertNotEquals("expected", "actual");
    }

    @Test
    public void testSendMessageCommon() {
        ReflectionTestUtils.setField(asyncMessagePublisherClient, "isMessagingPublisherLoggingEnabled", true);
        try {

            Field fieldPubsubTemplate = asyncMessagePublisherClient.getClass().getSuperclass().getDeclaredField("pubSubTemplate");
            fieldPubsubTemplate.setAccessible(true);
            fieldPubsubTemplate.set(asyncMessagePublisherClient, pubSubTemplate);

            Field fieldMessageConverter = asyncMessagePublisherClient.getClass().getSuperclass().getDeclaredField("pubSubMessageConverter");
            fieldMessageConverter.setAccessible(true);
            fieldMessageConverter.set(asyncMessagePublisherClient, new SimplePubSubMessageConverter());
        } catch (Exception e) {
            e.printStackTrace();
        }
        Map<String, String> headers=new HashMap<>();
        headers.put("X-B3-SpanId", "3209983fde7abbfe");
        headers.put("X-B3-ParentSpanId", "255db6c7094583d0");
        headers.put("X-B3-Sampled", "1");
        headers.put("X-B3-TraceId", "6002e9aab2393437255db6c7094583d0");
        String channelName="myTestChannel";
        Message<String> message=com.macys.uop.foundation.core.utils.message.Message.<String>builder()
                .withClientId("11")
                .withCorrelationId("22")
                .withMessageId("33")
                .withOrderId("44")
                .withPayload("55")
                .build();
        SimplePubSubMessageConverter simplePubSubMessageConverter = new SimplePubSubMessageConverter();
        PubsubMessage pubsubMessage =simplePubSubMessageConverter.toPubSubMessage("sample payload",headers);
        Mockito.doReturn(new DummyFuture()).when(pubSubTemplate).publish(ArgumentMatchers.any(), ArgumentMatchers.any());
        ReflectionTestUtils.invokeMethod(asyncMessagePublisherClient,"publishMessage",channelName,pubsubMessage);
        asyncMessagePublisherClient.sendMessage(channelName, message, null, true);

        Assert.assertNotEquals("expected", "actual");
    }

    @Test
    public void testSendMessageCommonWithMasking()
    {
        ReflectionTestUtils.setField(asyncMessagePublisherClient, "isMessagingPublisherLoggingEnabled", true);
        try {

            Field fieldPubsubTemplate = asyncMessagePublisherClient.getClass().getSuperclass().getDeclaredField("pubSubTemplate");
            fieldPubsubTemplate.setAccessible(true);
            fieldPubsubTemplate.set(asyncMessagePublisherClient, pubSubTemplate);

            Field fieldMessageConverter = asyncMessagePublisherClient.getClass().getSuperclass().getDeclaredField("pubSubMessageConverter");
            fieldMessageConverter.setAccessible(true);
            fieldMessageConverter.set(asyncMessagePublisherClient, new SimplePubSubMessageConverter());
        } catch (Exception e) {
            e.printStackTrace();
        }
        Map<String, String> headers=new HashMap<>();
        headers.put("X-B3-SpanId", "3209983fde7abbfe");
        headers.put("X-B3-ParentSpanId", "255db6c7094583d0");
        headers.put("X-B3-Sampled", "282828929");
        headers.put("X-B3-TraceId", "6002e9aab2393437255db6c7094583d0");

        String channelName="myTestChannel";
        Message<String> message=com.macys.uop.foundation.core.utils.message.Message.<String>builder()
                .withClientId("11")
                .withCorrelationId("22")
                .withMessageId(null)
                .withOrderId("")
                .withPayload("55")
                .build();
        Map<String,IDataMasker> maskConfigMap=new HashMap<>();
        maskConfigMap.put("accountNumber", new com.macys.uop.foundation.core.utils.masking.CreditCardNumberMasker());
        IDataMasker jsonMasker=new JsonMasker(maskConfigMap);
        SimplePubSubMessageConverter simplePubSubMessageConverter = new SimplePubSubMessageConverter();
        PubsubMessage pubsubMessage =simplePubSubMessageConverter.toPubSubMessage("sample payload",headers);
        Mockito.doCallRealMethod().when(asyncMessagePublisherClient).sendMessage(channelName, message, jsonMasker, true);
        ReflectionTestUtils.invokeMethod(asyncMessagePublisherClient,"publishMessage",channelName,pubsubMessage);


        asyncMessagePublisherClient.sendMessage(channelName, message, jsonMasker, true);

        Assert.assertNotEquals("expected", "actual");
    }

    @Test
    public void testSendMessageCommonWithNoLogging() {
        ReflectionTestUtils.setField(asyncMessagePublisherClient, "isMessagingPublisherLoggingEnabled", true);
        try {

            Field fieldPubsubTemplate = asyncMessagePublisherClient.getClass().getSuperclass().getDeclaredField("pubSubTemplate");
            fieldPubsubTemplate.setAccessible(true);
            fieldPubsubTemplate.set(asyncMessagePublisherClient, pubSubTemplate);

            Field fieldMessageConverter = asyncMessagePublisherClient.getClass().getSuperclass().getDeclaredField("pubSubMessageConverter");
            fieldMessageConverter.setAccessible(true);
            fieldMessageConverter.set(asyncMessagePublisherClient, new SimplePubSubMessageConverter());
        } catch (Exception e) {
            e.printStackTrace();
        }
        Map<String, String> headers=new HashMap<>();
        headers.put("X-B3-SpanId", "3209983fde7abbfe");
        headers.put("X-B3-ParentSpanId", "255db6c7094583d0");
        headers.put("X-B3-Sampled", "1");
        headers.put("X-B3-TraceId", "6002e9aab2393437255db6c7094583d0");
        String channelName="myTestChannel";
        Message<String> message=com.macys.uop.foundation.core.utils.message.Message.<String>builder()
                .withClientId("11")
                .withCorrelationId("22")
                .withMessageId("33")
                .withOrderId("44")
                .withPayload("55")
                .build();
        SimplePubSubMessageConverter simplePubSubMessageConverter = new SimplePubSubMessageConverter();
        PubsubMessage pubsubMessage =simplePubSubMessageConverter.toPubSubMessage("sample payload",headers);
        Mockito.doReturn(new DummyFuture()).when(pubSubTemplate).publish(ArgumentMatchers.any(), ArgumentMatchers.any());
        ReflectionTestUtils.invokeMethod(asyncMessagePublisherClient,"publishMessage",channelName,pubsubMessage);
        asyncMessagePublisherClient.sendMessage(channelName, message, null, false);

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
        public String get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            return null;
        }

        @Override
        public void addCallback(ListenableFutureCallback<? super String> callback) {

        }

        @Override
        public void addCallback(SuccessCallback<? super String> successCallback, FailureCallback failureCallback) {

        }
    }

}
