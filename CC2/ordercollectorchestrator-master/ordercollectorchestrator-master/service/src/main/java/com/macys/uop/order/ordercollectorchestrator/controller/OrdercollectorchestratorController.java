package com.macys.uop.order.ordercollectorchestrator.controller;

import com.macys.uop.foundation.core.utils.validation.CreateGroup;
import com.macys.uop.order.model.Order;
import com.macys.uop.order.ordercollectorchestrator.service.IOrdercollectorchestratorService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Api("ordercollectorchestrator")
@RequiredArgsConstructor
public class OrdercollectorchestratorController implements IOrdercollectorchestratorController {

    private final IOrdercollectorchestratorService ordercollectorchestratorService;

    /**
     * placeholder endpoint for collectorder request Stream
     * @param orderRequest
     * @return Status
     */
    @Override
    public ResponseEntity<Object> collectOrder(@ApiParam(value = "Order request", required = true)
            @Validated({CreateGroup.class}) @RequestBody  Order orderRequest) {
        Object data = ordercollectorchestratorService.collectOrder(orderRequest);
      if (data != null) {
        return new ResponseEntity<>(data, HttpStatus.OK);
      } else {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
      }
    }

    /**
     * Placeholder endpoint for FraudResponse Stream
     * @param fraudResponse
     * @return Status
     */
    @Override
    public ResponseEntity<Object> checkResponse(@ApiParam(value = "Fraud response", required = true)
            @Validated({CreateGroup.class}) @RequestBody Order fraudResponse) {
        Object data = ordercollectorchestratorService.checkResponse(fraudResponse);
      if (data != null) {
        return new ResponseEntity<>(data, HttpStatus.OK);
      } else {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
      }
    }
}
