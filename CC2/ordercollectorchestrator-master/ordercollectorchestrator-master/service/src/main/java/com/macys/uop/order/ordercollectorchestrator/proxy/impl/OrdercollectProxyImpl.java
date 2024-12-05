package com.macys.uop.order.ordercollectorchestrator.proxy.impl;

import com.macys.uop.foundation.core.utils.exception.ExceptionHandlerUtil;
import com.macys.uop.foundation.core.utils.rest.client.RestClient;
import com.macys.uop.foundation.core.utils.rest.client.RestClientRequest;
import com.macys.uop.foundation.core.utils.rest.client.RestClientResponse;
import com.macys.uop.order.model.Order;
import com.macys.uop.order.ordercollectorchestrator.proxy.IOrdercollectProxy;
import com.macys.uop.order.ordercollectorchestrator.utils.OrdercollectorchestratorConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrdercollectProxyImpl implements IOrdercollectProxy, ExceptionHandlerUtil {

    private final RestClient<Order, Order> restClient;

    @Value("${ordercollect-service.collectorder.base_uri}")
    private String collectOrderBaseUri;

    /**
     * Rest call to OrderCollect Service to create order
     * @param order
     * @param headers
     * @return Order
     */
    public Order collectOrder(final Order order, final MultiValueMap<String, String> headers) {
        getLogMessageBuilder(log).withContext(OrdercollectorchestratorConstants.COLLECTORDER_PROXY)
                .withAdditionalInfo("Started proxy call to ordercollect service: " + order.getCustomerOrderId())
                .withOrderId(OrdercollectorchestratorConstants.ORDERID)
                .withMessageId(OrdercollectorchestratorConstants.MESSAGEID)
                .withCorrelationId(OrdercollectorchestratorConstants.CORRELATIONID)
                .withClientId(OrdercollectorchestratorConstants.CLIENTID)
                .build().logAsInfo();
      HttpHeaders httpHeaders = new HttpHeaders(headers);
      RestClientRequest<Order> request = RestClientRequest.<Order>builder()
          .withUrl(collectOrderBaseUri)
          .withBody(order)
          .withMethod(HttpMethod.POST.name())
          .withHeaders(httpHeaders)
          .build();

      RestClientResponse<Order> serviceResponse = restClient.execute(request, Order.class);
        getLogMessageBuilder(log).withContext(OrdercollectorchestratorConstants.COLLECTORDER_PROXY)
                .withAdditionalInfo("Finished proxy call to ordercollect service: " + order.getCustomerOrderId())
                .withOrderId(OrdercollectorchestratorConstants.ORDERID)
                .withMessageId(OrdercollectorchestratorConstants.MESSAGEID)
                .withCorrelationId(OrdercollectorchestratorConstants.CORRELATIONID)
                .withClientId(OrdercollectorchestratorConstants.CLIENTID).build()
                .logAsInfo();
      return serviceResponse.getBody();
    }
}
