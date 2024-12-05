package com.macys.uop.order.ordercollectorchestrator.proxy.impl;

import static org.junit.Assert.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.macys.uop.foundation.core.utils.Constant;
import com.macys.uop.foundation.core.utils.execution.ServiceContextUtil;
import com.macys.uop.foundation.core.utils.rest.client.RestClient;
import com.macys.uop.foundation.core.utils.rest.client.RestClientRequest;
import com.macys.uop.foundation.core.utils.rest.client.RestClientResponse;
import com.macys.uop.foundation.core.utils.test.TestContextUtil;
import com.macys.uop.order.model.Order;
import com.macys.uop.order.model.Profile;
import com.macys.uop.order.ordercollectorchestrator.proxy.IOrderenrichmentProxy;
import com.macys.uop.order.ordercollectorchestrator.utils.OrdercollectorchestratorUtil;
import com.macys.uop.order.ordercollectorchestrator.utils.TestUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import org.junit.After;
import org.junit.Assert;
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
import org.springframework.web.client.HttpServerErrorException;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = OrderenrichmentProxyImpl.class)
public class OrderenrichmentProxyImplTest implements TestContextUtil, ServiceContextUtil,
        OrdercollectorchestratorUtil {

    ObjectMapper objectMapper;
    Order orderRequest;
    @MockBean
    private RestClient restClient;
    @Autowired
    private IOrderenrichmentProxy orderenrichmentProxy;

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
        Order order = new Order();
        order.setOrderId("orderId");
        order.setProfileId("ZOLA");
        order.setProfileVersion("1");

        HashMap<String, String> rules = new HashMap<String, String>();
        rules.put("enrichment", "true");
        rules.put("payment", "true");

        Profile profile = new Profile();
        profile.setProfileId("profileId");
        profile.setProfileRules(rules);
        order.setProfiles(new ArrayList<>(Collections.singletonList(profile)));

        RestClientResponse<Order> restClientResponse = new RestClientResponse<Order>();
        restClientResponse.setBody(order);
        restClientResponse.setStatus(HttpStatus.OK);

        Mockito.when(restClient.execute(ArgumentMatchers.any(RestClientRequest.class), ArgumentMatchers.<Class<Order>>any()))
                .thenReturn(restClientResponse);

        Order orderResponse = orderenrichmentProxy.enrichOrder(order, getDefaultHttpHeaders());

        assertEquals("orderId", orderResponse.getOrderId());
        assertEquals("ZOLA", orderResponse.getProfileId());
    }

    @Test
    public void testColletOrder_OrderNull() {

        Order order = new Order();
        order.setProfileId("ZOLA");
        order.setProfileVersion("1");

        HashMap<String, String> rules = new HashMap<String, String>();
        rules.put("enrichment", "true");

        Profile profile = new Profile();
        profile.setProfileId("profileId");
        profile.setProfileRules(rules);
        order.setProfiles(new ArrayList<>(Collections.singletonList(profile)));

        RestClientResponse<Order> restClientResponse = new RestClientResponse<Order>();
        restClientResponse.setBody(null);
        restClientResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);

        Mockito.when(restClient.execute(ArgumentMatchers.any(RestClientRequest.class), ArgumentMatchers.<Class<Order>>any()))
                .thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

        Assert.assertThrows(Exception.class, () -> orderenrichmentProxy.enrichOrder(TestUtils.getOrderReqest(), getDefaultHttpHeaders()));
    }
}
