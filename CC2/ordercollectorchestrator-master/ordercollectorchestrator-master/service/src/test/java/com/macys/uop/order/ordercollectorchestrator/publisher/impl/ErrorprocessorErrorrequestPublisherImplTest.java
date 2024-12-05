package com.macys.uop.order.ordercollectorchestrator.publisher.impl;

import static org.junit.Assert.assertEquals;

import com.macys.uop.foundation.core.utils.Constant;
import com.macys.uop.foundation.core.utils.execution.ServiceContextUtil;
import com.macys.uop.foundation.core.utils.msg.publisher.MessagePublisherClient;
import com.macys.uop.foundation.core.utils.test.TestContextUtil;
import com.macys.uop.order.model.Order;
import com.macys.uop.order.ordercollectorchestrator.publisher.IErrorprocessorErrorrequestPublisher;
import com.macys.uop.order.ordercollectorchestrator.utils.OrdercollectorchestratorUtil;
import java.util.concurrent.ExecutionException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.Message;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ErrorprocessorErrorrequestPublisherImpl.class)
public class ErrorprocessorErrorrequestPublisherImplTest
        implements TestContextUtil, ServiceContextUtil, OrdercollectorchestratorUtil {

    @Autowired
    private IErrorprocessorErrorrequestPublisher errorprocessorPublisher;

    @MockBean
    private MessagePublisherClient publisherClient;

    @Before
    public void beforeTest() {
        initContext();
        HttpHeaders headers=new HttpHeaders();
        headers.add(Constant.MESSAGEID_HDR, "1");
        headers.add(Constant.ORDERID_HDR, "2");
        headers.add(Constant.CLIENTID_HDR, "3");
        headers.add(Constant.CORRELATIONID_HDR, "4");
        getServiceRequestContext().setApplicationName("ordercollectorchestrator");
        getServiceRequestContext().setHeaders(headers);
    }

    @After
    public void afterTest() {
        clearContext();
    }


    @Test
    public void publishTest() throws ExecutionException, InterruptedException {
        Order order = new Order();
        order.setOrderId("123");
        Mockito.when(publisherClient.sendMessage(Mockito.anyString(), Mockito.any(Message.class))).thenReturn("OK");
        String result = errorprocessorPublisher.publishMessage("MessageString", getDefaultMessageHeaders());
        assertEquals("OK", result);
    }

}
