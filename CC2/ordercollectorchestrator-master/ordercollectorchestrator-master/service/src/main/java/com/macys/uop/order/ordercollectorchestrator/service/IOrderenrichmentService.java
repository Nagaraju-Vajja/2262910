package com.macys.uop.order.ordercollectorchestrator.service;

import com.macys.uop.order.model.Order;
import org.springframework.http.HttpHeaders;

public interface IOrderenrichmentService {

	Order enrichOrder(Order order, HttpHeaders headers);
}
