package com.macys.uop.order.ordercollectorchestrator.proxy.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.macys.uop.foundation.core.utils.Constant;
import com.macys.uop.foundation.core.utils.execution.ServiceContextUtil;
import com.macys.uop.foundation.core.utils.rest.client.RestClient;
import com.macys.uop.foundation.core.utils.rest.client.RestClientRequest;
import com.macys.uop.foundation.core.utils.rest.client.RestClientResponse;
import com.macys.uop.foundation.core.utils.test.TestContextUtil;
import com.macys.uop.order.model.Order;
import com.macys.uop.order.ordercollectorchestrator.proxy.IOrdercollectProxy;
import com.macys.uop.order.ordercollectorchestrator.utils.OrdercollectorchestratorUtil;
import com.macys.uop.order.ordercollectorchestrator.utils.ProfileEvaluatorUtil;
import com.macys.uop.order.ordercollectorchestrator.utils.TestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = OrdercollectProxyImpl.class)
public class OrderCollectProxyImplTest implements TestContextUtil, ServiceContextUtil,
        OrdercollectorchestratorUtil {

    ObjectMapper objectMapper;
    Order orderRequest;
    @MockBean
    private RestClient restClient;
    @MockBean
    private RestClientResponse restClientResponse;
    @MockBean
    private ProfileEvaluatorUtil profileEvaluatorUtil;
    @Autowired
    private IOrdercollectProxy ordercollectProxy;

    @Before
    public void beforeTest() {
        initContext();
        getServiceRequestContext().setApplicationName("ordercollectorchestrator");
        getServiceRequestContext().setHeaders(getDefaultHttpHeaders());
    }

    @After
    public void afterTest() {
        clearContext();
    }

    @Test
    public void testCollectOrder() {
        RestClientResponse<Order> restClientResponse = new RestClientResponse<Order>();
        restClientResponse.setBody(TestUtils.getOrderCollectResponse());
        restClientResponse.setStatus(HttpStatus.OK);

        Mockito.when(restClient.execute(ArgumentMatchers.any(RestClientRequest.class), ArgumentMatchers.<Class<Order>>any()))
                .thenReturn(restClientResponse);

        //Mockito.when(profileEvaluatorUtil.evaluateProfile(Mockito.anyString(), Mockito.any(), Mockito.anyString())).thenReturn(TestUtils.getOrderProfileResponse());

        Order orderResponse = ordercollectProxy.collectOrder(TestUtils.getOrderReqest(), getDefaultHttpHeaders());

        assertEquals("1Y57IRIEGPGPQ", orderResponse.getOrderId());
        assertEquals("firstName", orderResponse.getAssociateDetails().get(0).getFirstName());
    }

    @Test
    public void testColletOrder_OrderNull() {
        RestClientResponse<Order> restClientResponse = new RestClientResponse<Order>();
        restClientResponse.setBody(null);
        restClientResponse.setStatus(HttpStatus.BAD_REQUEST);

        Mockito.when(restClient.execute(ArgumentMatchers.any(RestClientRequest.class), ArgumentMatchers.<Class<Order>>any()))
                .thenThrow(new RuntimeException("TEST"));

        //Mockito.when(profileEvaluatorUtil.evaluateProfile(Mockito.anyString(), Mockito.any(), Mockito.anyString()))
            //.thenReturn(TestUtils.getOrderProfileResponse2());

        assertThrows(Exception.class, () -> ordercollectProxy.collectOrder(TestUtils.getStreamReqest(), getDefaultHttpHeaders()));
    }
}
