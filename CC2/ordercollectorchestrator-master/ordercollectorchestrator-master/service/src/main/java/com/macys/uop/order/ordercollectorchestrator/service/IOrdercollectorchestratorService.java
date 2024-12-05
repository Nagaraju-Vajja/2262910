package com.macys.uop.order.ordercollectorchestrator.service;

import com.macys.uop.order.model.Order;

public interface IOrdercollectorchestratorService {

	Object collectOrder(Order orderRequest);

	Object checkResponse(Order fraudResponse);
}
