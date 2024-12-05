package com.macys.uop.order.ordercollectorchestrator.controller;

import brave.SpanCustomizer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.macys.uop.common.commonlib.Status;
import com.macys.uop.foundation.core.utils.Constant;
import com.macys.uop.foundation.core.utils.json.JsonUtils;
import com.macys.uop.foundation.core.utils.ordererror.OrderErrorMessagePublisher;
import com.macys.uop.foundation.core.utils.test.TestContextUtil;
import com.macys.uop.foundation.core.utils.validation.IHeaderCheck;
import com.macys.uop.foundation.core.utils.web.interceptor.IRequestProcessor;
import com.macys.uop.foundation.core.utils.xml.XmlUtils;
import com.macys.uop.order.model.Order;
import com.macys.uop.order.ordercollectorchestrator.service.IOrdercollectorchestratorService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import com.macys.uop.foundation.core.utils.epf.EPFMessagePublisher;

@RunWith(SpringRunner.class)
@WebMvcTest(OrdercollectorchestratorController.class)
public class OrderCollectOrchestratorControllerTest implements TestContextUtil {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EPFMessagePublisher epfMessagePublisher;

    @MockBean
    private XmlUtils xmlUtils;

    @MockBean
    private IOrdercollectorchestratorService service;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private SpanCustomizer spanCustomizer;

    @MockBean
    private JsonUtils jsonUtils;

    @MockBean
    private IHeaderCheck headerCheck;

    @MockBean
    private IRequestProcessor iRequestProcessor;

    @MockBean
    private OrderErrorMessagePublisher orderErrorMessagePublisher;


    private Order defaultOrder() {
        Order order = new Order();
        order.setOrderId("OrderId");
        return order;
    }

    private HttpHeaders getDefaultHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(Constant.MESSAGEID_HDR, "1");
        headers.add(Constant.ORDERID_HDR, "2");
        headers.add(Constant.CLIENTID_HDR, "3");
        headers.add(Constant.CORRELATIONID_HDR, "4");

        return headers;
    }

    @Test
    public void testCollectOrderStatusOK() throws Exception {
        Mockito.when(service.collectOrder(Mockito.any(Order.class))).thenReturn(new Status());
        Mockito.when(iRequestProcessor.processHeaders(Mockito.any())).thenReturn(getDefaultHttpHeaders());
        String requestBody = mapper.writeValueAsString(defaultOrder());
        mockMvc.perform(
                MockMvcRequestBuilders.post("/v1/orders").headers(getDefaultHttpHeaders()).content(requestBody)
                        .accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testCollectOrderStatusNotFound() throws Exception {
        Mockito.when(service.collectOrder(Mockito.any(Order.class))).thenReturn(null);
        Mockito.when(iRequestProcessor.processHeaders(Mockito.any())).thenReturn(getDefaultHttpHeaders());
        String requestBody = mapper.writeValueAsString(defaultOrder());
        mockMvc.perform(
                MockMvcRequestBuilders.post("/v1/orders").headers(getDefaultHttpHeaders()).content(requestBody)
                        .accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.NOT_FOUND.value()))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testCheckResponseOK() throws Exception {
        Mockito.when(service.checkResponse(Mockito.any(Order.class))).thenReturn(new Status());
        Mockito.when(iRequestProcessor.processHeaders(Mockito.any())).thenReturn(getDefaultHttpHeaders());
        String requestBody = mapper.writeValueAsString(defaultOrder());
        mockMvc.perform(
                MockMvcRequestBuilders.post("/v1/lock/orders").headers(getDefaultHttpHeaders()).content(requestBody)
                    .accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
            .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testCheckResponseNotFound() throws Exception {
        Mockito.when(service.checkResponse(Mockito.any(Order.class))).thenReturn(null);
        Mockito.when(iRequestProcessor.processHeaders(Mockito.any())).thenReturn(getDefaultHttpHeaders());
        String requestBody = mapper.writeValueAsString(defaultOrder());
        mockMvc.perform(
                MockMvcRequestBuilders.post("/v1/lock/orders").headers(getDefaultHttpHeaders()).content(requestBody)
                    .accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(MockMvcResultMatchers.status().is(HttpStatus.NOT_FOUND.value()))
            .andDo(MockMvcResultHandlers.print());
    }
}
