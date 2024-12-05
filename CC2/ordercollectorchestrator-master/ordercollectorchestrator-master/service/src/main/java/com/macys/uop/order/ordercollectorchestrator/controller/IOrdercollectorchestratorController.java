package com.macys.uop.order.ordercollectorchestrator.controller;

import com.macys.uop.foundation.core.utils.validation.CreateGroup;
import com.macys.uop.order.model.Order;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface IOrdercollectorchestratorController {

      /**
       * @param orderRequest
       */
      @PostMapping(value = "${controller.collectorder.path}",
          consumes = {MediaType.APPLICATION_JSON_VALUE},
          produces = {MediaType.APPLICATION_JSON_VALUE})
      @ApiOperation(value = "", tags = "", response = Object.class)
      @ApiResponses({
          @ApiResponse(code = 200, message = "OK"),
          @ApiResponse(code = 400, message = "Bad Request"),
          @ApiResponse(code = 500, message = "Internal Server Error")})
      ResponseEntity<Object> collectOrder(
          @ApiParam(value = "Order request", required = true)
          @Validated({CreateGroup.class})
          @RequestBody Order orderRequest);

      /**
       * @param fraudResponse
       */
      @PostMapping(value = "${controller.checkresponse.path}",
          consumes = {MediaType.APPLICATION_JSON_VALUE},
          produces = {MediaType.APPLICATION_JSON_VALUE})
      @ApiOperation(value = "", tags = "", response = Object.class)
      @ApiResponses({
          @ApiResponse(code = 200, message = "OK"),
          @ApiResponse(code = 400, message = "Bad Request"),
          @ApiResponse(code = 500, message = "Internal Server Error")})
      ResponseEntity<Object> checkResponse(
          @ApiParam(value = "Fraud response", required = true)
          @Validated({CreateGroup.class})
          @RequestBody Order fraudResponse);
}
