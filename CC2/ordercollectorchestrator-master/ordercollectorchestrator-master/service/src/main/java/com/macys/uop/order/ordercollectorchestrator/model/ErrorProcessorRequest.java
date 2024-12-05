package com.macys.uop.order.ordercollectorchestrator.model;

import com.macys.uop.foundation.core.utils.exception.Error;
import java.util.Map;

import com.macys.uop.order.ordercollectorchestrator.utils.OrderErrorCodes;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ErrorProcessorRequest {
    Error error;
    Throwable throwable;
    OrderErrorCodes errorCodes;
    int statusCode;
    String failedState;
    String payload;
    String referenceType;
    String referenceId;
    String errorType;
    String channelNameToPublsih;
    Map<String, String> headers;
}
