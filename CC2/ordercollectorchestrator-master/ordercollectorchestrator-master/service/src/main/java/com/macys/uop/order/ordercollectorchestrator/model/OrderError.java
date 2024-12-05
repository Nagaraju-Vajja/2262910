package com.macys.uop.order.ordercollectorchestrator.model;

import com.fasterxml.jackson.annotation.*;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({
    "applicationName",
    "responseCode",
    "responseMessage",
    "sellerOrderId",
    "orderId",
    "order",
    "error"
})
public class OrderError {

    @JsonProperty("applicationName")
    private String applicationName;
    @JsonProperty("responseCode")
    private String responseCode;
    @JsonProperty("responseMessage")
    private String responseMessage;
    @JsonProperty("sellerOrderId")
    private String sellerOrderId;
    @JsonProperty("partnerOrderId")
    private String partnerOrderId;
    @JsonProperty("orderId")
    private String orderId;
    @JsonProperty("error")
    private Error error;
    @JsonProperty("order")
    private Object order;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public com.macys.uop.order.ordercollectorchestrator.model.OrderError withApplicationName(String applicationName) {
        this.applicationName = applicationName;
        return this;
    }

    public com.macys.uop.order.ordercollectorchestrator.model.OrderError withResponseCode(String responseCode) {
        this.responseCode = responseCode;
        return this;
    }

    public com.macys.uop.order.ordercollectorchestrator.model.OrderError withResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
        return this;
    }
}
