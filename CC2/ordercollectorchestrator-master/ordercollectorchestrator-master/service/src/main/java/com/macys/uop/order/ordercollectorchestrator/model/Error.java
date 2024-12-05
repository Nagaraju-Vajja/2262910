package com.macys.uop.order.ordercollectorchestrator.model;

import com.fasterxml.jackson.annotation.*;
import com.macys.uop.common.omconfig.api.ErrorDetails;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({
    "code",
    "description",
    "errorDetailsList"
})
public class Error {

    @JsonProperty("code")
    private String code;
    @JsonProperty("description")
    private String description;
    @JsonProperty("errorDetailsList")
    private List<ErrorDetails> errorDetailsList = new ArrayList<ErrorDetails>();
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public Error withCode(String code) {
        this.code = code;
        return this;
    }
}
