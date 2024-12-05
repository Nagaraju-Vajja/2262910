package com.macys.uop.order.ordercollectorchestrator.service;

import com.macys.uop.order.model.Order;
import com.macys.uop.order.ordercollectorchestrator.model.Acknowledgement;
import com.macys.uop.order.ordercollectorchestrator.model.OrderError;

import java.util.concurrent.ExecutionException;

public interface ICollectorderResponsePublishService {
	Acknowledgement publish(Order order, OrderError orderError) throws InterruptedException, ExecutionException;
}
