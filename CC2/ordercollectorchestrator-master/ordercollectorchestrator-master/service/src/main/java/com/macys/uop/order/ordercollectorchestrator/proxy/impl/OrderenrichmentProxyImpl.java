package com.macys.uop.order.ordercollectorchestrator.proxy.impl;

import com.macys.uop.foundation.core.utils.exception.ExceptionHandlerUtil;
import com.macys.uop.foundation.core.utils.rest.client.RestClient;
import com.macys.uop.foundation.core.utils.rest.client.RestClientRequest;
import com.macys.uop.foundation.core.utils.rest.client.RestClientResponse;
import com.macys.uop.order.model.Order;
import com.macys.uop.order.ordercollectorchestrator.proxy.IOrderenrichmentProxy;
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
public class OrderenrichmentProxyImpl implements IOrderenrichmentProxy, ExceptionHandlerUtil {

    private final RestClient<Order, Order> restClient;

    @Value("${orderenrichment-service.orderenrichment.base_uri}")
    private String enrichBaseUri;

    /**
     * Rest call to OrderEnrichment Service to enrich order
     * @param orderRequest
     * @param headers
     * @return Order
     */
    public Order enrichOrder(final Order orderRequest, final MultiValueMap<String, String> headers) {
        getLogMessageBuilder(log).withContext(OrdercollectorchestratorConstants.ENRICHORDER_PROXY)
                .withAdditionalInfo("Started proxy call to orderenrichment service: " + orderRequest.getOrderId())
                .withOrderId(OrdercollectorchestratorConstants.ORDERID)
                .withMessageId(OrdercollectorchestratorConstants.MESSAGEID)
                .withCorrelationId(OrdercollectorchestratorConstants.CORRELATIONID)
                .withClientId(OrdercollectorchestratorConstants.CLIENTID).build()
                .logAsInfo();
      HttpHeaders httpHeaders = new HttpHeaders(headers);
      RestClientRequest<Order> request = RestClientRequest.<Order>builder()
          .withUrl(enrichBaseUri)
          .withBody(orderRequest)
          .withMethod(HttpMethod.POST.name())
          .withHeaders(httpHeaders)
          .build();
        getLogMessageBuilder(log).withContext(OrdercollectorchestratorConstants.ENRICHORDER_PROXY)
                .withAdditionalInfo("Finished proxy call to orderenrichment service: " + orderRequest.getOrderId())
                .withOrderId(OrdercollectorchestratorConstants.ORDERID)
                .withMessageId(OrdercollectorchestratorConstants.MESSAGEID)
                .withCorrelationId(OrdercollectorchestratorConstants.CORRELATIONID)
                .withClientId(OrdercollectorchestratorConstants.CLIENTID).build()
                .logAsInfo();
      RestClientResponse<Order> serviceResponse = restClient.execute(request, Order.class);
      return serviceResponse.getBody();
    }
}
