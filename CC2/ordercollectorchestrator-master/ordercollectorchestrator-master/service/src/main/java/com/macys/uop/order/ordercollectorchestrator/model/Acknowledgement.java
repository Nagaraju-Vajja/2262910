package com.macys.uop.order.ordercollectorchestrator.model;

import com.fasterxml.jackson.annotation.*;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({
    "source",
    "serviceName",
    "responseCode",
    "responseMessage",
    "order"
})
public class Acknowledgement {

    @JsonProperty("source")
    private String source;
    @JsonProperty("serviceName")
    private String serviceName;
    @JsonProperty("responseCode")
    private String responseCode;
    @JsonProperty("responseMessage")
    private String responseMessage;
    @JsonProperty("order")
    private AcknowledgementOrder order;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public Acknowledgement withSource(String source) {
        this.source = source;
        return this;
    }

    public Acknowledgement withServiceName(String serviceName) {
        this.serviceName = serviceName;
        return this;
    }

    public Acknowledgement withResponseCode(String responseCode) {
        this.responseCode = responseCode;
        return this;
    }

    public Acknowledgement withResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
        return this;
    }

    public Acknowledgement withOrder(AcknowledgementOrder order) {
        this.order = order;
        return this;
    }

    public Acknowledgement withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }
}
