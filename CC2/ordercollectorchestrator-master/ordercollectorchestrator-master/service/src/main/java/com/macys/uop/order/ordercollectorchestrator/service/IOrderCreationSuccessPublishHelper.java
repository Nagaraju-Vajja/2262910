package com.macys.uop.order.ordercollectorchestrator.service;

import java.util.Map;
import java.util.concurrent.ExecutionException;

import com.macys.uop.order.model.Order;

public interface IOrderCreationSuccessPublishHelper {
	
	String publish(Order order, Map<String, String> messageHeader) throws InterruptedException, ExecutionException;
}
