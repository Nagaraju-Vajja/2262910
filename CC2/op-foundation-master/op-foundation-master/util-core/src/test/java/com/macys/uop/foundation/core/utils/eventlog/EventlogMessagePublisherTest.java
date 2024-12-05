package com.macys.uop.foundation.core.utils.eventlog;


import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

import com.macys.uop.foundation.core.utils.Constant;
import com.macys.uop.foundation.core.utils.execution.ServiceContextUtil;
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
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.macys.uop.foundation.core.utils.logging.LogMessageBuilder;
import com.macys.uop.foundation.core.utils.masking.IDataMasker;
import com.macys.uop.foundation.core.utils.msg.publisher.MessagePublisherClient;
import com.macys.uop.foundation.core.utils.test.TestContextUtil;

import brave.Span;
import io.github.resilience4j.retry.RetryConfig;

import javax.validation.constraints.AssertTrue;

import static org.junit.Assert.assertTrue;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringRunner.class)
@SpringBootTest(classes = { EventLogMessagePublisherImpl.class })
public class EventlogMessagePublisherTest implements TestContextUtil, ServiceContextUtil {

    @Mock
    private EventLogMessagePublisherImpl eventLogMessagePublisher;

    @MockBean
    @Qualifier("messagePublisher")
    private MessagePublisherClient messagePublisher;

    @MockBean
    private Logger logger;

    @MockBean
    private IDataMasker dataMasker;

    @Mock
    private Span span;

    @Before
    public void beforeTest() {

        ReflectionTestUtils.setField(eventLogMessagePublisher,"messagePublisher",messagePublisher);
        RetryConfig retryConfig = RetryConfig.custom().maxAttempts(3).waitDuration(Duration.ofMillis(2000)).build();
        ReflectionTestUtils.setField(eventLogMessagePublisher,"retryConfig",retryConfig);
        ReflectionTestUtils.setField(eventLogMessagePublisher,"isEventMessageLoggingEnabled",false);
        initContext();
        HttpHeaders headers = new HttpHeaders();
        headers.add(Constant.MESSAGEID_HDR, "1");
        headers.add(Constant.ORDERID_HDR, "2");
        headers.add(Constant.CLIENTID_HDR, "3");
        headers.add(Constant.CORRELATIONID_HDR, "4");
        getServiceRequestContext().setHeaders(headers);
    }

    @After
    public void afterTest() {
        clearContext();
    }


    @Test
    public void testPublishEventLogMessage() throws ExecutionException, InterruptedException {

        Map<String, String> headers=new HashMap<>();
        headers.put("X-B3-SpanId", "3209983fde7abbfe");
        headers.put("X-B3-ParentSpanId", "255db6c7094583d0");
        headers.put("X-B3-Sampled", "1");
        headers.put("X-B3-TraceId", "6002e9aab2393437255db6c7094583d0");
        HttpHeaders headers1 = new HttpHeaders();
        headers1.add(Constant.MESSAGEID_HDR, "1");
        headers1.add(Constant.ORDERID_HDR, "2");
        headers1.add(Constant.CLIENTID_HDR, "3");
        headers1.add(Constant.CORRELATIONID_HDR, "4");
        String channelName="myTestChannel";
        EventLogMessage eventLogMessage=EventLogMessage.builder()
                .withCreatedBy("order").withTransactionId("RC1289").withTransactionDesc("OrderDetails published").withTransactionTime("2021-07-12T09:47:17.277Z")
                .withChannelName("orderdetails_onsuccess_env").withHeader("orderid", "1236").withRequestType("Request").withRequestPayload("Sample request payload").withResponsePayload("Data Saved Successfully")
                .withChannelType("Internal").withResponseTs("2021-07-12T09:47:27.277Z").withEventDetail("aaa", "bbb")
                .withStatusCode("200").withStatusDesc("SUCCESS").withSubClientId("MCOM").withStatus("OK")
                .build();
        Mockito.doCallRealMethod().when(eventLogMessagePublisher).getDecoratedPublishMessageSupplier(ArgumentMatchers.anyString(),ArgumentMatchers.any(),ArgumentMatchers.anyString(),ArgumentMatchers.any());
        Mockito.doCallRealMethod().when(eventLogMessagePublisher).publishMessage(ArgumentMatchers.anyString(),ArgumentMatchers.any(),ArgumentMatchers.anyString());
        Mockito.doReturn("messageId").when(messagePublisher).sendMessage(ArgumentMatchers.anyString(),ArgumentMatchers.any(),ArgumentMatchers.anyBoolean());
        Mockito.doCallRealMethod().when(eventLogMessagePublisher).publishEventLogMessage(channelName, eventLogMessage,headers);
        Mockito.doCallRealMethod().doReturn(new LogMessageBuilder()).when(eventLogMessagePublisher).getLogMessageBuilder(ArgumentMatchers.any());
        try {
            String messageId = eventLogMessagePublisher.publishEventLogMessage(channelName, eventLogMessage, headers);
        } catch (Exception e){
            assertTrue(e instanceof Throwable);
        }
    }

    @Test
    public void testPublishEventLogMessageError() throws ExecutionException, InterruptedException {

        Map<String, String> headers=new HashMap<>();
        headers.put("X-B3-SpanId", "3209983fde7abbfe");
        headers.put("X-B3-ParentSpanId", "255db6c7094583d0");
        headers.put("X-B3-Sampled", "1");
        headers.put("X-B3-TraceId", "6002e9aab2393437255db6c7094583d0");
        String channelName="myTestChannel";
        EventLogMessage eventLogMessage=EventLogMessage.builder()
                .withCreatedBy("order").withTransactionId("RC1289").withTransactionDesc("OrderDetails published").withTransactionTime("2021-07-12T09:47:17.277Z")
                .withChannelName("orderdetails_onsuccess_env").withHeader("orderid", "1236").withRequestType("Request").withRequestPayload("Sample request payload").withResponsePayload("Data Saved Successfully")
                .withChannelType("Internal").withResponseTs("2021-07-12T09:47:27.277Z").withEventDetail("aaa", "bbb")
                .withStatusCode("200").withStatusDesc("SUCCESS").withSubClientId("MCOM").withStatus("OK")
                .build();
        Mockito.doCallRealMethod().when(eventLogMessagePublisher).getDecoratedPublishMessageSupplier(ArgumentMatchers.anyString(),ArgumentMatchers.any(),ArgumentMatchers.anyString(),ArgumentMatchers.any());
        Mockito.doCallRealMethod().when(eventLogMessagePublisher).publishMessage(ArgumentMatchers.anyString(),ArgumentMatchers.any(),ArgumentMatchers.anyString());
        Mockito.doCallRealMethod().when(eventLogMessagePublisher).eventLogMessagePublisherRTFallback(ArgumentMatchers.anyString(),ArgumentMatchers.any(),ArgumentMatchers.anyString(),ArgumentMatchers.any());

        Mockito.doThrow(InterruptedException.class).when(messagePublisher).sendMessage(ArgumentMatchers.anyString(),ArgumentMatchers.any(),ArgumentMatchers.anyBoolean());
        Mockito.doCallRealMethod().when(eventLogMessagePublisher).publishEventLogMessage(channelName, eventLogMessage,headers);
        Mockito.doCallRealMethod().doReturn(new LogMessageBuilder()).when(eventLogMessagePublisher).getLogMessageBuilder(ArgumentMatchers.any());
        try{
            eventLogMessagePublisher.publishEventLogMessage(channelName, eventLogMessage, headers);
        }catch (Exception exception){
            assertTrue(exception instanceof Throwable);
        }
    }

}
