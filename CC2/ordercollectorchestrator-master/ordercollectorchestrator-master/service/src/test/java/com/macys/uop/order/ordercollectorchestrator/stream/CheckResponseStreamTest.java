package com.macys.uop.order.ordercollectorchestrator.stream;

import static org.mockito.ArgumentMatchers.any;

import brave.Span;
import brave.Tracer;
import brave.Tracing;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.pubsub.v1.PubsubMessage;
import com.macys.uop.common.commonlib.Status;
import com.macys.uop.foundation.core.utils.Constant;
import com.macys.uop.foundation.core.utils.execution.ServiceContextUtil;
import com.macys.uop.foundation.core.utils.json.JsonUtils;
import com.macys.uop.foundation.core.utils.json.JsonUtilsImpl;
import com.macys.uop.foundation.core.utils.msg.subscriber.AbstractStreamSubscriber;
import com.macys.uop.foundation.core.utils.ordererror.OrderErrorMessagePublisher;
import com.macys.uop.foundation.core.utils.test.TestContextUtil;
import com.macys.uop.foundation.core.utils.validation.MessageValidator;
import com.macys.uop.fraudprocessor.model.FraudServiceResponse;
import com.macys.uop.order.ordercollectorchestrator.service.IOrdercollectorchestratorService;
import com.macys.uop.order.ordercollectorchestrator.service.impl.OrdercollectorchestratorServiceImpl;
import com.macys.uop.order.ordercollectorchestrator.utils.TestUtils;
import java.util.concurrent.ExecutionException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.gcp.pubsub.core.PubSubTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CheckResponseStream.class)
public class CheckResponseStreamTest implements TestContextUtil, ServiceContextUtil {

    @MockBean
	protected JsonUtils jsonUtils;
    @MockBean
	private PubSubTemplate pubSubTemplate;
    @MockBean
	private Tracer tracer;
    @MockBean
	private OrderErrorMessagePublisher orderErrorMessagePublisher;
    @MockBean
	private Span span;
    @MockBean
	private Subscriber subscriber;
    @MockBean
	private MessageValidator messageValidator;
    @MockBean
	private AbstractStreamSubscriber streamSubscriber;
    @MockBean
	private IOrdercollectorchestratorService ordercollectorchestratorService;

    @InjectMocks private CheckResponseStream stream;

    @Before public void beforeTest() {
        initContext();
        stream = Mockito.mock(CheckResponseStream.class, Mockito.CALLS_REAL_METHODS);
        ordercollectorchestratorService = Mockito.mock(OrdercollectorchestratorServiceImpl.class);
        ReflectionTestUtils.setField(stream, "subscriptionName", "subscriber");
        ReflectionTestUtils.setField(stream, "jsonUtils", new JsonUtilsImpl(new ObjectMapper()));
        ReflectionTestUtils
            .setField(stream, "ordercollectorchestratorService", ordercollectorchestratorService);
        ReflectionTestUtils.setField(stream, "messageValidator", messageValidator);
        HttpHeaders headers = new HttpHeaders();
        headers.add(Constant.MESSAGEID_HDR, "1");
        headers.add(Constant.ORDERID_HDR, "2");
        headers.add(Constant.CLIENTID_HDR, "3");
        headers.add(Constant.CORRELATIONID_HDR, "4");
        getServiceRequestContext().setApplicationName("ordercollectorchestrator");
        getServiceRequestContext().setHeaders(headers);
        getServiceRequestContext().setBody("testpayload");
        Mockito.when(tracer.nextSpan()).thenReturn(span);
        Mockito.when(tracer.nextSpan().name("testpayload")).thenReturn(span);
        Mockito.when(span.start()).thenReturn(span);
        Tracing tracing = Tracing.newBuilder().build();
        Mockito.doReturn(tracing.tracer().withSpanInScope(span)).when(tracer).withSpanInScope(span);
        Mockito.doNothing().when(span).finish();

        Mockito.doReturn("testpayload").when((AbstractStreamSubscriber) stream).getPayload();

    }

    @After public void afterTest() {
        clearContext();
    }

    @Test public void testInvokeService() throws ExecutionException, InterruptedException {
        Mockito.when(messageValidator.validateMessage(any())).thenReturn(true);
        Mockito.when(
            jsonUtils.convertFromJson(any(), ArgumentMatchers.<Class<FraudServiceResponse>>any()))
            .thenReturn(TestUtils.getStreamReqest_Fraud());
        Mockito.when(ordercollectorchestratorService.checkResponse(any()))
            .thenReturn(new Status());
        stream.invokeService(PubsubMessage.newBuilder().build());
    }

    @Test public void testDuplicateMessage() {
        Mockito.when(messageValidator.validateMessage(any())).thenReturn(false);
        stream.invokeService(PubsubMessage.newBuilder().build());
    }

    @Test public void testInvokeServiceScenario1() throws ExecutionException, InterruptedException {
        HttpHeaders headers = new HttpHeaders();
        headers.add(Constant.MESSAGEID_HDR, "1");
        headers.add(Constant.ORDERID_HDR, "2");
        headers.add(Constant.CLIENTID_HDR, "3");

        Mockito.when(
            jsonUtils.convertFromJson(any(), ArgumentMatchers.<Class<FraudServiceResponse>>any()))
            .thenReturn(TestUtils.getStreamReqest_Fraud());
        Mockito.when(ordercollectorchestratorService.checkResponse(any()))
            .thenReturn(new Status());
        stream.invokeService(PubsubMessage.newBuilder().build());
    }

    @Test public void testSubscriberName() {
        Assert.assertEquals("subscriber", stream.getTopicSubscription());
    }

}
