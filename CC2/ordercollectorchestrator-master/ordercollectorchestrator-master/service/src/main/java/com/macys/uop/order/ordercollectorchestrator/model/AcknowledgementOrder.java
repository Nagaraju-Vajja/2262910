package com.macys.uop.order.ordercollectorchestrator.model;

import com.fasterxml.jackson.annotation.*;
import com.macys.uop.order.model.OrderLine;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({
    "orderId",
    "sellerOrderId",
        "partnerOrderId"
})
public class AcknowledgementOrder {

    @JsonProperty("orderId")
    private String orderId;
    @JsonProperty("sellerOrderId")
    private String sellerOrderId;
    @JsonProperty("partnerOrderId")
    private String partnerOrderId;
    @JsonProperty("orderChannelDivision")
    private String orderChannelDivision;
    @JsonProperty("orderLines")
    private List<OrderLine> orderLines = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public AcknowledgementOrder withOrderId(String orderId) {
        this.orderId = orderId;
        return this;
    }

    public AcknowledgementOrder withSellerOrderId(String sellerOrderId) {
        this.sellerOrderId = sellerOrderId;
        return this;
    }

    public AcknowledgementOrder withPartnerOrderId(String partnerOrderId) {
        this.partnerOrderId = partnerOrderId;
        return this;
    }

    public AcknowledgementOrder withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }
}
