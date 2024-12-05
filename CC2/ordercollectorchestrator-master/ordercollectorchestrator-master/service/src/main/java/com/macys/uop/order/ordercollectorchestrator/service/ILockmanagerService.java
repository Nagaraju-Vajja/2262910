package com.macys.uop.order.ordercollectorchestrator.service;

import org.springframework.http.HttpHeaders;

import com.macys.uop.order.model.Order;

public interface ILockmanagerService {
	void lockOrder(Order order, HttpHeaders httpHeaders);

	void lockOrderByPartnerFulfillmentId(Order order, HttpHeaders httpHeaders);

}
