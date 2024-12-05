package com.macys.uop.order.ordercollectorchestrator.service.impl;

import com.macys.uop.order.model.Order;
import com.macys.uop.order.ordercollectorchestrator.proxy.IOrderenrichmentProxy;
import com.macys.uop.order.ordercollectorchestrator.service.IOrderenrichmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderenrichmentServiceImpl implements IOrderenrichmentService {

	private final IOrderenrichmentProxy orderenrichmentProxy;

	/**
	 * Service Call for Order Enrichment
	 *
	 * @param order
	 * @param headers
	 * @return Order
	 */
	@Override
	public Order enrichOrder(Order order, HttpHeaders headers) {
		return orderenrichmentProxy.enrichOrder(order, headers);
	}
}
