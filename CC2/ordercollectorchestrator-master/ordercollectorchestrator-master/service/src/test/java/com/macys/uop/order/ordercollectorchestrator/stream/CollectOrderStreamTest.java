package com.macys.uop.order.ordercollectorchestrator.stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

import brave.Span;
import brave.Tracer;
import brave.Tracing;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.protobuf.ByteString;
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
import com.macys.uop.order.model.Order;
import com.macys.uop.order.ordercollectorchestrator.service.IOrdercollectorchestratorService;
import com.macys.uop.order.ordercollectorchestrator.service.impl.OrdercollectorchestratorServiceImpl;
import com.macys.uop.order.ordercollectorchestrator.utils.TestUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.gcp.pubsub.core.PubSubTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CollectOrderStream.class)
public class CollectOrderStreamTest implements TestContextUtil, ServiceContextUtil {

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
    protected JsonUtils jsonUtils;
    @Mock
    private Publisher mockPublisher;

    @MockBean
    private IOrdercollectorchestratorService ordercollectorchestratorService;

    @InjectMocks
    private CollectOrderStream stream;

    private PubsubMessage pubsubMessage;

    @Before
    public void beforeTest() {
        initContext();
        stream = Mockito.mock(CollectOrderStream.class, Mockito.CALLS_REAL_METHODS);
        ordercollectorchestratorService = Mockito.mock(OrdercollectorchestratorServiceImpl.class);
        ReflectionTestUtils.setField(stream, "subscriptionName", "subscriber");
        ReflectionTestUtils.setField(stream, "jsonUtils", new JsonUtilsImpl(new ObjectMapper()));
        ReflectionTestUtils.setField(stream, "ordercollectorchestratorService", ordercollectorchestratorService);
        ReflectionTestUtils.setField(stream, "messageValidator", messageValidator);
        ReflectionTestUtils.setField(stream, "applicationName", "ordercollectorchestrator");
        HttpHeaders headers=new HttpHeaders();
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

        Mockito.doReturn("testpayload").when((AbstractStreamSubscriber)stream).getPayload();
    }

    @After
    public void afterTest() {
        clearContext();
    }

    @Test
    public void testInvokeService() throws ExecutionException, InterruptedException {
        Mockito.when(messageValidator.validateMessage(any())).thenReturn(true);
        Mockito.when(jsonUtils.convertFromJson(any(),
                ArgumentMatchers.<Class<Order>>any())).thenReturn(TestUtils.getStreamReqest());
        Mockito.when(ordercollectorchestratorService.collectOrder(any())).thenReturn(new Status());
        stream.invokeService(PubsubMessage.newBuilder().build());
    }

    @Test
    public void testDuplicateMessage() {
        Mockito.when(messageValidator.validateMessage(any())).thenReturn(false);
        stream.invokeService(PubsubMessage.newBuilder().build());
    }

    @Test
    public void testInvokeServiceScenario1() {
    	HttpHeaders headers=new HttpHeaders();
        headers.add(Constant.MESSAGEID_HDR, "1");
        headers.add(Constant.ORDERID_HDR, "2");
        headers.add(Constant.CLIENTID_HDR, "3");

        Mockito.when(jsonUtils.convertFromJson(any(),
                ArgumentMatchers.<Class<Order>>any())).thenReturn(TestUtils.getStreamReqest());
        Mockito.when(ordercollectorchestratorService.collectOrder(any())).thenReturn(new Status());
        stream.invokeService(PubsubMessage.newBuilder().build());
    }

    @Test
    public void testProcessHeaders1() {
        Map<String, String> headers=new HashMap<>();
        headers.put(Constant.MESSAGEID_HDR, "1");
        headers.put(Constant.ORDERID_HDR, "2");
        headers.put(Constant.CLIENTID_HDR, "3");

        Map<String, String> map = stream.processHeaders(PubsubMessage.newBuilder()
            .setData(ByteString.copyFromUtf8("testMessage"))
            .setMessageId("1")
            .putAllAttributes(headers)
            .build());
        assertEquals("1", map.get(Constant.MESSAGEID_HDR));
        assertEquals("2", map.get(Constant.ORDERID_HDR));

    }

    @Test
    public void testProcessHeaders2() {
        Map<String, String> headers=new HashMap<>();
        headers.put(Constant.MESSAGEID_HDR, "1");
        headers.put(Constant.CLIENTID_HDR, "3");
        headers.put(Constant.CORRELATIONID_HDR, "4");

        Map<String, String> map = stream.processHeaders(PubsubMessage.newBuilder()
            .setData(ByteString.copyFromUtf8("testMessage"))
            .setMessageId("3453456345")
            .putAllAttributes(headers)
            .build());
        assertEquals("ordercollectorchestrator", map.get(Constant.CLIENTID_HDR));
        assertEquals("4", map.get(Constant.CORRELATIONID_HDR));

    }


    @Test
    public void testSubscriberName() {
        Assert.assertEquals("subscriber", stream.getTopicSubscription());
    }

}
